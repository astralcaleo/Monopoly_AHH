package partida.avatares;

import monopoly.*;
import monopoly.casillas.CasillaX;
import partida.Jugador;

import java.util.ArrayList;

public class Avatar {

    protected String id; // Identificador: una letra generada aleatoriamente.
    protected Jugador jugador; // Jugador al que pertenece este avatar.
    protected Jugador banca;
    protected CasillaX lugar; // Casilla donde se encuentra el avatar.
    protected boolean movAvanzado; // Tipo de movimiento (true = avanzado; false = normal)
    protected int turnosBloqueados;
    protected String tipo; // Tipo de avatar (por ejemplo, "pelota" o "coche")
    protected int tiradasDobles;

    // Constructor vacío
    public Avatar() {}

    // Constructor principal
    public Avatar(String tipo,Jugador jugador, CasillaX lugar, ArrayList<Avatar> avCreados) {
        this.tipo=tipo;
        this.jugador = jugador;
        this.lugar = lugar;
        this.movAvanzado = false;
        this.turnosBloqueados = 0;
        this.tiradasDobles=0;
        generarId(avCreados);
    }

    public void moverAvatar(ArrayList<ArrayList<CasillaX>> casillas, int valorTirada) {
        this.lugar.eliminarAvatar(this);

        int nuevaPosicion = (this.lugar.getPosicion() + valorTirada) % 40;
        if ((this.lugar.getPosicion() + valorTirada) >= 40) {
            this.jugador.sumarFortuna(Valor.SUMA_VUELTA);
        } 

        // Determinar la nueva casilla
        CasillaX nCasilla = null;
        for (ArrayList<CasillaX> l : casillas) {
            for (CasillaX i : l) {
                if (i.getPosicion() == nuevaPosicion) {
                    nCasilla = i;
                    break;
                }
            }
            if (nCasilla != null) break;
        }

        this.lugar = nCasilla;
        nCasilla.anhadirAvatar(this);
    }

    public void moverAvatar(ArrayList<ArrayList<CasillaX>> casillas, String cDestino) {
        this.lugar.eliminarAvatar(this);

        CasillaX nCasilla = null;
        for (ArrayList<CasillaX> l : casillas) {
            for (CasillaX i : l) {
                if (i.getNombre().equals(cDestino) && i.getPosicion() < this.lugar.getPosicion()) {
                    this.jugador.sumarFortuna(Valor.SUMA_VUELTA);
                }
                if (i.getNombre().equals(cDestino)) {
                    nCasilla = i;
                    break;
                }
            }
            if (nCasilla != null) break;
        }

        this.lugar = nCasilla;
        nCasilla.anhadirAvatar(this);
    }

    private void generarId(ArrayList<Avatar> avCreados) {
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        boolean igual = false;
        while (!igual) {
            char nuevoId = abc.charAt((int) (Math.random() * 26));
            igual = true;
            for (Avatar av : avCreados) {
                if (av.id.equals(Character.toString(nuevoId))) {
                    igual = false;
                    break;
                }
            }
            if (igual) {
                this.id = Character.toString(nuevoId);
            }
        }
    }

    public void moverEnBasico(Avatar avatar){
        avatar.movAvanzado = false;
    }

    public void moverEnAvanzadao(Avatar avatar){
        avatar.movAvanzado = true;
    }

    @Override
     public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(this.id).append(",\n");
        sb.append("tipo: ").append(this.tipo).append(",\n");
        sb.append("casilla: ").append(this.lugar.getNombre()).append(",\n");
        sb.append("jugador: ").append(this.jugador.getNombre()).append("\n");
        return sb.toString();
    }


    //GETTERS

    /*Método para obtener el identificador del avatar*/
    public String getID(){
        return this.id;
    }

    /*Método para obtener el tipo del avatar*/
    public String getTipo() {
        return this.tipo;
    }

    /*Método para obtener el jugador correspondiente al avatar*/
    public Jugador getJugador(){
        return this.jugador;
    }

    /*Método para obtener la casilla en la que se encuentra el avatar*/
    public CasillaX getLugar(){
        return this.lugar;
    }

     /*Método para obtener el modo de movimiento del avatar*/
    public boolean getmovAvanzado() {
        return this.movAvanzado;
    }

    public int getTurnosBloqueados() {
        return this.turnosBloqueados;
    }

    public int getTiradasDobles() {
        return this.tiradasDobles;
    }

    //SETTERS
    /*Método para obtener el identificador del avatar*/
    public void setID(String id) {
        this.id = id;
    }

    /*Método para obtener el tipo del avatar*/
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /*Método para modificar el jugador correspondiente al avatar*/
    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    /*Método para modificar la casilla en la que se encuentra el avatar*/
    public void setLugar(CasillaX lugar) {
        this.lugar = lugar;
    }

    public void setmovAvanzado(Jugador jugador) {
        this.movAvanzado = !this.movAvanzado;
    }

    public void setTurnosBloqueados(int turnos) {
        this.turnosBloqueados = turnos;
    }

    public void setTiradasDobles(int tiradasDobles) {
        this.tiradasDobles = tiradasDobles;
    }


}
