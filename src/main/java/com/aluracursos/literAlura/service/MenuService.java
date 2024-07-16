package com.aluracursos.literAlura.service;

import com.aluracursos.literAlura.Dtos.LibroDTO;
import com.aluracursos.literAlura.Dtos.ResultadosLibros;
import com.aluracursos.literAlura.entities.Autor;
import com.aluracursos.literAlura.entities.Libro;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.List;


public class MenuService {

    private Scanner scanner = new Scanner(System.in);

    private ObtenerJsonAPI obtenerJsonAPI = new ObtenerJsonAPI();

    private ConversorJsonADatos conversorJsonADatos = new ConversorJsonADatos();

    LibroService libroService;

    AutorService autorService;

    public MenuService(LibroService libroService, AutorService autorService) {
        this.libroService = libroService;
        this.autorService = autorService;
    }

    public void menu() {
        int opcion;
        do {
            mostrarMenu();

            try {
                opcion = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                opcion = 7;
            } finally {
                scanner.nextLine(); // Consumir el salto de línea

            }

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 6);
    }

    private void mostrarMenu() {
        System.out.println("\n--- Menú LiterAlura ---");
        System.out.println("1. Buscar libro por Título");
        System.out.println("2. Listar Libros Registrados");
        System.out.println("3. Listar Autores Registrados");
        System.out.println("4. Listar Autores Vivos en un determinado año");
        System.out.println("5. Listar libros por idioma");
        System.out.println("6. Salir");
        System.out.print("Elija una opción: ");
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Ingrese el titulo del libro a buscar: ");
        String titulo = scanner.nextLine();

        if (!titulo.isEmpty()) {

            List<Libro> librosEncontrados = libroService.obtenerLibrosPorTituloParecido(titulo);

            if (librosEncontrados.isEmpty()) {
                System.out.println("Libro no encontrado en la base de datos. Buscando en la API...");
                buscarEnAPIYGuardar(titulo);
            } else {
                System.out.println("Libros encontrados en la base de datos:");
                librosEncontrados.forEach(System.out::println);
            }
        } else {
            System.out.println("El título no puede estar vacío.");
        }
    }

    private void buscarEnAPIYGuardar(String titulo) {
        String jsonLibro = obtenerJsonAPI.obtenerJsonAPI("?search=" + titulo.replace(" ", "%20"));
        ResultadosLibros resultadosLibrosAPI = conversorJsonADatos.conversorJsonADatos(jsonLibro, ResultadosLibros.class);

        if (resultadosLibrosAPI.resultadosLibrosAPI().isEmpty()) {
            System.out.println("Libro no encontrado en la API.");
        } else {
            for (LibroDTO libroDTO : resultadosLibrosAPI.resultadosLibrosAPI()) {
                if (libroDTO.titulo().toLowerCase().contains(titulo.toLowerCase())) {
                    try {
                        guardarLibroYAutor(libroDTO);
                        System.out.println("Libro guardado: " + libroDTO.titulo());
                        return;
                    } catch (DataIntegrityViolationException e) {
                        System.out.println("El libro ya existe en la base de datos.");
                    }
                }
            }
            System.out.println("No se encontró un libro exacto con ese título en la API.");
        }
    }

