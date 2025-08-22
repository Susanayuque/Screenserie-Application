package com.susanayuque.screenmatch.repository;

import com.susanayuque.screenmatch.dto.EpisodioDTO;
import com.susanayuque.screenmatch.model.Categoria;
import com.susanayuque.screenmatch.model.Episodio;
import com.susanayuque.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie,Long> {
   Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie );

   List<Serie> findTop5ByOrderByEvaluacionDesc();
   List<Serie>findByGenero(Categoria categoria);
   //Metodo Derived Queries:
   //List<Serie>findByTotalDeTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalDeTemporadas, Double evaluacion);
   //Metodo Native Query:
   //@Query( value = "SELECT * FROM series WHERE series.total_de_temporadas <= 2 AND series.evaluacion >=8", nativeQuery = true)
   //Metodo JPQL: s=representa a la clase "Serie"
   @Query("SELECT s FROM Serie s WHERE s.totalDeTemporadas <= :totalDeTemporadas AND s.evaluacion >= :evaluacion ")
   List<Serie>seriesPorTemporadaYEvaluacion(int totalDeTemporadas, Double evaluacion);

   @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
   List<Episodio> episodiosPorNombre(String nombreEpisodio);

   @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5")
   List<Episodio> top5Episodios(Serie serie);

   @Query("SELECT s FROM Serie s " + "JOIN s.episodios e " + "GROUP BY s " + "ORDER BY MAX(e.fechaDeLanzamiento) DESC LIMIT 5")
   List<Serie> lanzamientosMasRecientes();
   @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numeroTemporada")
   List<Episodio> obtenerTemporadasPorNumero(Long id, Long numeroTemporada);
   @Query("SELECT e FROM Episodio e WHERE e.serie.id = :id ORDER BY e.evaluacion DESC")
   List<Episodio> obtenerTop5EpisodiosPorSerie(Long id);
}
