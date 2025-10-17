package com.example.mavenproject.restful.config;

import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;
import com.example.mavenproject.temperature.TemperatureResource;

@ApplicationPath("/webapi")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages("com.example.mavenproject.temperature");
        register(TemperatureResource.class);
    }
}
