package com.susanayuque.screenmatch.service;

public interface IConvierteDatos {

    <T> T obtenerDatos(String json, Class<T> clase);
}
