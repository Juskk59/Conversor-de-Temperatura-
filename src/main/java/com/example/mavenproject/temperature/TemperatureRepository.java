package com.example.mavenproject.temperature;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

/**
 * Repositório para gerenciar operações de persistência de objetos {@link Temperature} no MongoDB.
 * Implementa o padrão Singleton utilizando um enum para garantir uma única instância.
 * Responsável por traduzir objetos Temperature para Documentos MongoDB e vice-versa.
 */
public enum TemperatureRepository {
    INSTANCE;

    private final String MONGODB_CONN = "mongodb://localhost:28017";
    private final String TEMP_DB      = "olympus";
    private final String TEMP_COLL    = "temperatures";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> coll;

    /**
     * Construtor privado do repositório. Inicializa a conexão com o MongoDB
     * e obtém a coleção de temperaturas.
     */
    TemperatureRepository() {
        mongoClient = MongoClients.create(MONGODB_CONN);
        database    = mongoClient.getDatabase(TEMP_DB);
        coll        = database.getCollection(TEMP_COLL);
    }

    /**
     * Insere um novo registro de temperatura no banco de dados.
     * @param temperature O objeto {@link Temperature} a ser inserido.
     * @return {@code true} se a inserção foi bem-sucedida, {@code false} caso contrário.
     */
    public boolean insert(Temperature temperature) {
        Document temperatureDocument = new Document("uuid", temperature.getUuid())
            .append("timestamp", temperature.getTimestamp())
            .append("dateTime", temperature.getDateTime() != null ? temperature.getDateTime().toString() : null)
            .append("inputValue", temperature.getInputValue())
            .append("inputType", temperature.getInputType())
            .append("outputType", temperature.getOutputType())
            .append("outputValue", temperature.getOutputValue());
        return coll.insertOne(temperatureDocument).getInsertedId() != null;
    }

    /**
     * Atualiza um registro de temperatura existente no banco de dados.
     * O registro é identificado pelo seu UUID.
     * @param temperature O objeto {@link Temperature} com os dados atualizados.
     * @return {@code true} se a atualização foi bem-sucedida, {@code false} caso contrário.
     */
    public boolean update(Temperature temperature) {
        Bson queryFilter = Filters.eq("uuid", temperature.getUuid());
        Bson updateOperations = Updates.combine(
            Updates.set("timestamp", temperature.getTimestamp()),
            Updates.set("dateTime", temperature.getDateTime() != null ? temperature.getDateTime().toString() : null),
            Updates.set("inputValue", temperature.getInputValue()),
            Updates.set("inputType", temperature.getInputType()),
            Updates.set("outputType", temperature.getOutputType()),
            Updates.set("outputValue", temperature.getOutputValue())
        );
        return coll.updateOne(queryFilter, updateOperations).getModifiedCount() > 0L;
    }

    /**
     * Exclui um registro de temperatura do banco de dados.
     * @param temperature O objeto {@link Temperature} a ser excluído.
     * @return {@code true} se a exclusão foi bem-sucedida, {@code false} caso contrário.
     */
    public boolean delete(Temperature temperature) {
        return delete(temperature.getUuid());
    }

    /**
     * Exclui um registro de temperatura do banco de dados pelo seu UUID.
     * @param uuid O UUID do registro a ser excluído.
     * @return {@code true} se a exclusão foi bem-sucedida, {@code false} caso contrário.
     */
    public boolean delete(String uuid) {
        Bson filter = Filters.eq("uuid", uuid);
        return coll.deleteOne(filter).getDeletedCount() > 0L;
    }

