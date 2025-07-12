package com.gabrielnilsonespindola.digital.signature.project.services;

import java.io.File;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import digital.signature.project.services.HashService;

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
			
			
		}

		@Test
		void erroAoCalcularOHashSHA512DoArquivo() {

		}

	}

	@Nested
	class saveHashToFile {

		@Test
		void salvarHashParaArquivoFuncionando() throws Exception {

		}

		@Test
		void salvarHashEmArquivoNãoFunciona() {

		}
	}

}
