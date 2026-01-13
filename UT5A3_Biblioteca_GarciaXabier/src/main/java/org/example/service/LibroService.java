package org.example.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.example.dto.LibroDto;
import org.springframework.stereotype.Service;

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
}
