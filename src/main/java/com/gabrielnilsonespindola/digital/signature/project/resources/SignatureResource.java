package com.gabrielnilsonespindola.digital.signature.project.resources;

import java.io.File;
import java.util.Map;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.gabrielnilsonespindola.digital.signature.project.services.HashService;
import com.gabrielnilsonespindola.digital.signature.project.services.SignatureService;
import com.gabrielnilsonespindola.digital.signature.project.services.VerifyService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/signatures")
public class SignatureResource {

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private HashService hashService;

    @Autowired
    private SignatureService signatureService;

    @PostMapping("/signature")
    public ResponseEntity<String> assinarArquivo(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("certificado") MultipartFile certificado,
            @RequestParam("senha") String senha) {

        try {
            log.info("Iniciando processo de assinatura para o arquivo: {}", arquivo.getOriginalFilename());

            File arquivoOriginal = File.createTempFile("original-", ".txt");
            File certificadoPkcs12 = File.createTempFile("cert-", ".pfx");

            arquivo.transferTo(arquivoOriginal);
            certificado.transferTo(certificadoPkcs12);
            
            String hash = hashService.calcularHashSHA512(arquivoOriginal);
            File hashFile = new File("src/main/resources/arquivos/hash.txt");
            hashService.salvarHashEmArquivo(hash, hashFile);
           
            byte[] assinatura = signatureService.assinarArquivoCMS(arquivoOriginal, certificadoPkcs12, senha);
            
            File pastaArquivos = new File("src/main/resources/arquivos");
            if (!pastaArquivos.exists()) {
                pastaArquivos.mkdirs(); 
            }
            String nomeArquivoAssinado = "assinatura-" + System.currentTimeMillis() + ".p7s";
            File arquivoAssinado = new File(pastaArquivos, nomeArquivoAssinado);
            signatureService.salvarAssinaturaEmArquivo(assinatura, arquivoAssinado);

            log.info("Arquivo .p7s salvo com sucesso em: {}", arquivoAssinado.getAbsolutePath());
           
            String base64 = Base64.toBase64String(assinatura);
            return ResponseEntity.ok(base64);

        } catch (Exception e) {
            log.error("Erro ao assinar arquivo", e);
            return ResponseEntity.badRequest().body("Erro ao assinar arquivo: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verificarAssinatura(
            @RequestParam("arquivoAssinado") MultipartFile arquivoAssinado,
            @RequestParam("cadeia") MultipartFile[] cadeia) {

        try {
            log.info("Iniciando verificação da assinatura digital...");
            log.info("Arquivo assinado recebido: {}", arquivoAssinado.getOriginalFilename());
            log.info("Cadeia de certificados recebida: {} arquivos", cadeia.length);

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

            return ResponseEntity.ok(Map.of("status", status));

        } catch (Exception e) {
            log.error("Erro ao verificar assinatura", e);
            return ResponseEntity.badRequest().body("Erro ao verificar assinatura: " + e.getMessage());
        }
    }

}

