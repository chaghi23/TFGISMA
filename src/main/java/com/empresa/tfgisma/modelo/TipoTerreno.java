package com.empresa.tfgisma.modelo;

public class TipoTerreno {
    private int id;
    private String nombreTerreno;

    public TipoTerreno() {
    }

    public TipoTerreno(int id, String nombreTerreno) {
        this.id = id;
        this.nombreTerreno = nombreTerreno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreTerreno() {
        return nombreTerreno;
    }

    public void setNombreTerreno(String nombreTerreno) {
        this.nombreTerreno = nombreTerreno;
    }
}