# Projeto Digital Signature "Desafio prático - Desenvolvedor back-end Java" - Bry Tecnologia

# Sobre 

Este projeto consiste em uma API REST desenvolvida em Java com Spring Boot, que realiza assinaturas digitais no padrão CMS (PKCS#7), utilizando certificados no formato PKCS#12 (.pfx). As principais funcionalidades da API incluem:

● Assinatura Digital de arquivos utilizando certificados digitais (PKCS12).

● Validação de Assinaturas: verificação da integridade, validade e informações do signatário.

● Geração de Hash SHA-512 de arquivos.

● Manipulação de Arquivos para salvar e recuperar assinaturas e arquivos hash.

● Testes Unitários com JUnit 5 e Mockito, cobrindo principais fluxos e exceções.

● Pipeline CI com GitHub Actions: build automático.

● Log com Logback + SLF4J, armazenado localmente com estrutura organizada.

● Coleção do Postman para testes práticos dos endpoints da API.

Este projeto simula um fluxo real de assinatura digital utilizado em ambientes corporativos, como sistemas jurídicos, financeiros e governamentais, garantindo integridade e autenticidade de documentos. A aplicação utiliza bibliotecas robustas como BouncyCastle para assinatura no padrão CMS/PKCS#7, além de estar preparada para CI, testes automatizados e integração com ferramentas de validação de cadeia de certificação (ICP-Brasil). É ideal como prova de conceito ou base para sistemas que exigem conformidade com segurança de documentos digitais.

# Fluxo geral da aplicação

● Usuário envia um arquivo .txt e certificado .pfx para o endpoint de assinatura.

● O sistema gera o hash SHA-512, assina usando BouncyCastle e retorna a assinatura CMS .p7s.

● O usuário pode verificar a assinatura via API, enviando o .p7s e a cadeia de certificados.

● O sistema valida a assinatura, verifica a confiança da cadeia e retorna status + informações.

● Todas as ações são logadas e testadas automaticamente no GitHub Actions a cada atualização.


# Tecnologias Utilizadas 

● Java 17

● Spring Boot 3.1.4

● BouncyCastle (criptografia e assinatura digital CMS - PKCS#7)

● SHA-512 (resumo criptográfico)

● Apache Commons IO (manipulação de arquivos)

● PKCS#12 (.pfx) para certificados digitais

● SLF4J + Logback (logs e registros)

● Maven (build e gerenciamento de dependências)

● JUnit 5 / Mockito (testes unitários)

● GitHub Actions (CI - integração contínua)

● Postman (testes de endpoints)

● Eclipse IDE

# Como executar o projeto
# Back end

● Pré-requisitos: 

● Java 17

● STS IDE

● Clonar repositório

git clone https://github.com/gabrielnilsonespindola/digital-signature-project.git

● Entrar na pasta do projeto back end

cd digital-signature-project

● Executar o projeto

./mvnw spring-boot:run

# Importando a collection do Postman
Para facilitar os testes dos endpoints da API, este projeto inclui uma collection do Postman.

# Como importar:

● Abra o Postman

● Clique em "Import" no canto superior esquerdo

● Selecione a aba "File"

● Clique em "Upload Files"

● Escolha o arquivo digital signature.postman_collection.json (incluso neste repositório na pasta raiz do projeto, subpasta "postman")

● Clique em "Import"

# Testando aplicação via postman

● Iniciar aplicação, após estar rodando na porta local da sua maquina e estar com o import do aquivo JSON feito podera enviar as requisições HTTP do projeto com as seguintes configurações asseguradas no ambiente POSTMAN.

● EndPoint -POST- /signature  -  http://localhost:8080/signatures/signature , na aba "Body" marca a opção "form-data". Nesta aba selecionar os campos e verificar a existencia dos arquivos.

Key "arquivo"  -  Type "File"   -  Value "doc.txt" (Importar arquivo do diretorio do projeto)

Key "certificado"  -  Type "File"  Value "certificado_teste_hub_pfx" (Importar arquivo do diretorio do projeto)

Key "senha"  -  Type  "Text"  Value  "bry123456"

Gera uma assinatura digital PKCS#7 attached (.p7s) do arquivo doc.txt

Utiliza o certificado .pfx informado

Retorna a assinatura em Base64 no corpo da resposta

● EndPoint -POST- /verify  -  http://localhost:8080/signatures/verify , na aba "Body" marca a opção "form-data". Nesta aba selecionar os campos e verificar a existencia dos arquivos.

OBS : Este método é chamado após a resposta "OK" do endpoint anterior "signature"

Key "arquivoAssinado"  -  Type "File"  Value "assinatura-1752273355643.p7s" (Importar arquivo do diretorio do projeto)

Key "cadeia" -  Type "File"  Value "ac_bry_servidor_seguro_v3.cer" e "ac_raiz_bry_v3.cer" (Importar arquivos do diretorio do projeto)

Verifica se a assinatura .p7s é válida para o arquivo original.

Confirma integridade e autenticidade com base na cadeia de certificação ICP-Brasil.

Retorna um objeto com:

Status da verificação 

Nome do signatário (CN)

Algoritmo de hash

Data/hora da assinatura

# Autor : 

Gabriel Nilson Espindola

🔗 https://www.linkedin.com/in/gabriel-nilson-espindola-065694297/






