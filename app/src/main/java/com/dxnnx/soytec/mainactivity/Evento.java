package com.dxnnx.soytec.mainactivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DXNNX on 9/8/17.
 */

public class Evento {
    String id;
    String nombre;
    String fecha;
    String descripcion;
    String foto;
    String ubicacion;
    int estado;
    String categoria;

    Evento(String id, String nombre, String fecha,String descripcion, String foto,String ubicacion,int estado,String categoria) {
        this.nombre = nombre;
        this.id = id;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.foto = foto;
        this.ubicacion= ubicacion;
        this.estado= estado;
        this.categoria= categoria;


    }

    public int getEstado() { return estado; }
    public String getCategoria() { return categoria; }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() { return ubicacion; }

    public String getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFoto() {
        return foto;
    }

    public String getId() {
        return id;
    }
}
