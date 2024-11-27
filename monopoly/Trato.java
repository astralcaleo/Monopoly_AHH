package monopoly;

import partida.*;

public class Trato {
    private String ID;
    private Jugador propositor;
    private Jugador destinatario;
    private String oferta;
    private String demanda;
    static int id_actual = 0;

    int generarID() {
        return ++id_actual;
    }

    public Trato(){

    }

    public Trato(Jugador propositor, Jugador destinatario, String oferta, String demanda){
        this.ID = "trato" + generarID();
        this.propositor = propositor;
        this.destinatario = destinatario;
        this.oferta = oferta;
        this.demanda = demanda;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("id: ").append(this.ID).append("\n");
        sb.append("jugadorPropone: ").append(this.propositor).append(",\n");
        sb.append("trato: cambiar (").append(this.oferta + "," + this.demanda + ")").append("\n");
        sb.append("}\n");
        return sb.toString();
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public Jugador getPropositor() {
        return propositor;
    }

    public void setPropositor(Jugador propositor) {
        this.propositor = propositor;
    }

    public Jugador getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Jugador destinatario) {
        this.destinatario = destinatario;
    }

    public String getOferta() {
        return oferta;
    }

    public void setOferta(String oferta) {
        this.oferta = oferta;
    }

    public String getDemanda() {
        return demanda;
    }

    public void setDemanda(String demanda) {
        this.demanda = demanda;
    }

}
