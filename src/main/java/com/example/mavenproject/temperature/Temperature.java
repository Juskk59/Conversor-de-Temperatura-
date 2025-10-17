package com.example.mavenproject.temperature;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa um registro de temperatura, incluindo valores de entrada e saída,
 * tipos de unidade (e.g., Celsius, Fahrenheit), data/hora e um identificador único.
 * Esta classe atua como o modelo de dados para as operações de temperatura.
 */
public class Temperature {
    private String uuid;
    private LocalDateTime dateTime;
    private BigDecimal inputValue;
    private String inputType;
    private String outputType;
    private BigDecimal outputValue;
    private long timestamp;

    /**
     * Construtor padrão da classe Temperature.
     * Necessário para frameworks de persistência e serialização.
     */
    public Temperature() {}

    /**
     * Construtor completo para inicializar um objeto Temperature com todos os seus atributos.
     * @param dateTime A data e hora do registro da temperatura.
     * @param inputValue O valor da temperatura na unidade de entrada.
     * @param inputType O tipo da unidade de entrada (e.g., "CELSIUS", "FAHRENHEIT").
     * @param outputType O tipo da unidade de saída desejada.
     * @param outputValue O valor da temperatura convertido para a unidade de saída.
     * @param uuid Um identificador único para este registro de temperatura.
     * @param timestamp O timestamp em milissegundos do registro.
     */
    public Temperature(LocalDateTime dateTime, BigDecimal inputValue, String inputType, String outputType, BigDecimal outputValue, String uuid, long timestamp) {
        this.dateTime = dateTime;
        this.inputValue = inputValue;
        this.inputType = inputType;
        this.outputType = outputType;
        this.outputValue = outputValue;
        this.uuid = uuid;
        this.timestamp = timestamp;
    }

    // Métodos Getters para acessar os atributos da temperatura.
    /**
     * Retorna o identificador único (UUID) do registro de temperatura.
     * @return O UUID da temperatura.
     */
    public String getUuid() { return uuid; }
    /**
     * Retorna a data e hora do registro da temperatura.
     * @return O {@link LocalDateTime} do registro.
     */
    public LocalDateTime getDateTime() { return dateTime; }
    /**
     * Retorna o valor da temperatura na unidade de entrada.
     * @return O {@link BigDecimal} do valor de entrada.
     */
    public BigDecimal getInputValue() { return inputValue; }
    /**
     * Retorna o tipo da unidade de entrada da temperatura.
     * @return A {@link String} representando o tipo de entrada.
     */
    public String getInputType() { return inputType; }
    /**
     * Retorna o tipo da unidade de saída da temperatura.
     * @return A {@link String} representando o tipo de saída.
     */
    public String getOutputType() { return outputType; }
    /**
     * Retorna o valor da temperatura convertido para a unidade de saída.
     * @return O {@link BigDecimal} do valor de saída.
     */
    public BigDecimal getOutputValue() { return outputValue; }
    /**
     * Retorna o timestamp em milissegundos do registro da temperatura.
     * @return O valor {@code long} do timestamp.
     */
    public long getTimestamp() { return timestamp; }

    // Métodos Setters para definir os atributos da temperatura.
    /**
     * Define o identificador único (UUID) do registro de temperatura.
     * @param uuid O novo UUID.
     */
    public void setUuid(String uuid) { this.uuid = uuid; }
    /**
     * Define a data e hora do registro da temperatura.
     * @param dateTime O novo {@link LocalDateTime}.
     */
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    /**
     * Define o valor da temperatura na unidade de entrada.
     * @param inputValue O novo {@link BigDecimal} do valor de entrada.
     */
    public void setInputValue(BigDecimal inputValue) { this.inputValue = inputValue; }
    /**
     * Define o tipo da unidade de entrada da temperatura.
     * @param inputType A nova {@link String} representando o tipo de entrada.
     */
    public void setInputType(String inputType) { this.inputType = inputType; }
    /**
     * Define o tipo da unidade de saída da temperatura.
     * @param outputType A nova {@link String} representando o tipo de saída.
     */
    public void setOutputType(String outputType) { this.outputType = outputType; }
    /**
     * Define o valor da temperatura convertido para a unidade de saída.
     * @param outputValue O novo {@link BigDecimal} do valor de saída.
     */
    public void setOutputValue(BigDecimal outputValue) { this.outputValue = outputValue; }
    /**
     * Define o timestamp em milissegundos do registro da temperatura.
     * @param timestamp O novo valor {@code long} do timestamp.
     */
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}