package com.empresa.tfgisma.modelo;

public class Equipo {
    private int id;
    private String nombre;
    private Ciudad ciudad;
    private Competicion competicion;
    private TipoTerreno tipoTerreno;
    private String entrenador;
    private int golesFavor;
    private int golesContra;


    public Equipo() {
    }

    public Equipo(int id, String nombre, Ciudad ciudad, Competicion competicion, TipoTerreno tipoTerreno, String entrenador, int golesFavor, int golesContra) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.competicion = competicion;
        this.tipoTerreno = tipoTerreno;
        this.entrenador = entrenador;
        this.golesFavor = golesFavor;
        this.golesContra = golesContra;
    }

    public Equipo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public Competicion getCompeticion() {
        return competicion;
    }

    public void setCompeticion(Competicion competicion) {
        this.competicion = competicion;
    }

    public TipoTerreno getTipoTerreno() {
        return tipoTerreno;
    }

    public void setTipoTerreno(TipoTerreno tipoTerreno) {
        this.tipoTerreno = tipoTerreno;
    }

    public String getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Usuario usuario) {
        this.entrenador = entrenador;
    }

    public int getGolesFavor() {
        return golesFavor;
    }

    public void setGolesFavor(int golesFavor) {
        this.golesFavor = golesFavor;
    }

    public int getGolesContra() {
        return golesContra;
    }

    public void setGolesContra(int golesContra) {
        this.golesContra = golesContra;
    }

    @Override
    public String toString() {
        return nombre;
    }
}