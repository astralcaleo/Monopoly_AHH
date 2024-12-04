package monopoly.edificios;

import java.util.ArrayList;

import monopoly.casillas.propiedades.Solar;
import partida.Jugador;

public class PistaDeporte extends Edificio {
    // Constructor
    public PistaDeporte(Jugador propietario, Solar ubicacion, ArrayList<Edificio> edificiosConstruidos) {
        super(propietario, ubicacion, edificiosConstruidos);       // Llama al constructor de la clase base Edificio
        super.setCoste(super.getUbicacion().getValorInicial() * 1.25f);
    }
}