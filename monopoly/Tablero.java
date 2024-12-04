package monopoly;

import partida.*;
import partida.avatares.Avatar;

import java.util.ArrayList;
import java.util.HashMap;

import monopoly.casillas.acciones.AccionCajaComunidad;
import monopoly.casillas.acciones.AccionSuerte;
import monopoly.casillas.*;
import monopoly.casillas.propiedades.*;


public class Tablero {
    //Atributos.
    private ArrayList<ArrayList<Casilla>> posiciones; //Posiciones del tablero: se define como un arraylist de arraylists de casillas (uno por cada lado del tablero).
    private HashMap<String, Grupo> grupos; //Grupos del tablero, almacenados como un HashMap con clave String (será el color del grupo).
    private Jugador banca; //Un jugador que será la banca.

    private static Tablero instance;

    //Constructor: únicamente le pasamos el jugador banca (que se creará desde el menú).
    public Tablero(Jugador banca) {
        this.banca = banca;
        this.posiciones = new ArrayList<ArrayList<Casilla>>();
        this.grupos = new HashMap<String, Grupo>();
        this.generarCasillas();
    }

    //Método para crear todas las casillas del tablero. Formado a su vez por cuatro métodos (1/lado).
    private void generarCasillas() {
        this.insertarLadoSur();
        this.insertarLadoOeste();
        this.insertarLadoNorte();
        this.insertarLadoEste();
    }
    

    //Método para insertar las casillas del lado sur.
    private void insertarLadoSur() {
        ArrayList<Casilla> ladoSur = new ArrayList<Casilla>();
    
        ladoSur.add(new Especial("Salida", 0));
        ladoSur.add(new AccionCajaComunidad("Caja", 2)); 
        ladoSur.add(new Impuesto("Imp1", 4, Valor.SUMA_VUELTA/2f)); 
        ladoSur.add(new Transporte("Trans1", 5, Valor.SUMA_VUELTA));
        ladoSur.add(new AccionSuerte("Suerte", 7)); 
        
        Solar solar1 = new Solar("Solar1", 1, 600000f); 
        Solar solar2 = new Solar("Solar2", 3, 600000f); 
        Grupo grupoNegro = new Grupo(solar1, solar2, Valor.BLACK);
        this.grupos.put(Valor.BLACK, grupoNegro);
        solar1.setGrupo(grupoNegro);
        solar2.setGrupo(grupoNegro);


        Solar solar3 = new Solar("Solar3", 6, 520000f); 
        Solar solar4 = new Solar("Solar4", 8, 520000f); 
        Solar solar5 = new Solar("Solar5", 9, 520000f); 
        Grupo grupoCian = new Grupo(solar3, solar4, solar5, Valor.CYAN);
        this.grupos.put(Valor.CYAN, grupoCian);
        solar3.setGrupo(grupoCian);
        solar4.setGrupo(grupoCian);
        solar5.setGrupo(grupoCian);

        ladoSur.add(solar1);
        ladoSur.add(solar2);
        ladoSur.add(solar3);
        ladoSur.add(solar4);
        ladoSur.add(solar5);
        
        this.posiciones.add(ladoSur);
    }

    //Método que inserta casillas del lado oeste.
    private void insertarLadoOeste() {
        ArrayList<Casilla> ladoOeste = new ArrayList<Casilla>();
    
        ladoOeste.add(new Especial("Cárcel", 10)); 
        ladoOeste.add(new Servicio("Serv1", 12, 0.75f*(Valor.SUMA_VUELTA))); 
        ladoOeste.add(new Transporte("Trans2", 15, Valor.SUMA_VUELTA)); 
        ladoOeste.add(new AccionCajaComunidad("Caja", 17));

        Solar solar6 = new Solar("Solar6", 11, 680000f); 
        Solar solar7 = new Solar("Solar7", 13, 680000f); 
        Solar solar8 = new Solar("Solar8", 14, 680000f); 
        Grupo grupoRosa = new Grupo(solar6, solar7, solar8, Valor.PURPLE);
        this.grupos.put(Valor.PURPLE, grupoRosa);
        solar6.setGrupo(grupoRosa);
        solar7.setGrupo(grupoRosa);
        solar8.setGrupo(grupoRosa);


        Solar solar9 = new Solar("Solar9", 16, 885000f); 
        Solar solar10 = new Solar("Solar10", 18, 885000f);
        Solar solar11 = new Solar("Solar11", 19, 885000f);
        Grupo grupoAmarillo = new Grupo(solar9, solar10, solar11, Valor.YELLOW);
        this.grupos.put(Valor.YELLOW, grupoAmarillo);
        solar9.setGrupo(grupoAmarillo);
        solar10.setGrupo(grupoAmarillo);
        solar11.setGrupo(grupoAmarillo);

        ladoOeste.add(solar6);
        ladoOeste.add(solar7);
        ladoOeste.add(solar8);
        ladoOeste.add(solar9);
        ladoOeste.add(solar10);
        ladoOeste.add(solar11);

        this.posiciones.add(ladoOeste);
    }

