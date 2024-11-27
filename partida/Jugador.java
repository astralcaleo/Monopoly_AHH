package partida;

import java.util.ArrayList;

import monopoly.*;
import monopoly.casillas.Casilla;
import monopoly.edificios.Edificio;
import partida.avatares.Avatar;


public class Jugador {

    //Atributos:
    private String nombre; //Nombre del jugador
    private Avatar avatar; //Avatar que tiene en la partida.
    private float fortuna; //Dinero que posee.
    private float gastos; //Gastos realizados a lo largo del juego.
    private boolean enCarcel; //Será true si el jugador está en la carcel
    private int tiradasCarcel; //Cuando está en la carcel, contará las tiradas sin éxito que ha hecho allí para intentar salir (se usa para limitar el numero de intentos).
    private int vueltas; //Cuenta las vueltas dadas al tablero.
    private ArrayList<Casilla> propiedades; //Propiedades que posee el jugador.
    private ArrayList<Float> estadisticas; //Estadisticas del jugador.
    private ArrayList<Edificio> edificios; //Edificios construidos por el jugador

    //Constructor vacío. Se usará para crear la banca.
    public Jugador(){
        this.propiedades = new ArrayList<Casilla>();
    }

    /*Constructor principal. Requiere parámetros:
    * Nombre del jugador, tipo del avatar que tendrá, casilla en la que empezará y ArrayList de
    * avatares creados (usado para dos propósitos: evitar que dos jugadores tengan el mismo nombre y
    * que dos avatares tengan mismo ID). Desde este constructor también se crea el avatar.
     */
    public Jugador(String nombre, String tipoAvatar, Casilla inicio, ArrayList<Avatar> avCreados) {
        this.nombre = nombre;
        this.avatar = new Avatar(tipoAvatar, this, inicio, avCreados);
        avCreados.add(this.avatar);

        this.fortuna = Valor.FORTUNA_INICIAL;
        this.gastos = 0;
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<Casilla>();
        this.edificios = new ArrayList<Edificio>();
        this.estadisticas = new ArrayList<Float>(7);
        for (int i = 0; i < 7; i++){this.estadisticas.add(0.0f);}
    }

    //Otros métodos:
    //Método para añadir una propiedad al jugador. Como parámetro, la casilla a añadir.
    public void anhadirPropiedad(Casilla casilla) {
        this.propiedades.add(casilla);
    }

    //Método para eliminar una propiedad del arraylist de propiedades de jugador.
    public void eliminarPropiedad(Casilla casilla) {
        this.propiedades.remove(casilla);
    }

    //Método para añadir fortuna a un jugador
    //Como parámetro se pide el valor a añadir. Si hay que restar fortuna, se pasaría un valor negativo.
    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    //Método para sumar gastos a un jugador.
    //Parámetro: valor a añadir a los gastos del jugador (será el precio de un solar, impuestos pagados...).
    public void sumarGastos(float valor) {
        this.gastos += valor;
        this.fortuna -= valor;
    }

    /*Método para establecer al jugador en la cárcel. 
    * Se requiere disponer de las casillas del tablero para ello (por eso se pasan como parámetro).*/
    public void encarcelar(ArrayList<ArrayList<Casilla>> pos) {
        this.avatar.moverAvatar(pos, "Cárcel");
        this.enCarcel = true;
        this.tiradasCarcel = 0;
    }

    //Método para añadir un edificio a la lista de edificios del jugador
    public void comprarEdificio(Edificio edificio){
        this.edificios.add(edificio);
    }

    //Método para eliminar un edificio de la lista de edificios del jugador
    public void venderEdificio(Edificio edificio){
        this.edificios.remove(edificio);
    }

