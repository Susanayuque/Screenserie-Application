package com.susanayuque.screenmatch;

import com.susanayuque.screenmatch.model.DatosEpisodio;
import com.susanayuque.screenmatch.model.DatosSerie;
import com.susanayuque.screenmatch.model.DatosTemporadas;
import com.susanayuque.screenmatch.principal.Ejemplos;
import com.susanayuque.screenmatch.principal.Principal;
import com.susanayuque.screenmatch.repository.SerieRepository;
import com.susanayuque.screenmatch.service.ConsumoAPI;
import com.susanayuque.screenmatch.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}
}
