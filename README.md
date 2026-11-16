# Valky — api-java

Backend (Java + Spring Boot) do projeto **Valky**, da Global Solution 2026/2 – CitizenTech (FIAP).
Cuida de **login e contas**, **ficha de contexto**, **contatos de confiança** e do **resumo da ficha por IA**.
É a API consumida pelo app Mobile para tudo que é de conta e ficha (o alerta fica no `api-dotnet`).

> **Problema:** mulheres em risco de feminicídio só conseguem usar os apps de pânico oficiais depois da medida protetiva.
> A Valky protege desde o BO e entrega à polícia o contexto que a própria mulher deixou registrado.

## Tecnologias

Java 21 · Spring Boot 4 · Spring Data JPA (PostgreSQL) · Spring Security + JWT · **Spring AI** · **RabbitMQ** · Bean Validation · Swagger/OpenAPI

## Como rodar

### 1. Pré-requisitos
JDK 21, Docker (ou PostgreSQL e RabbitMQ instalados) e uma chave de API de LLM compatível com a API da OpenAI.

### 2. Banco e fila
```bash
DB_PASSWORD=sua_senha docker compose up -d
```
Sobe o PostgreSQL (`valky_db`, usuário `valky_app`) e o RabbitMQ (painel em http://localhost:15672, `guest`/`guest`).

### 3. Variáveis de ambiente

| Variável | Obrigatória | Para que serve |
|---|---|---|
| `DB_PASSWORD` | sim | senha do PostgreSQL |
| `JWT_SECRET` | sim | segredo da assinatura do JWT, **mínimo 32 caracteres** |
| `AI_API_KEY` | para o resumo por IA | chave do provedor de LLM |
| `AI_MODEL` | não | modelo (padrão `gpt-4o-mini`) |
| `SPRING_AI_OPENAI_BASE_URL` | não | outro provedor compatível com a API da OpenAI |
| `DB_URL`, `DB_USERNAME` | não | padrão `jdbc:postgresql://localhost:5432/valky_db` / `valky_app` |
| `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD` | não | padrão `localhost:5672`, `guest`/`guest` |
| `JWT_EXPIRACAO_SEGUNDOS` | não | validade do token (padrão 3600) |
| `SEED_ENABLED` | não | `false` desliga a criação dos usuários de teste |

Sem `AI_API_KEY` a API sobe normalmente; apenas o resumo fica com status `FALHA`.

Linux/macOS:
```bash
export DB_PASSWORD=sua_senha
export JWT_SECRET=troque-por-um-segredo-com-mais-de-32-caracteres
export AI_API_KEY=sua_chave
./mvnw spring-boot:run
```
Windows (PowerShell):
```powershell
$env:DB_PASSWORD="sua_senha"
$env:JWT_SECRET="troque-por-um-segredo-com-mais-de-32-caracteres"
$env:AI_API_KEY="sua_chave"
.\mvnw.cmd spring-boot:run
```

### 4. Testes
```bash
./mvnw test
```
Usam H2 em memória (perfil `test`); não precisam de PostgreSQL, RabbitMQ nem chave de IA.

### 5. Documentação interativa
Swagger UI: **http://localhost:8080/swagger-ui.html** — faça login em `/api/auth/login`, copie o `token` e clique em **Authorize**.

## Usuários de teste (criados ao subir, se `SEED_ENABLED` ≠ `false`)

| Perfil | E-mail | Senha |
|---|---|---|
| GERENTE | `gerente@valky.com` | `Gerente@123` |
| CIDADA | `cidada@valky.com` | `Cidada@123` |

O cadastro público (`POST /api/usuarios`) **sempre** cria uma CIDADA; não existe jeito de se cadastrar como gerente pela API.

## Segurança (Spring Security + JWT)

1. `POST /api/auth/login` valida e-mail/senha (BCrypt) e devolve um JWT HS256 com `sub` (e-mail), `uid` (id) e `roles` (`CIDADA` ou `GERENTE`).
2. O app envia `Authorization: Bearer <token>`; a API é *stateless* e valida assinatura e validade em toda requisição.
3. Sem token ou token inválido → **401**. Token válido sem permissão → **403**.

| Recurso | CIDADA | GERENTE |
|---|---|---|
| Cadastro e login | público | público |
| Ver/editar/excluir a **própria** conta | ✅ | ✅ (ver e excluir qualquer conta) |
| Listar todos os usuários (paginado) | ❌ | ✅ |
| Estatísticas agregadas (`/api/gestao/**`) | ❌ | ✅ |
| **Ficha de contexto e contatos de confiança** | só os próprios | ❌ (dado sensível) |

## Endpoints principais

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/login` | login, devolve o JWT |
| POST | `/api/usuarios` | cadastro público |
| GET | `/api/usuarios?page=0&size=10` | lista paginada (GERENTE) |
| GET / PUT / DELETE | `/api/usuarios/{id}` | consulta / atualização / exclusão |
| GET / POST | `/api/contatos-confianca/usuario/{usuarioId}` | lista / cadastra contatos |
| DELETE | `/api/contatos-confianca/{id}` | exclui um contato da própria usuária |
| GET / PUT | `/api/fichas-contexto/usuario/{usuarioId}` | consulta / cria-atualiza a ficha |
| GET | `/api/fichas-contexto/usuario/{usuarioId}/resumo` | status e texto do resumo da IA |
| POST | `/api/fichas-contexto/usuario/{usuarioId}/resumo` | gera o resumo agora (síncrono) |
| GET | `/api/gestao/estatisticas` | números agregados (GERENTE) |

Erros seguem sempre o mesmo formato: `{ "timestamp", "status", "erro", "mensagem", "campos" }` (`campos` só em erro de validação).

## Modelagem

`Usuario` 1—N `ContatoConfianca` · `Usuario` 1—1 `FichaContexto`

A ficha guarda os campos fixos (possível envolvido, relação, arma, veículo/placa, endereços, BO, medida protetiva), os dois textos livres e o resumo da IA com seu status.

## Spring AI — resumo da ficha

Quando a usuária salva a ficha, a API gera um **resumo curto** (até 6 tópicos) para a polícia ler rápido, usando o `ChatClient` do Spring AI.
Não é um chat solto: faz parte do fluxo do alerta (a polícia recebe o resumo junto com a localização).

Cuidados de projeto, porque a ficha **não é prova**:
- o prompt proíbe inventar dados e chamar alguém de culpado; só se fala em "possível envolvido" e "risco informado";
- o texto sempre começa com *"Possível envolvido, segundo informação da usuária, não verificada."*, acrescentado pelo código e não pelo modelo;
- nome, e-mail e telefone da usuária **não** são enviados ao LLM, apenas o conteúdo da ficha;
- se a IA falhar (limite de requisições, chave inválida), a ficha continua salva e o resumo fica com status `FALHA`.

## Arquitetura distribuída — opção escolhida: **A) RabbitMQ**

```
PUT /fichas-contexto/...  ──►  salva ficha (resumo = PENDENTE)
                                   │ publica "ficha.atualizada" (id da ficha)
                                   ▼
                       exchange valky.ficha ──► fila valky.ficha.resumo
                                                       │
                                  @RabbitListener (FichaResumoConsumer)
                                                       ▼
                                  Spring AI gera o resumo ──► grava (GERADO / FALHA)
```

- A requisição HTTP responde na hora; a IA, que é lenta, roda de forma assíncrona.
- A mensagem leva só o **id** da ficha: o dado sensível não trafega pela fila.
- Se o RabbitMQ estiver fora do ar, salvar a ficha continua funcionando; use `POST .../resumo` para gerar o resumo manualmente.
- Se a ficha for editada enquanto a IA responde, o resumo velho é descartado (gravação condicionada à versão da ficha).

