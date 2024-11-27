package juego;

import tablero.Casilla;
import interfaz.Comando;
import interfaz.Consola;
import monopoly.interfaz.ConsolaNormal;

import java.util.ArrayList;
import java.util.List;

public class Juego implements Comando {
    private ArrayList<Jugador> jugadores; // Lista de jugadores
    private Tablero tablero; // Instancia del tablero
    private static Consola consola; // Instancia estática de la consola

    public Juego() {
        this.jugadores = new ArrayList<>();
        this.tablero = new Tablero(); // Inicializar el tablero
        consola = new ConsolaNormal(); // Crear la instancia de la consola
    }

    // Método para iniciar el juego, agregar jugadores, y preparar la partida
    public void iniciarJuego() {
        consola.imprimir("¡Bienvenido al Monopoly!");
        // Aquí puedes agregar lógica para inicializar jugadores, pedir sus nombres, etc.
        // Ejemplo:
        consola.imprimir("Inicializando el tablero y jugadores...");
        // Otros métodos de configuración
    }

    // Método para realizar el turno de un jugador, mover al avatar, etc.
    public void turnoJugador(Jugador jugador) {
        consola.imprimir("Es el turno de " + jugador.getNombre());
        // Ejemplo de lógica de turno: lanzar dados, mover avatar, etc.
    }

    // Otros métodos adicionales que podrías necesitar, como iniciar un trato, aceptar trato, etc.

    // Método main para iniciar el juego desde la consola
    public static void main(String[] args) {
        Juego juego = new Juego();
        juego.iniciarJuego();
    }
}