package monopoly.casillas;

import partida.*;
import java.util.ArrayList;

import monopoly.Valor;
import monopoly.casillas.propiedades.Solar;

public class Grupo {
    // Atributos
    private ArrayList<Solar> miembros;          // Casillas miembros del grupo.
    private String colorGrupo;                  // Color del grupo
    private int numCasillas;                    // Número de casillas del grupo.
    private ArrayList<Integer> edificios;       // Cantidad de edificios (0: casas, 1: hoteles, 2: piscinas, 3: pistas deporte)

    // Constructor para cuando el grupo está formado por DOS CASILLAS:
    public Grupo(Solar cas1, Solar cas2, String colorGrupo) {
        this.miembros = new ArrayList<Solar>();
        anhadirCasilla(cas1);
        anhadirCasilla(cas2);
        this.colorGrupo = colorGrupo;
        this.edificios = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++){this.edificios.add(0);}
    }

    // Constructor para cuando el grupo está formado por TRES CASILLAS
    public Grupo(Solar cas1, Solar cas2, Solar cas3, String colorGrupo) {
        this.miembros = new ArrayList<Solar>();
        anhadirCasilla(cas1);
        anhadirCasilla(cas2);
        anhadirCasilla(cas3);
        this.colorGrupo = colorGrupo;
        this.edificios = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++){this.edificios.add(0);}
    }

    // Getters y Setters
    public ArrayList<Solar> getMiembros() {return this.miembros;}
    public void setMiembros(ArrayList<Solar> miembros) {this.miembros = miembros;}

    public String getColor(){return this.colorGrupo;}
    public void setColorGrupo(String colorGrupo) {this.colorGrupo = colorGrupo;}

    public int getNumCasillas() {return this.numCasillas;}
    public void setNumCasillas(int numCasillas) {this.numCasillas = numCasillas;}

    public ArrayList<Integer> getEdificios() {return edificios;}
    public void setEdificios(ArrayList<Integer> edificios) {this.edificios = edificios;}
    

    // Métodos de Grupo
    // Método que añade una casilla al array de casillas miembro de un grupo
    public void anhadirCasilla(Solar miembro) {this.miembros.add(miembro); ++this.numCasillas;}

    // Método que comprueba si el jugador pasado tiene en su haber todas las casillas del grupo
    public boolean esDuenhoGrupo(Jugador jugador) {
        for(Solar i : this.miembros){if(!i.getPropietario().equals(jugador)){return false;}}
        return true;
    }

    // Método que indica la posición o el número que ocuparía cada grupo en el orden de juego
    public int posicionGrupo(){
        switch (colorGrupo) {
            case Valor.BLACK:   return 0;
            case Valor.CYAN:    return 1;
            case Valor.PURPLE:  return 2;
            case Valor.YELLOW:  return 3;
            case Valor.RED:     return 4;
            case Valor.WHITE:   return 5;
            case Valor.GREEN:   return 6;
            case Valor.BLUE:    return 7;
            default:            return 0;
        }
    }

    // Método que devuelve el String color del grupo
    public String colorGrupo(){
        switch (this.colorGrupo) {
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
}