    //Método para insertar las casillas del lado norte.
    private void insertarLadoNorte() {
        ArrayList<Casilla> ladoNorte = new ArrayList<Casilla>();
    
        ladoNorte.add(new Especial("Parking", 20)); 
        ladoNorte.add(new AccionSuerte("Suerte", 22));
        ladoNorte.add(new Transporte("Trans3", 25, Valor.SUMA_VUELTA)); 
        ladoNorte.add(new Servicio("Serv2", 28, 0.75f*(Valor.SUMA_VUELTA))); 

        Solar solar12 = new Solar("Solar12", 21, 1150000f);
        Solar solar13 = new Solar("Solar13", 23, 1150000f);
        Solar solar14 = new Solar("Solar14", 24, 1150000f);
        Grupo grupoRojo = new Grupo(solar12, solar13, solar14, Valor.RED);
        this.grupos.put(Valor.RED, grupoRojo);
        solar12.setGrupo(grupoRojo);
        solar13.setGrupo(grupoRojo);
        solar14.setGrupo(grupoRojo);
        
        Solar solar15 = new Solar("Solar15", 26, 1500000f);
        Solar solar16 = new Solar("Solar16", 27, 1500000f);
        Solar solar17 = new Solar("Solar17", 29, 1500000f);
        Grupo grupoBlanco = new Grupo(solar15, solar16, solar17, Valor.WHITE);
        this.grupos.put(Valor.WHITE, grupoBlanco);
        solar15.setGrupo(grupoBlanco);
        solar16.setGrupo(grupoBlanco);
        solar17.setGrupo(grupoBlanco);

        ladoNorte.add(solar12);
        ladoNorte.add(solar13);
        ladoNorte.add(solar14);
        ladoNorte.add(solar15);
        ladoNorte.add(solar16);
        ladoNorte.add(solar17);

        this.posiciones.add(ladoNorte);
    }

    //Método que inserta las casillas del lado este.
    private void insertarLadoEste() {
        ArrayList<Casilla> ladoEste = new ArrayList<Casilla>();
    
        ladoEste.add(new Especial("IrCarcel", 30));  
        ladoEste.add(new AccionCajaComunidad("Caja", 33)); 
        ladoEste.add(new Transporte("Trans4", 35, Valor.SUMA_VUELTA)); 
        ladoEste.add(new AccionSuerte("Suerte", 36)); 
        ladoEste.add(new Impuesto("Imp2", 38, Valor.SUMA_VUELTA));

        Solar solar18 = new Solar("Solar18", 31, 1950000f);
        Solar solar19 = new Solar("Solar19", 32, 1950000f);
        Solar solar20 = new Solar("Solar20", 34, 1950000f);
        Grupo grupoVerde = new Grupo(solar18, solar19, solar20, Valor.GREEN);
        this.grupos.put(Valor.GREEN, grupoVerde);
        solar18.setGrupo(grupoVerde);
        solar19.setGrupo(grupoVerde);
        solar20.setGrupo(grupoVerde);


        Solar solar21 = new Solar("Solar21", 37, 3850000f);
        Solar solar22 = new Solar("Solar22", 39, 3850000f);
        Grupo grupoAzul = new Grupo(solar21, solar22, Valor.BLUE);
        this.grupos.put(Valor.BLUE, grupoAzul);
        solar21.setGrupo(grupoAzul);
        solar22.setGrupo(grupoAzul);

        ladoEste.add(solar18);
        ladoEste.add(solar19);
        ladoEste.add(solar20);
        ladoEste.add(solar21);
        ladoEste.add(solar22);

        this.posiciones.add(ladoEste);
    }

