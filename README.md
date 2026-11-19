# Valky — api-java

Backend (Java + Spring Boot) do projeto **Valky**, da Global Solution 2026/2 – CitizenTech (FIAP).
Cuida de **login e contas**, **ficha de contexto**, **contatos de confiança** e do **resumo da ficha por IA**.
É a API consumida pelo app Mobile para tudo que é de conta e ficha (o alerta fica no `api-dotnet`).

> **Problema:** mulheres em risco de feminicídio só conseguem usar os apps de pânico oficiais depois da medida protetiva.
> A Valky protege desde o BO e entrega à polícia o contexto que a própria mulher deixou registrado.

## Tecnologias

Java 21 · Spring Boot 4 · Spring Data JPA (PostgreSQL) · Spring Security + JWT · **Spring AI (Google Gemini)** · **RabbitMQ** · Bean Validation · Swagger/OpenAPI

## Como rodar

### 1. Pré-requisitos
- **JDK 21**
- **Docker Desktop** (ou PostgreSQL e RabbitMQ instalados na máquina)
- Uma **chave da API do Google Gemini**, gerada em [aistudio.google.com](https://aistudio.google.com) → *Get API key*

### 2. Banco e fila
Linux/macOS:
```bash
DB_PASSWORD=sua_senha docker compose up -d
```
Windows (PowerShell):
```powershell
$env:DB_PASSWORD="sua_senha"
docker compose up -d
```
Sobe o PostgreSQL na porta **5433** (`valky_db`, usuário `valky_app`) e o RabbitMQ na porta 5672 (painel em http://localhost:15672, `guest`/`guest`).

> A senha definida aqui é gravada na **primeira** vez que o banco é criado. Para trocar depois, recrie o volume:
> `docker compose down -v` e suba de novo (apaga os dados de teste).

### 3. Variáveis de ambiente

| Variável | Obrigatória | Para que serve |
|---|---|---|
| `DB_PASSWORD` | sim | senha do PostgreSQL (a mesma usada no `docker compose`) |
| `JWT_SECRET` | sim | segredo da assinatura do JWT, **mínimo 32 caracteres** |
| `AI_API_KEY` | para o resumo por IA | chave da API do Google Gemini |
| `AI_MODEL` | não | modelo (padrão `gemini-3.5-flash-lite`) |
| `SPRING_AI_OPENAI_BASE_URL` | não | padrão: endpoint do Gemini compatível com a OpenAI (`https://generativelanguage.googleapis.com/v1beta/openai`) |
| `DB_URL`, `DB_USERNAME` | não | padrão `jdbc:postgresql://localhost:5433/valky_db` (porta do docker compose) / `valky_app` |
| `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USER`, `RABBITMQ_PASSWORD` | não | padrão `localhost:5672`, `guest`/`guest` |
| `JWT_EXPIRACAO_SEGUNDOS` | não | validade do token (padrão 3600) |
| `SEED_ENABLED` | não | `false` desliga a criação dos usuários de teste |

> Usa um PostgreSQL instalado na máquina em vez do Docker? Defina
> `DB_URL=jdbc:postgresql://localhost:5432/valky_db` e use a senha do seu banco em `DB_PASSWORD`
> (o banco `valky_db` e o usuário `valky_app` precisam existir).

Sem `AI_API_KEY` a API sobe normalmente; apenas o resumo fica com status `FALHA`.

**A chave da IA nunca deve ser escrita no código nem commitada** — passe sempre por variável de ambiente.

Linux/macOS:
```bash
export DB_PASSWORD=sua_senha
export JWT_SECRET=troque-por-um-segredo-com-mais-de-32-caracteres
export AI_API_KEY=sua_chave_do_gemini
./mvnw spring-boot:run
```
Windows (PowerShell):
```powershell
$env:DB_PASSWORD="sua_senha"
$env:JWT_SECRET="troque-por-um-segredo-com-mais-de-32-caracteres"
$env:AI_API_KEY="sua_chave_do_gemini"
.\mvnw.cmd spring-boot:run
```

IntelliJ: em **Run → Edit Configurations → ValkyApplication → Environment variables**, cole:
```
DB_PASSWORD=sua_senha;JWT_SECRET=troque-por-um-segredo-com-mais-de-32-caracteres;AI_API_KEY=sua_chave_do_gemini
```

A aplicação está no ar quando aparecer `Started ValkyApplication` no console.

### 4. Testes
```bash
./mvnw test
```
Usam H2 em memória (perfil `test`); não precisam de PostgreSQL, RabbitMQ nem chave de IA.

### 5. Documentação interativa
Swagger UI: **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)** — faça login em `/api/auth/login`, copie o `token` e clique em **Authorize**.

### 6. Testando o resumo por IA (passo a passo)
1. `POST /api/auth/login` com `{ "email": "cidada@valky.com", "senha": "Cidada@123" }` → copie o `token` e anote o `usuarioId`.
2. Clique em **Authorize** e cole o token.
3. `PUT /api/fichas-contexto/usuario/{usuarioId}` com, por exemplo:
   ```json
   {
     "informacoesImportantes": "Ex-companheiro me ameaçou por mensagem semana passada.",
     "orientacoesEmergencia": "Não ligar para meu celular, ir direto ao endereço.",
     "nomeAgressor": "João",
     "relacaoAgressor": "ex-companheiro",
     "possuiArma": true,
     "veiculo": "Gol prata ABC-1D23",
     "enderecosConhecidos": "Rua das Flores, 100",
     "numeroBo": "BO-2026-12345",
     "medidaProtetiva": true
   }
   ```
   A resposta volta na hora com `"resumoStatus": "PENDENTE"`.
4. Aguarde alguns segundos e chame `GET /api/fichas-contexto/usuario/{usuarioId}/resumo` → `"status": "GERADO"` com o resumo em tópicos.

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

**Provedor:** Google Gemini, acessado pelo starter `spring-ai-starter-model-openai` através do endpoint do Gemini compatível com a API da OpenAI. Por isso dá para trocar de provedor (OpenAI, Groq etc.) só mudando `SPRING_AI_OPENAI_BASE_URL`, `AI_MODEL` e `AI_API_KEY`, sem mexer no código.
O modelo padrão é o `gemini-3.5-flash-lite`, escolhido por responder em ~1 segundo — modelos maiores podem levar mais de 1 minuto, o que não serve para uma situação de emergência.

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
                                  Spring AI (Gemini) gera o resumo ──► grava (GERADO / FALHA)
```

- A requisição HTTP responde na hora; a IA, que é lenta, roda de forma assíncrona.
- A mensagem leva só o **id** da ficha: o dado sensível não trafega pela fila.
- Se o RabbitMQ estiver fora do ar, salvar a ficha continua funcionando; use `POST .../resumo` para gerar o resumo manualmente.
- Se a ficha for editada enquanto a IA responde, o resumo velho é descartado (gravação condicionada à versão da ficha).

## Problemas comuns

| Sintoma | Causa provável | Solução |
|---|---|---|
| `password authentication failed for user "valky_app"` | senha diferente da usada ao criar o banco, ou app conectando em outro PostgreSQL | confira `DB_PASSWORD`/`DB_URL`; ou `docker compose down -v` e suba de novo |
| `Could not resolve placeholder 'JWT_SECRET'` | variável não definida | defina `JWT_SECRET` (mín. 32 caracteres) |
| Resumo com `FALHA` e `404 ... model ... not found` no log | modelo indisponível para a chave | troque `AI_MODEL` por um modelo listado pela sua chave |
| Resumo fica muito tempo em `PENDENTE` | modelo lento | use um modelo `flash-lite` (padrão) |
| Erro de compilação / versão do Java | JDK diferente do 21 | selecione o JDK 21 no IntelliJ (*Project Structure → SDK*) |