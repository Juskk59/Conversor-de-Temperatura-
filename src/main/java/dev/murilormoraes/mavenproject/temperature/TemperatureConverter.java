package dev.murilormoraes.mavenproject.temperature;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TemperatureConverter {

    public static BigDecimal convert(BigDecimal value, String from, String to) {
        if (from.equalsIgnoreCase(to)) return value;

        double v = value.doubleValue();
        double result;

        switch (from.toUpperCase()) {
            case "C":
                result = to.equalsIgnoreCase("F") ? (v * 9/5) + 32 :
                         to.equalsIgnoreCase("K") ? v + 273.15 : v;
                break;
            case "F":
                result = to.equalsIgnoreCase("C") ? (v - 32) * 5/9 :
                         to.equalsIgnoreCase("K") ? ((v - 32) * 5/9) + 273.15 : v;
                break;
            case "K":
                result = to.equalsIgnoreCase("C") ? v - 273.15 :
                         to.equalsIgnoreCase("F") ? (v - 273.15) * 9/5 + 32 : v;
                break;
            default:
                throw new IllegalArgumentException("Tipo de temperatura inválido: " + from);
        }

        return BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
    }
}
