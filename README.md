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

### 🧪 Endpoints Importantes

🔑 Autenticação


- POST	/api/auth/register	Registro de novo usuário
- POST	/api/auth/login	Login com JWT
- GET	/auth/login	Inicia OAuth com Mercado Livre
- POST	/auth/token	Recebe code e gera access_token

🛒 Anúncios

- POST	/items/clone/{itemId}?userId={userId}	Clona um anúncio e salva como draft
- PUT	/items/drafts/{id}	Edita um draft
- POST	/items/publish/{draftId}	Publica um draft
- GET	/items/drafts/user/{userId}	Lista drafts do usuário

