package com.aluracursos.literAlura.Dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResultadosLibros( @JsonAlias("results")
                                List<LibroDTO> resultadosLibrosAPI) {

}
