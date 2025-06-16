# EasyList

**EasyList** é um produto da **EasyTech**, uma plataforma voltada para gestão inteligente de anúncios no Mercado Livre. O sistema permite a clonagem de anúncios existentes, edição em formato de rascunho (draft) e posterior publicação diretamente pela API do Mercado Livre. Tudo isso integrado a uma autenticação segura com JWT e controle de acesso baseado em roles.

---

## 🚀 Funcionalidades

### 🔐 Autenticação e Segurança
- Registro e login de usuários com JWT.
- Atribuição de permissões (`ROLE_USER`, `ROLE_ADMIN`).
- Armazenamento seguro de senhas com BCrypt.
- Proteção de endpoints com Spring Security.

### 🛒 Integração com Mercado Livre
- Login OAuth 2.0 com PKCE.
- Armazenamento seguro de tokens de acesso e refresh.
- Consulta de dados do usuário conectado (`/users/me`).
- Webhook configurável (em breve).

### ✏️ Gerenciamento de Anúncios
- Clonagem de anúncios do Mercado Livre usando `item_id`.
- Armazenamento dos anúncios clonados como **Drafts** para edição.
- Edição de drafts antes da publicação.
- Publicação de drafts como novos anúncios.
- Listagem de drafts por usuário.

---

## 🧱 Estrutura de Pacotes

```plaintext
easylist/
├── controller/                          # Controladores REST (AuthController, ItemController)
├── dto/                                 # Data Transfer Objects (Request/Response)
├── exception/                           # Tratamento global de exceções
├── model/                               # Entidades JPA (User, Token, DraftItem)
├── repository/                          # Repositórios Spring Data JPA
├── security/                            # Configuração do Spring Security e filtros JWT
├── service/                             # Lógica de negócios (auth, integração ML)
├── util/                                # Utilitários diversos (ex: geração de PKCE)
├── resources/
│   └── application.properties           # Configuração da aplicação
└── pom.xml                              # Gerenciamento de dependências Maven
---
```
## 🛠️ Tecnologias Utilizadas

- Java 17  
- Spring Boot  
- Spring Security  
- Spring Data JPA  
- PostgreSQL  
- JWT   
- Swagger UI  
- Lombok  
- Maven
- Mercado Livre API
- Ngrok

---

## 📦 Instalação

### Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL
- Postman



### Clone o projeto

```bash
git clone https://github.com/seu-usuario/easylist.git
cd easylist

```

### Atualize application.properties:

- spring.datasource.url=jdbc:postgresql://localhost:5432/easylist_db
- spring.datasource.username=postgres
- spring.datasource.password=your_password
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=true

### Configuração do JWT

- jwt.secret=uma-chave-segura-com-32-caracteres-ou-mais
- jwt.expiration=86400000

### Configuração do Swagger

- springdoc.api-docs.path=/v3/api-docs
- springdoc.swagger-ui.path=/swagger-ui.html
- springdoc.swagger-ui.enabled=true
- springdoc.packages-to-scan=com.mvp.op.controller

# Documentação do Sistema - Integração Mercado Livre com Nota Fiscal e Estoque

## Visão Geral

O sistema implementado integra um backend em Java Spring Boot com a API do Mercado Livre, permitindo gestão de:

* Clonagem e edição de itens
* Publicação de rascunhos
* Agendamento de publicações
* Controle de estoque
* Emissão e gestão de notas fiscais eletrônicas (NFe)

---

## Funcionalidades Implementadas

### 1. Clonagem de Anúncio

* **Endpoint**: `POST /items/clone/{itemId}`
* Clona um produto do Mercado Livre e o salva como rascunho (DraftItem)
* Salva imagens, descrição e outros atributos

### 2. Edição de Rascunhos

* **Endpoint**: `PUT /items/drafts/{id}`
* Permite editar dados como título, preço, descrição, estoque e imagens

### 3. Listagem de Rascunhos por Usuário

