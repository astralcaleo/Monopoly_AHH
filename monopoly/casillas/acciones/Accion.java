package monopoly.casillas.acciones;

import monopoly.casillas.Casilla;
import partida.Jugador;

public abstract class Accion extends Casilla {
    // Constructor
    public Accion(String nombre, int posicion) {
        super(nombre, posicion);       // Llama al constructor de la clase base Casilla
    }

    // Métodos heredados
    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public abstract boolean evaluarCasilla(Jugador actual, int tirada);

    // Método abstracto para imprimir la información de cada tipo de casilla
    @Override 
    public abstract void infoCasilla();

    // Método toString para representar la propiedad
    @Override
    public String toString() {return super.toString();}
}
