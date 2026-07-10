
# ⚙️ Asthma Space — Backend API

**API REST do ecossistema Asthma Space**

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white)](#)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-4169E1?logo=postgresql&logoColor=white)](#)
[![Flyway](https://img.shields.io/badge/Migrations-Flyway-CC0200?logo=flyway&logoColor=white)](#)
[![Render](https://img.shields.io/badge/Deploy-Render-46E3B7?logo=render&logoColor=black)](#)

---

## 📌 Sobre

API REST do **Asthma Space** — aplicativo de gestão e bem-estar respiratório para pacientes com asma, desenvolvido como projeto acadêmico. O backend centraliza autenticação, regras de negócio, persistência e integrações externas, atendendo tanto o app Android quanto o painel administrativo web.

### Ecossistema

| Repositório | Descrição |
|---|---|
| 📱 [App Android](https://github.com/Paulo-back/AsmatchSpace-App) | Aplicativo mobile para pacientes |
| ⚙️ **Backend API** *(este repo)* | API REST com autenticação e regras de negócio |
| 🖥️ [Painel Admin](https://github.com/Paulo-back/Asthma_Space-panel) | Gerenciamento de usuários e perfis |
| 🌐 [Landing Page](https://github.com/Paulo-back/AsthmaSpace_webPage) | Página de apresentação — [ver online](https://asthma-space-web-page.vercel.app/) |

### Infraestrutura

```
   App Android ──┐
                 ├──── REST + JWT ────▶  Spring Boot (Render)
   Painel Admin ─┘                            │
                                              ├──▶ PostgreSQL (Railway)
                                              ├──▶ OpenWeather Air Pollution API
                                              └──▶ ViaCEP
```

---

## 🛠️ Stack e principais decisões

- **Spring Boot** com **Spring Security**: autenticação stateless via **JWT** e senhas com **BCrypt** — sem sessão no servidor, o que permite reciclagem de instâncias no free tier sem impacto
- **PostgreSQL** hospedado no **Railway**, com schema 100% versionado em migrações **Flyway** — recriar o banco em qualquer ambiente é executar a aplicação uma vez
- **Controle de acesso por perfil**: `ADMIN` (painel web) e `USER` (app)
- **JPA/Hibernate** com queries otimizadas em lote para respeitar os limites do free tier
- **Geração de PDF** com OpenPDF (relatórios do diário de sintomas)
- **Disciplina de timezone**: todas as operações de data usam explicitamente `America/Sao_Paulo` — o servidor roda em UTC, e datas sem zona explícita gerariam registros no dia errado para usuários no Brasil
- **Health endpoint leve** (`GET /`): responde antes da inicialização completa, evitando que o health check do Render reinicie a instância durante o boot

## 🧩 Domínios principais

- **Autenticação e contas** — login, cadastro, recuperação de senha com token de expiração, regras de negócio como CPF imutável após definido
- **Lembretes** — modelados no padrão *Template + Instances*: `lembrete_templates` guarda a configuração de recorrência (única, diária, semanal) e `lembrete_instancias` guarda as ocorrências por dia com status `PENDENTE`/`CONCLUIDO`/`IGNORADO`, viabilizando histórico de adesão ao tratamento sem tornar a edição da recorrência destrutiva
- **Diário de sintomas** — registro diário com exportação em PDF
- **Perfis e administração** — CRUD de usuários, ativação/desativação de contas e gestão de roles, consumidos pelo painel web

---

## 🚀 Executando localmente

### Pré-requisitos
- Java 17+
- PostgreSQL (local ou em nuvem)
- Maven (ou o wrapper `./mvnw` incluído)

### Configuração

Defina as variáveis de ambiente (ou o `application.properties` local):

```properties
# Banco de dados
DATABASE_URL=jdbc:postgresql://localhost:5432/asthmaspace
DATABASE_USER=postgres
DATABASE_PASSWORD=sua_senha

# JWT
JWT_SECRET=segredo
```

### Rodando

```bash
./mvnw spring-boot:run
```

O Flyway aplica as migrações automaticamente na primeira execução, criando o schema completo.

---

## ☁️ Deploy

- **API**: Render (free tier), com deploy automático a partir deste repositório
- **Keep-alive**: cron-job.org faz ping a cada 5 minutos, mantendo a instância ativa e tornando cold starts raros
- **Banco**: Neon (plano Hobby)

---

## 👥 Autores

Projeto desenvolvido como trabalho acadêmico:

- **Paulo Rosa** — [GitHub](https://github.com/Paulo-back) | [LinkedIn](https://www.linkedin.com/in/paulo-henrique-rosa-dev/)
- **Edimário Silva de Paula** — [GitHub](https://github.com/DePaulaEd) | [LinkedIn](https://www.linkedin.com/in/edimario-silva/)
- **Stefanne Pardim de Arruda Souza** — [LinkedIn](https://www.linkedin.com/in/stefannepardim/)
