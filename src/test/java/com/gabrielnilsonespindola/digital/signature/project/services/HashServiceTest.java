package com.gabrielnilsonespindola.digital.signature.project.services;

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
	
	@Nested
	class calcularHashSHA512 {
		
		@Test
		void gerarArquivoHashSHA512Corretamente() {
			
		}
		
		@Test
		void ErroAoCalcularOHashSHA512DoArquivo() {
			
		}
		
	}
	
	@Nested
	class saveHashToFile {
		
		@Test
		void salvarHashParaArquivoFuncionando() {
			
		}
		
		@Test
		void SalvarHashEmArquivoNãoFunciona() {
			
		}
	}

}
