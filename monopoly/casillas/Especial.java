package monopoly.casillas;

import monopoly.Tablero;
import monopoly.Juego;
import monopoly.Valor;
import partida.Jugador;
import partida.avatares.Avatar;

public class Especial extends Casilla {
    //Atributos
    private float bote;                // Dinero acumulado en la casilla de Parking

    // Constructor
    public Especial(String nombre, int posicion) {
        super(nombre, posicion);       // Llama al constructor de la clase padre Casilla
    }

    // Métodos de Especial
    // Método para añadir valor a una casilla
    public void sumarBote(float suma) {this.bote += suma;}

    // Métodos heredados
    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public boolean evaluarCasilla(Jugador actual, int tirada, Tablero tablero, int turno, Juego menu){
        if (super.getNombre().equals("IrCarcel")){
            Juego.consola.imprimir("El jugador " + actual.getNombre() + " se dirige a la cárcel.");
            actual.setEncarcelado(true);
            actual.getEstadisticas().set(6, actual.getEstadisticas().get(6) + 1f);
        } else if (super.getNombre().equals("Parking")){
            actual.sumarFortuna(bote);
            actual.getEstadisticas().set(5, actual.getEstadisticas().get(5) + bote);
            Juego.consola.imprimir("El jugador " + actual.getNombre() + " recibe un bote de " + bote + "€.");
            bote = 0;
        } return true;
    }

    // Método toString para representar la propiedad
    @Override
    public String toString() {return super.getNombre() + "\n\tTipo: Especial";}

    // Método abstracto para imprimir la información de cada tipo de casilla
    @Override
    public void infoCasilla(){
        toString();
        StringBuilder sb = new StringBuilder();
        switch (super.getNombre()) {
            case "Parking":
                sb.append("\n\tBote: ").append(bote);
                sb.append("\n\tJugadores: [");
                for(Avatar i : super.getAvatares()){sb.append(i.getJugador().getNombre()).append(", ");}
                if (super.getAvatares().size() > 0){
                    sb.deleteCharAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                } sb.append("]").append("\n");
                break;
            
            case "Cárcel":
                sb.append("\n\tSalir: ").append(0.25*Valor.SUMA_VUELTA);
                sb.append("\n\tJugadores: ");
                for(Avatar i : super.getAvatares()){
                    if(i.getJugador().getEncarcelado()){sb.append("[").append(i.getJugador().getNombre()).append(", ").append(i.getJugador().getTurnosCarcel()).append("] ");}
                } 
                break;
        }
    }
}
