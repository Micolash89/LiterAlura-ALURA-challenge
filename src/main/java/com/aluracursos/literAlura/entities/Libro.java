package com.aluracursos.literAlura.entities;

import com.aluracursos.literAlura.Dtos.LibroDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "Libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    @ManyToOne()
    @JoinColumn(name = "autor_id")
    private Autor autor;

    private String lenguajes;

    private Integer numeroDeDescargas;


    public Libro() {
    }

    public Libro (LibroDTO libroDTO) {
        this.titulo = libroDTO.titulo();
        this.lenguajes = libroDTO.lenguajes().get(0);
        this.numeroDeDescargas = libroDTO.numeroDeDescargas();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLenguajes() {
        return lenguajes;
    }

    public void setLenguajes(String lenguajes) {
        this.lenguajes = lenguajes;
    }

    public Integer getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Integer numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }

    @Override
    public String toString() {
        return  " - - - - Libro - - - - \n" +
                " Titulo: " + titulo + "\n" +
                " Autores: " + autor.getNombre() +"\n" +
                " Lenguajes: " + lenguajes +"\n" +
                " Numero De Descargas: " + numeroDeDescargas +"\n" ;
    }
}
