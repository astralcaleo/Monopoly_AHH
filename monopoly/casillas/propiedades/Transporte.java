package monopoly.casillas.propiedades;

import monopoly.Tablero;
import monopoly.Juego;
import monopoly.Valor;
import monopoly.casillas.Casilla;
import partida.Jugador;

public class Transporte extends Propiedad {
    //Atributos
    private float alquiler;            // Dinero que se cobra como renta
    private Jugador propietario;       // Nombre del propietario actual (null si no tiene)

    // Constructor
    public Transporte(String nombre, int posicion, float valor) {
        super(nombre, posicion, valor);       // Llama al constructor de la clase padre Propiedad
        this.propietario = super.getPropietario();
        this.alquiler = super.getAlquiler();
    }

    // Métodos heredados
    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public boolean evaluarCasilla(Jugador actual, int tirada, Tablero tablero, int turno, Juego menu){
        if (!this.propietario.equals(null) && !this.propietario.equals(actual)) {
            if(!super.isHipotecada()){
                float propio = 0f;
                for(Casilla cas : this.propietario.getPropiedades()){
                    if(cas instanceof Transporte){
                        propio = propio + 0.25f;
                    }
                } alquiler = Valor.SUMA_VUELTA * propio;

                if (actual.getFortuna() < alquiler) {
                    System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                    System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                    return false; 
                } else {
                    actual.sumarGastos(alquiler);
                    this.propietario.sumarFortuna(alquiler);

                    System.out.println("El jugador " + actual.getNombre() + " paga " + alquiler + "€ al jugador " + this.propietario.getNombre());
                    super.setRentabilidad(super.getRentabilidad() + alquiler);
                }
            } else{System.out.println("La casilla de transporte se encuentra hipotecada. No se cobrarán alquileres.");}
        } return true;
    }

    // Método toString para representar la propiedad
    @Override
    public String toString() {return super.toString() + "\n\tTipo: Transporte";}

    // Método abstracto para imprimir la información de cada tipo de casilla
    @Override
    public void infoCasilla(){toString();}
}
