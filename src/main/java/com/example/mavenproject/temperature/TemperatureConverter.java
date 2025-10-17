package com.example.mavenproject.temperature;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Classe utilitária responsável por realizar conversões entre diferentes escalas de temperatura.
 * Suporta conversões entre Celsius (C), Fahrenheit (F) e Kelvin (K).
 */
public class TemperatureConverter {

    /**
     * Converte um valor de temperatura de uma escala para outra.
     * @param value O valor numérico da temperatura a ser convertida.
     * @param from A escala de temperatura de origem (e.g., "CELSIUS", "FAHRENHEIT", "KELVIN").
     * @param to A escala de temperatura de destino (e.g., "CELSIUS", "FAHRENHEIT", "KELVIN").
     * @return Um {@link BigDecimal} representando o valor da temperatura convertido, com duas casas decimais.
     * @throws IllegalArgumentException Se um tipo de temperatura inválido for fornecido.
     */
    public static BigDecimal convert(BigDecimal value, String from, String to) {
        // Se os tipos de origem e destino forem os mesmos, não há necessidade de conversão.
        if (from.equalsIgnoreCase(to)) {
            return value.setScale(2, RoundingMode.HALF_UP);
        }

        double v = value.doubleValue();
        double result;

        // Normaliza os tipos de temperatura para evitar problemas de caixa.
        String normalizedFrom = from.toUpperCase();
        String normalizedTo = to.toUpperCase();

        switch (normalizedFrom) {
            case "C":
                result = normalizedTo.equals("F") ? (v * 9/5) + 32 :
                         normalizedTo.equals("K") ? v + 273.15 : v;
                break;
            case "F":
                result = normalizedTo.equals("C") ? (v - 32) * 5/9 :
                         normalizedTo.equals("K") ? ((v - 32) * 5/9) + 273.15 : v;
                break;
            case "K":
                result = normalizedTo.equals("C") ? v - 273.15 :
                         normalizedTo.equals("F") ? (v - 273.15) * 9/5 + 32 : v;
                break;
            default:
                throw new IllegalArgumentException("Tipo de temperatura de origem inválido: " + from);
        }

        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }
}
