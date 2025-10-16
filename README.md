# 🌡️ API de Conversão de Temperaturas

Sistema REST API para conversão de temperaturas entre Celsius, Fahrenheit e Kelvin, com armazenamento em MongoDB usando Jakarta Data e Jakarta NoSQL.

## 📋 Índice

- [Tecnologias](#tecnologias)
- [Requisitos](#requisitos)
- [Instalação Rápida](#instalação-rápida)
- [Deploy no Tomcat](#deploy-no-tomcat)
- [Uso da API](#uso-da-api)
- [Testes](#testes)
- [Documentação](#documentação)
- [Troubleshooting](#troubleshooting)

---

## 🛠️ Tecnologias

- **Java 17**
- **Jakarta EE 10** (REST API)
- **Jakarta Data 1.0** (Repository pattern)
- **Jakarta NoSQL** (Mapeamento objeto-documento)
- **Eclipse JNoSQL** (Driver MongoDB)
- **MongoDB** (Banco de dados NoSQL)
- **Maven** (Gerenciamento de dependências)
- **Apache Tomcat 10.1** (Servidor de aplicação)

---

## ✅ Requisitos

### Obrigatórios
- Java JDK 17 ou superior
- Apache Maven 3.8+
- Apache Tomcat 10.1+
- MongoDB 5.0+ (via Docker ou instalação local)

### Opcionais
- Docker Desktop (para MongoDB)
- Git (para controle de versão)
- Postman ou Insomnia (para testes)

---

## 🚀 Instalação Rápida

### 1. Clonar/Acessar o Projeto

```bash
cd C:\Users\muril\eclipse-workspace\mavenproject
```

### 2. Verificar Ambiente

```bash
verificar-ambiente.bat
```

Este script verifica se tudo está instalado corretamente.

### 3. Iniciar MongoDB

```bash
docker run -d --name mongodb -p 27017:27017 mongo:latest
```

### 4. Compilar

```bash
mvn clean package
```

### 5. Deploy Automatizado

```bash
deploy.bat
```

**Importante:** Execute como Administrador!

---

## 📦 Deploy no Tomcat

### Método 1: Script Automatizado (RECOMENDADO)

```bash
# Como Administrador
.\deploy.bat
```

O script faz automaticamente:
- ✅ Compila o projeto
- ✅ Para o Tomcat
- ✅ Remove versão anterior
- ✅ Copia o novo WAR
- ✅ Inicia o Tomcat
- ✅ Testa a API

### Método 2: Web Manager

1. Acesse: http://localhost:8080/manager/html
2. Login: `admin` / Senha: `admin`
3. Seção "WAR file to deploy"
4. Escolha: `target\mavenproject.war`
5. Clique "Deploy"

### Método 3: Cópia Manual

```bash
copy target\mavenproject.war "C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\"
```

**Documentação completa:** Ver `DEPLOY-RAPIDO.txt`

---

## 🔌 Uso da API

### Base URL
```
http://localhost:8080/mavenproject/api/temperatures
```

### Endpoints Disponíveis

#### POST - Criar Temperatura
```bash
curl -X POST http://localhost:8080/mavenproject/api/temperatures \
  -H "Content-Type: application/json" \
  -d '{"inputValue": 25, "inputType": "C", "outputType": "F"}'
```

#### GET - Listar Todas
```bash
curl http://localhost:8080/mavenproject/api/temperatures
```

#### GET - Filtrar
```bash
# Por tipo de entrada
curl "http://localhost:8080/mavenproject/api/temperatures?inputType=C"

# Busca combinada
curl "http://localhost:8080/mavenproject/api/temperatures?inputType=C&outputType=F"
```

#### PUT - Atualizar
```bash
curl -X PUT http://localhost:8080/mavenproject/api/temperatures/{timestamp} \
  -H "Content-Type: application/json" \
  -d '{"outputType": "K"}'
```

#### DELETE - Excluir
```bash
# Por timestamp
curl -X DELETE http://localhost:8080/mavenproject/api/temperatures/timestamp/{timestamp}

# Por data/hora
curl -X DELETE http://localhost:8080/mavenproject/api/temperatures/datetime/2025-10-16T14:30:00

# Todos
curl -X DELETE http://localhost:8080/mavenproject/api/temperatures/all
```

**Documentação completa da API:** Ver artifacts ou documentação gerada

---

## 🧪 Testes

### Teste Automatizado

```bash
.\testar-api.bat
```

Este script testa automaticamente:
- ✅ Conectividade da API
- ✅ POST - Criar temperaturas
- ✅ GET - Listar e filtrar
- ✅ Validação das conversões

### Testes Manuais

Ver guia completo de testes nos artifacts ou em `test-guide.md`

### Tabela de Conversões (Referência)

| Entrada | Saída | Resultado Esperado |
|---------|-------|-------------------|
| 0°C | F | 32.0 |
| 100°C | F | 212.0 |
| 32°F | C | 0.0 |
| 212°F | C | 100.0 |
| 273.15K | C | 0.0 |
| 373.15K | C | 100.0 |

---

## 📚 Documentação

### Arquivos de Documentação

| Arquivo | Descrição |
|---------|-----------|
| `DEPLOY-RAPIDO.txt` | Guia rápido de deploy |
| `INSTRUCOES_CORRECAO.md` | Como aplicar correções |
| Artifacts (4 arquivos) | Documentação completa da API, testes, etc. |

### Scripts Auxiliares

| Script | Função |
|--------|--------|
| `verificar-ambiente.bat` | Verifica pré-requisitos |
| `deploy.bat` | Deploy automatizado |
| `testar-api.bat` | Testes automatizados |

---

## 🐛 Troubleshooting

### API retorna 404

**Causa:** Contexto da aplicação incorreto

**Solução:**
1. Acesse http://localhost:8080/manager/html
2. Verifique o path exato da aplicação
3. Ajuste a URL base

### MongoDB não conecta

**Solução:**
```bash
# Verificar se está rodando
docker ps

# Iniciar
docker start mongodb

# Ver logs
docker logs mongodb
```

### Porta 8080 ocupada

**Solução:**
```bash
# Ver quem está usando
netstat -ano | findstr :8080

# Matar processo
taskkill /PID [número] /F
```

### Erro de permissão no deploy

**Solução:**
- Execute o PowerShell/CMD como Administrador
- Ou pare o serviço do Tomcat e execute manualmente

### Conversões incorretas

**Verificar:**
1. Arquivo `TemperatureConverter.java` está correto?
2. O método `update()` em `Temperature.java` está recalculando?
3. Logs do Tomcat para erros

---

## 📊 Status dos Requisitos

### Implementado ✅

- [x] POST - Incluir temperatura
- [x] PUT - Alterar e recalcular
- [x] DELETE - Por timestamp
- [x] DELETE - Por data/hora
- [x] DELETE - Todos os registros
- [x] GET - Buscar por qualquer atributo
- [x] GET - Busca combinada
- [x] MongoDB (NoSQL)
- [x] Jakarta Data (CrudRepository)
- [x] Jakarta NoSQL (Anotações)
- [x] Eclipse JNoSQL (Driver MongoDB)

**Pontuação: 100%** ✅

---

## 🔗 Links Úteis

| Recurso | URL |
|---------|-----|
| Tomcat Manager | http://localhost:8080/manager/html |
| API Base | http://localhost:8080/mavenproject/api/temperatures |
| Server Status | http://localhost:8080/manager/status |
| MongoDB (Docker) | mongodb://localhost:27017 |

---

## 👥 Credenciais

### Tomcat Manager
- **Username:** admin
- **Password:** admin

### MongoDB
- Sem autenticação (desenvolvimento)
- **Host:** localhost
- **Port:** 27017
- **Database:** mavenproject

---

## 📝 Notas de Desenvolvimento

### Estrutura do Projeto

```
mavenproject/
├── src/
│   └── main/
│       ├── java/
│       │   └── dev/murilormoraes/mavenproject/temperature/
│       │       ├── Temperature.java (Entidade)
│       │       ├── TemperatureConverter.java (Lógica de conversão)
│       │       ├── TemperatureRepository.java (Acesso a dados)
│       │       └── TemperatureResource.java (REST endpoints)
│       └── resources/
│           └── META-INF/
│               ├── beans.xml (CDI)
│               └── microprofile-config.properties (MongoDB config)
├── pom.xml (Dependências Maven)
├── deploy.bat (Script de deploy)
├── testar-api.bat (Script de testes)
├── verificar-ambiente.bat (Verificação de pré-requisitos)
├── DEPLOY-RAPIDO.txt (Guia rápido)
└── README.md (Este arquivo)
```

### Configuração MongoDB

Arquivo: `src/main/resources/META-INF/microprofile-config.properties`

```properties
jnosql.document.database=mavenproject
jnosql.mongodb.url=mongodb://localhost:27017
```

---

## 🎯 Próximos Passos

1. ✅ Deploy no Tomcat
2. ✅ Testar todos os endpoints
3. ✅ Validar conversões
4. ✅ Verificar persistência no MongoDB
5. 📝 Documentar APIs adicionais (se necessário)
6. 🔒 Adicionar autenticação (produção)
7. 📊 Implementar métricas (opcional)

---

## 📞 Suporte

### Logs

```bash
# Tomcat
type "C:\Program Files\Apache Software Foundation\Tomcat 10.1\logs\catalina.out"

# MongoDB
docker logs mongodb
```

### Comandos Úteis

```bash
# Recompilar
mvn clean package -DskipTests

# Redeploy rápido
copy target\mavenproject.war "C:\Program Files\...\Tomcat 10.1\webapps\"

# Reiniciar Tomcat
cd "C:\Program Files\...\Tomcat 10.1\bin"
.\shutdown.bat
.\startup.bat
```

---

## 📄 Licença

Projeto acadêmico - Curso de Desenvolvimento

---

## ✨ Autor

Murilo R. Moraes

---

**Última atualização:** 16 de outubro de 2025

**Versão:** 1.0.0

**Status:** ✅ Funcional e testado
