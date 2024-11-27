package monopoly.edificios;

import partida.*;
import java.util.ArrayList;

import monopoly.Valor;
import monopoly.casillas.Casilla;
import monopoly.casillas.Grupo;

public class Edificio {
    
    //Atributos:
    private String ID;
    private String tipo;
    private Jugador duenho;
    private Casilla ubicacion;
    private Grupo grupo;
    private int cantidad = 1;
    private float coste;
    
    //Constructores:
    public Edificio() {
    }//Parámetros vacíos
    
    public Edificio(String tipo, Jugador duenho, Casilla ubicacion, ArrayList<Edificio> listaEdificios) {
        this.tipo = tipo;
        this.duenho = duenho;
        this.ubicacion = ubicacion;
        this.grupo = ubicacion.getGrupo();
        generarId(listaEdificios);
        calcularCoste();
    }

    private void generarId(ArrayList<Edificio> listaEdificios) {
        int num = 0;
        StringBuilder sb = new StringBuilder();
        if(!listaEdificios.isEmpty()){
            for(Edificio ed : listaEdificios){
                if(ed.grupo.equals(this.grupo) && ed.tipo.equals(this.tipo)){this.cantidad=ed.cantidad+1; num=1;}
            }
        }if(num==0){this.cantidad = 1;}
        
        sb.append(this.tipo).append("-").append(this.colorGrupo()).append(this.cantidad);
        this.ID = sb.toString();
    }

    private void calcularCoste(){
        if(this.tipo.equals("casa") || this.tipo.equals("hotel"))
            {this.coste = this.ubicacion.getValor_inicial()*0.6f;}
        else if(this.tipo.equals("piscina"))
            {this.coste = this.ubicacion.getValor_inicial()*0.4f;}
        else {this.coste = this.ubicacion.getValor_inicial()*1.25f;}
    }

    private String colorGrupo(){
        switch (this.grupo.getColor()) {
            case Valor.BLACK:
                return "negro";
            case Valor.RED:
                return "rojo";
            case Valor.GREEN:
                return "verde";
            case Valor.YELLOW:
                return "amarillo";
            case Valor.BLUE:
                return "azul";
            case Valor.PURPLE:
                return "rosa";
            case Valor.CYAN:
                return "cian";
            case Valor.WHITE:
                return "blanco";
            default:
                return "La casilla no pertenece a ningún grupo.";
        }

    }

    //Para imprimir la información del avatar, modificamos el método toString().
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(this.ID).append(",\n");
        sb.append("propietario: ").append(this.duenho.getNombre()).append(",\n");
        sb.append("casilla: ").append(this.ubicacion.getNombre()).append(",\n");
        sb.append("grupo: ").append(this.colorGrupo()).append(",\n");
        sb.append("coste: ").append(this.coste);
        return sb.toString();
    }

    //GETTERS

    public int getCantidad() {
        return cantidad;
    }

    public Jugador getDuenho() {
        return duenho;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public String getID() {
        return ID;
    }

    public String getTipo() {
        return tipo;
    }

    public Casilla getUbicacion() {
        return ubicacion;
    }

}
