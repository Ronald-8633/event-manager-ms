# Event Manager MS

Sistema de Gestão de Eventos

## 🚀 Sobre o Projeto

O Event Manager MS é uma aplicação Spring Boot que oferece uma solução completa para gestão de eventos, incluindo:

- **Gestão de Eventos**: CRUD completo de eventos com categorias e localizações
- **Sistema de Usuários**: Diferentes perfis (usuário, organizador, admin)
- **Arquitetura Limpa**: Seguindo princípios de Clean Architecture
- **API REST**: Documentada com Swagger/OpenAPI
- **Banco NoSQL**: MongoDB para flexibilidade e escalabilidade

## 🏗️ Arquitetura

O projeto segue os princípios de Clean/Hexagonal Architecture com as seguintes camadas:

```
src/main/java/br/com/eventmanager/
├── domain/           # Entidades e regras de negócio
├── application/      # Casos de uso e serviços
├── adapter/          # Adaptadores de entrada e saída
│   ├── inbound/     # Controllers REST
│   └── outbound/    # Repositórios e integrações
└── config/          # Configurações da aplicação
```

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 3.2.12**
- **Spring Data MongoDB**
- **Lombok**
- **MapStruct**
- **Swagger/OpenAPI**
- **MongoDB**

## 📋 Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- MongoDB 5.0+ (local ou remoto)

## 🔧 Configuração

### 1. Configure o MongoDB
Crie um arquivo `.env` na raiz do projeto:
```bash
MONGODB_URI=mongodb://localhost:27017/event-manager-ms
```

Ou configure diretamente no `application.yml`:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/event-manager-ms
```

### 2. Execute a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080/api`

## 📚 API Documentation

### Swagger UI
- **URL**: `http://localhost:8080/api/swagger-ui.html`
- **API Docs**: `http://localhost:8080/api/api-docs`

### Endpoints Principais

#### Eventos
- `POST /api/v1/events` - Criar evento
- `GET /api/v1/events` - Listar todos os eventos com detalhes completos
- `GET /api/v1/events/{id}` - Buscar evento por ID com detalhes completos
- `GET /api/v1/events/category/{category}` - Buscar eventos por categoria
- `GET /api/v1/events/status/{status}` - Buscar eventos por status
- `PUT /api/v1/events/{id}` - Atualizar evento
- `DELETE /api/v1/events/{id}` - Deletar evento
- `POST /api/v1/events/{id}/publish` - Publicar evento
- `POST /api/v1/events/{id}/attendees/{userId}` - Adicionar participante

#### Categorias
- `GET /api/v1/categories` - Listar todas as categorias ativas
- `GET /api/v1/categories/{id}` - Buscar categoria por ID
- `GET /api/v1/categories/name/{name}` - Buscar categoria por nome

#### Localizações
- `GET /api/v1/locations` - Listar todas as localizações ativas
- `GET /api/v1/locations/{id}` - Buscar localização por ID
- `GET /api/v1/locations/city/{city}` - Buscar localizações por cidade
- `GET /api/v1/locations/capacity/{minCapacity}` - Buscar localizações por capacidade mínima

## 🗄️ Estrutura do Banco

### Collections MongoDB

#### Events
- `id`: Identificador único
- `title`: Título do evento
- `description`: Descrição
- `category`: Categoria
- `location`: Localização
- `startDate`: Data de início
- `endDate`: Data de fim
- `maxCapacity`: Capacidade máxima
- `currentCapacity`: Capacidade atual
- `price`: Preço
- `organizerId`: ID do organizador
- `status`: Status (DRAFT, PUBLISHED, CANCELLED, COMPLETED)
- `tags`: Lista de tags
- `attendees`: Set de participantes

#### Users
- `id`: Identificador único
- `name`: Nome do usuário
- `email`: Email (único)
- `password`: Senha
- `role`: Perfil (USER, ORGANIZER, ADMIN)
- `status`: Status (ACTIVE, INACTIVE, SUSPENDED)
- `interests`: Lista de interesses
- `organizedEvents`: Eventos organizados
- `attendedEvents`: Eventos participados

#### Categories
- `id`: Identificador único
- `name`: Nome da categoria
- `description`: Descrição
- `iconUrl`: Ícone
- `color`: Cor
- `isActive`: Status ativo

#### Locations
- `id`: Identificador único
- `name`: Nome do local
- `address`: Endereço completo
- `capacity`: Capacidade
- `coordinates`: Latitude e longitude


- **Desenvolvedor**: Ronald Carvalho
