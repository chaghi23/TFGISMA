package com.empresa.tfgisma.modelo;

public class Jugador {
    private int id;
    private String nombre;
    private String apellido;
    private String posicion;
    private int golesTotales;
    private int idEquipo;
    private int tarjetasAmarillas;
    private int tarjetasRojas;
    private String fechaNacimiento;

    public Jugador() {
    }

    public Jugador(int id, String nombre, String apellido, String posicion, int golesTotales, int idEquipo, int tarjetasAmarillas, int tarjetasRojas, String fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.posicion = posicion;
        this.golesTotales = golesTotales;
        this.idEquipo = idEquipo;
        this.tarjetasAmarillas = tarjetasAmarillas;
        this.tarjetasRojas = tarjetasRojas;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Jugador(int id, String nombre, String apellido, String posicion, int golesTotales, int tarjetasAmarillas, int tarjetasRojas, String fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.posicion = posicion;
        this.golesTotales = golesTotales;
        this.tarjetasAmarillas = tarjetasAmarillas;
        this.tarjetasRojas = tarjetasRojas;
        this.fechaNacimiento = fechaNacimiento;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getGolesTotales() {
        return golesTotales;
    }

    public void setGolesTotales(int golesTotales) {
        this.golesTotales = golesTotales;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public int getTarjetasAmarillas() {
        return tarjetasAmarillas;
    }

    public void setTarjetasAmarillas(int tarjetasAmarillas) {
        this.tarjetasAmarillas = tarjetasAmarillas;
    }

    public int getTarjetasRojas() {
        return tarjetasRojas;
    }

    public void setTarjetasRojas(int tarjetasRojas) {
        this.tarjetasRojas = tarjetasRojas;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}