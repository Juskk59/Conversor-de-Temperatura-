package dev.murilormoraes.mavenproject.temperature;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Param;

public interface TemperatureRepository extends CrudRepository<Temperature, Long> {

    // Buscar por tipo de entrada
    @Query("select t from Temperature t where t.inputType = :inputType")
    List<Temperature> findByInputType(@Param("inputType") String inputType);

    // Buscar por tipo de saída
    @Query("select t from Temperature t where t.outputType = :outputType")
    List<Temperature> findByOutputType(@Param("outputType") String outputType);

    // Buscar por valor de entrada
    @Query("select t from Temperature t where t.inputValue = :inputValue")
    List<Temperature> findByInputValue(@Param("inputValue") BigDecimal value);

    // Buscar por valor de saída
    @Query("select t from Temperature t where t.outputValue = :outputValue")
    List<Temperature> findByOutputValue(@Param("outputValue") BigDecimal value);

    // Buscar por data/hora exata
    @Query("select t from Temperature t where t.dateTime = :dateTime")
    Optional<Temperature> findByDateTime(@Param("dateTime") LocalDateTime dateTime);

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
}