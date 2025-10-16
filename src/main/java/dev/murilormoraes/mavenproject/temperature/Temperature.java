package dev.murilormoraes.mavenproject.temperature;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class Temperature {

    @Id
    private long timestamp; // milissegundos da data/hora
    @Column
    private LocalDateTime dateTime;
    @Column
    private BigDecimal inputValue;
    @Column
    private String inputType;   // e.g. "C", "F", "K"
    @Column
    private String outputType;  // e.g. "C", "F", "K"
    @Column
    private BigDecimal outputValue;

    public Temperature() {}

    public Temperature(LocalDateTime dateTime, BigDecimal inputValue, String inputType, String outputType) {
        this.dateTime = dateTime;
        this.timestamp = dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        this.inputValue = inputValue;
        this.inputType = inputType;
        this.outputType = outputType;
        this.outputValue = TemperatureConverter.convert(inputValue, inputType, outputType);
    }

    public long getTimestamp() { return timestamp; }
    public LocalDateTime getDateTime() { return dateTime; }
    public BigDecimal getInputValue() { return inputValue; }
    public String getInputType() { return inputType; }
    public String getOutputType() { return outputType; }
    public BigDecimal getOutputValue() { return outputValue; }

    public void update(BigDecimal newInputValue, String newInputType, String newOutputType) {
        if (newInputValue != null) this.inputValue = newInputValue;
        if (newInputType != null) this.inputType = newInputType;
        if (newOutputType != null) this.outputType = newOutputType;
        this.outputValue = TemperatureConverter.convert(this.inputValue, this.inputType, this.outputType);
    }
}