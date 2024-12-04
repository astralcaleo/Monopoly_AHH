package monopoly.casillas.acciones;

import java.util.Scanner;

import monopoly.Juego;
import monopoly.Tablero;
import monopoly.cartas.Carta;
import monopoly.cartas.CartaSuerte;
import monopoly.interfaz.ConsolaNormal;
import partida.Jugador;

public class AccionSuerte extends Accion{
    // Constructor
    public AccionSuerte(String nombre, int posicion) {
        super(nombre, posicion);       // Llama al constructor de la clase base Casilla
    }

    // Métodos heredados
    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public boolean evaluarCasilla(Jugador actual, int tirada, Tablero tablero, int turno, Juego menu){
        ConsolaNormal consola = new ConsolaNormal();
        int numero = consola.leerEnIntervalo(actual.getNombre() + ", elige una carta entre 1 y 6:", 1, 6);
        
        CartaSuerte carta = new CartaSuerte();
        Carta cartaSeleccionada = carta.barajarSuerte(numero);
        System.out.println("Acción: " + cartaSeleccionada.getAccion());
        if(cartaSeleccionada.getNombre().equals("suerte1")){
            actual.getAvatar().moverAvatar(tablero.getPosiciones(),"Trans1");
            tablero.encontrar_casilla("Trans1").getCaidas().set(turno, tablero.encontrar_casilla("Trans1").getCaidas().get(turno)+1);
            actual.getAvatar().setLugar(tablero.encontrar_casilla("Trans1"));
        
        } else if(cartaSeleccionada.getNombre().equals("suerte2")){
            actual.getAvatar().moverAvatar(tablero.getPosiciones(),"Solar15");
            tablero.encontrar_casilla("Solar15").getCaidas().set(turno, tablero.encontrar_casilla("Solar15").getCaidas().get(turno)+1);
            actual.getAvatar().setLugar(tablero.encontrar_casilla("Solar15"));
        
        } else if(cartaSeleccionada.getNombre().equals("suerte3")){
            actual.sumarFortuna(500000);
            actual.getEstadisticas().set(5, actual.getEstadisticas().get(5) + 500000);
        
        } else if(cartaSeleccionada.getNombre().equals("suerte4")){
            actual.getAvatar().moverAvatar(tablero.getPosiciones(),"Solar3");
            tablero.encontrar_casilla("Solar3").getCaidas().set(turno, tablero.encontrar_casilla("Solar3").getCaidas().get(turno)+1);
            actual.getAvatar().setLugar(tablero.encontrar_casilla("Solar3"));
        
        } else if(cartaSeleccionada.getNombre().equals("suerte5")){
            System.out.println("El jugador " + actual.getNombre() + " se dirige a la cárcel.");
            actual.encarcelar(tablero.getPosiciones());
            tablero.encontrar_casilla(actual.getAvatar().getLugar().getNombre()).eliminarAvatar(super.getAvatares().get(turno));
            tablero.encontrar_casilla("Cárcel").anhadirAvatar(super.getAvatares().get(turno));
            actual.getAvatar().setLugar(tablero.encontrar_casilla("Cárcel"));
            tablero.encontrar_casilla("Cárcel").getCaidas().set(turno, tablero.encontrar_casilla("Cárcel").getCaidas().get(turno)+1);
            actual.getEstadisticas().set(6, actual.getEstadisticas().get(6) + 1f);
        
        } else if(cartaSeleccionada.getNombre().equals("suerte6")){
            actual.sumarFortuna(1000000f);
            actual.getEstadisticas().set(5, actual.getEstadisticas().get(5) + 1000000f);
        } return true;
    }

    // Método toString para representar la propiedad
    @Override
    public String toString() {return super.toString() + "\n\tTipo: Suerte";}

    // Método abstracto para imprimir la información de cada tipo de casilla
    @Override 
    public void infoCasilla(){toString();}
}
