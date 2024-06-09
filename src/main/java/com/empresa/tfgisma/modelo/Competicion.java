package com.empresa.tfgisma.modelo;

import java.util.Date;

public class Competicion {
    private int id;
    private String nombre;
    private String categoria;
    private Date anioCompeticion;

    public Competicion() {
    }

    public Competicion(int id, String nombre, String categoria, Date anioCompeticion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.anioCompeticion = anioCompeticion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Date getAnioCompeticion() {
        return anioCompeticion;
    }

    public void setAnioCompeticion(Date anioCompeticion) {
        this.anioCompeticion = anioCompeticion;
    }
}