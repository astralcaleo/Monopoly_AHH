package monopoly.edificios;

import java.util.ArrayList;

import monopoly.casillas.propiedades.Solar;
import partida.Jugador;

public class Hotel extends Edificio {
    //Atributos
    private float coste;            // Precio de construcción del hotel

    // Constructor
    public Hotel(Jugador propietario, Solar ubicacion, ArrayList<Edificio> edificiosConstruidos) {
        super(propietario, ubicacion, edificiosConstruidos);       // Llama al constructor de la clase base Edificio
        this.coste = super.getUbicacion().getValorInicial() * 0.6f;
    }
}