    private void separador(StringBuilder output){
        output.append(" ");
        for (int i = 0; i < 16; i++) {
            output.append("\u2500");
        }
        output.append(" ");
    }

    private String espacios(Casilla cas){
        int i = 16 - (cas.getNombre()).length() - cas.getAvatares().size();
        if(cas.getAvatares().size()>0){
            --i;
        }
        return " ".repeat(i);
    }
    
    //Para imprimir el tablero, modificamos el método toString().
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for(int pos=0;pos<=10;pos++){
            separador(output);
        }
        output.append(System.lineSeparator());
        for(int pos=20;pos<=30;pos++){
            Casilla cas = this.encontrar_casilla(pos);
            Grupo grup;
            if(cas instanceof Solar){
                Solar solar = (Solar) cas;
                grup = solar.getGrupo();
            } else{grup = null;}
            
            String color = grup != null ? grup.getColor() : "Sin Grupo";
            switch (color) {
                case Valor.WHITE:
                    output.append("| " + Valor.WHITE + cas.getNombre() + Valor.RESET + espacios(cas));
                    if (!cas.getAvatares().isEmpty()) {
                        output.append("&");
                        for (Avatar avatar : cas.getAvatares()){output.append(avatar.getID());}
                    }
                    break;

                case Valor.RED:
                    output.append("| " + Valor.RED + cas.getNombre()+ Valor.RESET+espacios(cas));
                    if (!cas.getAvatares().isEmpty()) {
                        output.append("&");
                        for (Avatar avatar : cas.getAvatares()) {output.append(avatar.getID());}
                    }
                    break;

                default:
                    output.append("| "+ cas.getNombre()+ espacios(cas));
                    if (!cas.getAvatares().isEmpty()) {
                        output.append("&");
                        for (Avatar avatar : cas.getAvatares()) {output.append(avatar.getID());}
                    }
            }
            
        }
        output.append("|");
        output.append(System.lineSeparator());

        for(int pos=0;pos<=10;pos++){
            separador(output);
        }
        output.append(System.lineSeparator());
        int pos2=31;
        for(int pos1=19;pos1>=11;pos1--){
                Casilla cas1 = encontrar_casilla(pos1);
                Casilla cas2 = encontrar_casilla(pos2);
                
                Grupo grup1, grup2;
                if(cas1 instanceof Solar){
                    Solar solar = (Solar) cas1;
                    grup1 = solar.getGrupo();
                } else{grup1 = null;}
                
                if(cas2 instanceof Solar){
                    Solar solar = (Solar) cas2;
                    grup2 = solar.getGrupo();
                } else{grup2 = null;}

                String color1 = grup1 != null ? grup1.getColor() : "Sin Grupo";
                String color2 = grup2 != null ? grup2.getColor() : "Sin Grupo";
                switch (color1) {

                    case Valor.YELLOW:
                        output.append("| " + Valor.YELLOW + cas1.getNombre()+ Valor.RESET+espacios(cas1));
                        if (!cas1.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas1.getAvatares() ) {
                                output.append(avatar.getID());
                            }
                        }
                        output.append("|");
                        break;

                    case Valor.PURPLE:
                        output.append("| " + Valor.PURPLE + cas1.getNombre()+ Valor.RESET+espacios(cas1));
                        if (!cas1.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas1.getAvatares() ) {
                                output.append(avatar.getID());
                            }
                        }
                        output.append("|");
                        break;
                    
