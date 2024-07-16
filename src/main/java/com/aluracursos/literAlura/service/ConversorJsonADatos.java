package com.aluracursos.literAlura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ConversorJsonADatos implements IconvierteJsonDatosAPI{
    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public <T> T conversorJsonADatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json,clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
