package com.challenge.literalura.repository;

import com.challenge.literalura.modelo.Idioma;
import com.challenge.literalura.modelo.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    Optional<Libro> findLibroBytitulo(String titulo);
    List<Libro> findLibrosByIdioma(Idioma idioma);


}
