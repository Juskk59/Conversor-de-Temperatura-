package dev.murilormoraes.mavenproject.temperature;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/temperatures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemperatureResource {

    @Inject
    private TemperatureRepository repository;

    // POST - incluir
    @POST
    public Response create(Temperature temperature) {
        if (temperature.getDateTime() == null) {
            temperature = new Temperature(LocalDateTime.now(), temperature.getInputValue(),
                                          temperature.getInputType(), temperature.getOutputType());
        }
        repository.save(temperature);
        return Response.status(Response.Status.CREATED).entity(temperature).build();
    }

    // PUT - alterar
    @PUT
    @Path("/{timestamp}")
    public Response update(@PathParam("timestamp") long timestamp, Temperature update) {
        Optional<Temperature> opt = repository.findById(timestamp);
        if (opt.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();

        Temperature temp = opt.get();
        temp.update(update.getInputValue(), update.getInputType(), update.getOutputType());
        repository.save(temp);
        return Response.ok(temp).build();
    }

    // DELETE - por timestamp
    @DELETE
    @Path("/{timestamp}")
    public Response delete(@PathParam("timestamp") long timestamp) {
        repository.deleteById(timestamp);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/datetime/{dateTime}")
    public Response deleteByDateTime(@PathParam("dateTime") String dateTimeStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
        repository.deleteByDateTime(dateTime);
        return Response.noContent().build();
    }
    
    // DELETE - todos
    @DELETE
    public Response deleteAll() {
        var all = (List<Temperature>) repository.findAll();
        repository.deleteAll(all);
        return Response.noContent().build();
    }

    // GET - filtrar ou listar todos
    @SuppressWarnings("unchecked")
    @GET
    public Response find(
            @QueryParam("inputType") String inputType,
            @QueryParam("outputType") String outputType,
            @QueryParam("inputValue") BigDecimal inputValue,
            @QueryParam("outputValue") BigDecimal outputValue) {
        
        List<Temperature> result;
        
        // Busca combinada
        if (inputType != null && outputType != null) {
            result = repository.findByInputTypeAndOutputType(inputType, outputType);
        }
        // Buscas individuais
        else if (inputType != null) result = repository.findByInputType(inputType);
        else if (outputType != null) result = repository.findByOutputType(outputType);
        else if (inputValue != null) result = repository.findByInputValue(inputValue);
        else if (outputValue != null) result = repository.findByOutputValue(outputValue);
        else result = (List<Temperature>) repository.findAll();
        
        return Response.ok(result).build();
    }
}
