package com.challenge.literalura.principal;

import com.challenge.literalura.modelo.*;
import com.challenge.literalura.repository.AutorRepository;
import com.challenge.literalura.repository.LibroRepository;
import com.challenge.literalura.service.ConsumoApi;
import com.challenge.literalura.service.ConvierteDatos;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private Scanner teclado  = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private List<Libro> libros;
    private List<Autor> autores;
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;



    public Principal(AutorRepository autorRepository, LibroRepository libroRepository){
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void menu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libros por titulo
                    2 - Mostrar libros buscados
                    3 - Mostrar autores buscados
                    4 - Mostrar autores vivos por año
                    5 - Mostrar libros por idioma
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    mostrarLibrosRegistrados();
                    break;
                case 3:
                    mostrarAutoresRegistrados();
                    break;
                case 4:
                    mostrarAutoresPorAnio();
                    break;
                case 5:
                    mostrarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }


    private String realizarBusqueda (){
        System.out.println("Escribe el nombre del libro a buscar: ");
        var nombreLibro = teclado.nextLine();
        String url = URL_BASE + "?search=" + nombreLibro.replace(" ", "%20");
        System.out.println("Esperando la respuesta...");
        String respuesta = consumoApi.obtenerDatos(url);
        return respuesta;
    }


    private void buscarLibroPorTitulo() {
        String respuesta = realizarBusqueda();
        DatosConsulta datosConsultaAPI =convierteDatos.obtenerDatos(respuesta, DatosConsulta.class);
        if (datosConsultaAPI.numeroLibros() !=0) {
            DatosLibro primerLibro = datosConsultaAPI.resultado().get(0);
            Autor autorLibro = new Autor(primerLibro.autores().get(0));
            Optional<Libro> libroBase = libroRepository.findLibroBytitulo(primerLibro.titulo());
            if (libroBase.isPresent()) {
                System.out.println("No se puede registrar el mismo líbro ");
                //System.out.println(libroBase);
            } else {
                Optional<Autor> autorDeBase = autorRepository.findByNombre(autorLibro.getNombre());
                if (autorDeBase.isPresent()) {
                    autorLibro = autorDeBase.get();
                } else {
                    autorRepository.save(autorLibro);
                }

                Libro libro = new Libro(primerLibro);
                libro.setAutor(autorLibro);
                libroRepository.save(libro);
                System.out.println(libro);
            }
        } else {
            System.out.println("Líbro no encontrado...");
        }
    }

    private void mostrarLibrosRegistrados() {
        libros = libroRepository.findAll();
        libros.stream().forEach(System.out::println);

    }

    private void mostrarAutoresRegistrados() {
        autores = autorRepository.findAll();
        autores.stream().forEach(System.out::println);
    }

    private void mostrarAutoresPorAnio() {
        System.out.println("Ingrese el año de nacimiento de los autores que desea buscar: ");
        try {
            int anio = teclado.nextInt();
            List<Autor> autores = autorRepository.findAutoresByAnio(anio);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores nacidos en el año " + anio);
            } else {
                System.out.println("Autores nacidos en el año " + anio + ":");
                autores.forEach(System.out::println);
            }
        } catch (InputMismatchException e) {
            System.out.println("Ingrese un año válido (número entero).");
            teclado.nextLine(); // Limpiar el buffer del scanner
        }
    }


    private void buscarPorIdioma(Idioma idioma){
        libros = libroRepository.findLibrosByIdioma(idioma);
        if (libros.isEmpty()){
            System.out.println("No hay libros encontrados");
        } else {
            libros.stream().forEach(System.out::println);
        }
    }

    private void mostrarLibrosPorIdioma() {
        String menuIdioma = """
            Ingrese el idioma para buscar los libros: 
            es >> Español
            en >> Inglés
            fr >> Francés 
            pt >> Portugués
            """;

        System.out.println(menuIdioma);
        String idiomaBuscado = teclado.nextLine().trim().toLowerCase(); // Tratar la entrada para evitar problemas con mayúsculas/minúsculas y espacios

        Idioma idioma;

        switch (idiomaBuscado) {
            case "es":
                idioma = Idioma.ESPANOL;
                break;
            case "en":
                idioma = Idioma.INGLES;
                break;
            case "fr":
                idioma = Idioma.FRANCES;
                break;
            case "pt":
                idioma = Idioma.PORTUGUES;
                break;
            default:
                System.out.println("Idioma ingresado no válido.");
                return;
        }

        buscarPorIdioma(idioma);
    }





















}
