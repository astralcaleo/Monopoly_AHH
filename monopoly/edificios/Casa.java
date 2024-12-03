package monopoly.edificios;

import java.util.ArrayList;

import monopoly.casillas.propiedades.Solar;
import partida.Jugador;

public class Casa extends Edificio {
    //Atributos
    private float coste;            // Precio de construcción de la casa

    // Constructor
    public Casa(Jugador propietario, Solar ubicacion, ArrayList<Edificio> edificiosConstruidos) {
        super(propietario, ubicacion, edificiosConstruidos);       // Llama al constructor de la clase base Edificio
        this.coste = super.getUbicacion().getValorInicial() * 0.6f;
    }
}