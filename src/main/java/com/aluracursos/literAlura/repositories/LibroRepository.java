package com.aluracursos.literAlura.repositories;

import com.aluracursos.literAlura.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByTituloContainsIgnoreCase(String titulo);

    Optional<Libro> findFirstByTituloIgnoreCase(String titulo);

    List<Libro> findByLenguajesIgnoreCase(String idioma);
}
