package digital.signature.project.services;

import java.io.File;
import java.util.Map;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import digital.signature.project.services.exceptions.DigitalSignatureException;
import digital.signature.project.services.exceptions.ObjectNotFoundException;
import digital.signature.project.services.exceptions.VerificationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SignatureFacadeService {

    @Autowired
    private HashService hashService;

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private VerifyService verifyService;

    public String assinarArquivo(MultipartFile arquivo, MultipartFile certificado, String senha) {
        try {
            log.info("Início do processo de assinatura - arquivo: {}, certificado: {}", arquivo.getOriginalFilename(), certificado.getOriginalFilename());

            File arquivoOriginal = File.createTempFile("original-", ".txt");
            File certificadoPkcs12 = File.createTempFile("cert-", ".pfx");

            arquivo.transferTo(arquivoOriginal);
            certificado.transferTo(certificadoPkcs12);

            String hash = hashService.calcularHashSHA512(arquivoOriginal);
            File hashFile = new File("src/main/resources/arquivos/hash.txt");
            hashService.salvarHashEmArquivo(hash, hashFile);
            log.info("Hash SHA-512 salvo no arquivo: {}", hashFile.getAbsolutePath());

            byte[] assinatura = signatureService.assinarArquivoCMS(arquivoOriginal, certificadoPkcs12, senha);
            String base64 = Base64.toBase64String(assinatura);

            log.info("Assinatura CMS gerada com sucesso e codificada em Base64.");

            return base64;

        } catch (ObjectNotFoundException onfe) {
            log.error("Objeto não encontrado durante assinatura", onfe);
            throw onfe;
        } catch (Exception e) {
            log.error("Erro geral ao assinar arquivo", e);
            throw new DigitalSignatureException("Erro no processo de assinatura: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> verificarAssinatura(MultipartFile arquivoAssinado, MultipartFile[] cadeia) {
        try {
            log.info("Início do processo de verificação de assinatura - arquivo assinado: {}, quantidade de certificados na cadeia: {}",
                    arquivoAssinado.getOriginalFilename(), cadeia.length);

            File p7s = File.createTempFile("assinatura-", ".p7s");
            arquivoAssinado.transferTo(p7s);
            log.info("Arquivo .p7s salvo temporariamente em: {}", p7s.getAbsolutePath());

            File[] cadeiaCertificados = new File[cadeia.length];
            for (int i = 0; i < cadeia.length; i++) {
                File certTemp = File.createTempFile("cert-", ".cer");
                cadeia[i].transferTo(certTemp);
                cadeiaCertificados[i] = certTemp;
                log.info("Certificado {} salvo temporariamente em: {}", i, certTemp.getAbsolutePath());
            }

            boolean resultado = verifyService.verify(p7s, cadeiaCertificados);

            String status = resultado ? "VALIDO" : "INVALIDO";
            log.info("Resultado da verificação: {}", status);

            return Map.of("status", status);

        } catch (VerificationException ve) {
            log.error("Erro de verificação de assinatura", ve);
            throw ve;
        } catch (Exception e) {
            log.error("Erro geral ao verificar assinatura", e);
            throw new DigitalSignatureException("Erro no processo de verificação: " + e.getMessage(), e);
        }
    }
}