                    default:
                        output.append("| "+ cas1.getNombre()+ espacios(cas1));
                        if (!cas1.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas1.getAvatares() ) {
                                output.append(avatar.getID());
                            }
                        }
                        output.append("|");
                        break;

                }
                output.append(" ".repeat(161));
                switch (color2) {

                    case Valor.GREEN:
                        output.append("| " + Valor.GREEN + cas2.getNombre()+ Valor.RESET+ espacios(cas2));
                        if (!cas2.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas2.getAvatares() ) {
                                output.append(avatar.getID());
                            }
                        }
                        output.append("|");
                        break;

                    case Valor.BLUE:
                        output.append("| " + Valor.BLUE + cas2.getNombre()+ Valor.RESET+ espacios(cas2));
                        if (!cas2.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas2.getAvatares()) {
                                output.append(avatar.getID());
                            }
                        }
                        output.append("|");
                        break;

                    default:
                        output.append("| "+ cas2.getNombre()+ espacios(cas2));
                        if (!cas2.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas2.getAvatares()) {
                                output.append(avatar.getID());
                            }
                        }
                        output.append("|");
                        break;
                }
                
                output.append(System.lineSeparator());
                if(pos1!=11){
                    separador(output);
                    output.append(" ".repeat(162));
                    separador(output);
                    output.append(System.lineSeparator());
                }

                pos2++;
            }
            

        for(int pos=0;pos<=10;pos++){
            separador(output);
        }
        output.append(System.lineSeparator());
        for(int pos=10;pos>=0;pos--){
            Casilla cas = this.encontrar_casilla(pos);
            Grupo grup;
            if(cas instanceof Solar){
                Solar solar = (Solar) cas;
                grup = solar.getGrupo();
            } else{grup = null;}
            String color = grup != null ? grup.getColor() : "Sin Grupo";
                switch (color) {
                    case Valor.CYAN:
                        output.append("| " + Valor.CYAN + cas.getNombre()+ Valor.RESET+espacios(cas));
                        if (!cas.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas.getAvatares()) {
                                output.append(avatar.getID());
                            }
                        }
                        break;

                    case Valor.BLACK:
                        output.append("| " + Valor.BLACK + cas.getNombre()+ Valor.RESET+espacios(cas));
                        if (!cas.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas.getAvatares()) {
                                output.append(avatar.getID());
                            }
                        }
                        break;
                    
                    default:
                        output.append("| "+ cas.getNombre()+ espacios(cas));
                        if (!cas.getAvatares().isEmpty()) {
                            output.append("&");
                            for (Avatar avatar : cas.getAvatares()) {
                                output.append(avatar.getID());
                            }
                        }   
                }
        }
            output.append("|");
            output.append(System.lineSeparator());
    
            for(int pos=0;pos<=10;pos++){
                separador(output);
            }
            output.append(System.lineSeparator());

            return output.toString(); 
        
    }
        
    
    //Método usado para buscar la casilla con el nombre pasado como argumento:
    public Casilla encontrar_casilla(String nombre) {
        for (ArrayList<Casilla> lado : this.posiciones) {
            for (Casilla i : lado) {
                if (i.getNombre().equals(nombre)) {return i;}
            }
        }
        System.out.println("Casilla no encontrada");
        return null;
    }

    //Método usado para buscar la casilla con la posición pasada como argumento:
    public Casilla encontrar_casilla(int posicion) {
        for (ArrayList<Casilla> lado : this.posiciones) {
            for (Casilla i : lado) {
                if (i.getPosicion() == posicion) {return i;}
            }
        }
        System.out.println("Casilla no encontrada");
        return null;
    }



    //GETTERS
    /*Método para obtener el listado de casillas*/
    public ArrayList<ArrayList<Casilla>> getPosiciones(){
        return this.posiciones;
    }

    /*Método para obtener el listado de grupos*/
    public HashMap<String, Grupo> getGrupos() {
        return this.grupos;
    }

    /*Método para obtener el jugador "banca"*/
    public Jugador getBanca() {
        return this.banca;
    }

    public static Tablero getInstance(Jugador banca) {
        if (instance == null) {
            instance = new Tablero(banca);
        }
        return instance;
    }

    //SETTERS
    /*Método para modificar el listado de casillas*/
    public void setPosiciones(ArrayList<ArrayList<Casilla>> posiciones) {
        this.posiciones = posiciones;
    }

    /*Método para modificar el listado de grupos*/
    public void setGrupos(HashMap<String, Grupo> grupos) {
        this.grupos = grupos;
    }

    /*Método para modificar el jugador "banca"*/
    public void setBanca(Jugador banca) {
        this.banca = banca;
    }
}
