package org.example.api;


import org.example.dto.LibroDto;
import org.example.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api")
public class LibroController {

    @Autowired
     LibroService libroService ;


    @PostMapping("/libros")
    public ResponseEntity<String> guardarLibro(@RequestBody  LibroDto libro){
        libroService.guardarLibro(libro);

        return new ResponseEntity<>(HttpStatus.CREATED );

    }
    @GetMapping("/libros/{id}")
    public ResponseEntity<LibroDto> consultarLibro(@PathVariable  String id) throws ExecutionException, InterruptedException {

        LibroDto libro = libroService.consultarLibro(id) ;

        if (libro != null){
            return new ResponseEntity<LibroDto>(libro , HttpStatus.OK) ;
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND) ;

    }
}
