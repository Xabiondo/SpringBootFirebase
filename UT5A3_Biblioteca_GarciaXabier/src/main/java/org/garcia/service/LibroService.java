package org.garcia.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.Api;
import org.garcia.dto.LibroDto;
import org.garcia.dto.LibroResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private static final String COLLECTION_NAME = "libros";

    public LibroResponse guardarLibro(LibroDto libro) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference docRef = baseDeDatos.collection(COLLECTION_NAME).document();

        ApiFuture<WriteResult> future = docRef.set(libro);
        future.get();

        return new LibroResponse(docRef.getId(), libro);
    }

    public LibroResponse cambiarLibro(String id, LibroDto libro) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME).document(id);

        ApiFuture<DocumentSnapshot> future = referenciaDocumento.get();
        future.get();


        return new LibroResponse(id, libro);
    }

    public LibroResponse consultarLibro(String id) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = referenciaDocumento.get();
        DocumentSnapshot documento = future.get();

        if (documento.exists()) {
            LibroDto libroDto = documento.toObject(LibroDto.class);
            return new LibroResponse(id, libroDto);
        } else {
            return null;
        }
    }

    public List<LibroResponse> consultarLibros() throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        CollectionReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> future = referenciaDocumento.get();
        QuerySnapshot documento = future.get();

        return documento.getDocuments().stream()
                .map(doc -> new LibroResponse(doc.getId(), doc.toObject(LibroDto.class)))
                .collect(Collectors.toList());
    }

    public LibroResponse actualizarLibro(String id, LibroDto cambios) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME).document(id);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Map<String, Object> mapaActualizados = mapper.convertValue(cambios, Map.class);

        if (mapaActualizados.isEmpty()) {
            throw new  IllegalArgumentException("no se enviaron datos para actualizar");

        }

        ApiFuture<WriteResult> futureActualizacion = referenciaDocumento.update(mapaActualizados);
        futureActualizacion.get();
        return new LibroResponse(id, cambios);
    }

    public void borrarLibro(String id) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDelete = baseDeDatos.collection(COLLECTION_NAME).document(id);

        ApiFuture<WriteResult> futureDelete = referenciaDelete.delete();
        futureDelete.get() ;
    }

    public List<LibroResponse> busquedaPersonalizada(String genero, Boolean disponible,
                                                     Double minimaValoracion, Integer limit) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        Query peticion = baseDeDatos.collection(COLLECTION_NAME);

        if (genero != null && !genero.isEmpty()) {
            peticion = peticion.whereArrayContains("generos", genero);
        }

        if (disponible != null) {
            peticion = peticion.whereEqualTo("disponible", disponible);
        }

        if (minimaValoracion != null) {
            peticion = peticion.whereGreaterThanOrEqualTo("valoracionMedia", minimaValoracion);
        }

        if (limit != null && limit > 0) {
            peticion = peticion.limit(limit);
        }

        ApiFuture<QuerySnapshot> futuro = peticion.get();
        List<QueryDocumentSnapshot> documentos = futuro.get().getDocuments();

        return documentos.stream()
                .map(doc -> new LibroResponse(doc.getId(), doc.toObject(LibroDto.class)))
                .collect(Collectors.toList());
    }
}