package monopoly.edificios;

import java.util.ArrayList;

import monopoly.casillas.propiedades.Solar;
import partida.Jugador;

public class Piscina extends Edificio {
    //Atributos
    private float coste;            // Precio de construcción de la casa

    // Constructor
    public Piscina(Jugador propietario, Solar ubicacion, ArrayList<Edificio> edificiosConstruidos) {
        super(propietario, ubicacion, edificiosConstruidos);       // Llama al constructor de la clase base Edificio
        this.coste = super.getUbicacion().getValorInicial() * 0.4f;
    }
}