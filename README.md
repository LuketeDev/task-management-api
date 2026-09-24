# Task Management API

API REST para gerenciamento de tarefas, desenvolvida com Java e Spring Boot, com foco em boas práticas de desenvolvimento backend, organização em camadas, validação, persistência relacional e testes automatizados.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- Lombok
- JUnit 5
- Mockito
- AssertJ
- Testcontainers
- OpenAPI / Swagger
- Gradle

## Arquitetura

O projeto utiliza uma arquitetura em camadas para separar responsabilidades:

```text
1. Controller
2. Mapper
3. Service
4. Repository
5. PostgreSQL
```

### TaskController

[TaskController.java](./src/main/java/com/lukete/task_manager_api/controller/TaskController.java)

Responsável por receber as requisições HTTP, validar os dados de entrada e retornar as respostas da API.

### DTO

Os DTOs são utilizados para separar o contrato da API das entidades persistidas no banco de dados, sendo eles:

- [CreateTaskRequest](./src/main/java/com/lukete/task_manager_api/dto/request/CreateTaskRequest.java)
- [UpdateTaskRequest](./src/main/java/com/lukete/task_manager_api/dto/request/UpdateTaskRequest.java)
- [UpdateTaskStatusRequest](./src/main/java/com/lukete/task_manager_api/dto/request/UpdateTaskStatusRequest.java)
- [TaskResponse](./src/main/java/com/lukete/task_manager_api/dto/request/TaskResponse.java)

### TaskMapper

[TaskMapper.java](./src/main/java/com/lukete/task_manager_api/mapper/TaskMapper.java)

Responsável pela conversão entre DTOs e entidades.

```text
Request DTO <-> Task
```

### TaskService

[TaskService.java](./src/main/java/com/lukete/task_manager_api/service/TaskService.java)

Concentra as regras de negócio da aplicação, incluindo criação, atualização, alteração de status e exclusão de tarefas.

### Repository

[TaskRepository.java](./src/main/java/com/lukete/task_manager_api/repository/TaskRepository.java)
Utiliza Spring Data JPA para realizar a persistência das tarefas no PostgreSQL.

### Database

O schema do banco é gerenciado pelo Flyway.

O Hibernate está configurado com:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Isso significa que o Hibernate valida o schema existente, enquanto o Flyway é responsável pelas alterações estruturais do banco.

---

## Funcionalidades

A API permite:

- Criar tarefas
- Listar tarefas
- Buscar uma tarefa por ID
- Atualizar uma tarefa
- Alterar o status de uma tarefa
- Excluir uma tarefa
- Validar dados de entrada
- Controlar transições de status

---

## Modelo de Task

[Task.java](./src/main/java/com/lukete/task_manager_api/entity/Task.java)

Cada tarefa possui:

| Campo         | Tipo         | Descrição                  |
| ------------- | ------------ | -------------------------- |
| `id`          | UUID         | Identificador da tarefa    |
| `title`       | String       | Título da tarefa           |
| `description` | String       | Descrição opcional         |
| `status`      | TaskStatus   | Status atual               |
| `priority`    | TaskPriority | Prioridade                 |
| `dueDate`     | LocalDate    | Data limite                |
| `createdAt`   | Instant      | Data de criação            |
| `updatedAt`   | Instant      | Data da última atualização |

### TaskStatus

[TaskStatus.java](./src/main/java/com/lukete/task_manager_api/entity/TaskStatus.java)

```text
PENDING
IN_PROGRESS
COMPLETED
CANCELLED
```

### Prioridades

[TaskPriority.java](./src/main/java/com/lukete/task_manager_api/entity/TaskPriority.java)

```text
LOW
MEDIUM
HIGH
```

---

## Regras de negócio

### Status inicial

Toda nova tarefa inicia com `status = PENDING`

### Prioridade padrão

Quando nenhuma prioridade é informada, a tarefa recebe: `priority = MEDIUM`

### Transições de status

As transições permitidas são:

```text
PENDING
 ├──→ IN_PROGRESS
 └──→ CANCELLED

IN_PROGRESS
 ├──→ COMPLETED
 └──→ CANCELLED

COMPLETED
 └──→ nenhuma transição

CANCELLED
 └──→ nenhuma transição
```

Transições não permitidas são rejeitadas pela camada de serviço.

### Datas de auditoria

- `createdAt` é definido na criação e não pode ser alterado.
- `updatedAt` é atualizado quando a entidade sofre uma alteração.

