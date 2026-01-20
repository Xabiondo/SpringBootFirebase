package org.garcia.api;

import jakarta.validation.Valid;
import org.garcia.dto.LibroDto;
import org.garcia.dto.LibroResponse;
import org.garcia.service.LibroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @PostMapping
    public ResponseEntity<LibroResponse> guardarLibro(@Valid @RequestBody LibroDto libroDto) throws ExecutionException, InterruptedException {
        LibroResponse guardado = libroService.guardarLibro(libroDto);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponse> consultarLibro(@PathVariable String id) throws ExecutionException, InterruptedException {
        LibroResponse libro = libroService.consultarLibro(id);
        if (libro != null) {
            return ResponseEntity.ok(libro);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<LibroResponse>> consultarLibros() throws ExecutionException, InterruptedException {
        List<LibroResponse> libros = libroService.consultarLibros();
        return ResponseEntity.ok(libros);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroResponse> cambioTotal(@PathVariable String id, @Valid @RequestBody LibroDto libroDto) throws ExecutionException, InterruptedException {
        LibroResponse actualizado = libroService.cambiarLibro(id, libroDto);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LibroResponse> cambioParcial(@PathVariable String id, @RequestBody LibroDto libroDto) throws ExecutionException, InterruptedException {
        LibroResponse actualizado = libroService.actualizarLibro(id, libroDto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarLibro(@PathVariable String id) throws ExecutionException, InterruptedException {
        if (libroService.consultarLibro(id) != null) {
            libroService.borrarLibro(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<LibroResponse>> busquedaPersonalizada(
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) Double minimoValoracion,
            @RequestParam(required = false) Integer limit) throws ExecutionException, InterruptedException {

        List<LibroResponse> librosFiltrados = libroService.busquedaPersonalizada(genero, disponible, minimoValoracion, limit);
        return ResponseEntity.ok(librosFiltrados);
    }

    @GetMapping("/autor")
    public ResponseEntity<List<LibroResponse>> porAutor(@RequestParam String autor) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.porAutor(autor));
    }

    @GetMapping("/rango-anyo")
    public ResponseEntity<List<LibroResponse>> porRangoAnyo(@RequestParam int inicio, @RequestParam int fin) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.porRangoAnyo(inicio, fin));
    }

    @GetMapping("/top")
    public ResponseEntity<List<LibroResponse>> obtenerTop(@RequestParam int limit) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.obtenerTop(limit));
    }

    @GetMapping("/cortos-disponibles")
    public ResponseEntity<List<LibroResponse>> cortosDisponibles(@RequestParam int maxPaginas) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.cortosDisponibles(maxPaginas));
    }

    @GetMapping("/etiquetas")
    public ResponseEntity<List<LibroResponse>> porEtiquetas(@RequestParam List<String> etiquetas) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.porEtiquetas(etiquetas));
    }

    @GetMapping("/titulo-prefijo")
    public ResponseEntity<List<LibroResponse>> tituloEmpiezaPor(@RequestParam String prefijo) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.tituloEmpiezaPor(prefijo));
    }

    @GetMapping("/contar-genero")
    public ResponseEntity<Long> contarGenero(@RequestParam String genero) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.contarGenero(genero));
    }

    @GetMapping("/paginado")
    public ResponseEntity<List<LibroResponse>> paginado(@RequestParam int offset, @RequestParam int limit) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.paginado(offset, limit));
    }

    @GetMapping("/no-disponibles")
    public ResponseEntity<List<LibroResponse>> obtenerNoDisponibles() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.obtenerNoDisponibles());
    }

    @GetMapping("/bestsellers")
    public ResponseEntity<List<LibroResponse>> bestSellers(@RequestParam int anyoLimite) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.bestSellers(anyoLimite));
    }

    @GetMapping("/autor-recientes")
    public ResponseEntity<List<LibroResponse>> autorRecientes(@RequestParam String autor) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.autorRecientes(autor));
    }

    @GetMapping("/sin-etiquetas")
    public ResponseEntity<List<LibroResponse>> sinEtiquetas() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.sinEtiquetas());
    }

    @GetMapping("/genero-extenso")
    public ResponseEntity<List<LibroResponse>> generoYExtenso(@RequestParam String genero, @RequestParam int minPaginas) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.generoYExtenso(genero, minPaginas));
    }

    @GetMapping("/rango-valoracion")
    public ResponseEntity<List<LibroResponse>> rangoValoracion(@RequestParam double min, @RequestParam double max) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.rangoValoracion(min, max));
    }

    @GetMapping("/ultima-novedad")
    public ResponseEntity<List<LibroResponse>> ultimaNovedad() throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.ultimaNovedad());
    }

    @GetMapping("/generos-exactos")
    public ResponseEntity<List<LibroResponse>> generosExactos(@RequestParam List<String> generos) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.generosExactos(generos));
    }

    @GetMapping("/por-anyos")
    public ResponseEntity<List<LibroResponse>> porAnyos(@RequestParam List<Integer> anyos) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.porAnyos(anyos));
    }

    @GetMapping("/disponibles-autor")
    public ResponseEntity<List<LibroResponse>> disponiblesAutor(@RequestParam String autor) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.disponiblesAutor(autor));
    }

    @GetMapping("/extension-extrema")
    public ResponseEntity<List<LibroResponse>> porExtensionExtrema(@RequestParam int paginas) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.porExtensionExtrema(paginas));
    }

    @GetMapping("/autor-valoracion")
    public ResponseEntity<List<LibroResponse>> autorYValoracion(@RequestParam String autor, @RequestParam double min) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(libroService.autorYValoracion(autor, min));
    }
}