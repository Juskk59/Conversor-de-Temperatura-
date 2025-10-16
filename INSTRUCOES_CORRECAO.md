# 🔧 Instruções para Aplicar as Correções

## 📋 Resumo da Análise

Seu código **ESTÁ CUMPRINDO** a maioria dos requisitos, mas precisa de algumas correções para estar 100% completo.

### ✅ O que está funcionando:
- POST - Incluir temperatura
- PUT - Alterar com recálculo automático
- DELETE por timestamp
- DELETE todos registros
- GET com filtros básicos
- MongoDB configurado
- Jakarta Data e NoSQL implementados

### ⚠️ O que precisa ser corrigido:
1. DELETE por data/hora (não implementado)
2. GET com múltiplos filtros combinados (limitado)
3. Faltam métodos no Repository
4. Tag inválida no pom.xml

---

## 🚀 Como Aplicar as Correções

### Opção 1: Substituição Completa (RECOMENDADO)

#### Passo 1: Fazer Backup
```bash
cd C:\Users\muril\eclipse-workspace\mavenproject

# Backup dos arquivos
copy src\main\java\dev\murilormoraes\mavenproject\temperature\*.java backup\
copy pom.xml pom.xml.bak
```

#### Passo 2: Substituir os Arquivos

Os arquivos corrigidos foram fornecidos nos artifacts acima. Você precisa substituir:

1. **TemperatureRepository.java** - Adiciona métodos de busca e deleção
2. **TemperatureResource.java** - Adiciona endpoints faltantes
3. **pom.xml** - Corrige tag inválida

Copie o conteúdo dos artifacts e cole nos respectivos arquivos.

#### Passo 3: Compilar
```bash
mvn clean package
```

#### Passo 4: Testar
```bash
# Iniciar MongoDB (se não estiver rodando)
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Deploy no Tomcat e testar
curl http://localhost:8080/mavenproject/api/temperatures
```

---

### Opção 2: Correções Manuais

Se preferir fazer as alterações manualmente:

#### 1. TemperatureRepository.java

Adicione estes métodos após os existentes:

```java
// Buscar por valor de saída
@Query("select t from Temperature t where t.outputValue = :outputValue")
List<Temperature> findByOutputValue(@Param("outputValue") BigDecimal value);

// Buscar por timestamp
@Query("select t from Temperature t where t.timestamp = :timestamp")
Optional<Temperature> findByTimestamp(@Param("timestamp") long timestamp);

// Buscar combinando tipo de entrada e saída
@Query("select t from Temperature t where t.inputType = :inputType and t.outputType = :outputType")
List<Temperature> findByInputTypeAndOutputType(
    @Param("inputType") String inputType, 
    @Param("outputType") String outputType
);

// Buscar por tipo de entrada e valor
@Query("select t from Temperature t where t.inputType = :inputType and t.inputValue = :inputValue")
List<Temperature> findByInputTypeAndInputValue(
    @Param("inputType") String inputType, 
    @Param("inputValue") BigDecimal inputValue
);

// Excluir por data/hora
@Query("delete from Temperature t where t.dateTime = :dateTime")
void deleteByDateTime(@Param("dateTime") LocalDateTime dateTime);

// Excluir por timestamp
@Query("delete from Temperature t where t.timestamp = :timestamp")
void deleteByTimestamp(@Param("timestamp") long timestamp);
```

Não esqueça de adicionar os imports:
```java
import jakarta.data.repository.Param;
```

#### 2. TemperatureResource.java

Adicione este método para DELETE por data/hora:

```java
import java.time.format.DateTimeFormatter;

// DELETE - Excluir por data/hora
@DELETE
@Path("/datetime/{dateTime}")
public Response deleteByDateTime(@PathParam("dateTime") String dateTimeStr) {
    try {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Optional<Temperature> temp = repository.findByDateTime(dateTime);
        
        if (temp.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Temperatura não encontrada\"}")
                .build();
        }
        
        repository.deleteByDateTime(dateTime);
        return Response.noContent().build();
    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("{\"error\": \"Formato de data inválido. Use ISO-8601: yyyy-MM-ddTHH:mm:ss\"}")
            .build();
    }
}
```