    /**
     * Exclui registros de temperatura com base em um critério de data e hora ou timestamp.
     * Se ambos os parâmetros forem fornecidos, a busca será feita prioritariamente por {@code dateTime}.
     * @param dateTime O {@link LocalDateTime} para buscar (será convertido para String para comparação).
     * @param timestamp O timestamp em milissegundos para buscar.
     * @return O número de documentos excluídos que correspondem ao critério.
     */
    public long deleteByDateTimeOrTimestamp(LocalDateTime dateTime, Long timestamp) {
        Bson deletionFilter = null;
        
        if (dateTime != null) {
            // Cria filtro para igualdade do campo 'dateTime' (armazenado como String no DB)
            deletionFilter = Filters.eq("dateTime", dateTime.toString());
        } else if (timestamp != null) {
            // Cria filtro para igualdade do campo 'timestamp' (armazenado como Long no DB)
            deletionFilter = Filters.eq("timestamp", timestamp);
        }
        
        if (deletionFilter != null) {
            // Usamos deleteMany pois pode haver múltiplos documentos que atendam ao critério.
            return coll.deleteMany(deletionFilter).getDeletedCount();
        }
        return 0L;
    }

    /**
     * Exclui todos os registros de temperatura da coleção.
     * @return O número total de documentos que foram excluídos.
     */
    public int delete() {
        long totalDocuments = coll.countDocuments();
        if (totalDocuments > 0L) {
            coll.drop(); // Remove a coleção inteira, efetivamente excluindo todos os documentos.
        }
        return (int) totalDocuments;
    }

    /**
     * Recupera todos os registros de temperatura do banco de dados.
     * @return Uma {@link List} de todos os objetos {@link Temperature} encontrados.
     */
    public List<Temperature> findAll() {
        List<Temperature> temperatures = new ArrayList<>();
        FindIterable<Document> documents = coll.find();
        for (Document doc : documents) {
            temperatures.add(docToTemperature(doc));
        }
        return temperatures;
    }

    /**
     * Busca um registro de temperatura específico pelo seu UUID.
     * @param uuid O UUID do registro a ser encontrado.
     * @return Um {@link Optional} contendo o {@link Temperature} se encontrado, ou um {@link Optional#empty()} caso contrário.
     */
    public Optional<Temperature> findByUuid(String uuid) {
        Bson queryFilter = Filters.eq("uuid", uuid);
        Document document = coll.find(queryFilter).first();
        if (document != null) {
            return Optional.of(docToTemperature(document));
        }
        return Optional.empty();
    }

    /**
     * Busca um registro de temperatura pela data e hora exatas.
     * @param dateTime O {@link LocalDateTime} para buscar.
     * @return Um {@link Optional} contendo o {@link Temperature} se encontrado, ou um {@link Optional#empty()} caso contrário.
     */
    public Optional<Temperature> findByDateTime(LocalDateTime dateTime) {
        Bson queryFilter = Filters.eq("dateTime", dateTime.toString());
        Document document = coll.find(queryFilter).first();
        if (document != null) {
            return Optional.of(docToTemperature(document));
        }
        return Optional.empty();
    }

    /**
     * Busca registros de temperatura pelo tipo de entrada.
     * @param inputType O tipo de entrada (ex: "CELSIUS").
     * @return Uma {@link List} de objetos {@link Temperature} que correspondem ao tipo de entrada.
     */
    public List<Temperature> findByInputType(String inputType) {
        Bson queryFilter = Filters.eq("inputType", inputType);
        List<Temperature> temperatures = new ArrayList<>();
        for (Document document : coll.find(queryFilter)) {
            temperatures.add(docToTemperature(document));
        }
        return temperatures;
    }

    /**
     * Busca registros de temperatura pelo tipo de saída.
     * @param outputType O tipo de saída (ex: "FAHRENHEIT").
     * @return Uma {@link List} de objetos {@link Temperature} que correspondem ao tipo de saída.
     */
    public List<Temperature> findByOutputType(String outputType) {
        Bson queryFilter = Filters.eq("outputType", outputType);
        List<Temperature> temperatures = new ArrayList<>();
        for (Document document : coll.find(queryFilter)) {
            temperatures.add(docToTemperature(document));
        }
        return temperatures;
    }

    /**
     * Busca registros de temperatura pelo valor de entrada.
     * @param value O valor de entrada da temperatura.
     * @return Uma {@link List} de objetos {@link Temperature} que correspondem ao valor de entrada.
     */
    public List<Temperature> findByInputValue(BigDecimal value) {
        Bson queryFilter = Filters.eq("inputValue", value);
        List<Temperature> temperatures = new ArrayList<>();
        for (Document document : coll.find(queryFilter)) {
            temperatures.add(docToTemperature(document));
        }
        return temperatures;
    }

