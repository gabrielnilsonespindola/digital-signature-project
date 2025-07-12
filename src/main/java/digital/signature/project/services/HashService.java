package digital.signature.project.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import digital.signature.project.services.exceptions.DigitalSignatureException;

import java.io.File;
import java.security.MessageDigest;
import java.security.Security;
import java.util.Formatter;

@Slf4j
@Service
public class HashService {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public String calcularHashSHA512(File arquivo) {
        try {
            byte[] conteudo = FileUtils.readFileToByteArray(arquivo);
            MessageDigest md = MessageDigest.getInstance("SHA-512", "BC");
            byte[] hashBytes = md.digest(conteudo);
            String hashHex = bytesParaHex(hashBytes);
            log.info("Hash SHA-512 calculado com sucesso.");
            return hashHex;
        } catch (Exception e) {
            log.error("Erro ao calcular o hash SHA-512 do arquivo", e);
            throw new DigitalSignatureException ("Erro ao calcular hash SHA - 512 " + e.getMessage(), e);
        }
    }

    private String bytesParaHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String resultado = formatter.toString();
        formatter.close();
        return resultado;
    }

    public void salvarHashEmArquivo(String hash, File destino) {
        try {
            FileUtils.writeStringToFile(destino, hash, "UTF-8");
            log.info("Hash SHA-512 salvo no arquivo com sucesso: {}", destino.getAbsolutePath());
        } catch (Exception e) {
            log.error("Erro ao salvar o hash no arquivo", e);
            throw new DigitalSignatureException("Erro ao salvar hash no arquivo" + e.getMessage(), e);
        }
    }
}
