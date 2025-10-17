package com.example.mavenproject.temperature;

/**
 * Recurso RESTful para gerenciamento de temperaturas.
 * Fornece endpoints para operações CRUD (Criar, Ler, Atualizar, Excluir) de registros de temperatura.
 * Utiliza Jakarta EE (JAX-RS) para a exposição dos serviços web.
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/temperatures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemperatureResource {

    @POST
    /**
     * Cria um novo registro de temperatura no sistema.
     * Atribui automaticamente UUID, data/hora e timestamp se não forem fornecidos.
     * Recalcula o valor de saída da temperatura com base nos tipos de entrada e saída.
     * @param temperature O objeto Temperature a ser persistido.
     * @return Response com o status da operação e o objeto Temperature criado.
     */
    public Response create(Temperature temperature) {
        if (temperature.getDateTime() == null) {
            temperature.setDateTime(LocalDateTime.now());
        }
        if (temperature.getUuid() == null || temperature.getUuid().isEmpty()) {
            temperature.setUuid(java.util.UUID.randomUUID().toString());
        }
        if (temperature.getTimestamp() == 0L) {
            temperature.setTimestamp(System.currentTimeMillis());
        }
        
        // Garante que o valor de saída seja calculado corretamente antes da persistência.
        // Isso evita inconsistências caso o cliente não envie o valor calculado.
        try {
            BigDecimal convertedValue = TemperatureConverter.convert(
                temperature.getInputValue(),
                temperature.getInputType(),
                temperature.getOutputType()
            );
            temperature.setOutputValue(convertedValue);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        
        // Tenta persistir o novo registro de temperatura.
        boolean isPersisted = TemperatureRepository.INSTANCE.insert(temperature);
        if (isPersisted) {
            return Response.status(Response.Status.CREATED).entity(temperature).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Falha ao criar o registro de temperatura.").build();
        }
    }

    @PUT
    @Path("/{uuid}")
    /**
     * Atualiza um registro de temperatura existente com base no seu UUID.
     * Permite modificar o tipo de entrada/saída e o valor de entrada.
     * O valor de saída é recalculado automaticamente após as alterações.
     * @param uuid O identificador único do registro de temperatura a ser atualizado.
     * @param update O objeto Temperature contendo os dados atualizados.
     * @return Response com o status da operação e o objeto Temperature atualizado.
     */
    public Response update(@PathParam("uuid") String uuid, Temperature update) {
        Optional<Temperature> foundTemperature = TemperatureRepository.INSTANCE.findByUuid(uuid);
        if (foundTemperature.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();

        Temperature temp = foundTemperature.get();
        
        // Atualiza os campos essenciais de entrada e os tipos de conversão.
        temp.setInputValue(update.getInputValue());
        temp.setInputType(update.getInputType());
        temp.setOutputType(update.getOutputType());
        
        // Campos de data/hora e timestamp são opcionais na atualização.
        // Se fornecidos, sobrescrevem os valores existentes.
        if (update.getDateTime() != null) temp.setDateTime(update.getDateTime());
        if (update.getTimestamp() != 0L) temp.setTimestamp(update.getTimestamp());

        // Recalcula o valor de saída para refletir as possíveis mudanças nos tipos ou valor de entrada.
        try {
            BigDecimal convertedValue = TemperatureConverter.convert(
                temp.getInputValue(),
                temp.getInputType(),
                temp.getOutputType()
            );
            temp.setOutputValue(convertedValue);
        } catch (IllegalArgumentException e) {
            // Em caso de tipos de conversão inválidos, retorna um erro.
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        // Tenta persistir as alterações no registro de temperatura.
        boolean isUpdated = TemperatureRepository.INSTANCE.update(temp);
        if (isUpdated) {
            return Response.ok(temp).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Falha ao atualizar o registro de temperatura.").build();
        }
    }

    @DELETE
    @Path("/{uuid}")
    /**
     * Exclui um registro de temperatura específico pelo seu UUID.
     * @param uuid O identificador único do registro de temperatura a ser excluído.
     * @return Response com o status da operação.
     */
    public Response delete(@PathParam("uuid") String uuid) {
        // Tenta excluir o registro de temperatura pelo UUID.
        boolean isDeleted = TemperatureRepository.INSTANCE.delete(uuid);
        if (isDeleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Registro de temperatura não encontrado para exclusão.").build();
        }
    }

    /**
     * Exclui registros de temperatura com base em um critério de data/hora ou timestamp.
     * Pelo menos um dos parâmetros (dateTime ou timestamp) deve ser fornecido.
     * @param dateTimeStr String representando a data e hora no formato ISO 8601 (ex: 2025-10-15T10:30:00).
     * @param timestamp Valor do timestamp em milissegundos.
     * @return Response com o número de registros excluídos ou uma mensagem de erro.
     */
    @DELETE
    @Path("/by-time")
    public Response deleteByTime(
        @QueryParam("dateTime") String dateTimeStr,
        @QueryParam("timestamp") Long timestamp) {
        
        if (dateTimeStr == null && timestamp == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Pelo menos 'dateTime' (yyyy-MM-ddTHH:mm:ss) ou 'timestamp' deve ser fornecido.").build();
        }

        LocalDateTime dateTime = null;
        if (dateTimeStr != null) {
            try {
                dateTime = LocalDateTime.parse(dateTimeStr);
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Formato de 'dateTime' inválido. Use o padrão ISO 8601 (ex: 2025-10-15T10:30:00).").build();
            }
        }
        
        // Delega a lógica de exclusão ao repositório, que trata a busca por data/hora ou timestamp.
        long deletedCount = TemperatureRepository.INSTANCE.deleteByDateTimeOrTimestamp(dateTime, timestamp);

        if (deletedCount > 0)
            return Response.ok(deletedCount + " registro(s) excluído(s).").build();
        
        return Response.status(Response.Status.NOT_FOUND).entity("Nenhum registro encontrado para exclusão.").build();
    }
    
    @DELETE
    /**
     * Exclui todos os registros de temperatura armazenados no sistema.
     * @return Response com o número total de registros excluídos.
     */
    public Response deleteAll() {
        // Realiza a exclusão de todos os registros de temperatura.
        int deletedCount = TemperatureRepository.INSTANCE.delete();
        return Response.ok(deletedCount).entity(deletedCount + " registros de temperatura excluídos com sucesso.").build();
    }

    @GET
    /**
     * Recupera registros de temperatura com base em diversos critérios de busca.
     * Permite filtrar por tipo de entrada, tipo de saída, valor de entrada ou valor de saída.
     * Se nenhum parâmetro for fornecido, todos os registros são retornados.
     * @param inputType O tipo de temperatura de entrada (ex: "CELSIUS").
     * @param outputType O tipo de temperatura de saída (ex: "FAHRENHEIT").
     * @param inputValue O valor da temperatura de entrada.
     * @param outputValue O valor da temperatura de saída.
     * @return Response com uma lista de objetos Temperature que correspondem aos critérios.
     */
    public Response find(
             @QueryParam("inputType") String inputType,
             @QueryParam("outputType") String outputType,
             @QueryParam("inputValue") BigDecimal inputValue,
             @QueryParam("outputValue") BigDecimal outputValue) {

        List<Temperature> result;

        if (inputType != null && outputType != null) {
            result = TemperatureRepository.INSTANCE.findByInputTypeAndOutputType(inputType, outputType);
        } else if (inputType != null && inputValue != null) {
             result = TemperatureRepository.INSTANCE.findByInputTypeAndInputValue(inputType, inputValue);
        } else if (inputType != null) {
            result = TemperatureRepository.INSTANCE.findByInputType(inputType);
        } else if (outputType != null) {
            result = TemperatureRepository.INSTANCE.findByOutputType(outputType);
        } else if (inputValue != null) {
            result = TemperatureRepository.INSTANCE.findByInputValue(inputValue);
        } else if (outputValue != null) {
            result = TemperatureRepository.INSTANCE.findByOutputValue(outputValue);
        } else {
            result = TemperatureRepository.INSTANCE.findAll();
        }

        return Response.ok(result).build();
    }

    @GET
    @Path("/{uuid}")
    /**
     * Recupera um registro de temperatura específico pelo seu UUID.
     * @param uuid O identificador único do registro de temperatura.
     * @return Response com o objeto Temperature encontrado ou status NOT_FOUND.
     */
    public Response findByUuid(@PathParam("uuid") String uuid) {
        Optional<Temperature> foundTemperature = TemperatureRepository.INSTANCE.findByUuid(uuid);
        if (foundTemperature.isPresent()) {
            return Response.ok(foundTemperature.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("Registro de temperatura não encontrado.").build();
        }
    }
}