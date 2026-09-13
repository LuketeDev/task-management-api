# Task Manager API

API REST de gerenciamento de tarefas, construída com Java 21, Spring Boot e PostgreSQL.

O projeto possui o bootstrap e o modelo JPA inicial de `Task`. Ainda não há endpoints, serviços, repositórios ou migrations do domínio.

## Tecnologias

- Java 21
- Spring Boot
- Gradle
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- OpenAPI/Swagger
- JUnit 5, Mockito e Testcontainers
- Docker Compose

## Pré-requisitos

- JDK 21
- Docker e Docker Compose (para o PostgreSQL local e os testes de integração)

## Executar localmente

Inicie o PostgreSQL:

```bash
docker compose up -d
```

Inicie a aplicação:

```bash
./gradlew bootRun
```

No Windows:

```bat
gradlew.bat bootRun
```

> Enquanto a primeira migration da tabela `tasks` não for adicionada, a aplicação não iniciará fora do ambiente de testes. Isso é intencional: o Hibernate está em modo `validate` e não cria schema automaticamente.

Após iniciada, a documentação Swagger estará disponível em [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

## Configuração do banco

Por padrão, a aplicação utiliza as variáveis abaixo. Os valores padrão são exclusivamente locais, definidos também no `compose.yaml`; não há credenciais reais no repositório.

| Variável | Valor padrão |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/task_management` |
| `DB_USERNAME` | `task_management` |
| `DB_PASSWORD` | `task_management` |

Você pode sobrescrevê-las no ambiente antes de iniciar a aplicação.

## Banco de dados e Flyway

As migrações devem ser adicionadas em `src/main/resources/db/migration`, usando nomes como `V1__create_tasks.sql`.

O Hibernate está configurado com `ddl-auto=validate`: ele valida as entidades contra o schema, mas não cria nem altera tabelas. O Flyway será responsável por toda evolução do banco.

## Testes e compilação

Execute os testes:

```bash
./gradlew test
```

Compile e execute todos os checks:

```bash
./gradlew build
```

O teste de contexto usa Testcontainers para iniciar um PostgreSQL descartável. Portanto, o Docker precisa estar em execução para rodar os testes.

## Validar o Docker Compose

```bash
docker compose config
```
