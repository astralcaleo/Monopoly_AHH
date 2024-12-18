package partida.avatares;

import monopoly.*;
import monopoly.casillas.Casilla;
import monopoly.casillas.propiedades.Propiedad;
import partida.Jugador;

import java.util.ArrayList;

public class Pelota extends Avatar {

    public Pelota(Jugador jugador, Casilla lugar, ArrayList<Avatar> avCreados, Juego juego) {
        super("pelota",jugador, lugar, avCreados, juego);
    }

    public void mover(int dado1, int dado2, Tablero tablero, Jugador banca) {
        int desplazamiento = dado1 + dado2;
        int posicionActual = this.getLugar().getPosicion();
    
        if (desplazamiento <= 4) {
            retrocederPelota(desplazamiento, posicionActual, tablero, banca);
        } else {
            avanzarPelota(desplazamiento, posicionActual, tablero, banca);
        }
    
        gestionarDadosDobles(dado1, dado2, tablero, banca);
    }
    
    private void retrocederPelota(int desplazamiento, int posicionActual, Tablero tablero, Jugador banca) {
        int j = 1; // Movimiento inicial
        for (int i = 1; i <= desplazamiento; i += j) {
            int nuevaPosicion = ((posicionActual - j) % 40 + 40) % 40;
            Casilla destino = tablero.encontrar_casilla(nuevaPosicion);
    
            Juego.consola.imprimir("El avatar " + this.getID() + " retrocede " + j + " posiciones, desde " + this.getLugar().getNombre() + " hasta " + destino.getNombre());
    
            this.moverAvatar(tablero.getPosiciones(), (-j + 40) % 40);
            this.setLugar(destino);
            destino.evaluarCasilla(this.getJugador(), desplazamiento, tablero, juego.getTurno(), juego);
    
            // Alternar entre pasos de 1 o 2 casillas
            j = (desplazamiento - i == 1) ? 1 : 2;
        }
    }
    
    private void avanzarPelota(int desplazamiento, int posicionActual, Tablero tablero, Jugador banca) {
        int j = 5; // Primer paso es a la 5ª casilla impar
        for (int i = 5; i <= desplazamiento; i += 2) {
            int nuevaPosicion = (posicionActual + j) % 40;
            Casilla destino = tablero.encontrar_casilla(nuevaPosicion);
    
            Juego.consola.imprimir("El avatar " + this.getID() + " avanza " + j + " posiciones, desde " + this.getLugar().getNombre() + " hasta " + destino.getNombre());
    
            this.moverAvatar(tablero.getPosiciones(), j);
            this.setLugar(destino);
            destino.evaluarCasilla(this.getJugador(), desplazamiento, tablero, juego.getTurno(), juego);

            if ((this.getLugar() instanceof Propiedad)) {

                getJuego().verTablero();
                                
                Juego.consola.imprimir("\ncontinuar ");
                Juego.consola.imprimir("comprar " + this.getLugar().getNombre());
                Juego.consola.imprimir("hipotecar " + this.getLugar().getNombre());
                Juego.consola.imprimir("deshipotecar " + this.getLugar().getNombre());
                Juego.consola.imprimir("edificar \"TIPOEDIFICIO\"");
                Juego.consola.imprimir("vender \"TIPOEDIFICIO\"" + this.getLugar().getNombre() + " \"CANTIDAD\"\n");

                String comando = Juego.consola.leer("Introduce comando:");
                while (comando.isEmpty()) {comando = Juego.consola.leer(" ");}
                getJuego().analizarComando(comando);
            }
    
            // Alternar entre pasos de 1 o 2 casillas
            j = (desplazamiento - i == 1) ? 1 : 2;
        }
    }
    
    private void gestionarDadosDobles(int dado1, int dado2, Tablero tablero, Jugador banca) {
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