package com.challenge.literalura.modelo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosConsulta(@JsonAlias("count") Integer numeroLibros, @JsonAlias("next") String pagProxima, @JsonAlias("previous") String pagAnterior,
                            @JsonAlias("results") List<DatosLibro> resultado) {
}
