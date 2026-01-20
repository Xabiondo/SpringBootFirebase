package org.garcia.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.garcia.dto.LibroDto;
import org.garcia.dto.LibroResponse;
import org.springframework.stereotype.Service;

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

        ApiFuture<WriteResult> future = referenciaDocumento.set(libro);
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
        // Reutilizamos el helper para simplificar código
        return ejecutarConsulta(baseDeDatos.collection(COLLECTION_NAME));
    }

    public LibroResponse actualizarLibro(String id, LibroDto cambios) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME).document(id);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Map<String, Object> mapaActualizados = mapper.convertValue(cambios, Map.class);

        if (mapaActualizados.isEmpty()) {
            throw new IllegalArgumentException("No se enviaron datos para actualizar");
        }

        ApiFuture<WriteResult> futureActualizacion = referenciaDocumento.update(mapaActualizados);
        futureActualizacion.get();
        return new LibroResponse(id, cambios);
    }

    public void borrarLibro(String id) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDelete = baseDeDatos.collection(COLLECTION_NAME).document(id);
        ApiFuture<WriteResult> futureDelete = referenciaDelete.delete();
        futureDelete.get();
    }



    // 1. Búsqueda dinámica original (Multifiltro)
    public List<LibroResponse> busquedaPersonalizada(String genero, Boolean disponible,
                                                     Double minimaValoracion, Integer limit) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        Query peticion = baseDeDatos.collection(COLLECTION_NAME);

        if (genero != null && !genero.isEmpty()) peticion = peticion.whereArrayContains("generos", genero);
        if (disponible != null) peticion = peticion.whereEqualTo("disponible", disponible);
        if (minimaValoracion != null) peticion = peticion.whereGreaterThanOrEqualTo("valoracionMedia", minimaValoracion);
        if (limit != null && limit > 0) peticion = peticion.limit(limit);

        return ejecutarConsulta(peticion);
    }

    // 2. Por Autor exacto
    public List<LibroResponse> porAutor(String autor) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME).whereEqualTo("autor", autor));
    }

    // 3. Rango de años
    public List<LibroResponse> porRangoAnyo(int inicio, int fin) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("anyoPublicacion", inicio)
                .whereLessThanOrEqualTo("anyoPublicacion", fin));
    }

    // 4. Top N mejores valorados
    public List<LibroResponse> obtenerTop(int limit) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .orderBy("valoracionMedia", Query.Direction.DESCENDING).limit(limit));
    }

    // 5. Libros cortos disponibles
    public List<LibroResponse> cortosDisponibles(int maxPaginas) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Nota: Requiere índice compuesto en Firestore
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereEqualTo("disponible", true)
                .whereLessThanOrEqualTo("numPaginas", maxPaginas)
                .orderBy("numPaginas"));
    }

    // 6. Contiene alguna de estas etiquetas
    public List<LibroResponse> porEtiquetas(List<String> etiquetas) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereArrayContainsAny("etiquetas", etiquetas));
    }

    // 7. Autocomplete de título (prefijo)
    public List<LibroResponse> tituloEmpiezaPor(String prefijo) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .orderBy("titulo").startAt(prefijo).endAt(prefijo + "\uf8ff"));
    }

    // 8. Contar por género
    public long contarGenero(String genero) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        AggregateQuerySnapshot snapshot = db.collection(COLLECTION_NAME)
                .whereArrayContains("generos", genero)
                .count().get().get();
        return snapshot.getCount();
    }

    // 9. Paginación simple
    public List<LibroResponse> paginado(int offset, int limit) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .orderBy("titulo").offset(offset).limit(limit));
    }

    // 10. No disponibles (Reposición)
    public List<LibroResponse> obtenerNoDisponibles() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME).whereEqualTo("disponible", false));
    }

    // 11. Best Sellers (Valoración > 9 y antiguos)
    public List<LibroResponse> bestSellers(int anyoLimite) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereGreaterThan("valoracionMedia", 9.0)
                .whereLessThan("anyoPublicacion", anyoLimite));
    }

    // 12. Autor ordenado por fecha DESC
    public List<LibroResponse> autorRecientes(String autor) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereEqualTo("autor", autor)
                .orderBy("anyoPublicacion", Query.Direction.DESCENDING));
    }

    // 13. Libros sin etiquetas (null)
    public List<LibroResponse> sinEtiquetas() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME).whereEqualTo("etiquetas", null));
    }

    // 14. Género específico y más de N páginas
    public List<LibroResponse> generoYExtenso(String gen, int minPag) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereArrayContains("generos", gen)
                .whereGreaterThan("numPaginas", minPag));
    }

    // 15. Rango valoración específico
    public List<LibroResponse> rangoValoracion(double min, double max) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereGreaterThanOrEqualTo("valoracionMedia", min)
                .whereLessThanOrEqualTo("valoracionMedia", max));
    }

    // 16. Última novedad (1 solo)
    public List<LibroResponse> ultimaNovedad() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .orderBy("anyoPublicacion", Query.Direction.DESCENDING).limit(1));
    }

    // 17. Lista de géneros exacta (Array matching)
    public List<LibroResponse> generosExactos(List<String> generos) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME).whereEqualTo("generos", generos));
    }

    // 18. En años específicos (IN)
    public List<LibroResponse> porAnyos(List<Integer> anyos) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME).whereIn("anyoPublicacion", anyos));
    }

    // 19. Disponibles de un autor
    public List<LibroResponse> disponiblesAutor(String autor) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereEqualTo("autor", autor)
                .whereEqualTo("disponible", true));
    }

    // 20. Libros muy largos (> 800 pag) o muy cortos (< 100 pag)
    public List<LibroResponse> porExtensionExtrema(int p) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME).whereGreaterThan("numPaginas", p));
    }

    // 21. Búsqueda por autor y valoración mínima
    public List<LibroResponse> autorYValoracion(String autor, double min) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return ejecutarConsulta(db.collection(COLLECTION_NAME)
                .whereEqualTo("autor", autor)
                .whereGreaterThanOrEqualTo("valoracionMedia", min));
    }

    // ==========================================
    // Método para no repetir todo el rato el mismo código
    // ==========================================
    private List<LibroResponse> ejecutarConsulta(Query query) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documentos = future.get().getDocuments();

        return documentos.stream()
                .map(doc -> new LibroResponse(doc.getId(), doc.toObject(LibroDto.class)))
                .collect(Collectors.toList());
    }
}