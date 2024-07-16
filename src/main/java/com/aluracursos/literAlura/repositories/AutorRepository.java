package com.aluracursos.literAlura.repositories;

import com.aluracursos.literAlura.entities.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findFirstByNombreIgnoreCase(String nombre);

    //LessThanEqual, para buscar registros menores o iguales a un valor
    List<Autor> findByFechaDeFallecimientoGreaterThanEqualAndFechaDeNacimientoLessThanEqual(Integer fechaDeFallecimiento, Integer fechaDeNacimiento);

}
