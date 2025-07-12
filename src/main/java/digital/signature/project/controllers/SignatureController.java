package digital.signature.project.controllers;

import digital.signature.project.services.SignatureFacadeService;
import digital.signature.project.services.exceptions.DigitalSignatureException;
import digital.signature.project.services.exceptions.ObjectNotFoundException;
import digital.signature.project.services.exceptions.VerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/signatures")
public class SignatureController {

    @Autowired
    private SignatureFacadeService assinaturaFacadeService;

    @PostMapping("/signature")
    public ResponseEntity<?> assinarArquivo(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("certificado") MultipartFile certificado,
            @RequestParam("senha") String senha) {

        try {
            String assinaturaBase64 = assinaturaFacadeService.assinarArquivo(arquivo, certificado, senha);
            return ResponseEntity.ok(Map.of("assinatura_base64", assinaturaBase64));

        } catch (ObjectNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));

        } catch (DigitalSignatureException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            log.error("Erro inesperado ao assinar", e);
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro interno inesperado ao assinar."));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verificarAssinatura(
            @RequestParam("arquivoAssinado") MultipartFile arquivoAssinado,
            @RequestParam("cadeia") MultipartFile[] cadeia) {

        try {
            Map<String, Object> resultado = assinaturaFacadeService.verificarAssinatura(arquivoAssinado, cadeia);
            return ResponseEntity.ok(resultado);

        } catch (VerificationException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));

        } catch (DigitalSignatureException e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", e.getMessage()));

        } catch (Exception e) {
            log.error("Erro inesperado ao verificar assinatura", e);
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro interno inesperado ao verificar assinatura."));
        }
    }
}