Altere o DELETE por timestamp existente para:

```java
// DELETE - Excluir por timestamp
@DELETE
@Path("/timestamp/{timestamp}")
public Response deleteByTimestamp(@PathParam("timestamp") long timestamp) {
    Optional<Temperature> temp = repository.findByTimestamp(timestamp);
    
    if (temp.isEmpty()) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity("{\"error\": \"Temperatura não encontrada\"}")
            .build();
    }
    
    repository.deleteByTimestamp(timestamp);
    return Response.noContent().build();
}
```

Altere o DELETE all para:

```java
// DELETE - Excluir todos
@DELETE
@Path("/all")
public Response deleteAll() {
    List<Temperature> all = (List<Temperature>) repository.findAll();
    repository.deleteAll(all);
    return Response.noContent().build();
}
```

Modifique o método GET para suportar múltiplos filtros:

```java
@GET
public Response find(
        @QueryParam("inputType") String inputType,
        @QueryParam("outputType") String outputType,
        @QueryParam("inputValue") BigDecimal inputValue,
        @QueryParam("outputValue") BigDecimal outputValue,
        @QueryParam("timestamp") Long timestamp,
        @QueryParam("dateTime") String dateTimeStr) {

    try {
        List<Temperature> result = new ArrayList<>();

        // Se timestamp fornecido
        if (timestamp != null) {
            repository.findByTimestamp(timestamp).ifPresent(result::add);
            return Response.ok(result).build();
        }

        // Se data/hora fornecida
        if (dateTimeStr != null) {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            repository.findByDateTime(dateTime).ifPresent(result::add);
            return Response.ok(result).build();
        }

        // Busca combinada
        if (inputType != null && outputType != null) {
            result = repository.findByInputTypeAndOutputType(inputType, outputType);
        }
        else if (inputType != null && inputValue != null) {
            result = repository.findByInputTypeAndInputValue(inputType, inputValue);
        }
        // Buscas individuais
        else if (inputType != null) {
            result = repository.findByInputType(inputType);
        }
        else if (outputType != null) {
            result = repository.findByOutputType(outputType);
        }
        else if (inputValue != null) {
            result = repository.findByInputValue(inputValue);
        }
        else if (outputValue != null) {
            result = repository.findByOutputValue(outputValue);
        }
        else {
            result = (List<Temperature>) repository.findAll();
        }

        return Response.ok(result).build();

    } catch (Exception e) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("{\"error\": \"" + e.getMessage() + "\"}")
            .build();
    }
}
```

Adicione também método para buscar por ID:

```java
// GET - Buscar por ID
@GET
@Path("/{timestamp}")
public Response findById(@PathParam("timestamp") long timestamp) {
    Optional<Temperature> temp = repository.findByTimestamp(timestamp);
    
    if (temp.isEmpty()) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity("{\"error\": \"Temperatura não encontrada\"}")
            .build();
    }
    
    return Response.ok(temp.get()).build();
}
```

Adicione os imports necessários:
```java
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
```

#### 3. pom.xml

Corrija a linha 8:
```xml
<!-- ANTES (ERRADO) -->
<n>${project.artifactId}</n>

<!-- DEPOIS (CORRETO) -->
<name>${project.artifactId}</name>
```

---

## ✅ Verificação Pós-Correção

Após aplicar as correções, execute:

```bash
# 1. Compilar
mvn clean package

# 2. Verificar se gerou o WAR
ls -la target/mavenproject.war

# 3. Deploy no Tomcat

# 4. Testar
curl http://localhost:8080/mavenproject/api/temperatures
```

---

## 📚 Documentação Adicional

Consulte os outros artifacts criados:

1. **Documentação da API** - Exemplos completos de uso
2. **Resumo Executivo** - Lista detalhada de problemas e soluções
3. **Guia de Testes** - Scripts para validar todas as funcionalidades

---

## 📞 Suporte

Se tiver dúvidas:
1. Verifique os logs do Tomcat
2. Teste a conexão com MongoDB
3. Valide se todos os arquivos foram salvos corretamente

**Boa sorte com as correções! 🚀**
