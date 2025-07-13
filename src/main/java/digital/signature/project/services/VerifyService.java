package digital.signature.project.services;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;
import org.springframework.stereotype.Service;
import digital.signature.project.services.exceptions.VerificationException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.File;
import java.io.FileInputStream;
import java.security.Security;
import java.security.cert.*;
import java.util.*;

@Slf4j
@Service
public class VerifyService {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public boolean verify(File arquivoAssinado, File[] cadeiaCertificados) {
        try (FileInputStream fis = new FileInputStream(arquivoAssinado)) {

            byte[] signedDataBytes = fis.readAllBytes();
            CMSSignedData signedData = new CMSSignedData(signedDataBytes);

            SignerInformation signer = signedData.getSignerInfos().getSigners().iterator().next();
            SignerId signerId = signer.getSID();

            Store<?> certStoreGeneric = signedData.getCertificates();
            Collection<?> certCollection = certStoreGeneric.getMatches(signerId);

            if (certCollection.isEmpty()) {
                throw new VerificationException("Certificado do assinante não encontrado no arquivo assinado");
            }

            X509CertificateHolder certHolder = (X509CertificateHolder) certCollection.iterator().next();

            X509Certificate certAssinante = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(certHolder);

            boolean assinaturaValida = signer.verify(
                    new JcaSimpleSignerInfoVerifierBuilder()
                            .setProvider("BC")
                            .build(certAssinante)
            );

            if (!assinaturaValida) {
                log.warn("Assinatura inválida.");
                throw new VerificationException("A assinatura do arquivo é inválida");
            }

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            List<X509Certificate> certificados = new ArrayList<>();
            Set<TrustAnchor> trustAnchors = new HashSet<>();

            for (int i = 0; i < cadeiaCertificados.length; i++) {
                try (FileInputStream certFis = new FileInputStream(cadeiaCertificados[i])) {
                    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certFis);
                    certificados.add(cert);                    
                    if (i == cadeiaCertificados.length - 1) {
                        trustAnchors.add(new TrustAnchor(cert, null));
                    }
                }
            }

            CertPath certPath = certFactory.generateCertPath(certificados);
            PKIXParameters params = new PKIXParameters(trustAnchors);
            params.setRevocationEnabled(false);

            CertPathValidator validator = CertPathValidator.getInstance("PKIX");
            validator.validate(certPath, params);

            String dn = certAssinante.getSubjectX500Principal().getName();
            String cn = new LdapName(dn).getRdns().stream()
                    .filter(rdn -> rdn.getType().equalsIgnoreCase("CN"))
                    .map(Rdn::getValue)
                    .findFirst()
                    .orElse("CN não encontrado").toString();

            AttributeTable signedAttributes = signer.getSignedAttributes();
            Attribute signingTimeAttr = signedAttributes.get(CMSAttributes.signingTime);
            Date signingTime = null;
            if (signingTimeAttr != null) {
                ASN1Set attrValues = signingTimeAttr.getAttrValues();
                ASN1Encodable attrValue = attrValues.getObjectAt(0);
                signingTime = ((ASN1UTCTime) attrValue).getDate();
            }

            log.info("Assinatura válida.");
            log.info("Nome do signatário (CN): {}", cn);
            log.info("Data/hora da assinatura: {}", signingTime);

            return true;

        } catch (Exception e) {
            log.error("Erro na verificação da assinatura", e);
            throw new VerificationException("Erro ao verificar a assinatura digital: " + e.getMessage(), e);
        }
    }
}

