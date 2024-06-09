package com.empresa.tfgisma.modelo;

public class Favorito {
    private int id;
    private Equipo equipo;
    private Usuario usuario;

    public Favorito() {
    }

    public Favorito(int id, Equipo equipo, Usuario usuario) {
        this.id = id;
        this.equipo = equipo;
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
