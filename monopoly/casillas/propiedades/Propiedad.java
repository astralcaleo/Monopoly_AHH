package monopoly.casillas.propiedades;

import monopoly.Juego;
import monopoly.Tablero;
import monopoly.casillas.Casilla;
import monopoly.excepciones.PropiedadNoDisponibleException;
import monopoly.excepciones.PropiedadYaHipotecadaException;
import monopoly.excepciones.SaldoInsuficienteException;
import partida.Jugador;

public abstract class Propiedad extends Casilla {
    //Atributos
    private float valor;                // Precio de venta la propiedad
    private float valorInicial;         // Precio de venta al inicio de la partida la propiedad
    private float alquiler;             // Dinero que se cobra como renta
    private Jugador propietario;        // Nombre del propietario actual (null si no tiene)
    private boolean hipotecada;         // Indica si la propiedad está hipotecada
    private float hipoteca;             // Dinero que se recibe al hipotecar la propiedad
    private float rentabilidad;         // Dinero que ha sido obtenido en esta casilla mediante cobro de alquileres

    // Constructor
    public Propiedad(String nombre, int posicion, float valor, Jugador propietario) {
        super(nombre, posicion);       // Llama al constructor de la clase base Casilla
        this.valorInicial = this.valor = valor;
        this.propietario = propietario;       // Inicialmente sin propietario
        this.hipotecada = false;              // Inicialmente no hipotecada
        this.hipoteca = (this.valorInicial)/2f;
        this.rentabilidad = 0f;
        this.alquiler = 0f;
    }

    // Getters y Setters
    public float getValor() {return valor;}
    public void setValor(float valor) {this.valor = valor;}

    public float getValorInicial() {return valorInicial;}
    public void setValorInicial(float valorInicial) {this.valorInicial = valorInicial;}

    public float getAlquiler() {return alquiler;}
    public void setAlquiler(float alquiler) {this.alquiler = alquiler;}

    public Jugador getPropietario() {return propietario;}
    public void setPropietario(Jugador propietario) {this.propietario = propietario;}

    public float getHipoteca() {return hipoteca;}
    public void setHipoteca(float hipoteca) {this.hipoteca = hipoteca;}

    public boolean isHipotecada() {return hipotecada;}
    public void setHipotecada(boolean hipotecada) {this.hipotecada = hipotecada;}

    public float getRentabilidad() {return rentabilidad;}
    public void setRentabilidad(float rentabilidad) {this.rentabilidad = rentabilidad;}

    // Métodos de Propiedad
    // Devuelve true si la propiedad no tiene dueño
    public boolean estaDisponible() {return (propietario.getNombre().equals("banca"));}

    // Devuelve true si la propiedad es del jugador que lo pregunta
    public boolean perteneceAJugador(Jugador jugador) {
        if (jugador.equals(propietario)){return true;}
        return false;
    }

    // Devuelve el alquiler que se debe pagar si la casilla no está hipotecada
    public float alquiler() {
        if (hipotecada) {
            Juego.consola.imprimir("La propiedad está hipotecada. No se cobra alquiler.");
            return 0f;
        } return alquiler;
    }

    // Realiza el proceso de compra de una propiedad
    public void comprar(Jugador solicitante, Jugador banca) throws Exception{
        if (estaDisponible()) {
            if (solicitante.getFortuna() >= this.valor) {
                solicitante.sumarGastos(this.valor);
                solicitante.getEstadisticas().set(0, solicitante.getEstadisticas().get(0) + this.valor);
                this.propietario = solicitante;
                solicitante.anhadirPropiedad(this);
                banca.sumarFortuna(this.valor);
                Juego.consola.imprimir("El jugador " + propietario.getNombre() + " compra la casilla " + super.getNombre() + " por " + this.valor + "€. Su fortuna actual es " + solicitante.getFortuna() + "€.");
            } else throw new SaldoInsuficienteException("No se dispone de dinero suficiente para comprar");
        } else {throw new PropiedadNoDisponibleException("La casilla no está en venta.");}
    }

    // Hipoteca la propiedad si no lo está previamente
    public void hipotecar(Jugador solicitante) throws Exception {
        if(propietario.equals(solicitante)){
            if (!hipotecada) {
                hipotecada = true;
                Juego.consola.imprimir("La propiedad " + super.getNombre() + " ha sido hipotecada.");
                solicitante.sumarFortuna(this.hipoteca);
            } else {throw new PropiedadYaHipotecadaException("La propiedad ya está hipotecada.");}
        } else {throw new PropiedadNoDisponibleException("La propiedad no pertenece al jugador " + solicitante.getNombre() + ".");}
    }

    // Deshipoteca la propiedad si estaba hipotecada
    public void deshipotecar(Jugador solicitante) throws Exception{
        if (hipotecada) {
            float deuda = 1.1f*(this.hipoteca);
            if (solicitante.getFortuna() >= deuda) {
                solicitante.sumarGastos(deuda);
                this.propietario = solicitante;
                solicitante.anhadirPropiedad(this);
                hipotecada = false;
                Juego.consola.imprimir("La propiedad " + super.getNombre() + " ha sido deshipotecada.");
            } else {throw new SaldoInsuficienteException("El jugador " + solicitante.getNombre() + " no puede pagar la cantidad para deshipotecar.");}
        } else {Juego.consola.imprimir("La propiedad no está hipotecada.");}
    }

    // Métodos heredados
    // Método abstracto para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public abstract boolean evaluarCasilla(Jugador actual, int tirada, Tablero tablero, int turno, Juego menu);

    // Método abstracto para imprimir la información de cada tipo de casilla
    @Override 
    public abstract void infoCasilla();

    // Método toString para representar la propiedad
    @Override
    public String toString() {
        return super.toString() + 
               "\n\tValor de compra: " + valor + 
               "\n\tAlquiler: " + alquiler + 
               "\n\tPropietario: " + ((propietario.getNombre().equals("banca"))? "Banca" : propietario.getNombre()) + 
               "\n\tHipotecada: " + (hipotecada ? "SI" : "NO");
    }
}
