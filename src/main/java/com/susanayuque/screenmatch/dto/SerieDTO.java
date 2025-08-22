package com.susanayuque.screenmatch.dto;

import com.susanayuque.screenmatch.model.Categoria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SerieDTO(
        Long id,
        String titulo,
        Integer totalDeTemporadas,
        Double evaluacion,
        String poster,
        Categoria genero,
        String actores,
        String sinopsis){
}