    private void guardarLibroYAutor(LibroDTO libroDTO) {
        Autor autor = autorService.obtenerAutorPorNombreExacto(libroDTO.autores().get(0).nombre())
                .orElseGet(() -> new Autor(libroDTO.autores().get(0)));

        autorService.guardarAutor(autor);

        Libro libro = new Libro(libroDTO);
        libro.setAutor(autor);
        libroService.guardarLibro(libro);
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("Libros registrados:");
            libros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorService.obtenerTodosLosAutores();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            System.out.println("Autores registrados:");
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingrese el año para buscar autores vivos: ");
        try {
            int anio = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            List<Autor> autoresVivos = autorService.obtenerAutoresVivosEnUnAnio(anio);
            if (autoresVivos.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + anio);
            } else {
                System.out.println("Autores vivos en el año " + anio + ":");
                autoresVivos.forEach(System.out::println);
            }
        } catch (InputMismatchException e) {
            System.out.println("Por favor, ingrese un año válido.");
            scanner.nextLine(); // Limpiar el buffer del scanner
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma de los libros (es, en, fr, pt): ");
        String idioma = scanner.nextLine().toLowerCase();

        List<Libro> libros = libroService.obtenerLibrosPorIdoma(idioma);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma " + idioma);
        } else {
            System.out.println("Libros en " + idioma + ":");
            libros.forEach(System.out::println);

            mostrarEstadisticasLibros(libros);
        }
    }

    private void mostrarEstadisticasLibros(List<Libro> libros) {
        DoubleSummaryStatistics stats = libros.stream()
                .mapToDouble(Libro::getNumeroDeDescargas)
                .summaryStatistics();

        System.out.println("\nEstadísticas de descargas:");
        System.out.println("Promedio de descargas: " + stats.getAverage());
        System.out.println("Número máximo de descargas: " + stats.getMax());
        System.out.println("Número mínimo de descargas: " + stats.getMin());
        System.out.println("Total de libros: " + stats.getCount());
    }
}

