package com.empresa.tfgisma.modelo;

public class Partido {
    private int id;
    private Equipo rival;
    private Resultado resultado;

    public Partido() {
    }

    public Partido(int id, Equipo rival, Resultado resultado) {
        this.id = id;
        this.rival = rival;
        this.resultado = resultado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Equipo getRival() {
        return rival;
    }

    public void setRival(Equipo rival) {
        this.rival = rival;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }
}