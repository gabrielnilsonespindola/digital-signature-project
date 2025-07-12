package digital.signature.project.services;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.stereotype.Service;

import digital.signature.project.services.exceptions.DigitalSignatureException;
import digital.signature.project.services.exceptions.ObjectNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;

@Slf4j
@Service
public class SignatureService {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public byte[] assinarArquivoCMS(File arquivoOriginal, File pkcs12File, String senhaPkcs12) {
        try (FileInputStream fis = new FileInputStream(pkcs12File)) {

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(fis, senhaPkcs12.toCharArray());

            String alias = null;
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String a = aliases.nextElement();
                if (keyStore.isKeyEntry(a)) {
                    alias = a;
                    break;
                }
            }

            if (alias == null) {
                throw new ObjectNotFoundException("Nenhuma chave privada encontrada no KeyStore.");
            }

            log.info("Alias encontrado no keystore: {}", alias);

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, senhaPkcs12.toCharArray());
            if (privateKey == null) {
                throw new ObjectNotFoundException("Chave privada não encontrada para alias: " + alias);
            }

            Certificate cert = keyStore.getCertificate(alias);
            X509Certificate x509Cert = (X509Certificate) cert;

            byte[] data = java.nio.file.Files.readAllBytes(arquivoOriginal.toPath());
            CMSProcessableByteArray content = new CMSProcessableByteArray(data);

            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            ContentSigner signer = new JcaContentSignerBuilder("SHA512withRSA")
                    .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    .build(privateKey);

            generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                    new JcaDigestCalculatorProviderBuilder()
                            .setProvider("BC")
                            .build())
                    .build(signer, x509Cert));

            generator.addCertificates(new JcaCertStore(Collections.singletonList(x509Cert)));

            CMSSignedData signedData = generator.generate(content, true);

            log.info("Assinatura CMS gerada com sucesso.");
            return signedData.getEncoded();

        } catch (Exception e) {
            log.error("Erro ao assinar o arquivo CMS", e);
            throw new DigitalSignatureException("Erro ao assinar o arquivo digitalmente " + e.getMessage(), e);
        }
    }

    public void salvarAssinaturaEmArquivo(byte[] assinatura, File destino) {
        try (FileOutputStream fos = new FileOutputStream(destino)) {
            fos.write(assinatura);
            log.info("Arquivo de assinatura salvo com sucesso: {}", destino.getAbsolutePath());
        } catch (Exception e) {
            log.error("Erro ao salvar o arquivo de assinatura", e);
            throw new DigitalSignatureException("Erro ao salvar assinatura: " + e.getMessage(), e);
        }
    }

    public File assinarESalvarArquivoCMS(File arquivoOriginal, File pkcs12File, String senhaPkcs12,
                                         String caminhoArquivoAssinado) {
        byte[] assinatura = assinarArquivoCMS(arquivoOriginal, pkcs12File, senhaPkcs12);
        File arquivoAssinado = new File(caminhoArquivoAssinado);
        salvarAssinaturaEmArquivo(assinatura, arquivoAssinado);
        return arquivoAssinado;
    }
}
