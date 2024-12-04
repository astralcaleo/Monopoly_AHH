package monopoly.casillas;

import partida.*;
import java.util.ArrayList;

import monopoly.Valor;


public class Grupo {

    //Atributos
    private ArrayList<Casilla> miembros; //Casillas miembros del grupo.
    private String colorGrupo; //Color del grupo
    private int numCasillas; //Número de casillas del grupo.
    private ArrayList<Integer> edificios;

    //Constructor vacío.
    public Grupo() {}

    /*Constructor para cuando el grupo está formado por DOS CASILLAS:
    * Requiere como parámetros las dos casillas miembro y el color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, String colorGrupo) {
        this.miembros = new ArrayList<Casilla>();
        anhadirCasilla(cas1);
        anhadirCasilla(cas2);
        this.colorGrupo = colorGrupo;
        this.edificios = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++){this.edificios.add(0);}
    }

    /*Constructor para cuando el grupo está formado por TRES CASILLAS:
    * Requiere como parámetros las tres casillas miembro y el color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, Casilla cas3, String colorGrupo) {
        this.miembros = new ArrayList<Casilla>();
        anhadirCasilla(cas1);
        anhadirCasilla(cas2);
        anhadirCasilla(cas3);
        this.colorGrupo = colorGrupo;
        this.edificios = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++){this.edificios.add(0);}
    }

    /* Método que añade una casilla al array de casillas miembro de un grupo.
    * Parámetro: casilla que se quiere añadir.
     */
    public void anhadirCasilla(Casilla miembro) {
        this.miembros.add(miembro);
        ++this.numCasillas;
    }

    /*Método que comprueba si el jugador pasado tiene en su haber todas las casillas del grupo:
    * Parámetro: jugador que se quiere evaluar.
    * Valor devuelto: true si es dueño de todas las casillas del grupo, false en otro caso.
     */
    public boolean esDuenhoGrupo(Jugador jugador) {
        for(Casilla i : this.miembros){
            if(!i.getDuenho().getNombre().equals(jugador.getNombre())){return false;}
        } return true;
    }

    /*Método que indica la posición o el número que ocuparía cada grupo en el orden de juego */
    public int posicionGrupo(){
        switch (colorGrupo) {
            case Valor.BLACK: return 0;
            case Valor.CYAN: return 1;
            case Valor.PURPLE: return 2;
            case Valor.YELLOW: return 3;
            case Valor.RED: return 4;
            case Valor.WHITE: return 5;
            case Valor.GREEN: return 6;
            case Valor.BLUE: return 7;
            default: return 0;
        }
    }

    //GETTERS
    /*Método para obtener la lista de casillas del grupo*/
    public ArrayList<Casilla> getMiembros() {
        return this.miembros;
    }

    /*Método para obtener el nombre (color) del grupo*/
    public String getColor(){
        return this.colorGrupo;
    }

    /*Método para obtener el número de casillas del grupo*/
    public int getNumCasillas() {
        return this.numCasillas;
    }

    public ArrayList<Integer> getEdificios() {
        return edificios;
    }

    //SETTERS
    /*Método para modificar la lista de casillas del grupo*/
    public void setMiembros(ArrayList<Casilla> miembros) {
        this.miembros = miembros;
    }

    /*Método para modificar el nombre (color) del grupo*/
    public void setColorGrupo(String colorGrupo) {
        this.colorGrupo = colorGrupo;
    }

    /*Método para modificar el número de casillas del grupo*/
    public void setNumCasillas(int numCasillas) {
        this.numCasillas = numCasillas;
    }

    public void setEdificios(ArrayList<Integer> edificios) {
        this.edificios = edificios;
    }
}
