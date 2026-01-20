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
}