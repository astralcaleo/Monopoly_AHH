package partida.avatares;

import monopoly.*;
import monopoly.casillas.CasillaX;
import partida.Jugador;

import java.util.ArrayList;

public class Sombrero extends Avatar {

    public Sombrero(Jugador jugador, CasillaX lugar, ArrayList<Avatar> avCreados) {
        super("sombrero",jugador, lugar, avCreados);
    }

}