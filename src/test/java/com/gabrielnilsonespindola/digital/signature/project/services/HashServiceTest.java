package com.gabrielnilsonespindola.digital.signature.project.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import digital.signature.project.services.HashService;
import digital.signature.project.services.exceptions.DigitalSignatureException;

@ExtendWith(MockitoExtension.class)
public class HashServiceTest {

	@InjectMocks
	HashService hashService;

	private File carregarArquivo(String nomeArquivo) throws Exception {
		return new File(getClass().getClassLoader().getResource("arquivos/" + nomeArquivo).toURI());
	}

	@Nested
	class calcularHashSHA512 {

		@Test
		void gerarArquivoHashSHA512Corretamente() throws Exception {
			
			File arquivoDoc = carregarArquivo("doc.txt");			
			String hash = hashService.calcularHashSHA512(arquivoDoc);
			
			assertNotNull(arquivoDoc);
			assertDoesNotThrow(() -> hashService.calcularHashSHA512(arquivoDoc));
			assertEquals(128 , hash.length());
			
		}

		@Test
		void erroAoCalcularOHashSHA512DoArquivo() throws Exception {
			
			File arquivoInvalido = new File("arquivos/inexistente.txt");
			
			DigitalSignatureException objException = assertThrows(DigitalSignatureException.class,
					() -> hashService.calcularHashSHA512(arquivoInvalido));	
			assertTrue(objException.getMessage().contains("Erro ao calcular hash SHA - 512"));

		}
	}

	@Nested
	class salvarHashEmArquivo {

		@Test
		void salvarHashParaArquivoFuncionando() throws Exception {
			
			String hash = "abcd1234";
			File destino = File.createTempFile("hash-test", ".txt");
			
			hashService.salvarHashEmArquivo(hash, destino);
			String conteudoSalvo = java.nio.file.Files.readString(destino.toPath());
			assertEquals(hash,conteudoSalvo);
			
		}

		@Test
		void salvarHashEmArquivoNãoFunciona() {
			
			String hash = "abcd1234";
			File destino = new File("Z:\\naoexiste\\hash.txt");
			
			DigitalSignatureException objException = assertThrows(DigitalSignatureException.class,
					() -> hashService.salvarHashEmArquivo(hash,destino));	
			assertTrue(objException.getMessage().contains("Erro ao salvar hash no arquivo"));

		}
	}

}
