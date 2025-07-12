package com.gabrielnilsonespindola.digital.signature.project.services;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import digital.signature.project.services.SignatureService;

@ExtendWith(MockitoExtension.class)
public class SignatureServiceTest {
	
	@InjectMocks
	SignatureService signatureService;
	
	@Nested
	class assinarArquivoCMS {
		
		@Test
		void assinarArquivoCMSFuncionandoPfxValido() {
			
		}
		
		@Test
		void semChavePrivadaNoPfx() {
			
		}
		
		@Test
		void senhaIncorreta() {
			
		}
		
		@Test
		void arquivoInexistente() {
			
		}
	}
	
	@Nested
	class salvarAssinaturaEmArquivo {
		
		@Test
		void salvamentoCorreto() {
			
		}
		
		@Test
		void caminhoInvalidoNaoSalva() {
			
		}
	}
	
	@Nested
	class assinarESalvarArquivoCMS {
		
		@Test
		void assinaturaESalvamentoOk() {
			
		}
		
		@Test
		void keyStoreError() {
			
		}
	}

}
