package com.aluracursos.literAlura.Dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LibroDTO(
        @JsonAlias("title")String titulo,
        @JsonAlias("authors") List<AutorDTO> autores,
        @JsonAlias("languages")List<String> lenguajes,
        @JsonAlias("download_count")Integer numeroDeDescargas

) {

}