    //Para imprimir la información del jugador, modificamos el método toString().
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("nombre: ").append(this.nombre).append(",\n");
        sb.append("avatar: ").append(this.avatar.getID()).append(",\n");
        sb.append("fortuna: ").append(this.fortuna).append(",\n");
        sb.append("propiedades: [");
        if (propiedades == null) {
            sb.append(" - ");
        } else {
            for (Casilla i : propiedades) {
                sb.append("").append(i.getNombre());
                if(propiedades.indexOf(i) != (propiedades.size()-1)){
                    sb.append(", ");
                }
            }
        }
        /*sb.append("]\nhipotecas: [");
        if (hipotecas == null) {
            sb.append(" - ");
        } else {
            for (Casilla i : hipotecas) {
                sb.append("").append(i.getNombre()).append(", ");
            }
        }*/
        sb.append("]\nedificios: [");
        if (edificios.isEmpty()) {
            sb.append("");
        } else {
            for (int i = 0; i < edificios.size(); i++) {
                sb.append(edificios.get(i).getID());
                if (i < edificios.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]\n");
        
        return sb.toString();
    }

    //Método para imprimir las estadísticas del jugador
    public String toStringestadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n{\nestadisticas\n" + this.getNombre());
        sb.append("dineroInvertido: ").append(this.estadisticas.get(0)).append(",\n");
        sb.append("pagoTasasEImpuestos: ").append(this.estadisticas.get(1)).append(",\n");
        sb.append("pagoDeAlquileres: ").append(this.estadisticas.get(2)).append(",\n");
        sb.append("cobroDeAlquileres:").append(this.estadisticas.get(3)).append(",\n");
        sb.append("pasarPorCasillaDeSalida:").append(this.estadisticas.get(4)).append(",\n");
        sb.append("premiosInversionesOBote:").append(this.estadisticas.get(5)).append(",\n");
        sb.append("vecesEnLaCarcel:").append(this.estadisticas.get(6)).append("\n}");
        return sb.toString();
    }

    //GETTERS
    /*Método para obtener el nombre del jugador*/
    public String getNombre(){
        return this.nombre;
    }

    /*Método para obtener el avatar del jugador*/
    public Avatar getAvatar(){
        return this.avatar;
    }

    /*Método para obtener la fortuna actual del jugador*/
    public float getFortuna(){
        return this.fortuna;
    }

    /*Método para obtener los gastos realizados en todo el juego por el jugador*/
    public float getGastos() {
        return this.gastos;
    }

    /*Método para saber si el jugador está o no encarcelado*/
    public boolean getEncarcelado(){
        return this.enCarcel;
    }

    /*Método para obtener el número de tiradas sin éxito realizadas por el jugador*/
    public int getTiradasCarcel() {
        return this.tiradasCarcel;
    }

    /*Método para obtener el número de turnos que lleva el jugador en la cárcel*/
    public int getTurnosCarcel(){
        return this.tiradasCarcel + 1;
    }

    /*Método para obtener el número de vueltas completas al tablero dadas por el jugador*/
    public int getVueltas() {
        return this.vueltas;
    }

    /*Método para obtener la lista de propiedades del jugador*/
    public ArrayList<Casilla> getPropiedades() {
        return this.propiedades;
    }

    /*Método para obtener la lista de edificios del jugador*/
    public ArrayList<Edificio> getEdificios() {
        return this.edificios;
    }

    /*Método para obtener la estadísticas del jugador*/
    public ArrayList<Float> getEstadisticas(){
        return this.estadisticas;
    }

    //SETTERS
    /*Método para modificar el nombre del jugador*/
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /*Método para modificar el avatar del jugador*/
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    /*Método para modificar la fortuna actual del jugador*/
    public void setFortuna(float fortuna) {
        this.fortuna = fortuna;
    }

    /*Método para modificar los gastos del jugador*/
    public void setGastos(float gastos) {
        this.gastos = gastos;
    }

    /*Método para modificar el estado de cárcel (dentro o fuera) del jugador*/
    public void setEncarcelado(boolean carcel){
        this.enCarcel = carcel;
    }

    /*Método para modificar el número de tiradas desde la cárcel hechas por el jugador*/
    public void setTiradasCarcel(int tiradasCarcel) {
        this.tiradasCarcel = tiradasCarcel;
    }

    /*Método para modificar el número de vueltas al tablero del jugador*/
    public void setVueltas(int vueltas) {
        this.vueltas = vueltas;
    }

    /*Método para modificar la lista de propiedades del jugador*/
    public void setPropiedades(ArrayList<Casilla> propiedades) {
        this.propiedades = propiedades;
    }

    /*Método para modificar la lista de edificios del jugador*/
    public void setEdificios(ArrayList<Edificio> edificios) {
        this.edificios = edificios;
    }

    /*Método para modificar las estadísticas del jugador*/
    public void setEstadisticas(ArrayList<Float> estadisticas){
        this.estadisticas = estadisticas;
    }
}