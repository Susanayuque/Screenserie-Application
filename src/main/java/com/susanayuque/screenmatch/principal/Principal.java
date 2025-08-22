package com.susanayuque.screenmatch.principal;

import com.susanayuque.screenmatch.model.*;
import com.susanayuque.screenmatch.repository.SerieRepository;
import com.susanayuque.screenmatch.service.ConsumoAPI;
import com.susanayuque.screenmatch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi= new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=37c2ffa3";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu(){

        var opcion = -1;
        while (opcion!= 0){
            var menu = """
                    1- Buscar series
                    2- Buscar episodios
                    3- Buscar lista de series buscadas
                    4- Buscar series por titulo
                    5- Top 5 mejores series
                    6- Buscar series por genero
                    7- Buscar por numero maximo de temporadas y minimo de evaluacion
                    8- Buscar episodios por Titulo
                    9- Top 5 episodios por serie
                    
                    0- Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion){
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostraSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                case 7:
                    buscarPorTotalDeTemporadasYEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
                case 0:
                    System.out.println("Cerrando la aplicacion");
                    break;
                default:
                    System.out.println("Opcion Invalida");
            }
        }

    }

    //Busca los datos generales de las series
    private DatosSerie getDatosSerie(){
        System.out.println("Por favor escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE+nombreSerie.replace(" ", "+")+ API_KEY);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    // Busca los datos de todas las temporadas
    private void buscarEpisodioPorSerie(){
        mostraSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie de la cual deseas el detalle de episodios");
        var nombreSerie= teclado.nextLine();
        Optional<Serie> serie =series.stream()
                .filter(s->s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas() ; i++) {
                var json = consumoApi.obtenerDatos(URL_BASE+serieEncontrada.getTitulo().replace(" ", "+")+"&Season="+i+ API_KEY);
                var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporadas);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d->d.episodios().stream()
                            .map(e->new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }

    }

    private void buscarSerieWeb(){
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }
    private void mostraSeriesBuscadas() {

        series = repositorio.findAll();
//        List<Serie> series = new ArrayList<>();
//        series = datosSeries.stream()
//                .map(d -> new Serie(d))
//                .collect(Collectors.toList());

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo(){
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie= teclado.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()){
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada");
        }

    }

    private void buscarTop5Series(){
        List<Serie>topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s-> System.out.println("Serie: " + s.getTitulo() + " Evaluacion: " + s.getEvaluacion()));

    }
    private void buscarSeriesPorCategoria(){
        System.out.println("Escriba el genero/categoria de la serie que desea buscar");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria " + genero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarPorTotalDeTemporadasYEvaluacion(){
        System.out.println("Indica que el numero de temporadas");
        var temporadasRequeridas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("Ahora indica la evaluacion minima");
        var evaluacionRequerida = teclado.nextDouble();
        teclado.nextLine();
        //Con derived queries = mostrito
        //List<Serie> buscarPorTotalDeTemporadasyevaluacion = repositorio.findByTotalDeTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(temporadasRequeridas,evaluacionRequerida);
        //Con Native Query
        //List<Serie>buscarPorTotalTemporadasYEvaluacion = repositorio.seriesPorTemporadaYEvaluacion();
        //Con Query JPQL
        List<Serie>buscarPorTotalTemporadasYEvaluacion = repositorio.seriesPorTemporadaYEvaluacion(temporadasRequeridas, evaluacionRequerida);
        System.out.println("***** Series Filtradas por temporada y evaluacion *****");
        buscarPorTotalTemporadasYEvaluacion.forEach(s-> System.out.println("Serie: " + s.getTitulo() + " / Total de temporadas: " + s.getTotalDeTemporadas() + " / Evaluacion: " + s.getEvaluacion()));
    }

    private void buscarEpisodiosPorTitulo(){
        System.out.println("Cual es el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e->
                System.out.printf("Serie: %s Temporada %s Episodio %s Evaluacion %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));
    }

    private void buscarTop5Episodios(){
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach(e->
                    System.out.printf("Serie: %s - Temporada: %s - Episodio: %s - Titulo: %s - Evaluacion: %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getEvaluacion()));
        }
    }
















    //temporadas.forEach(System.out::println);

    // Mostrar solo el titulo de los episodios para las temporadas
//        for (int i = 0; i <datos.totalDeTemporadas(); i++) {
//            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }
    // Mejoras usando funciones Lambda
    //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    // Convertir todas las informaciones a una lista del tipo datos episodio

//    List<DatosEpisodio> datosEpisodios = temporadas.stream()
//            .flatMap(t-> t.episodios().stream())
//            .collect(Collectors.toList());

    //Top 5 episodios

//        System.out.println("Top 5 episodios");
//
//        datosEpisodios.stream()
//               .filter(e->!e.evaluacion().equalsIgnoreCase("N/A"))
//                .peek(e-> System.out.println("Primer filtro (N/A)" + e))
//                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .peek(e-> System.out.println("Segundo filtro prdenacion (Mayor a menor)" + e))
//                .map(e->e.titulo().toUpperCase())
//                .peek(e-> System.out.println("Tercer filtro Mayuscula (m>M)" + e))
//                .limit(5)
//                .forEach(System.out::println);


    //  Convirtiendo los datos a una lista de tipo episodio

//    List<Episodio> episodios = temporadas.stream()
//            .flatMap(t-> t.episodios().stream()
//                    .map(d-> new Episodio(t.numero(), d)))
//            .collect(Collectors.toList());
//        episodios.forEach(System.out::println);

    //Busqueda de episodios a partir de x año
    //System.out.println("Indique el año a partir de la cual desea ver los episodios");
//        var fecha = teclado.nextInt();
//        teclado.nextLine();
//
//        LocalDate fechaBusqueda = LocalDate.of(fecha,1,1);
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//        episodios.stream()
//                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
//                .forEach(e -> System.out.println(
//                        "Temporada= "+ e.getTemporada()+"//"+
//                                "Episodio= "+e.getTitulo() +"//"+
//                        "Fecha de lanzamiento= " + e.getFechaDeLanzamiento().format(dtf)
//                ));

    // Busca episodios por pedazo del titulo
//        System.out.println("Indique el titulo del episodio que desea ver");
//        var pedazoTitulo = teclado.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()){
//            System.out.println("Episodio encontrado");
//            System.out.println("Los datos son: " + episodioBuscado.get());
//        }else {
//            System.out.println("Episodio no encontrado");
//        }

    //Evaluacion por temporada

//    Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
//            .filter(e-> e.getEvaluacion()>0.0)
//            .collect(Collectors.groupingBy(Episodio::getTemporada,
//                    Collectors.averagingDouble(Episodio::getEvaluacion)));
//        System.out.println(evaluacionesPorTemporada);
//
//    DoubleSummaryStatistics est = episodios.stream()
//            .filter(e-> e.getEvaluacion()>0.0)
//            .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
//        System.out.println("La media de las evaluaciones es: " + est.getAverage());
//        System.out.println("El episodio mejor evaluado es: " + est.getMax());
//        System.out.println("El episodio peor evaluado es: " + est.getMin());
}
