package monopoly.edificios;

import java.util.ArrayList;

import monopoly.casillas.propiedades.Solar;
import partida.Jugador;

public class PistaDeporte extends Edificio {
    //Atributos
    private float coste;            // Precio de construcción de la casa

    // Constructor
    public PistaDeporte(Jugador propietario, Solar ubicacion, ArrayList<Edificio> edificiosConstruidos) {
        super(propietario, ubicacion, edificiosConstruidos);       // Llama al constructor de la clase base Edificio
        this.coste = super.getUbicacion().getValorInicial() * 1.25f;
    }
}