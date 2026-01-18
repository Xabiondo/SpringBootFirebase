package org.garcia.dto;

public class LibroResponse extends LibroDto {

    private String id;

    // Constructor vacío
    public LibroResponse() {
        super();
    }

    // Constructor completo
    public LibroResponse(String id, LibroDto dto) {
        this.id = id;
        this.setTitulo(dto.getTitulo());
        this.setAutor(dto.getAutor());
        this.setGeneros(dto.getGeneros());
        this.setAnyoPublicacion(dto.getAnyoPublicacion());
        this.setDisponible(dto.getDisponible());
        this.setNumPaginas(dto.getNumPaginas());
        this.setValoracionMedia(dto.getValoracionMedia());
        this.setEtiquetas(dto.getEtiquetas());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}