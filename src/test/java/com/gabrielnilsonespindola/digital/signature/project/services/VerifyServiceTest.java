package com.gabrielnilsonespindola.digital.signature.project.services;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import digital.signature.project.services.VerifyService;

@ExtendWith(MockitoExtension.class)
public class VerifyServiceTest {

	@InjectMocks
	VerifyService verifyService;
	
	@Nested
	class verify {
		
		@Test
		void assinaturaValida() {
			
		}
		
		@Test
		void assinaturaInvalida() {
			
		}
		
		@Test
		void arquivoMalformado() {
			
		}
		
		@Test
		void arquivoDeCertificadoInvalido() {
			
		}
		
		@Test
		void arquivoInexistente() {
			
		}
		
	}
	
	
}
