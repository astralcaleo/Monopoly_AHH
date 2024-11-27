package partida.avatares;

import monopoly.*;
import monopoly.casillas.Casilla;
import partida.Jugador;

import java.util.ArrayList;

public class Sombrero extends Avatar {

    public Sombrero(Jugador jugador, Casilla lugar, ArrayList<Avatar> avCreados) {
        super("sombrero",jugador, lugar, avCreados);
    }

}