---

## Endpoints

### Criar tarefa

```http
POST /api/v1/tasks
```

Exemplo:

```json
{
  "title": "Implementar autenticação",
  "description": "Adicionar autenticação à API",
  "priority": "HIGH",
  "dueDate": "2026-10-15"
}
```

Retorna:

```text
201 Created
```

---

### Listar tarefas

```http
GET /api/v1/tasks
```

Retorna:

```text
200 OK
```

---

### Buscar tarefa

```http
GET /api/v1/tasks/{id}
```

Retorna:

```text
200 OK
```

ou:

```text
404 Not Found
```

---

### Atualizar tarefa

```http
PUT /api/v1/tasks/{id}
```

Exemplo:

```json
{
  "title": "Implementar autenticação JWT",
  "description": "Adicionar autenticação JWT à API",
  "priority": "HIGH",
  "dueDate": "2026-10-20"
}
```

Retorna:

```text
200 OK
```

---

### Alterar status

```http
PATCH /api/v1/tasks/{id}/status
```

Exemplo:

```json
{
  "status": "IN_PROGRESS"
}
```

Retorna:

```text
200 OK
```

---

### Excluir tarefa

```http
DELETE /api/v1/tasks/{id}
```

Retorna:

```text
204 No Content
```

---

## Documentação da API

A API possui documentação OpenAPI/Swagger.

Com a aplicação em execução, a interface do Swagger pode ser acessada em:

```text
http://localhost:8080/swagger-ui.html
```

A especificação OpenAPI está disponível em:

```text
http://localhost:8080/v3/api-docs
```

A documentação utiliza anotações como:

- `@Tag`
- `@Operation`
- `@ApiResponses`
- `@ApiResponse`
- `@Schema`

---

## Banco de dados

A aplicação utiliza PostgreSQL.

O schema é versionado através do Flyway:

```text
src/main/resources/db/migration/
```

Para desenvolvimento local, o projeto possui configuração Docker Compose para o banco de dados.

Suba o PostgreSQL com:

```bash
docker compose up -d
```

---

## Configuração

As configurações do banco podem ser sobrescritas através de variáveis de ambiente:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

Na ausência dessas variáveis, a aplicação utiliza os valores padrão definidos em `application.yml`.

---

## Executando o projeto

### Pré-requisitos

- Java 21
- Docker
- Docker Compose

### 1. Clone o repositório

```bash
git clone <URL_DO_REPOSITORIO>
cd task-management-api
```

### 2. Inicie o PostgreSQL

```bash
docker compose up -d
```

### 3. Execute a aplicação

Linux/macOS:

```bash
./gradlew bootRun
```

Windows:

```bash
gradlew.bat bootRun
```

A API estará disponível em:

```text
http://localhost:8080
```

---

## Testes

O projeto utiliza:

- JUnit 5
- Mockito
- AssertJ
- Testcontainers

Execute os testes com:

```bash
./gradlew test
```

Para executar o build completo:

```bash
./gradlew build
```

Os testes de integração utilizam PostgreSQL através do Testcontainers, permitindo validar o comportamento da aplicação em um ambiente próximo ao banco utilizado em produção.

---

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/
│   │   └── com/lukete/task_manager_api/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       │   ├── request/
│   │       │   └── response/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── service/
│   │       └── TaskManagerApiApplication.java
│   │
│   └── resources/
│       ├── db/
│       │   └── migration/
│       └── application.yml
│
└── test/
    └── java/
        └── com/lukete/task_manager_api/
```

---

## Objetivo do projeto

Este projeto foi desenvolvido como parte de um portfólio de backend Java, com o objetivo de aplicar conceitos utilizados no desenvolvimento de APIs REST reais:

- arquitetura em camadas;
- separação entre DTOs e entidades;
- validação de entrada;
- regras de negócio;
- persistência com JPA;
- migrations com Flyway;
- testes unitários;
- testes de integração;
- PostgreSQL;
- Testcontainers;
- documentação OpenAPI;
- configuração com Docker.

---

## Próximos passos

Possíveis evoluções do projeto:

- tratamento global de exceções;
- paginação;
- filtros e ordenação;
- autenticação e autorização;
- gerenciamento de usuários;
- deploy da API;
- CI/CD;
- observabilidade.

---

## Licença

Este projeto foi desenvolvido para fins de estudo e portfólio.
