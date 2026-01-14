package org.example.service;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiClock;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.example.dto.LibroDto;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class LibroService {

    private static final String COLLECTION_NAME = "libros";

    public String guardarLibro(LibroDto libro){

        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference docRef = baseDeDatos.collection(COLLECTION_NAME).document();
        libro.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(libro);
        return docRef.getId();

    }
    public String cambiarLibro(LibroDto libro , String id){

        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME).document(id);
        libro.setId(referenciaDocumento.getId());
        ApiFuture<WriteResult> result = referenciaDocumento.set(libro);
        return referenciaDocumento.getId();

    }
    public  LibroDto consultarLibro(String id ) throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME).document(id);

        ApiFuture<DocumentSnapshot> future = referenciaDocumento.get();
        DocumentSnapshot documento = future.get();

        if (documento.exists()){
            return documento.toObject(LibroDto.class) ;
        }else {
            return  null ;
        }
    }
    public List<LibroDto> consultarLibros() throws ExecutionException, InterruptedException {
        Firestore baseDeDatos = FirestoreClient.getFirestore();
        CollectionReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> future = referenciaDocumento.get();
        QuerySnapshot documento = future.get();

        List<LibroDto> libros = documento.toObjects(LibroDto.class) ;
        return  libros ;
    }
    public void actualizarLibro(String id , LibroDto cambios){

        Firestore baseDeDatos = FirestoreClient.getFirestore();
        DocumentReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME).document(id);

        ObjectMapper mapper = new ObjectMapper() ;
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        Map mapaActualizados = new HashMap<>() ;
        mapaActualizados = mapper.convertValue(cambios , Map.class);

        if (!mapaActualizados.isEmpty()){
            referenciaDocumento.update(mapaActualizados) ;
        }

    }
    public Boolean borrarLibro(String id){
        Firestore baseDeDatos = FirestoreClient.getFirestore() ;
        DocumentReference referenciaDocumento = baseDeDatos.collection(COLLECTION_NAME).document(id) ;

        ApiFuture<DocumentSnapshot> documento = referenciaDocumento.get();
        documento.



    }
}