* **Endpoint**: `GET /items/drafts/user/{userId}`
* Lista todos os rascunhos criados pelo usuário

### 4. Publicação de Rascunhos

* **Endpoint**: `POST /items/publish/{draftId}`
* Publica um rascunho no Mercado Livre

---

### 5. Agendamento de Publicações

* **Agendamento**: via endpoint ou rotina automatizada
* **Endpoint**: `POST /schedule`
* Salva a publicação de um rascunho para um horário futuro
* **Executação automática**: `@Scheduled(fixedRate = 60000)` (a cada 1 minuto)

### 6. Listagem de Agendamentos

* **Endpoint**: `GET /schedule/{userId}`
* Lista publicações agendadas para o usuário

---

### 7. Controle de Estoque

* **Endpoint para atualizar**: `PUT /stock/update`
* Atualiza estoque de um rascunho e registra em StockEntry
* **Endpoint para listar estoque**: `GET /stock/{userId}`
* Lista todos os registros de estoque do usuário

---

### 8. Nota Fiscal Eletrônica (NFe)

#### a. Upload de Nota Fiscal

* **Endpoint**: `POST /nfe/upload`
* Envia dados de nota fiscal (JSON) para o Mercado Livre
* Salva em banco local via entidade `FiscalInvoice`

#### b. Consulta de Nota Fiscal

* **Endpoint**: `GET /nfe/user/{userId}`
* Lista todas as notas fiscais do usuário

#### c. Download da Nota Fiscal

* **Endpoint**: `GET /nfe/download/{userId}/{invoiceId}`
* Retorna os dados resumidos da nota fiscal, incluindo:

  * URL para download do PDF (`pdf_url`)
  * Status da nota (`status`)
  * Data de emissão (`issued_at`)

#### d. Recebimento de Notificações

* Webhook pode ser configurado para escutar eventos de notas
* A ser implementado: `POST /webhook/fiscal`

#### e. Regras Tributárias e Configurações Fiscais

* Referências implementadas para endpoints:

  * Composição de base ICMS/IPI
  * Composição PIS/COFINS
  * DIFAL
  * Mensagens fiscais (ex: operação de devolução, intermediador)
  * Envio de inscrições estaduais e municipais

---

## Entidades Principais

### DraftItem

* Representa um item rascunho clonado ou editado antes da publicação

### ScheduledPublication

* Agendamento de publicação futura de um DraftItem

### StockEntry

* Registro de controle de estoque para cada item e usuário

### FiscalInvoice

* Representa uma nota fiscal emitida, com dados como:

  * invoiceId
  * userId
  * status
  * downloadUrl
  * data de emissão

---

## Tecnologias e Integrações

* **Spring Boot**: backend principal
* **JPA/Hibernate**: persistência
* **Swagger/OpenAPI**: documentação dos endpoints
* **Mercado Livre API**: integração completa com publicação, estoque e fiscal
* **Ngrok**: utilizado para expor o webhook local durante desenvolvimento

---

## Próximos Passos (Sugestões)

* Implementar tela de dashboard para agendamentos e estoque
* Permitir exclusão/edição de notas fiscais pendentes
* Configurar reprocessamento de NF com erro
* Implementar atualização automática de token do Mercado Livre
* Validação tributária antes da publicação (baseada na UF do comprador)
* Histórico de publicações por item
* Integração com sistemas contábeis (Ex: NFE.io, Bling, Tiny ERP)

---

## Observações Finais

O sistema é modular, escalável e permite que o usuário escolha entre publicar manualmente, agendar, controlar estoque e emitir nota conforme necessidade. Toda lógica segue as diretrizes da documentação oficial do Mercado Livre.
# Documentação do Sistema - Integração Mercado Livre com Nota Fiscal e Estoque

## Visão Geral

O sistema implementado integra um backend em Java Spring Boot com a API do Mercado Livre, permitindo gestão de:

