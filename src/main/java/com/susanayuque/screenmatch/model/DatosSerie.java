package com.susanayuque.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSerie(
        @JsonAlias("Title") String titulo,
        @JsonAlias("totalSeasons") Integer totalDeTemporadas,
        @JsonAlias("imdbRating") String evaluacion,
        @JsonAlias("Actors") String actores,
        @JsonAlias("Genre") String genero,
        @JsonAlias("Plot") String sinopsis,
        @JsonAlias("Poster") String poster
        ) {
}
