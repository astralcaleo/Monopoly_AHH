package monopoly.edificios;

import java.util.ArrayList;

import monopoly.casillas.propiedades.Solar;
import partida.Jugador;

public class Casa extends Edificio {
    // Constructor
    public Casa(Jugador propietario, Solar ubicacion, ArrayList<Edificio> edificiosConstruidos) {
        super(propietario, ubicacion, edificiosConstruidos);       // Llama al constructor de la clase base Edificio
        super.setCoste(super.getUbicacion().getValorInicial() * 0.6f);
    }
}