package monopoly.casillas;

import java.util.ArrayList;

import partida.Jugador;
import partida.avatares.Avatar;

public abstract class Casilla {
    // Atributos
    private String nombre;              // Nombre de la casilla
    private int posicion;               // Posición en el tablero (entre 1 y 40)
    private ArrayList<Integer> caidas;  // Contador de cuántas veces se visita la casilla
    private ArrayList<Avatar> avatares; // Avatares que están situados en la casilla. 

    // Constructor
    public Casilla(String nombre, int posicion) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.caidas = new ArrayList<Integer>();
        this.avatares = new ArrayList<Avatar>();
    }

    // Getters y Setters
    public String getNombre() {return nombre;}
    //public void setNombre(String nombre) {this.nombre = nombre;}
    public int getPosicion() {return posicion;}
    public ArrayList<Integer> getCaidas() {return caidas;}
    public ArrayList<Avatar> getAvatares() {return avatares;}

    // Métodos de Casilla
    // Método utilizado para añadir un avatar al array de avatares en casilla.
    public void anhadirAvatar(Avatar avatar) {avatares.add(avatar);}

    // Método utilizado para eliminar un avatar del array de avatares en casilla.
    public void eliminarAvatar(Avatar avatar) {this.avatares.remove(avatar);}

    // Método que devuelve true si un avatar está situado en la casilla
    public boolean estaAvatar(Avatar avatar) {
        if (avatares.contains(avatar)) {return true;}
        return false;
    }

    // Método utilizado para conocer las veces que la casilla ha sido visitada
    public int frecuenciaVisita(){
        int fv = 0;
        for (Integer i : caidas) {fv += i;}
        return fv;
    }

    // Método que suma una caída de un jugador en la casilla
    public void sumarVisita(Avatar avatar) {this.caidas.set(avatares.indexOf(avatar), caidas.get(avatares.indexOf(avatar)) + 1);}

    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    public abstract boolean evaluarCasilla(Jugador actual, int tirada);

    // Método abstracto para imprimir la información de cada tipo de casilla
    public abstract void infoCasilla();

    // Método toString para mostrar información básica de la casilla
    @Override
    public String toString() {
        return nombre;
    }
}
