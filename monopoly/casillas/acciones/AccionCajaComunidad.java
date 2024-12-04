package monopoly.casillas.acciones;

import java.util.Scanner;

import monopoly.Tablero;
import monopoly.interfaz.*;
import monopoly.Juego;
import monopoly.Valor;
import monopoly.cartas.Carta;
import monopoly.cartas.CartaCajaComunidad;
import monopoly.casillas.*;
import partida.Jugador;

public class AccionCajaComunidad extends Accion{
    // Constructor
    public AccionCajaComunidad(String nombre, int posicion) {
        super(nombre, posicion);       // Llama al constructor de la clase base Casilla
    }

    // Métodos heredados
    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public boolean evaluarCasilla(Jugador actual, int tirada, Tablero tablero, int turno, Juego menu){
        ConsolaNormal consola = new ConsolaNormal();
        int numero = consola.leerEnIntervalo(actual.getNombre() + ", elige una carta entre 1 y 6:", 1, 6);

        CartaCajaComunidad carta = new CartaCajaComunidad();
        Carta cartaSeleccionada = carta.barajarCaja(numero);
        System.out.println("Acción: " + cartaSeleccionada.getAccion());
        if(cartaSeleccionada.getNombre().equals("caja1")){
            if(actual.getFortuna() < 500000f){
                System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                return false;
            } else{
                actual.sumarGastos(500000f);
                Especial parking = (Especial) menu.getTablero().encontrar_casilla("Parking");
                parking.sumarBote(500000f);
                actual.getEstadisticas().set(1, actual.getEstadisticas().get(1) + 500000);
            }
        
        } else if(cartaSeleccionada.getNombre().equals("caja2")){
            System.out.println("El jugador " + actual.getNombre() + " se dirige a la cárcel.");
            actual.encarcelar(menu.getTablero().getPosiciones());
            menu.getTablero().encontrar_casilla(actual.getAvatar().getLugar().getNombre()).eliminarAvatar(menu.getAvatares().get(menu.getTurno()));
            menu.getTablero().encontrar_casilla("Cárcel").anhadirAvatar(menu.getAvatares().get(menu.getTurno()));
            actual.getAvatar().setLugar(menu.getTablero().encontrar_casilla("Cárcel"));
            menu.getTablero().encontrar_casilla("Cárcel").getCaidas().set(menu.getTurno(), menu.getTablero().encontrar_casilla("Cárcel").getCaidas().get(menu.getTurno())+1);
            actual.getEstadisticas().set(6, actual.getEstadisticas().get(6) + 1f);
        
        } else if(cartaSeleccionada.getNombre().equals("caja3")){
            actual.getAvatar().moverAvatar(menu.getTablero().getPosiciones(),"Salida");
            menu.getTablero().encontrar_casilla("Salida").getCaidas().set(menu.getTurno(), menu.getTablero().encontrar_casilla("Salida").getCaidas().get(menu.getTurno())+1);
            actual.getAvatar().setLugar(menu.getTablero().encontrar_casilla("Salida"));
            actual.sumarFortuna(Valor.SUMA_VUELTA);
            actual.getEstadisticas().set(4, actual.getEstadisticas().get(4) + Valor.SUMA_VUELTA);
        
        } else if(cartaSeleccionada.getNombre().equals("caja4")){
            actual.sumarFortuna(2000000f);
            actual.getEstadisticas().set(5, actual.getEstadisticas().get(5) + 2000000f);
        
        } else if(cartaSeleccionada.getNombre().equals("caja5")){
            if(actual.getFortuna() < 1000000){
                System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                return false;
            } else{
                actual.sumarGastos(1000000f);
                Especial parking = (Especial) menu.getTablero().encontrar_casilla("Parking");
                parking.sumarBote(1000000f);
                actual.getEstadisticas().set(1, actual.getEstadisticas().get(1) + 1000000f);
            }
        
        } else if(cartaSeleccionada.getNombre().equals("caja6")){
            if(actual.getFortuna() < 200000*(menu.getJugadores().size())){ 
                System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                return false;
            } else{
                for(Jugador jugador : menu.getJugadores()){
                    if(!jugador.getNombre().equals(actual.getNombre())){
                        actual.sumarGastos(200000);
                        jugador.sumarFortuna(200000);
                        actual.getEstadisticas().set(1, actual.getEstadisticas().get(1) + 200000);
                    }
                }
            }
        } return true;
    }

    // Método toString para representar la propiedad
    @Override
    public String toString() {return super.toString() + "\n\tTipo: Comunidad";}

    // Método abstracto para imprimir la información de cada tipo de casilla
    @Override 
    public void infoCasilla(){toString();}
}
