package monopoly.casillas;

import partida.Jugador;

public class Impuesto extends Casilla {
    //Atributos
    private float impuesto;            // Impuesto a pagar al caer en la casilla

    // Constructor
    public Impuesto(String nombre, int posicion, float impuesto) {
        super(nombre, posicion);       // Llama al constructor de la clase padre Casilla
        this.impuesto = impuesto;

    }

    // Métodos heredados
    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public boolean evaluarCasilla(Jugador actual, int tirada){
        if (actual.getFortuna() < this.impuesto) {
            System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
            return false; 
        } else {
            actual.sumarGastos(this.impuesto);
            System.out.println("El jugador " + actual.getNombre() + " paga " + this.impuesto + "€ en impuestos a la banca.");
            return true;
        }
    }

    // Método toString para representar la propiedad
    @Override
    public String toString() {return super.getNombre() + "\n\tTipo: Impuesto" + "\n\tImpueso a pagar: " + impuesto;}

    // Método abstracto para imprimir la información de cada tipo de casilla
    @Override
    public void infoCasilla(){toString();}
}