    /**
     * Busca registros de temperatura pelo valor de saída.
     * @param value O valor de saída da temperatura.
     * @return Uma {@link List} de objetos {@link Temperature} que correspondem ao valor de saída.
     */
    public List<Temperature> findByOutputValue(BigDecimal value) {
        Bson queryFilter = Filters.eq("outputValue", value);
        List<Temperature> temperatures = new ArrayList<>();
        for (Document document : coll.find(queryFilter)) {
            temperatures.add(docToTemperature(document));
        }
        return temperatures;
    }

    /**
     * Busca registros de temperatura por uma combinação de tipo de entrada e tipo de saída.
     * @param inputType O tipo de entrada da temperatura.
     * @param outputType O tipo de saída da temperatura.
     * @return Uma {@link List} de objetos {@link Temperature} que correspondem aos critérios.
     */
    public List<Temperature> findByInputTypeAndOutputType(String inputType, String outputType) {
        Bson queryFilter = Filters.and(
            Filters.eq("inputType", inputType),
            Filters.eq("outputType", outputType)
        );
        List<Temperature> temperatures = new ArrayList<>();
        for (Document document : coll.find(queryFilter)) {
            temperatures.add(docToTemperature(document));
        }
        return temperatures;
    }

    /**
     * Busca registros de temperatura por uma combinação de tipo de entrada e valor de entrada.
     * @param inputType O tipo de entrada da temperatura.
     * @param inputValue O valor de entrada da temperatura.
     * @return Uma {@link List} de objetos {@link Temperature} que correspondem aos critérios.
     */
    public List<Temperature> findByInputTypeAndInputValue(String inputType, BigDecimal inputValue) {
        Bson queryFilter = Filters.and(
            Filters.eq("inputType", inputType),
            Filters.eq("inputValue", inputValue)
        );
        List<Temperature> temperatures = new ArrayList<>();
        for (Document document : coll.find(queryFilter)) {
            temperatures.add(docToTemperature(document));
        }
        return temperatures;
    }

    /**
     * Converte um {@link Document} do MongoDB para um objeto {@link Temperature}.
     * Lida com a conversão de tipos e tratamento de valores nulos.
     * @param document O Documento MongoDB a ser convertido.
     * @return Um objeto {@link Temperature} preenchido com os dados do documento.
     */
    private Temperature docToTemperature(Document document) {
        String uuid = document.getString("uuid");
        long timestamp = document.get("timestamp", Long.class) != null ? document.getLong("timestamp") : 0L;
        LocalDateTime dateTime = null;

        if (document.getString("dateTime") != null) {
            try {
                dateTime = LocalDateTime.parse(document.getString("dateTime"));
            } catch (Exception e) {
                // Loga o erro ou ignora, dependendo da política de tratamento de erros.
                // Para este projeto, vamos apenas ignorar formatos inválidos para não quebrar a aplicação.
            }
        }

        BigDecimal inputValue = null;
        Object inputObj = document.get("inputValue");
        if (inputObj instanceof org.bson.types.Decimal128) {
            inputValue = ((org.bson.types.Decimal128) inputObj).bigDecimalValue();
        } else if (inputObj instanceof Number) {
            inputValue = BigDecimal.valueOf(((Number) inputObj).doubleValue());
        }

        BigDecimal outputValue = null;
        Object outputObj = document.get("outputValue");
        if (outputObj instanceof org.bson.types.Decimal128) {
            outputValue = ((org.bson.types.Decimal128) outputObj).bigDecimalValue();
        } else if (outputObj instanceof Number) {
            outputValue = BigDecimal.valueOf(((Number) outputObj).doubleValue());
        }

        String inputType = document.getString("inputType");
        String outputType = document.getString("outputType");

        return new Temperature(dateTime, inputValue, inputType, outputType, outputValue, uuid, timestamp);
    }
}