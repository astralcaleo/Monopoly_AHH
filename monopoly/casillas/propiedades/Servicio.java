package monopoly.casillas.propiedades;

import monopoly.Valor;
import partida.Jugador;

public class Servicio extends Propiedad {
    //Atributos
    private float alquiler;            // Dinero que se cobra como renta
    private Jugador propietario;       // Nombre del propietario actual (null si no tiene)

    // Constructor
    public Servicio(String nombre, int posicion, float valor) {
        super(nombre, posicion, valor);       // Llama al constructor de la clase padre Propiedad
        this.propietario = super.getPropietario();
        this.alquiler = super.getAlquiler();
    }

    // Métodos heredados
    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public boolean evaluarCasilla(Jugador actual, int tirada){
        if (!this.propietario.equals(null) && !this.propietario.equals(actual)) {
            if(!super.isHipotecada()){
                float factor = super.getValor()/200f;
                Servicio imp1 = new Servicio("Serv1", 12, 0.75f*(Valor.SUMA_VUELTA)); 
                Servicio imp2 = new Servicio("Serv2", 28, 0.75f*(Valor.SUMA_VUELTA));
                if (this.propietario.getPropiedades().contains(imp1)){
                    if(this.propietario.getPropiedades().contains(imp2)){
                        alquiler = 10 * tirada * factor;
                    } else{alquiler = 4 * tirada * factor;}
                } else{alquiler = factor;}
                
                if (actual.getFortuna() < alquiler) {
                    System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                    System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                    return false; 
                } else {
                    actual.sumarGastos(alquiler);
                    this.propietario.sumarFortuna(alquiler);

                    System.out.println("El jugador " + actual.getNombre() + " paga " + alquiler + "€ al jugador " + this.propietario.getNombre());
                }
            } else {System.out.println("La casilla de servicio se encuentra hipotecada. No se cobrarán alquileres.");}
        } return true;
    }

    // Método toString para representar la propiedad
    @Override
    public String toString() {return super.toString() + "\n\tTipo: Servicio";}

    // Método abstracto para imprimir la información de cada tipo de casilla
    @Override
    public void infoCasilla(){toString();}
}
