package org.garcia.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;

public class LibroDto {


    @NotBlank(message = "el titulo es obligatorio")
    private String titulo ;

    @NotBlank(message = "el autor es obligatorio")
    private String autor ;

    @NotEmpty(message = "debe de haber al menos un género")
    private ArrayList<String> generos ;

    @Min(value = 1000  , message = "el año no es válido")
    private Integer anyoPublicacion ;
    private  Boolean disponible ;

    @Positive(message = "el número de páginas no puede ser negativo")
    private  Integer numPaginas ;
    private Double valoracionMedia  ;



    private ArrayList<String> etiquetas ;

    public LibroDto(){

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public ArrayList<String> getGeneros() {
        return generos;
    }

    public void setGeneros(ArrayList<String> generos) {
        this.generos = generos;
    }

    public Integer getAnyoPublicacion() {
        return anyoPublicacion;
    }

    public void setAnyoPublicacion(Integer anyoPublicacion) {
        this.anyoPublicacion = anyoPublicacion;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Integer getNumPaginas() {
        return numPaginas;
    }

    public void setNumPaginas(Integer numPaginas) {
        this.numPaginas = numPaginas;
    }

    public Double getValoracionMedia() {
        return valoracionMedia;
    }

    public void setValoracionMedia(Double valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }

    public ArrayList<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(ArrayList<String> etiquetas) {
        this.etiquetas = etiquetas;
    }
}
