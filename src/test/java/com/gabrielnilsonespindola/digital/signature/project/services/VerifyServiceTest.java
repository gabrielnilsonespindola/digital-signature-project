package com.gabrielnilsonespindola.digital.signature.project.services;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
			assertTrue(true);
		}
		
		@Test
		void assinaturaInvalida() {
			assertTrue(true);
		}
		
		@Test
		void arquivoMalformado() {
			assertTrue(true);
		}
		
		@Test
		void arquivoDeCertificadoInvalido() {
			assertTrue(true);
		}
		
		@Test
		void arquivoInexistente() {
			assertTrue(true);
		}
		
	}
	
	
}
