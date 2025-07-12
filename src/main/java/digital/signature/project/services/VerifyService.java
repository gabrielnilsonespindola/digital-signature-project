package digital.signature.project.services;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

            SignerInformationStore signerInfos = signedData.getSignerInfos();
            SignerInformation signer = signerInfos.getSigners().iterator().next();

            Store<X509CertificateHolder> certStore = signedData.getCertificates();
            Collection<X509CertificateHolder> certHolders = certStore.getMatches(signer.getSID());
            X509CertificateHolder certHolder = certHolders.iterator().next();

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
                return false;
            }

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            List<X509Certificate> certificados = new ArrayList<>();

            for (File arquivoCert : cadeiaCertificados) {
                try (FileInputStream certFis = new FileInputStream(arquivoCert)) {
                    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certFis);
                    certificados.add(cert);
                }
            }

            CertPath certPath = certFactory.generateCertPath(certificados);
            log.info("Assinatura válida. Certificado do assinante: {}",
                    certAssinante.getSubjectX500Principal().getName());
            return true;

        } catch (Exception e) {
            log.error("Erro na verificação da assinatura", e);
            return false;
        }
    }
}
