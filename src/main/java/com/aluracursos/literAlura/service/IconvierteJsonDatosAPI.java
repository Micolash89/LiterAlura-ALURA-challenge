package com.aluracursos.literAlura.service;

public interface IconvierteJsonDatosAPI {
    <T> T conversorJsonADatos(String json, Class <T> clase);
}
