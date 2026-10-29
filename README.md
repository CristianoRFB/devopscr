# Sistema DevOps — Atividade Acadêmica

**Autor:** Cristiano Ronaldo Ferreira Bueno

Projeto de faculdade desenvolvido em **Java 17, Spring Boot, Thymeleaf e PostgreSQL**. A interface utiliza uma identidade visual em vermelho e vinho.

## O que já está implementado

- Autenticação com perfis de usuário (USER) e administrador (ADMIN).
- Loja com pesquisa, catálogo, detalhes e imagens de produtos.
- Carrinho por sessão: adicionar, alterar quantidade, remover itens e calcular o total.
- Painel administrativo com cadastro, edição e exclusão de produtos.
- Gestão administrativa de pedidos, itens, alunos, cursos, professores, disciplinas e usuários.

## Limitação atual

O carrinho ainda não possui o botão de finalizar compra criando automaticamente um pedido. Os pedidos podem ser registrados e gerenciados pelo painel administrativo.

## Executar localmente

1. Instale o **Java 17** e o **PostgreSQL**.
2. Crie um banco de dados chamado `devops_db`.
3. Configure as variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` (consulte `devops/src/main/resources/application.properties`).
4. No diretório `devops`, execute `./mvnw spring-boot:run` (Linux/macOS) ou `mvnw.cmd spring-boot:run` (Windows).
5. Acesse `http://localhost:8080`.

O projeto contém contas de demonstração inicializadas no código. Elas são apenas para testes locais: altere as credenciais e revise a segurança antes de publicar o sistema na internet.

## Organização

- `devops/src/main/java`: controladores, serviços, entidades, repositórios e autenticação.
- `devops/src/main/resources/templates`: páginas Thymeleaf.
- `devops/src/main/resources/static/css/styles.css`: identidade visual.