* Clonagem e edição de itens
* Publicação de rascunhos
* Agendamento de publicações
* Controle de estoque
* Emissão e gestão de notas fiscais eletrônicas (NFe)

---

## Funcionalidades Implementadas

### 1. Clonagem de Anúncio

* **Endpoint**: `POST /items/clone/{itemId}`
* Clona um produto do Mercado Livre e o salva como rascunho (DraftItem)
* Salva imagens, descrição e outros atributos

### 2. Edição de Rascunhos

* **Endpoint**: `PUT /items/drafts/{id}`
* Permite editar dados como título, preço, descrição, estoque e imagens

### 3. Listagem de Rascunhos por Usuário

* **Endpoint**: `GET /items/drafts/user/{userId}`
* Lista todos os rascunhos criados pelo usuário

### 4. Publicação de Rascunhos

* **Endpoint**: `POST /items/publish/{draftId}`
* Publica um rascunho no Mercado Livre

---

### 5. Agendamento de Publicações

* **Agendamento**: via endpoint ou rotina automatizada
* **Endpoint**: `POST /schedule`
* Salva a publicação de um rascunho para um horário futuro
* **Executação automática**: `@Scheduled(fixedRate = 60000)` (a cada 1 minuto)

### 6. Listagem de Agendamentos

* **Endpoint**: `GET /schedule/{userId}`
* Lista publicações agendadas para o usuário

---

### 7. Controle de Estoque

* **Endpoint para atualizar**: `PUT /stock/update`
* Atualiza estoque de um rascunho e registra em StockEntry
* **Endpoint para listar estoque**: `GET /stock/{userId}`
* Lista todos os registros de estoque do usuário

---

### 8. Nota Fiscal Eletrônica (NFe)

#### a. Upload de Nota Fiscal

* **Endpoint**: `POST /nfe/upload`
* Envia dados de nota fiscal (JSON) para o Mercado Livre
* Salva em banco local via entidade `FiscalInvoice`

#### b. Consulta de Nota Fiscal

* **Endpoint**: `GET /nfe/user/{userId}`
* Lista todas as notas fiscais do usuário

#### c. Download da Nota Fiscal

* **Endpoint**: `GET /nfe/download/{userId}/{invoiceId}`
* Retorna os dados resumidos da nota fiscal, incluindo:

  * URL para download do PDF (`pdf_url`)
  * Status da nota (`status`)
  * Data de emissão (`issued_at`)

#### d. Recebimento de Notificações

* Webhook pode ser configurado para escutar eventos de notas
* A ser implementado: `POST /webhook/fiscal`

#### e. Regras Tributárias e Configurações Fiscais

* Referências implementadas para endpoints:

  * Composição de base ICMS/IPI
  * Composição PIS/COFINS
  * DIFAL
  * Mensagens fiscais (ex: operação de devolução, intermediador)
  * Envio de inscrições estaduais e municipais

---

## Entidades Principais

### DraftItem

* Representa um item rascunho clonado ou editado antes da publicação

### ScheduledPublication

* Agendamento de publicação futura de um DraftItem

### StockEntry

* Registro de controle de estoque para cada item e usuário

### FiscalInvoice

* Representa uma nota fiscal emitida, com dados como:

  * invoiceId
  * userId
  * status
  * downloadUrl
  * data de emissão

---


---

## Próximos Passos 

* Implementar tela de dashboard para agendamentos e estoque
* Permitir exclusão/edição de notas fiscais pendentes
* Configurar reprocessamento de NF com erro
* Implementar atualização automática de token do Mercado Livre
* Validação tributária antes da publicação (baseada na UF do comprador)
* Histórico de publicações por item
* Integração com sistemas contábeis (Ex: NFE.io, Bling, Tiny ERP)

---

## Observações Finais

O sistema é modular, escalável e permite que o usuário escolha entre publicar manualmente, agendar, controlar estoque e emitir nota conforme necessidade. Toda lógica segue as diretrizes da documentação oficial do Mercado Livre.