/**
 * public class MenuService {
 * <p>
 * private Scanner scanner = new Scanner(System.in);
 * <p>
 * private ObtenerJsonAPI ObtenerJsonAPI = new ObtenerJsonAPI();
 * <p>
 * private ConversorJsonADatos conversorJsonADatos = new ConversorJsonADatos();
 * <p>
 * LibroService libroService;
 * <p>
 * AutorService autorService;
 * <p>
 * private List<Libro> listaDeLibros = new ArrayList<>();
 * <p>
 * private List<Autor> listaDeAutores = new ArrayList<>();
 * <p>
 * <p>
 * public void menu() {
 * Scanner scanner = new Scanner(System.in);
 * <p>
 * String menuOpciones = """
 * Menu
 * Seleccione una opción:
 * <p>
 * 1) Buscar libro por Titulo
 * 2) Listar Libros Registrados
 * 3) Listar Autores Registrados
 * 4) Listar Autores Vivos en un determinado año
 * 5) Listar libros por idioma
 * 6) salir
 * """;
 * <p>
 * var opcion = 0;
 * <p>
 * <p>
 * do {
 * System.out.println(menuOpciones);
 * opcion = scanner.nextInt();
 * <p>
 * switch (opcion) {
 * <p>
 * case 1:
 * System.out.println("Buscar libro por Titulo");
 * buscarLibroPorTitulo();
 * break;
 * <p>
 * case 2:
 * System.out.println("Listar Libros Registrados");
 * listarLibrosRegistrados();
 * break;
 * <p>
 * case 3:
 * System.out.println("Listar Autores Registrados");
 * <p>
 * break;
 * <p>
 * case 4:
 * System.out.println("Listar Autores Vivos en un determinado año");
 * break;
 * <p>
 * case 5:
 * System.out.println("Listar libros por idioma");
 * break;
 * <p>
 * case 6:
 * System.out.println("salir");
 * break;
 * <p>
 * default:
 * System.out.println("opción no valida");
 * }
 * <p>
 * } while (opcion != 6);
 * <p>
 * }
 * <p>
 * private void buscarLibroPorTitulo() {
 * System.out.println("Ingrese el titulo del libro a buscar: ");
 * <p>
 * <p>
 * var titulo = scanner.nextLine();
 * if (!titulo.isEmpty()) {
 * listaDeLibros = libroService.obtenerLibrosPorTituloParecido(titulo);
 * }
 * <p>
 * if (listaDeLibros.isEmpty()) {
 * System.out.println("No se han encontrado resultados!");
 * } else {
 * System.out.println(listaDeLibros.size() > 0 ? "Resultados" : "Resultado");
 * System.out.println("------------");
 * listaDeLibros.forEach(System.out::println);
 * }
 * <p>
 * <p>
 * }
 * <p>
 * <p>
 * private void guardarLibroPorTitulo() {
 * boolean libroEncontrado = false;
 * System.out.println("Ingrese el titulo del libro a guardar: ");
 * <p>
 * <p>
 * var titulo = scanner.nextLine();
 * <p>
 * if (!titulo.isEmpty()) {
 * <p>
 * <p>
 * var jsonLibro = ObtenerJsonAPI.obtenerJsonAPI("?search=" + titulo.replace(" ", "%20"));
 * <p>
 * <p>
 * var resultadosLibrosAPI = conversorJsonADatos.conversorJsonADatos(jsonLibro, ResultadosLibros.class);
 * <p>
 * <p>
 * //si no se trajeron libros de la API
 * if (resultadosLibrosAPI.resultadosLibrosAPI().isEmpty()) {
 * <p>
 * System.out.println("Libro no encontrado!");
 * <p>
 * } else {
 * <p>
 * for (LibroDTO libroAPI : resultadosLibrosAPI.resultadosLibrosAPI()) {
 * try {
 * <p>
 * if (libroAPI.titulo().toLowerCase().equals(titulo.toLowerCase())) {
 * <p>
 * System.out.println("libro API "+libroAPI);
 * <p>
 * //var autorBuscado = autorService.obtenerAutorPorNombreExacto(libroAPI.autores().getFirst().nombre());
 * var autorBuscado = autorService.obtenerAutorPorNombreExacto(libroAPI.autores().get(0).nombre());
 * <p>
 * <p>
 * Libro libro = new Libro(libroAPI);
 * <p>
 * if (autorBuscado.isPresent()) {
 * <p>
 * libro.setAutor(autorBuscado.get());
 * <p>
 * libroService.guardarLibro(libro);
 * <p>
 * System.out.println("El libro " + libro.getTitulo() + " se guardo correctamente!");
 * libroEncontrado=true;
 * } else {
 * <p>
 * //Autor autor = new Autor(libroAPI.autores().getFirst());
 * Autor autor = new Autor(libroAPI.autores().get(0));
 * <p>
 * autorService.guardarAutor(autor);
 * <p>
 * libro.setAutor(autor);
 * <p>
 * libroService.guardarLibro(libro);
 * <p>
 * System.out.println("El libro " + libro.getTitulo() + " se guardo correctamente!");
 * libroEncontrado=true;
 * }
 * }
 * <p>
 * } catch (DataIntegrityViolationException e) {
 * libroEncontrado=true;
 * System.out.println("El libro ya se encuentra en la base de datos!");
 * }
 * <p>
 * }
 * if (!libroEncontrado){
 * System.out.println("Libro no encontrado!");
 * }
 * <p>
 * }
 * }
 * <p>
 * }
 * <p>
 * private void listarLibrosRegistrados() {
 * listaDeLibros = libroService.obtenerTodosLosLibros();
 * <p>
 * if (!listaDeLibros.isEmpty()) {
 * System.out.println("Listado de libros en la BD");
 * System.out.println("---------------------------");
 * listaDeLibros.forEach(a -> System.out.println(a.toString()));
 * } else {
 * System.out.println("No se han encontrado libros!");
 * }
 * <p>
 * }
 * <p>
 * private void listarAutoresRegistrados() {
 * <p>
 * listaDeAutores = autorService.obtenerTodosLosAutores();
 * <p>
 * if (!listaDeAutores.isEmpty()) {
 * System.out.println("Listado de autores en la BD");
 * System.out.println("---------------------------");
 * listaDeAutores.forEach(a -> System.out.println(a.toString()));
 * } else {
 * System.out.println("No se han encontrado autores!");
 * }
 * <p>
 * }
 * <p>
 * private void listarAutoresVivosEnUnAnio() {
 * System.out.println("Ingresa el año: ");
 * try {
 * <p>
 * var anio = scanner.nextInt();
 * listaDeAutores = autorService.obtenerAutoresVivosEnUnAnio(anio);
 * <p>
 * if (!listaDeAutores.isEmpty()) {
 * System.out.println("Lista de autores vivos en el año " + anio);
 * listaDeAutores.forEach(a -> System.out.println(a.toString()));
 * } else {
 * System.out.println("No se han encontrado autores vivos en el año " + anio);
 * }
 * <p>
 * listaDeAutores.clear();
 * } catch (InputMismatchException e) {
 * System.out.println("Error al cargar el año " + e.getMessage());
 * scanner.nextLine();
 * }
 * <p>
 * }
 * <p>
 * private void buscarLibrosPorIdioma() {
 * var menuIdioma = """
 * Ingresa un idioma:
 * ------------------
 * Español
 * Ingles
 * Frances
 * Português
 * """;
 * <p>
 * System.out.println(menuIdioma);
 * <p>
 * var idioma = scanner.nextLine();
 * <p>
 * switch (idioma.toLowerCase()) {
 * <p>
 * case "español":
 * listaDeLibros = libroService.obtenerLibrosPorIdoma("es");
 * break;
 * case "ingles":
 * listaDeLibros = libroService.obtenerLibrosPorIdoma("en");
 * break;
 * case "frances":
 * listaDeLibros = libroService.obtenerLibrosPorIdoma("fr");
 * break;
 * case "português":
 * listaDeLibros = libroService.obtenerLibrosPorIdoma("pt");
 * break;
 * default:
 * System.out.println("El idioma no esta en la lista!");
 * }
 * <p>
 * <p>
 * if (!listaDeLibros.isEmpty()) {
 * DoubleSummaryStatistics estadisticas = listaDeLibros.stream()
 * .filter(l -> l.getNumeroDeDescargas() > 0)
 * .collect(Collectors.summarizingDouble(Libro::getNumeroDeDescargas));
 * System.out.println("Estadisticas de libros en " + idioma);
 * System.out.println("---------------------------------------");
 * System.out.println("Promedio de descargas de libros: " + estadisticas.getAverage());
 * System.out.println("El libro mas descargado tiene " + estadisticas.getMax() + " descargas ");
 * System.out.println("EL libro menos descargado tiene " + estadisticas.getMin() + " descargas");
 * System.out.println("Cantidad de libros: " + estadisticas.getCount());
 * <p>
 * } else {
 * System.out.println("No se han encontrado libros en " + idioma);
 * }
 * <p>
 * }
 * <p>
 * <p>
 * private void eliminarAutorDeLaBD() {
 * <p>
 * System.out.println("Ingrese el nombre del autor: ");
 * System.out.println("-----------------------------");
 * var nombre = scanner.nextLine();
 * <p>
 * var autor = autorService.obtenerAutorPorNombreExacto(nombre);
 * <p>
 * if (autor.isPresent()) {
 * autorService.borrarAutor(autor.get());
 * System.out.println("Autor Eliminado!");
 * } else {
 * System.out.println("Autor no encontrado!");
 * }
 * <p>
 * <p>
 * }
 * <p>
 * private void eliminarLibroDeLaBD() {
 * System.out.println("Ingrese el nombre del libro: ");
 * System.out.println("-----------------------------");
 * var titulo = scanner.nextLine();
 * <p>
 * var libro = libroService.obtenerLibroPorTituloExacto(titulo);
 * <p>
 * if (libro.isPresent()) {
 * Autor autor = libro.get().getAutor();
 * <p>
 * autor.eliminarLibro(libro.get());
 * <p>
 * if (autor.getLibros().isEmpty()) {
 * autorService.eliminarAutor(autor);
 * } else {
 * autorService.guardarAutor(autor);
 * }
 * <p>
 * <p>
 * libroService.borrarLibro(libro.get());
 * <p>
 * <p>
 * System.out.println("Libro Eliminado!");
 * } else {
 * System.out.println("Libro no encontrado!");
 * }
 * <p>
 * }
 * <p>
 * <p>
 * }
 */
