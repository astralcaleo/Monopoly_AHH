package partida.avatares;

import monopoly.*;
import monopoly.casillas.CasillaX;
import partida.Jugador;

import java.util.ArrayList;

public class Pelota extends Avatar {

    public Pelota(Jugador jugador, CasillaX lugar, ArrayList<Avatar> avCreados) {
        super("pelota",jugador, lugar, avCreados);
    }

    public void moverPelota(int dado1, int dado2, Tablero tablero) {
        int desplazamiento = dado1 + dado2;
        int posicionActual = this.getLugar().getPosicion();
    
        if (desplazamiento <= 4) {
            retrocederPelota(desplazamiento, posicionActual, tablero);
        } else {
            avanzarPelota(desplazamiento, posicionActual, tablero);
        }
    
        gestionarDadosDobles(dado1, dado2, tablero);
    }
    
    private void retrocederPelota(int desplazamiento, int posicionActual, Tablero tablero) {
        int j = 1; // Movimiento inicial
        for (int i = 1; i <= desplazamiento; i += j) {
            int nuevaPosicion = ((posicionActual - j) % 40 + 40) % 40;
            CasillaX destino = tablero.encontrar_casilla(nuevaPosicion);
    
            System.out.println("El avatar " + this.getID() + " retrocede " + j + " posiciones, desde " + this.getLugar().getNombre() + " hasta " + destino.getNombre());
    
            this.moverAvatar(tablero.getPosiciones(), (-j + 40) % 40);
            this.setLugar(destino);
            destino.evaluarCasilla(this.getJugador(), banca, desplazamiento);
    
            // Alternar entre pasos de 1 o 2 casillas
            j = (desplazamiento - i == 1) ? 1 : 2;
        }
    }
    
    private void avanzarPelota(int desplazamiento, int posicionActual, Tablero tablero) {
        int j = 5; // Primer paso es a la 5ª casilla impar
        for (int i = 5; i <= desplazamiento; i += j) {
            int nuevaPosicion = (posicionActual + j) % 40;
            CasillaX destino = tablero.encontrar_casilla(nuevaPosicion);
    
            System.out.println("El avatar " + this.getID() + " avanza " + j + " posiciones, desde " + this.getLugar().getNombre() + " hasta " + destino.getNombre());
    
            this.moverAvatar(tablero.getPosiciones(), j);
            this.setLugar(destino);
            destino.evaluarCasilla(this.getJugador(), banca, desplazamiento);
    
            // Alternar entre pasos de 1 o 2 casillas
            j = (desplazamiento - i == 1) ? 1 : 2;
        }
    }
    
    private void gestionarDadosDobles(int dado1, int dado2, Tablero tablero) {
        if (dado1 == dado2) {
            int tiradasDobles = this.getTiradasDobles();
            tiradasDobles++;
            this.setTiradasDobles(tiradasDobles);
    
            if (tiradasDobles == 3) {
                this.getJugador().encarcelar(tablero.getPosiciones());
                Juego.consola.imprimir(this.getJugador().getNombre() + " ha sacado tres dobles seguidos y va a la cárcel.");
                this.setTiradasDobles(0); // Reiniciar contador al encarcelar
            } else if (this.getJugador().getEncarcelado()) {
                this.getJugador().setEncarcelado(false);
                this.getJugador().setTiradasCarcel(0);
                Juego.consola.imprimir(this.getJugador().getNombre() + " sale de la cárcel. Puede lanzar los dados.");
            } else {
                Juego.consola.imprimir("Puede volver a lanzar los dados.");
            }
        } else {
            this.setTiradasDobles(0); // Reiniciar si no son dobles
        }
    }
    
    



}