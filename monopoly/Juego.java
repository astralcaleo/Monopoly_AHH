package monopoly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import monopoly.casillas.propiedades.*;
import monopoly.casillas.*;
import monopoly.edificios.*;
import monopoly.excepciones.*;
import monopoly.interfaz.*;
import partida.*;
import partida.avatares.*;


public class Juego implements Comando {
    //Atributos
    private ArrayList<Jugador> jugadores; //Jugadores de la partida.
    private ArrayList<Avatar> avatares; //Avatares en la partida.
    private int turno = 0; //Índice correspondiente a la posición en el arrayList del jugador (y el avatar) que tienen el turno
    private int lanzamientos = 0; //Variable para contar el número de lanzamientos de un jugador en un turno.
    private ArrayList<Integer> lanzamientostotales;
    private ArrayList<Integer> vueltastotales;
    private Tablero tablero; //Tablero en el que se juega.
    private Dado dado1; //Dos dados para lanzar y avanzar casillas.
    private Dado dado2;
    private Jugador banca; //El jugador banca.
    private boolean tirado = false; //Booleano para comprobar si el jugador que tiene el turno ha tirado o no.
    private boolean solvente; //Booleano para comprobar si el jugador que tiene el turno es solvente, es decir, si ha pagado sus deudas.
    private boolean fturno; //Booleano para comprobar si el jugador ha terminado o no.
    private boolean fin = true; //Booleano para comprobar si el juego ha terminado o no.
    private Scanner scanner; //Lectura de la línea de comandos
    private ArrayList<Edificio> edificiosConstruidos;
    private int doblesContador = 0;
    private ArrayList<ArrayList<Trato>> tratos;
    private Menu menu;
    public static ConsolaNormal consola = new ConsolaNormal();

    public Juego(Menu menu) {
        this.menu = menu;
        this.jugadores = new ArrayList<Jugador>();
        this.lanzamientostotales = new ArrayList<Integer>();
        this.vueltastotales = new ArrayList<Integer>();
        this.avatares = new ArrayList<Avatar>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.scanner = new Scanner(System.in);
        this.edificiosConstruidos = new ArrayList<Edificio>();
        this.tratos = new ArrayList<ArrayList<Trato>>();
        this.banca = new Jugador();
        this.banca.setNombre("banca");
        this.tablero = new Tablero(banca);
    }

    /*Método que realiza las acciones asociadas al comando 'crear jugador'.
    * Parámetros: nombre del jugador y tipo de avatar.
    */
   public void crearJugador(String nombre, String tipo) {
        if (this.jugadores.size()<6){
            Casilla inicio = this.tablero.encontrar_casilla(0);
            //inicio.setPosicion(0);
            Jugador nJugador = new Jugador(nombre, tipo, inicio, avatares, this);
            this.jugadores.add(nJugador);
            this.lanzamientostotales.add(0);
            this.vueltastotales.add(0);
            this.tablero.encontrar_casilla(0).anhadirAvatar(nJugador.getAvatar());
            for (int i = 0; i < 40; i++) {
                this.tablero.encontrar_casilla(i).getCaidas().add(0);
            }
            consola.imprimir("nombre: " + nJugador.getNombre() + ",\navatar: " + nJugador.getAvatar().getID());
        } else {
            consola.imprimir("Número máximo de jugadores alcanzado. No se pueden crear jugadores nuevos.");
        }
    }

    // Método para pedir y analizar los comandos en cada turno
    public void Turnos(){
        String comando;
        do{
            if (this.avatares.get(this.turno).getTurnosBloqueados() > 0) {
                this.avatares.get(this.turno).setTurnosBloqueados(this.avatares.get(this.turno).getTurnosBloqueados()-1);
                consola.imprimir(this.jugadores.get(this.turno).getNombre() + " no puede lanzar los dados");
                this.tirado=true;
                acabarTurno();
                break;
            }
            getConsola().imprimir(menu.toString());
            comando = consola.leer("Introduce comando: ");
            while(comando.isEmpty()){comando=this.scanner.nextLine();}
            analizarComando(comando);
            if(!this.fin){break;}
        }while(!this.fturno);
        if(this.fin){verTablero();}
    }

    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    public void acabarTurno() {
        if (this.tirado){
            int turno_act = this.turno + 1;
            if (turno_act == this.avatares.size()){
                turno_act = 0;
            }
            this.turno = turno_act;
            this.fturno = this.tirado && this.solvente;
            this.tirado = false;
            consola.imprimir("\nEl jugador actual es " + this.avatares.get(this.turno).getJugador().getNombre());
        } else {
            consola.imprimir("\nEl jugador actual no ha tirado. Debe realizar sus tiradas para acabar su turno.");
        }
    }

    // Método para mostrar el tablero por pantalla asociado al comando 'ver tablero'.
    public void verTablero() {
        consola.imprimir("\n" + this.tablero.toString());
    }

    /*Método que interpreta el comando introducido y toma la accion correspondiente.
    * Parámetro: cadena de caracteres (el comando).
    */
    public void analizarComando(String comando) {
        ArrayList<String> edificios = new ArrayList<>(Arrays.asList("casa", "hotel", "piscina", "pista"));
        String[] partes = comando.split(" ");
        if(partes[0].equals("jugador")){
            turnoJugador();
        } else if(partes.length == 2 && partes[0].equals("listar") && partes[1].equals("jugadores")){
            listarJugadores();
        } else if(partes.length == 2 && partes[0].equals("listar") && partes[1].equals("avatares")){
            listarAvatares();
        } else if(partes.length == 2 && partes[0].equals("lanzar") && partes[1].equals("dados")){
            lanzarDados();
        } else if(partes.length == 2 && partes[0].equals("cambiar") && partes[1].equals("modo")){
            cambiarModo();
        } else if(partes.length == 2 && partes[0].equals("acabar") && partes[1].equals("turno")){
            acabarTurno();
        } else if(partes.length == 2 && partes[0].equals("salir") && partes[1].equals("carcel")){
            try{
                salirCarcel();;
            } catch (SaldoInsuficienteException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (JugadorException e) {
                consola.imprimir("Error relacionado con el jugador: " + e.getMessage());
            } catch (JuegoException e) {
                consola.imprimir("Error general del juego: " + e.getMessage());
            } catch (Exception e) {
                consola.imprimir("Error inesperado: " + e.getMessage());
            }
        } else if(partes[0].equals("describir") && partes.length >= 2){
            if(partes[1].equals("jugador") && partes.length == 3){
                try{
                    descJugador(partes);
                } catch (JugadorNoExisteException e) {
                    consola.imprimir("Error: " + e.getMessage());
                } catch (JugadorException e) {
                    consola.imprimir("Error relacionado con el jugador: " + e.getMessage());
                } catch (JuegoException e) {
                    consola.imprimir("Error general del juego: " + e.getMessage());
                } catch (Exception e) {
                    consola.imprimir("Error inesperado: " + e.getMessage());
                }
            } else if(partes[1].equals("avatar") && partes.length == 3){
                descAvatar(partes[2]);
            } else if(partes.length == 2){
                descCasilla(partes[1]);
            } else{
                consola.imprimir("Comando inválido\n");
            }
        } else if(partes.length == 3 && partes[0].equals("estadisticas") && partes[1].equals("jugador")){
            try{
                estadisticasjugador(partes[2]);
            } catch (JugadorNoExisteException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (JugadorException e) {
                consola.imprimir("Error relacionado con el jugador: " + e.getMessage());
            } catch (JuegoException e) {
                consola.imprimir("Error general del juego: " + e.getMessage());
            } catch (Exception e) {
                consola.imprimir("Error inesperado: " + e.getMessage());
            }
        } else if(partes.length == 2 && partes[0].equals("listar") && partes[1].equals("enventa")){
            listarVenta();
        } else if(partes.length == 2 && partes[0].equals("comprar") && partes[1].equals(this.avatares.get(this.turno).getLugar().getNombre())){
            try{
                comprar(this.avatares.get(this.turno).getLugar().getNombre());
            } catch (PropiedadNoDisponibleException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (SaldoInsuficienteException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (JugadorException e) {
                consola.imprimir("Error relacionado con el jugador: " + e.getMessage());
            } catch (PropiedadException e) {
                consola.imprimir("Error relacionado con la propiedad: " + e.getMessage());
            } catch (JuegoException e) {
                consola.imprimir("Error general del juego: " + e.getMessage());
            } catch (Exception e) {
                consola.imprimir("Error inesperado: " + e.getMessage());
            }
        } else if(partes.length == 2 && partes[0].equals("hipotecar") && partes[1].equals(this.avatares.get(this.turno).getLugar().getNombre())){
            try{
                hipotecar(partes[1]);
            } catch (PropiedadNoDisponibleException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (PropiedadYaHipotecadaException e) {
                consola.imprimir("Error: " + e.getMessage());
            }catch (PropiedadException e) {
                consola.imprimir("Error relacionado con la propiedad: " + e.getMessage());
            } catch (JuegoException e) {
                consola.imprimir("Error general del juego: " + e.getMessage());
            } catch (Exception e) {
                consola.imprimir("Error inesperado: " + e.getMessage());
            }
        } else if(partes.length == 2 && partes[0].equals("deshipotecar") && partes[1].equals(this.avatares.get(this.turno).getLugar().getNombre())){
            try{
                deshipotecar(partes[1]);
            } catch (PropiedadNoDisponibleException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (SaldoInsuficienteException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (JugadorException e) {
                consola.imprimir("Error relacionado con la propiedad: " + e.getMessage());
            }catch (PropiedadException e) {
                consola.imprimir("Error relacionado con la propiedad: " + e.getMessage());
            } catch (JuegoException e) {
                consola.imprimir("Error general del juego: " + e.getMessage());
            } catch (Exception e) {
                consola.imprimir("Error inesperado: " + e.getMessage());
            }
        } else if(partes.length == 3 && partes[0].equals("declararse") && partes[1].equals("en") && partes[2].equals("bancarrota")){
            if(this.solvente) bancarrota(this.jugadores.get(this.turno), banca);
            else if(this.avatares.get(this.turno).getLugar() instanceof Propiedad){
                Propiedad prop = (Propiedad) this.avatares.get(this.turno).getLugar();
                bancarrota(this.jugadores.get(this.turno), prop.getPropietario());
            }
            else bancarrota(this.jugadores.get(this.turno), banca);
        } else if(partes.length == 2 && partes[0].equals("edificar") && edificios.contains(partes[1])){
            try{
                edificarCasilla(partes[1]);
            } catch (LimiteDeEdificacionAlcanzadoException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (PropiedadNoDisponibleException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (PropiedadNoEdificableException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (PropiedadException e) {
                consola.imprimir("Error relacionado con la propiedad: " + e.getMessage());
            } catch (SaldoInsuficienteException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (JugadorException e) {
                consola.imprimir("Error relacionado con el jugador: " + e.getMessage());
            }catch (JuegoException e) {
                consola.imprimir("Error general del juego: " + e.getMessage());
            } catch (Exception e) {
                consola.imprimir("Error inesperado: " + e.getMessage());
            }
        } else if(partes.length >= 2 && partes[0].equals("listar") && partes[1].equals("edificios")){
            ArrayList<String> grupos = new ArrayList<String>(8);
            grupos.add("negro"); grupos.add("rojo"); grupos.add("verde"); grupos.add("amarillo");
            grupos.add("azul"); grupos.add("rosa"); grupos.add("cian"); grupos.add("blanco");
            
            if(partes.length == 2){
                listarEdificios();
            } else if(grupos.contains(partes[2]) && partes.length == 3){
                listarEdificiosGrupo(partes[2]);
            } else{
                consola.imprimir("Comando inválido\n");
            }
        } else if(partes.length == 4 && partes[0].equals("vender") && edificios.contains(partes[1]) && partes[2].equals(this.avatares.get(this.turno).getLugar().getNombre())){
            venderEdificios(partes[1], partes[2], partes[3]);
        } else if(partes.length == 3 && partes[0].equals("info") && partes[1].equals("tipos") && partes[2].equals("tratos")){
            infotratos();
        } else if(partes.length == 2 && partes[0].equals("estadisticas") && partes[1].equals("juego")){
            estadisticasjuego();
        }  else if(partes.length == 2 && partes[0].equals("ver") && partes[1].equals("tablero")){
            verTablero();
        } else if (partes.length == 1 && partes[0].equals("continuar")) {
            return;
        } else if (partes.length == 5 && partes[0].equals("trato") && partes[2].equals("cambiar")){
            if(comando.matches("trato \\w+: cambiar \\(\\w+, \\w+\\)")){
                comando = comando.replaceAll("\\(", "");
                comando = comando.replaceAll("\\)", "");
                comando = comando.replace(",", "");
                comando = comando.replaceAll(":", ""); 
            }
            String[] parts = comando.split(" ");
            trato(parts[1],parts[3],parts[4]);
        } else if (partes.length == 7 && partes[0].equals("trato") && partes[2].equals("cambiar")){
            if(comando.matches("trato \\w+: cambiar \\(\\w+, \\w+ y \\w+\\)|trato \\w+: cambiar \\(\\w+ y \\w+, \\w+\\)")){
                comando = comando.replaceAll("\\(", "");
                comando = comando.replaceAll("\\)", "");
                comando = comando.replace(",", "");
                comando = comando.replaceAll(":", "");    
            }
            String[] parts = comando.split(" ");
            trato(parts[1],parts[3],parts[4],parts[5], parts[6]);
        } else if(partes.length == 2 && partes[0].equals("aceptar")){
            try{
            aceptarTrato(partes[1]);
            } catch (SaldoInsuficienteException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (JugadorException e) {
                consola.imprimir("Error relacionado con la propiedad: " + e.getMessage());
            } catch (JuegoException e) {
                consola.imprimir("Error general del juego: " + e.getMessage());
            } catch (Exception e) {
                consola.imprimir("Error inesperado: " + e.getMessage());
            }
        } else if(partes.length == 1 && partes[0].equals("tratos")){
            tratos();
        } else if(partes.length == 2 && partes[0].equals("eliminar")){
            eliminarTrato(partes[1]);
        }
        else if(partes.length == 3 && partes[0].equals("trucar")){
            try{
                if(!this.jugadores.get(this.turno).getAvatar().getmovAvanzado()) lanzarDados(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
            else{
                this.jugadores.get(this.turno).getAvatar().mover(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]),tablero,banca);
                this.tirado=true;
                }
            } catch (MovimientoInvalidoException e) {
                consola.imprimir("Error: " + e.getMessage());
            } catch (JugadorException e) {
                consola.imprimir("Error relacionado con el jugador: " + e.getMessage());
            } catch (JuegoException e) {
                consola.imprimir("Error general del juego: " + e.getMessage());
            } catch (Exception e) {
                consola.imprimir("Error inesperado: " + e.getMessage());
            }
        } else if(partes[0].equals("exit")){
            System.exit(0);
        } else{
            consola.imprimir("Comando inválido\n");
        }
        
    }

    private Jugador buscarJugador(String nombre){
        for (Jugador i : this.jugadores) {
            if (i.getNombre().equals(nombre)) {
                return i;
            }
        }
        consola.imprimir("El jugador " + nombre + " no está en la partida.");
        return null;
    }

    /*Método que realiza las acciones asociadas al comando 'jugador'.*/
    public void turnoJugador() {
        consola.imprimir("nombre: " + this.jugadores.get(this.turno).getNombre() + ",\navatar: " + this.avatares.get(this.turno).getID());
    }

    /*Método que realiza las acciones asociadas al comando 'describir jugador'.
    * Parámetro: comando introducido
     */
    public void descJugador(String[] partes) throws Exception{
        String nombreJugador = partes[2];
        Jugador i = buscarJugador(nombreJugador);
        if(i != null) consola.imprimir("{\n" + i.toString() + "}");
        else throw new JugadorNoExisteException("El jugador " + nombreJugador + " no existe.");
    }

    /*Método que que muestra la información sobre un jugador.
    * Parámetro: número (índice) que representa su posición en la lista de jugadores
     */
    private void descJugador(int numero) {
        Jugador dJugador = this.jugadores.get(numero);
        consola.imprimir(dJugador.toString()); 
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    public void listarJugadores() {
        for(int i=0; i<this.jugadores.size(); i++){
            consola.imprimir("{\n");
            descJugador(i);
            consola.imprimir((i == (this.jugadores.size()-1) ? "}\n" : "},\n"));
        }
    }

    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
    * Parámetro: id del avatar a describir.
    */
    public void descAvatar(String ID) {
        for (Avatar i : this.avatares) {
            if (i.getID().equals(ID)) {
                consola.imprimir("{\n" + i.toString() + "}"); 
                return;
            }
        }
        consola.imprimir("El avatar con ID " + ID + " no está en la partida");
    }

    /*Método que muestra la información sobre un avatar.
    * Parámetro: número (índice) que representa su posición en la lista de jugadores
     */
    private void descAvatar(int numero) {
        Avatar dAvatar = this.avatares.get(numero);
        consola.imprimir(dAvatar.toString()); 
    }

    // Método que realiza las acciones asociadas al comando 'listar avatares'.
    public void listarAvatares() {
        for(int i=0; i<this.avatares.size(); i++){
            consola.imprimir("{\n");
            descAvatar(i);
            consola.imprimir((i == (this.avatares.size()-1) ? "}\n" : "},\n"));
        }
    }

    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
    * Parámetros: nombre de la casilla a describir.
    */
    public void descCasilla(String nombre) {
        Casilla dCasilla = this.tablero.encontrar_casilla(nombre);
        if(dCasilla!=null){
            Juego.consola.imprimir("\n" + dCasilla.toString() + "\n\tVeces que han caído en esta casilla: ");
            for (int i = 0; i < jugadores.size(); i++) {
                Juego.consola.imprimir("[" + jugadores.get(i).getNombre() + ", " + dCasilla.getCaidas().get(i) + "]" + (i == (this.jugadores.size()-1) ? "" : ", "));
            } Juego.consola.imprimir("\n");
        }   
    }

    /*Método para cambiar de modo de lanzamiento (normal o avanzado) */
    public void cambiarModo(){
        if(!this.jugadores.get(this.turno).getAvatar().getmovAvanzado()){
            consola.imprimir("A partir de ahora el avatar " + this.jugadores.get(this.turno).getAvatar().getID() + ", de tipo " + this.jugadores.get(this.turno).getAvatar().getTipo() + ", se moverá en modo avanzado.");
        } else{consola.imprimir("A partir de ahora el avatar " + this.jugadores.get(this.turno).getAvatar().getID() + ", de tipo " + this.jugadores.get(this.turno).getAvatar().getTipo() + ", se moverá en modo normal.");}
        this.jugadores.get(this.turno).getAvatar().setmovAvanzado(this.jugadores.get(this.turno));
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    public void lanzarDados() {
        if(this.tirado){consola.imprimir("El jugador actual ya ha realizado todas sus tiradas.");}

        if (!this.tirado){
            int d1 = this.dado1.hacerTirada(), d2 = this.dado2.hacerTirada();
            if(!this.jugadores.get(this.turno).getEncarcelado()){
                int desp = d1 + d2;
                int mov = (this.avatares.get(this.turno).getLugar().getPosicion() + desp) % 40;
                
                String origen = this.avatares.get(this.turno).getLugar().getNombre(), destino = this.tablero.encontrar_casilla(mov).getNombre();
                int oint = this.avatares.get(this.turno).getLugar().getPosicion(), dint = this.tablero.encontrar_casilla(mov).getPosicion();

                consola.imprimir("El avatar " + this.avatares.get(this.turno).getID() + " avanza " + desp + " posiciones, desde " + origen + " hasta " + destino);
                this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                
                //this.tablero.encontrar_casilla(oint).eliminarAvatar(this.avatares.get(this.turno));
                //this.tablero.encontrar_casilla(mov).anhadirAvatar(this.avatares.get(this.turno));
                this.jugadores.get(this.turno).getAvatar().moverAvatar(this.tablero.getPosiciones(), desp);
                this.tablero.encontrar_casilla(destino).getCaidas().set(this.turno, this.tablero.encontrar_casilla(destino).getCaidas().get(this.turno)+1);
                
                this.solvente = this.tablero.encontrar_casilla(destino).evaluarCasilla(this.avatares.get(this.turno).getJugador(), desp, tablero, turno, this);
                
                if(this.fin){
                    if(this.tablero.encontrar_casilla(destino).getNombre().equals("IrCarcel") && this.jugadores.get(this.turno).getEncarcelado()){
                        this.jugadores.get(this.turno).encarcelar(this.tablero.getPosiciones());
                    }
                    if(this.tablero.encontrar_casilla(destino) instanceof Impuesto){
                        Impuesto imp = (Impuesto) this.jugadores.get(this.turno).getAvatar().getLugar();
                        Especial parking = (Especial) this.tablero.encontrar_casilla("Parking");
                        parking.sumarBote(imp.getImpuesto());
                    }
                verTablero();
                } else{ consola.imprimir("El jugador " + this.jugadores.get(this.turno).getNombre() + " está encarcelado. No se desplaza.");}

                if (d1 == d2){
                    ++this.lanzamientos;
                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                    this.tirado = false;
                    if (this.avatares.get(this.turno).getJugador().getEncarcelado()){
                        this.avatares.get(this.turno).getJugador().setEncarcelado(false);
                        this.avatares.get(this.turno).getJugador().setTiradasCarcel(0);
                        consola.imprimir(this.avatares.get(this.turno).getJugador().getNombre() + " sale de la cárcel. Puede lanzar los dados.");
                    } else{consola.imprimir("Puede volver a lanzar los dados.");} 

                    if (this.lanzamientos == 3){
                        this.avatares.get(this.turno).getJugador().encarcelar(this.tablero.getPosiciones());
                        consola.imprimir(this.jugadores.get(this.turno).getNombre() + " ha sacado tres dobles seguidos y va a la cárcel.");
                        this.lanzamientos = 0;
                        this.tirado = true;
                    }
                } else{this.tirado = true;}

                
                if(this.tablero.encontrar_casilla(oint).getPosicion()>this.tablero.encontrar_casilla(dint).getPosicion()){
                    this.avatares.get(this.turno).getJugador().setVueltas(this.avatares.get(this.turno).getJugador().getVueltas()+1);
                    this.avatares.get(this.turno).getJugador().sumarFortuna(Valor.SUMA_VUELTA);
                    this.avatares.get(this.turno).getJugador().getEstadisticas().set(4, this.avatares.get(this.turno).getJugador().getEstadisticas().get(4) + Valor.SUMA_VUELTA);
                    this.vueltastotales.set(this.turno,this.vueltastotales.get(this.turno) + 1);
                    
                    consola.imprimir("El jugador " + this.jugadores.get(this.turno).getNombre() + " ha cruzado la casilla de Salida. Recibe " + Valor.SUMA_VUELTA + "€.");
                    int ultimo = 1;
                    if(this.avatares.get(this.turno).getJugador().getVueltas() != 0 && this.avatares.get(this.turno).getJugador().getVueltas()%4 == 0){
                        for(Jugador j:this.jugadores){
                            if(!j.equals(this.jugadores.get(this.turno)) && j.getVueltas()<this.jugadores.get(this.turno).getVueltas()){
                                ultimo = 0;
                            }
                        } if (ultimo == 1){
                            aumentaPrecio();
                        }
                    }
                }
            }
        }
        this.lanzamientos = 0;
    }

    /*Función de dados trucados */
    public void lanzarDados(int valor1, int valor2) throws Exception {
        if(this.tirado){throw new MovimientoInvalidoException("El jugador actual ya ha realizado todas sus tiradas.");}

        if (!this.tirado){
            int d1 = valor1, d2 = valor2;
            if(!this.jugadores.get(this.turno).getEncarcelado()){
                int desp = d1 + d2;
                int mov = (this.avatares.get(this.turno).getLugar().getPosicion() + desp) % 40;
                
                String origen = this.avatares.get(this.turno).getLugar().getNombre(), destino = this.tablero.encontrar_casilla(mov).getNombre();
                int oint = this.avatares.get(this.turno).getLugar().getPosicion(), dint = this.tablero.encontrar_casilla(mov).getPosicion();

                consola.imprimir("El avatar " + this.avatares.get(this.turno).getID() + " avanza " + desp + " posiciones, desde " + origen + " hasta " + destino);
                this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                
                //this.tablero.encontrar_casilla(oint).eliminarAvatar(this.avatares.get(this.turno));
                //this.tablero.encontrar_casilla(mov).anhadirAvatar(this.avatares.get(this.turno));
                this.jugadores.get(this.turno).getAvatar().moverAvatar(this.tablero.getPosiciones(), desp);
                this.tablero.encontrar_casilla(destino).getCaidas().set(this.turno, this.tablero.encontrar_casilla(destino).getCaidas().get(this.turno)+1);
                
                this.solvente = this.tablero.encontrar_casilla(destino).evaluarCasilla(this.avatares.get(this.turno).getJugador(), desp, tablero, turno, this);
                
                if(this.fin){
                    if(this.tablero.encontrar_casilla(destino).getNombre().equals("IrCarcel") && this.jugadores.get(this.turno).getEncarcelado()){
                        this.jugadores.get(this.turno).encarcelar(this.tablero.getPosiciones());
                    }
                    if(this.tablero.encontrar_casilla(destino) instanceof Impuesto){
                        Impuesto imp = (Impuesto) this.jugadores.get(this.turno).getAvatar().getLugar();
                        Especial parking = (Especial) this.tablero.encontrar_casilla("Parking");
                        parking.sumarBote(imp.getImpuesto());
                    }
                verTablero();
                } else{ throw new MovimientoInvalidoException("El jugador " + this.jugadores.get(this.turno).getNombre() + " está encarcelado. No se desplaza.");}

                if (d1 == d2){
                    ++this.lanzamientos;
                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                    this.tirado = false;
                    if (this.avatares.get(this.turno).getJugador().getEncarcelado()){
                        this.avatares.get(this.turno).getJugador().setEncarcelado(false);
                        this.avatares.get(this.turno).getJugador().setTiradasCarcel(0);
                        consola.imprimir(this.avatares.get(this.turno).getJugador().getNombre() + " sale de la cárcel. Puede lanzar los dados.");
                    } else{consola.imprimir("Puede volver a lanzar los dados.");} 

                    if (this.lanzamientos == 3){
                        this.avatares.get(this.turno).getJugador().encarcelar(this.tablero.getPosiciones());
                        consola.imprimir(this.jugadores.get(this.turno).getNombre() + " ha sacado tres dobles seguidos y va a la cárcel.");
                        this.lanzamientos = 0;
                        this.tirado = true;
                    }
                } else{this.tirado = true;}

                
                if(this.tablero.encontrar_casilla(oint).getPosicion()>this.tablero.encontrar_casilla(dint).getPosicion()){
                    this.avatares.get(this.turno).getJugador().setVueltas(this.avatares.get(this.turno).getJugador().getVueltas()+1);
                    this.avatares.get(this.turno).getJugador().sumarFortuna(Valor.SUMA_VUELTA);
                    this.avatares.get(this.turno).getJugador().getEstadisticas().set(4, this.avatares.get(this.turno).getJugador().getEstadisticas().get(4) + Valor.SUMA_VUELTA);

                    consola.imprimir("El jugador " + this.jugadores.get(this.turno).getNombre() + " ha cruzado la casilla de Salida. Recibe " + Valor.SUMA_VUELTA + "€.");
                    int ultimo = 1;
                    if(this.avatares.get(this.turno).getJugador().getVueltas() != 0 && this.avatares.get(this.turno).getJugador().getVueltas()%4 == 0){
                        for(Jugador j:this.jugadores){
                            if(!j.equals(this.jugadores.get(this.turno)) && j.getVueltas()<this.jugadores.get(this.turno).getVueltas()){
                                ultimo = 0;
                            }
                        } if (ultimo == 1){
                            aumentaPrecio();
                        }
                    }
                }
            }
        }
        this.lanzamientos = 0;
    }


    public void lanzarDadosEsp(int valor1, int valor2) {
        if (this.tirado) {consola.imprimir("El jugador actual ya ha realizado todas sus tiradas.");}
        if (!this.tirado) {
            int d1 = valor1, d2 = valor2;
            int desp = d1 + d2;

            if (!this.jugadores.get(this.turno).getEncarcelado()) {
                if (this.jugadores.get(this.turno).getAvatar().getTipo().equals("pelota")) {
                    if (desp <= 4) {
                        int j=1;
                        for(int i=1;i<=desp;i+=2){
                            int mov = ((this.avatares.get(this.turno).getLugar().getPosicion() - j) % 40 + 40) % 40;
                            String origen = this.avatares.get(this.turno).getLugar().getNombre();
                            String destino = this.tablero.encontrar_casilla(mov).getNombre();
                            int oint = this.avatares.get(this.turno).getLugar().getPosicion();
                            int dint = this.tablero.encontrar_casilla(mov).getPosicion();

                            consola.imprimir("El avatar " + this.avatares.get(this.turno).getID() + " retrocede " + j + " posiciones, desde " + origen + " hasta " + destino);
                            this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                        
                            actualizar(mov,origen,destino,oint,dint,d1,d2,((-j % 40) + 40) % 40);
                            if (this.avatares.get(this.turno).getLugar() instanceof Propiedad) {
                                
                                consola.imprimir("continuar ");
                                consola.imprimir("comprar " + this.avatares.get(this.turno).getLugar().getNombre());
                                consola.imprimir("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                consola.imprimir("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                consola.imprimir("edificar \"TIPOEDIFICIO\"");
                                consola.imprimir("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"");

                                String comando = this.scanner.nextLine();
                                while (comando.isEmpty()) {comando = this.scanner.nextLine();}
                                analizarComando(comando);
                            } if(desp-i==1){
                                j=1;
                                i--;
                            } else{j=2;}
                        }

                    } else{
                        int j=5;
                        for(int i=5;i<=desp;i+=2){ 
                            int mov = (this.avatares.get(this.turno).getLugar().getPosicion() + j) % 40;
                            String origen = this.avatares.get(this.turno).getLugar().getNombre();
                            String destino = this.tablero.encontrar_casilla(mov).getNombre();
                            int oint = this.avatares.get(this.turno).getLugar().getPosicion();
                            int dint = this.tablero.encontrar_casilla(mov).getPosicion();

                            consola.imprimir("El avatar " + this.avatares.get(this.turno).getID() + " avanza " + j + " posiciones, desde " + origen + " hasta " + destino);
                            this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);

                            actualizar(mov,origen,destino,oint,dint,d1,d2,j);
                            if (this.avatares.get(this.turno).getLugar() instanceof Propiedad) {
                                
                                consola.imprimir("continuar ");
                                consola.imprimir("comprar " + this.avatares.get(this.turno).getLugar().getNombre());
                                consola.imprimir("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                consola.imprimir("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                consola.imprimir("edificar \"TIPOEDIFICIO\"");
                                consola.imprimir("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"");

                                String comando = this.scanner.nextLine();
                                while (comando.isEmpty()) {comando = this.scanner.nextLine();}
                                analizarComando(comando);
                            }

                            if(desp-i==1){
                                j=1;
                                i--;
                            } else{j=2;}
                        }
                    
                    } if (d1 == d2) {
                        doblesContador++;
                        this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                        if (doblesContador == 3) {
                            this.tirado = true;
                            this.avatares.get(this.turno).getJugador().encarcelar(this.tablero.getPosiciones());
                            consola.imprimir("El avatar ha sacado dobles tres veces seguidas y va a la cárcel.");
                            //acabarTurno();
                            return;
                        } else{
                            Juego.consola.imprimir("Has sacado doble, puedes volver a lanzar. Valor dados: ");
                            int dado1 = scanner.nextInt();
                            int dado2 = scanner.nextInt();
                            lanzarDadosEsp(dado1, dado2);
                        }
                    } else {
                        this.tirado=true;
                        //acabarTurno();
                        doblesContador = 0;
                    }
                    
                } else if (this.jugadores.get(this.turno).getAvatar().getTipo().equals("coche")) {
                    boolean doblesPermitido = false;
                    if (desp <= 4) {
                        int mov = ((this.avatares.get(this.turno).getLugar().getPosicion() - desp) % 40 + 40) % 40;
                        String origen = this.avatares.get(this.turno).getLugar().getNombre();
                        String destino = this.tablero.encontrar_casilla(mov).getNombre();
                        int oint = this.avatares.get(this.turno).getLugar().getPosicion();
                        int dint = this.tablero.encontrar_casilla(mov).getPosicion();

                        consola.imprimir("El avatar " + this.avatares.get(this.turno).getID() + " retrocede " + desp + " posiciones, desde " + origen + " hasta " + destino);
                        this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                    
                        actualizar(mov,origen,destino,oint,dint,d1,d2,((-desp % 40) + 40) % 40);
                        this.avatares.get(this.turno).setTurnosBloqueados(2);
                        //acabarTurno();
                    } else{
                        boolean compraRealizada = false;

                        if (desp > 4) {
                            int tiradasExtras = 0;
                            do {
                                int mov = (this.avatares.get(this.turno).getLugar().getPosicion() + desp) % 40;
                                String origen = this.avatares.get(this.turno).getLugar().getNombre();
                                String destino = this.tablero.encontrar_casilla(mov).getNombre();
                                int oint = this.avatares.get(this.turno).getLugar().getPosicion();
                                int dint = this.tablero.encontrar_casilla(mov).getPosicion();
                        
                                consola.imprimir("El avatar " + this.avatares.get(this.turno).getID() + " avanza " + desp + " posiciones, desde " + origen + " hasta " + destino);
                                this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                        
                                actualizar(mov, origen, destino, oint, dint, d1, d2, desp);
                        
                                if((this.avatares.get(this.turno).getLugar() instanceof Propiedad) && !compraRealizada) {
                                    consola.imprimir("continuar ");
                                    consola.imprimir("comprar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    consola.imprimir("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    consola.imprimir("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    consola.imprimir("edificar \"TIPOEDIFICIO\"");
                                    consola.imprimir("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"");
                        
                                    String comando = this.scanner.nextLine();
                                    String[] partes = comando.split(" ");
                                    while (comando.isEmpty()) {comando = this.scanner.nextLine();}
                                    analizarComando(comando);
                                    if(partes[0].equals("comprar")) compraRealizada = true;
                        
                                } else if(this.avatares.get(this.turno).getLugar() instanceof Solar){
                                    consola.imprimir("continuar ");
                                    consola.imprimir("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    consola.imprimir("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    consola.imprimir("edificar \"TIPOEDIFICIO\"");
                                    consola.imprimir("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"");
                                    String comando = this.scanner.nextLine();
                                    while (comando.isEmpty()) {comando = this.scanner.nextLine();}
                                    analizarComando(comando);
                                }
                        
                                tiradasExtras++;
                        
                                if (tiradasExtras < 3) {
                                    Juego.consola.imprimir("Vuelves a lanzar los dados. Valor dados: ");
                                    d1 = scanner.nextInt();
                                    d2 = scanner.nextInt();
                                    desp = d1 + d2;
                                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                                    doblesPermitido = false;
                                } else if (tiradasExtras == 3) {
                                    Juego.consola.imprimir("Última tirada del turno. Valor dados: ");
                                    d1 = scanner.nextInt();
                                    d2 = scanner.nextInt();
                                    desp = d1 + d2;
                                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                                    if (d1==d2){doblesPermitido = true;}
                                
                                } if (doblesPermitido && d1 == d2 && tiradasExtras==4) {
                                    Juego.consola.imprimir("Has sacado dobles en la última tirada. Se permite una tirada adicional.");
                                    doblesPermitido = false;
                                    Juego.consola.imprimir("Vuelves a lanzar los dados. Valor dados: ");
                                    d1 = scanner.nextInt();
                                    d2 = scanner.nextInt();
                                    desp = d1 + d2;
                                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                                } else if (tiradasExtras > 3 && !doblesPermitido) {break;}
                            } while (d1 + d2 > 4);
                            this.tirado=true;
                            //acabarTurno();
                        }
                    }
                }
            }
        }
        this.lanzamientos = 0;
    }

    // Método para aumentar el precio cada vez que todos los jugadores dan cuatro vueltas
    private void aumentaPrecio() {
        for(int i=0; i<40; i++){
            if(this.tablero.encontrar_casilla(i) instanceof Solar){
                Solar sol = (Solar) this.tablero.encontrar_casilla(i);
                if(sol.getPropietario().equals(null)){
                    sol.sumarValor(0.05f * sol.getValor());
                }
            }
        }
    }

    public void actualizar(int mov, String origen, String destino, int oint, int dint, int d1, int d2, int desp) {
        this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                
        //this.tablero.encontrar_casilla(oint).eliminarAvatar(this.avatares.get(this.turno));
        //this.tablero.encontrar_casilla(mov).anhadirAvatar(this.avatares.get(this.turno));
        this.jugadores.get(this.turno).getAvatar().moverAvatar(this.tablero.getPosiciones(), desp);
        this.tablero.encontrar_casilla(destino).getCaidas().set(this.turno, this.tablero.encontrar_casilla(destino).getCaidas().get(this.turno)+1);
        
        this.solvente = this.tablero.encontrar_casilla(destino).evaluarCasilla(this.avatares.get(this.turno).getJugador(), desp, tablero, turno, this);
        
        if(this.fin){
            if(this.tablero.encontrar_casilla(destino).getNombre().equals("IrCarcel") && this.jugadores.get(this.turno).getEncarcelado()){
                this.jugadores.get(this.turno).encarcelar(this.tablero.getPosiciones());
            }
            if(this.tablero.encontrar_casilla(destino) instanceof Impuesto){
                Especial parking = (Especial) this.tablero.encontrar_casilla("Parking");
                Impuesto imp = (Impuesto) this.jugadores.get(this.turno).getAvatar().getLugar();
                parking.sumarBote(imp.getImpuesto());
            }
        verTablero();
        } else{ consola.imprimir("El jugador " + this.jugadores.get(this.turno).getNombre() + " está encarcelado. No se desplaza.");}
        
        if(this.tablero.encontrar_casilla(oint).getPosicion()>this.tablero.encontrar_casilla(dint).getPosicion() && d1+d2>4){
            this.avatares.get(this.turno).getJugador().setVueltas(this.avatares.get(this.turno).getJugador().getVueltas()+1);
            this.avatares.get(this.turno).getJugador().sumarFortuna(Valor.SUMA_VUELTA);
            this.avatares.get(this.turno).getJugador().getEstadisticas().set(4, this.avatares.get(this.turno).getJugador().getEstadisticas().get(4) + Valor.SUMA_VUELTA);

            consola.imprimir("El jugador " + this.jugadores.get(this.turno).getNombre() + " ha cruzado la casilla de Salida. Recibe " + Valor.SUMA_VUELTA + "€.");
            int ultimo = 1;
            if(this.avatares.get(this.turno).getJugador().getVueltas() != 0 && this.avatares.get(this.turno).getJugador().getVueltas()%4 == 0){
                for(Jugador j:this.jugadores){
                    if(!j.equals(this.jugadores.get(this.turno)) && j.getVueltas()<this.jugadores.get(this.turno).getVueltas()){
                        ultimo = 0;
                    }
                } if (ultimo == 1){
                    aumentaPrecio();
                }
            }
        }
    }

    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
    * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    public void comprar(String nombre) throws Exception{
        if (this.tablero.encontrar_casilla(nombre) instanceof Propiedad){
            Propiedad prop = (Propiedad) this.tablero.encontrar_casilla(nombre);
            prop.comprar(this.avatares.get(this.turno).getJugador(), banca);
        } else{
            throw new PropiedadNoDisponibleException("Al jugador no le pertenece esta propiedad");
        }
    }

    public void hipotecar(String nombre) throws Exception{
        if(this.tablero.encontrar_casilla(nombre) instanceof Propiedad){
            Propiedad prop = (Propiedad) this.tablero.encontrar_casilla(nombre);
            prop.hipotecar(this.jugadores.get(this.turno));
        } else{
            consola.imprimir("Esta casilla no se puede hipotecar");
        }
    }

    public void deshipotecar(String nombre) throws Exception{
        if(this.tablero.encontrar_casilla(nombre) instanceof Propiedad){
            Propiedad prop = (Propiedad) this.tablero.encontrar_casilla(nombre);
            prop.deshipotecar(this.jugadores.get(this.turno));
        } else{consola.imprimir("Esta casilla no se puede deshipotecar");}
    }

    public void bancarrota(Jugador solicitante, Jugador demandante){
        if(solicitante.equals((demandante))){demandante = this.banca;}
        ArrayList<Propiedad> propiedades = new ArrayList<>(solicitante.getPropiedades());
        for (Propiedad cas : propiedades) {
            if(cas instanceof Solar){
                Solar sol = (Solar) cas;
                ArrayList<Edificio> edificiosCopia = new ArrayList<>(sol.getEdificios());
                for (Edificio ed : edificiosCopia) {
                    sol.getEdificios().remove(ed); // Eliminar de la lista original
                }

                for (int i = 0; i < sol.getNumEdificios().size(); i++) {
                    sol.getNumEdificios().set(i, 0);
                } for (int j = 0; j < sol.getGrupo().getEdificios().size(); j++){
                    sol.getGrupo().getEdificios().set(j, 0);
                }

                ArrayList<Edificio> listaEdificiosCopia = new ArrayList<>(edificiosConstruidos);
                for (Edificio ed : listaEdificiosCopia) {
                    if (ed.getUbicacion().equals(sol)) {
                        edificiosConstruidos.remove(ed);
                        solicitante.venderEdificio(ed);
                    }
                }
            }
            
            solicitante.eliminarPropiedad(cas);
            demandante.anhadirPropiedad(cas);
            cas.setPropietario(demandante);
        }

        float deuda = solicitante.getFortuna();
        demandante.sumarFortuna(deuda);
        solicitante.sumarGastos(deuda);
        if (demandante.getNombre().equals("banca")){
            consola.imprimir("El jugador " + solicitante.getNombre() + " se ha declarado en bancarrota. Sus propiedades pasan a estar de nuevo en venta al precio al que estaban.");
        } else{
            consola.imprimir("El jugador " + solicitante.getNombre() + " se ha declarado en bancarrota. Sus propiedades y fortuna pasan al jugador " + demandante.getNombre());
        }
        
        consola.imprimir("El jugador " + solicitante.getNombre() + " sale de la partida.");
        solicitante.getAvatar().getLugar().eliminarAvatar(solicitante.getAvatar()); 
        this.avatares.remove(this.turno);
        this.jugadores.remove(this.turno);
        
        this.fturno = true;
        this.tirado = false;
        
        if(this.turno>=this.jugadores.size()) this.turno = 0;
        this.fin =!((this.jugadores.size() == 1) || (this.jugadores.size() == 0));
        if (this.fin){
            consola.imprimir("\nEl jugador actual es " + this.avatares.get(this.turno).getJugador().getNombre());
        } else{
            Juego.consola.imprimir("La partida ha terminado. ");
            if(this.jugadores.size()==0){Juego.consola.imprimir("Todos los jugadores han perdido\n");
            } else{Juego.consola.imprimir("El jugador " + this.jugadores.get(0).getNombre() + " ha ganado\n");}
        }
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'. 
    public void salirCarcel() throws Exception {
        if (this.avatares.get(this.turno).getJugador().getEncarcelado()){
            if(this.avatares.get(this.turno).getJugador().getFortuna() >= (0.25f*Valor.SUMA_VUELTA)){  
                consola.imprimir(this.avatares.get(this.turno).getJugador().getNombre() + " paga " + (0.25f*Valor.SUMA_VUELTA) + "€ y sale de la cárcel."); 
                this.jugadores.get(this.turno).getEstadisticas().set(1, this.jugadores.get(this.turno).getEstadisticas().get(1) + (0.25f*Valor.SUMA_VUELTA));
                this.avatares.get(this.turno).getJugador().setEncarcelado(false);
            } else{
                throw new SaldoInsuficienteException(this.avatares.get(this.turno).getJugador().getNombre() + " no tiene el dinero suficiente para salir de la cárcel.");
            }  
        } else{
            consola.imprimir("El jugador no se encuentra en la cárcel");
        }
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    public void listarVenta() {
        ArrayList<Propiedad> lista = new ArrayList<Propiedad>();

        for(int i=0;i<40;i++){
            if(this.tablero.encontrar_casilla(i) instanceof Propiedad){
                Propiedad prop = (Propiedad) this.tablero.encontrar_casilla(i);
                if(prop.getPropietario().equals(null)){lista.add(prop);}
            }
        }
        
        if(lista.size()==0){Juego.consola.imprimir("No hay propiedades en venta");}
        
        else{
            for(Propiedad cas : lista){
                consola.imprimir(cas.getNombre() + "{");
                if(cas instanceof Solar){
                    Solar sol = (Solar) cas;
                    consola.imprimir("Tipo: Solar");
                    consola.imprimir("Grupo: " + sol.getGrupo().colorGrupo());
                } else if(cas instanceof Servicio){consola.imprimir("Tipo: Servicio");
                } else if(cas instanceof Transporte){consola.imprimir("Tipo: Transporte");}
    
                consola.imprimir("Valor: " + cas.getValor());
                consola.imprimir((lista.indexOf(cas) == (lista.size()-1) ? "}\n" : "},\n"));
            }
        }
    }
    
    public void edificarCasilla(String edificio) throws Exception{
        if(!edificio.equals("casa") && !edificio.equals("hotel") && !edificio.equals("piscina") && !edificio.equals("pista")){consola.imprimir("Comando inválido\n");}
        else{
            Solar sol = (Solar) avatares.get(this.turno).getLugar();
            if(!(avatares.get(this.turno).getLugar() instanceof Solar)){
                throw new PropiedadNoEdificableException("Esta casilla no es edificable.");
            } else if(!sol.getPropietario().equals(jugadores.get(this.turno))){
                throw new PropiedadNoDisponibleException("El jugador " + jugadores.get(this.turno).getNombre() + " no puede edificar en esta casilla porque no es de su propiedad.");
            } else if(sol.getCaidas().get(this.turno) <= 2 && !sol.getGrupo().esDuenhoGrupo(avatares.get(this.turno).getJugador())){
                throw new PropiedadNoEdificableException("El jugador " + jugadores.get(this.turno).getNombre() + " no puede edificar en esta casilla todavía.");
            } else if(sol.getGrupo().esDuenhoGrupo(avatares.get(this.turno).getJugador())){
                sol.construirEdificio(edificio, jugadores.get(this.turno), edificiosConstruidos);
            } else{sol.construirEdificio(edificio, jugadores.get(this.turno), edificiosConstruidos);}
        }
    }

    private void descEdificio(int numero) {
        Edificio dEdificio = this.edificiosConstruidos.get(numero);
        consola.imprimir(dEdificio.toString()); 
    }

    public void listarEdificios(){
        if(this.edificiosConstruidos.isEmpty()){consola.imprimir("No hay edificios construídos.\n");}
        else{
        for(int i=0; i<this.edificiosConstruidos.size(); i++){
            consola.imprimir("{");
            descEdificio(i);
            consola.imprimir((i == (this.edificiosConstruidos.size()-1) ? "}\n" : "},\n"));
        }}
    }

    public void listarEdificiosGrupo(String grupo) {
        String key;
        switch (grupo) {
            case "negro":
                key = Valor.BLACK;
                break;
            case "rojo":
                key = Valor.RED;
                break;
            case "verde":
                key = Valor.GREEN;
                break;
            case "amarillo":
                key = Valor.YELLOW;
                break;
            case "azul":
                key = Valor.BLUE;
                break;
            case "rosa":
                key = Valor.PURPLE;
                break;
            case "cian":
                key = Valor.CYAN;
                break;
            case "blanco":
                key = Valor.WHITE;
                break;
            default:
                consola.imprimir("Color de grupo no válido.");
                return;
        }
    
        Grupo grupoActual = this.tablero.getGrupos().get(key);
        ArrayList<Solar> casillasGrupo = grupoActual.getMiembros();
        ArrayList<Integer> contadorEdificios = new ArrayList<>(Arrays.asList(0, 0, 0, 0)); 
    
        for (Solar casilla : casillasGrupo) {
            consola.imprimir("{");
            consola.imprimir("propiedad: " + casilla.getNombre() + ",");
            contadorEdificios.set(0, contadorEdificios.get(0) + listarEdificiosPorTipo(casilla, "casa"));
            contadorEdificios.set(1, contadorEdificios.get(1) + listarEdificiosPorTipo(casilla, "hotel"));
            contadorEdificios.set(2, contadorEdificios.get(2) + listarEdificiosPorTipo(casilla, "piscina"));
            contadorEdificios.set(3, contadorEdificios.get(3) + listarEdificiosPorTipo(casilla, "pista de deporte"));
            consola.imprimir("alquiler: " + casilla.getAlquiler());
            consola.imprimir("}\n");
        }
    
        consola.imprimir("\nEdificios que aún se pueden construir en este grupo:");
        
        if (contadorEdificios.get(0) < grupoActual.getNumCasillas()) {
            consola.imprimir("\tCasas: " + (grupoActual.getNumCasillas() - contadorEdificios.get(0)));
        } if (contadorEdificios.get(1) < grupoActual.getNumCasillas()) {
            consola.imprimir("\tHoteles: " + (grupoActual.getNumCasillas() - contadorEdificios.get(1)));
        } if (contadorEdificios.get(2) < grupoActual.getNumCasillas()) {
            consola.imprimir("\tPiscinas: " + (grupoActual.getNumCasillas() - contadorEdificios.get(2)));
        } if (contadorEdificios.get(3) < grupoActual.getNumCasillas()) {
            consola.imprimir("\tPistas de deporte: " + (grupoActual.getNumCasillas() - contadorEdificios.get(3)));
        }
    
        if (contadorEdificios.get(0) >= grupoActual.getNumCasillas() && 
            contadorEdificios.get(1) == grupoActual.getNumCasillas() &&
            contadorEdificios.get(2) == grupoActual.getNumCasillas() &&
            contadorEdificios.get(3) == grupoActual.getNumCasillas()) {
            consola.imprimir("No se pueden construir más edificios en este grupo.");
        }
    }
    
    private int listarEdificiosPorTipo(Solar solar, String tipo) {
        ArrayList<Edificio> edificios = new ArrayList<>();
        switch (tipo) {
            case "casa":
                for (Edificio edificio : solar.getEdificios()) {
                    if (edificio instanceof Casa) {edificios.add(edificio);}
                } break;

            case "hotel":
                for (Edificio edificio : solar.getEdificios()) {
                    if (edificio instanceof Hotel) {edificios.add(edificio);}
                } break;

            case "piscina":
                for (Edificio edificio : solar.getEdificios()) {
                    if (edificio instanceof Piscina) {edificios.add(edificio);}
                } break;

            case "pista":
                for (Edificio edificio : solar.getEdificios()) {
                    if (edificio instanceof PistaDeporte) {edificios.add(edificio);}
                } break;
        
            default:
                break;
        }
        
        Juego.consola.imprimir(tipo + "s: " + (edificios.isEmpty() ? "-" : ""));
        for (Edificio edificio : edificios) {
            Juego.consola.imprimir("[" + edificio.getID() + "]");
        }
        consola.imprimir("\n");
        return edificios.size();
    }
    
    public void venderEdificios(String tipoedificio, String nombre, String cantidad){
        if(this.tablero.encontrar_casilla(nombre) instanceof Solar){
            Solar solar = (Solar) this.avatares.get(this.turno).getLugar();
            solar.venderEdificios(this.jugadores.get(this.turno), tipoedificio, cantidad, edificiosConstruidos);
        } else{consola.imprimir("Esta casilla no puede tener edificios");}
    }

    private void casillaMasRentable(){
        Propiedad prop = (Propiedad) this.tablero.encontrar_casilla(1);
        float masRentable = prop.getRentabilidad();
        for(ArrayList<Casilla> lado : this.tablero.getPosiciones()){
            for(Casilla cas : lado){
                if(cas instanceof Propiedad){
                    prop = (Propiedad) cas;
                    if(masRentable<prop.getRentabilidad()){masRentable = prop.getRentabilidad();}
                }
            }
        }

        for(ArrayList<Casilla> lado : this.tablero.getPosiciones()){
            for(Casilla cas : lado){
                if(cas instanceof Propiedad){
                    prop = (Propiedad) cas;
                    if(masRentable == prop.getRentabilidad()){Juego.consola.imprimir(cas.getNombre() + ", ");}
                }
            }
        }
        consola.imprimir("\n");
    }

    private void grupoMasRentable(){
        ArrayList<Float> masRentable = new ArrayList<Float>(8);
        for(int i=0;i<8;i++){masRentable.add(0f);}
        for(ArrayList<Casilla> lado : this.tablero.getPosiciones()){
            for(Casilla cas : lado){
                if(cas instanceof Solar){
                    Solar sol = (Solar) cas;
                    String color=sol.getGrupo().colorGrupo();
                    switch(color){
                        case "Negro":
                        masRentable.set(0, masRentable.get(0) + sol.getRentabilidad()); 
                        break;
                        case "Rojo":
                        masRentable.set(1, masRentable.get(1) + sol.getRentabilidad());
                        break;
                        case "Verde":
                        masRentable.set(2, masRentable.get(2) + sol.getRentabilidad());
                        break;
                        case "Amarillo":
                        masRentable.set(3, masRentable.get(3) + sol.getRentabilidad());
                        break;
                        case "Azul":
                        masRentable.set(4, masRentable.get(4) + sol.getRentabilidad());
                        break;
                        case "Rosa":
                        masRentable.set(5, masRentable.get(5) + sol.getRentabilidad());
                        break;
                        case "Cian":
                        masRentable.set(6, masRentable.get(6) + sol.getRentabilidad());
                        break;
                        case "Blanco":
                        masRentable.set(7, masRentable.get(7) + sol.getRentabilidad());
                        break;
                    }
                }
            }
        }
        
        float grupoMasRentable=masRentable.get(0);
        for(int i=1;i<8;i++){
            if(grupoMasRentable<masRentable.get(i)) grupoMasRentable = masRentable.get(i);
        }

        for(int i=0;i<8;i++){
            if(masRentable.get(i) == grupoMasRentable){
                if(i==0){ Juego.consola.imprimir("Negro, ");}  
                else if(i==1){ Juego.consola.imprimir("Rojo, ");}
                else if(i==2){ Juego.consola.imprimir("Verde, ");}
                else if(i==3){ Juego.consola.imprimir("Amarillo, ");}
                else if(i==4){ Juego.consola.imprimir("Azul, ");}
                else if(i==5){ Juego.consola.imprimir("Rosa, ");}
                else if(i==6){ Juego.consola.imprimir("Cian, ");}
                else if(i==7){ Juego.consola.imprimir("Blanco, ");}
            } 
        }
        consola.imprimir("\n");
    }

    private void casillaMasFrecuentada(){
        int maximo=0;

        for(ArrayList<Casilla> lado : this.tablero.getPosiciones()){
            for(Casilla cas : lado){
                int suma = cas.frecuenciaVisita();
                if(suma>maximo){maximo=suma;}
            }
        }

        for(ArrayList<Casilla> lado : this.tablero.getPosiciones()){
            for(Casilla cas : lado){
                int suma = cas.frecuenciaVisita();
                if(suma==maximo){Juego.consola.imprimir(cas.getNombre() + ", ");}
            }
        } consola.imprimir("\n");
    }

    private void jugadorMasVueltas(){
        int maximo = this.vueltastotales.get(0);

        for(int i=0;i<this.vueltastotales.size();i++){if(i>maximo) maximo = this.vueltastotales.get(i);}
        for(int i=0;i<this.vueltastotales.size();i++){if(this.vueltastotales.get(i)==maximo) Juego.consola.imprimir(this.jugadores.get(i).getNombre() + ", ");}
        consola.imprimir("\n");
    }

    private void MasVecesDados(){
        int maximo = this.lanzamientostotales.get(0);

        for(int i=0;i<this.lanzamientostotales.size();i++){
            if(i>maximo) maximo = this.lanzamientostotales.get(i);
        } for(int i=0;i<this.lanzamientostotales.size();i++){
            if(this.lanzamientostotales.get(i)==maximo) Juego.consola.imprimir(this.jugadores.get(i).getNombre() + ", ");
        } consola.imprimir("\n");
   }

   private void EnCabeza(){
        ArrayList<Float> fortuna = new ArrayList<>(this.jugadores.size());
        for(int i=0;i<this.jugadores.size();i++) fortuna.add(0f);

        for(int i=0;i<this.jugadores.size();i++){
            fortuna.set(i, fortuna.get(i) + this.jugadores.get(i).getFortuna());
            for(Propiedad cas : this.jugadores.get(i).getPropiedades()){
                if(cas instanceof Propiedad){
                    if(cas instanceof Solar){
                        Solar solar = (Solar) cas;
                        fortuna.set(i, fortuna.get(i) + solar.valorEdificios() + solar.getValor());
                    } else{fortuna.set(i, fortuna.get(i) + cas.getValor());}
                    
                }
            }
        }

        float max=0f;

        for(int i=0;i<fortuna.size();i++){if(max<fortuna.get(i)) max=fortuna.get(i);}
        for(int i=0;i<fortuna.size();i++){if(max==fortuna.get(i)) Juego.consola.imprimir(this.jugadores.get(i).getNombre() + ", ");}
        consola.imprimir("\n");

}
    
    public void estadisticasjuego(){
        Juego.consola.imprimir("estadisticas\n{\n");
        Juego.consola.imprimir("casillaMasRentable: "); casillaMasRentable();
        Juego.consola.imprimir("grupoMasRentable: "); grupoMasRentable();
        Juego.consola.imprimir("casillaMasFrecuentada: "); casillaMasFrecuentada();
        Juego.consola.imprimir("jugadorMasVueltas: "); jugadorMasVueltas();
        Juego.consola.imprimir("jugadorMasVecesDados: "); MasVecesDados();
        Juego.consola.imprimir("jugadorEnCabeza: "); EnCabeza();
        Juego.consola.imprimir("\n}\n");
    }

    public void estadisticasjugador(String nombre) throws Exception{
        for (Jugador i : this.jugadores) {
            if (i.getNombre().equals(nombre)) {
                consola.imprimir(i.toStringestadisticas());
                return;
            }
        } 
        throw new JugadorNoExisteException("El jugador " + nombre + " no existe.");
    }

    public void infotratos(){
        consola.imprimir("Modalidades de trato disponibles");
        consola.imprimir("cambiar (propiedad_1, propiedad_2)");
        consola.imprimir("cambiar (propiedad_1, cantidad_dinero)");
        consola.imprimir("cambiar (cantidad_dinero, propiedad_1)");
        consola.imprimir("cambiar (propiedad_1, propiedad_2 y cantidad_dinero)");
        consola.imprimir("cambiar (propiedad_1 y cantidad_dinero, propiedad_2)");
    }

    private static boolean esNumerico(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public void trato(String jugador, String inter1, String inter2){
        Jugador jug=buscarJugador(jugador);
        Propiedad prop1 = (Propiedad) this.tablero.encontrar_casilla(inter1), prop2 = (Propiedad) this.tablero.encontrar_casilla(inter2);
        if(esNumerico(inter1)){
            if(prop2.getPropietario().equals(jug)){
                consola.imprimir(jugador + ", ¿te doy " + inter1 + "€ y tú me das " + inter2 + "?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1,inter2);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else consola.imprimir("No se puede proponer el trato: " + inter2 + " no pertenece a " + jugador);
        }
        else if(esNumerico(inter2)){
            if(prop1.getPropietario().equals(this.jugadores.get(this.turno))){
                consola.imprimir(jugador + ", ¿te doy " + inter1 + " y tú me das " + inter2 + " €?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1,inter2);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else consola.imprimir("No se puede proponer el trato: " + inter1 + " no pertenece a " + this.jugadores.get(this.turno).getNombre());
        }
        else{
            if(prop1.getPropietario().equals(this.jugadores.get(this.turno)) && prop2.getPropietario().equals(jug)){
                consola.imprimir(jugador + ", ¿te doy " + inter1 + " y tú me das " + inter2 + "?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1,inter2);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else consola.imprimir("No se puede proponer el trato: Las dos propiedades deben pertenecer a ambos jugadores");
        }

        } 
    
    public void trato(String jugador, String inter1, String inter2, String inter3, String inter4){
        Jugador jug = buscarJugador(jugador);
        Propiedad prop1 = (Propiedad) this.tablero.encontrar_casilla(inter1), prop2 = (Propiedad) this.tablero.encontrar_casilla(inter2), prop4 = (Propiedad) this.tablero.encontrar_casilla(inter4);
        if(inter2.equals("y")){
            
            if(prop4.getPropietario().equals(jug) && prop1.getPropietario().equals(this.jugadores.get(this.turno))){
                consola.imprimir(jugador + ", ¿te doy " + inter1 + " y " + inter3 + " € y tú me das " + inter4 + "?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1 + " " + inter3,inter4);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else consola.imprimir("No se puede proponer el trato: Las dos propiedades deben pertenecer a ambos jugadores");}
        else {
            if(prop2.getPropietario().equals(jug) && prop1.getPropietario().equals(this.jugadores.get(this.turno))){
                consola.imprimir(jugador + ", ¿te doy " + inter1 + " y tú me das " + inter2 + " y " + inter4 + " €?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1,inter2 + " "+ inter4);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else consola.imprimir("No se puede proponer el trato: Las dos propiedades deben pertenecer a ambos jugadores");
        }
        }

    public void aceptarTrato(String idtrato) throws Exception{
        Trato tratoaceptar = null;
        for(Trato trato : tratos.get(this.turno)){
            if(trato.getID().equals(idtrato)){
                tratoaceptar = trato; 
            }
        }
        String respuesta = "si";
        if(tratoaceptar == null) consola.imprimir("No se ha podido encontrar el trato introducido"); 
        else{
            String oferta[] = tratoaceptar.getOferta().split(" "), demanda[] = tratoaceptar.getDemanda().split(" ");
            if(oferta.length == 2){
                
                if(tratoaceptar.getPropositor().getFortuna()>=Integer.parseInt(oferta[1])){
                    Propiedad pro1 = (Propiedad) this.tablero.encontrar_casilla(oferta[0]), pro2 = (Propiedad) this.tablero.encontrar_casilla(demanda[0]);
                    if(pro1.isHipotecada()){
                        consola.imprimir(pro1.getNombre() + " está hipotecada ¿Quieres continuar aceptando el trato? (si/no)");
                        respuesta = scanner.nextLine();}
                    if(respuesta.equals("si")){
                        pro1.setPropietario(tratoaceptar.getDestinatario());
                        tratoaceptar.getDestinatario().anhadirPropiedad(pro1);
                        tratoaceptar.getPropositor().eliminarPropiedad(pro1);
                        pro2.setPropietario(tratoaceptar.getPropositor());
                        tratoaceptar.getPropositor().anhadirPropiedad(pro2);
                        tratoaceptar.getDestinatario().eliminarPropiedad(pro2);
                        tratoaceptar.getPropositor().sumarGastos(Float.parseFloat(oferta[1]));
                        tratoaceptar.getDestinatario().sumarFortuna(Float.parseFloat(oferta[1]));
                        tratos.get(this.turno).remove(tratoaceptar);
                        consola.imprimir("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[1] + " € y " + oferta[0]);       
                }
                }
                else throw new SaldoInsuficienteException("El trato no puede ser aceptado: " + tratoaceptar.getPropositor().getNombre() + " no tiene " + oferta[1]+ " €");
        
            }
            else if(demanda.length == 2){
                if(tratoaceptar.getDestinatario().getFortuna()>=Integer.parseInt(demanda[1])){
                    Propiedad pro1 = (Propiedad) this.tablero.encontrar_casilla(oferta[0]),pro2 = (Propiedad) this.tablero.encontrar_casilla(demanda[0]);
                    if(pro1.isHipotecada()){
                        consola.imprimir(pro1.getNombre() + " está hipotecada ¿Quieres continuar aceptando el trato? (si/no)");
                        respuesta = scanner.nextLine();}
                        if(respuesta.equals("si")){
                            pro1.setPropietario(tratoaceptar.getDestinatario());
                            tratoaceptar.getDestinatario().anhadirPropiedad(pro1);
                            tratoaceptar.getPropositor().eliminarPropiedad(pro1);
                            pro2.setPropietario(tratoaceptar.getPropositor());
                            tratoaceptar.getPropositor().anhadirPropiedad(pro2);
                            tratoaceptar.getDestinatario().eliminarPropiedad(pro2);
                            tratoaceptar.getDestinatario().sumarGastos(Float.parseFloat(demanda[1]));
                            tratoaceptar.getPropositor().sumarFortuna(Float.parseFloat(demanda[1]));
                            tratos.get(this.turno).remove(tratoaceptar);
                            consola.imprimir("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " y " + demanda[1]+ " € y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[0]);
                        }
                }
                else throw new SaldoInsuficienteException("El trato no puede ser aceptado: " + tratoaceptar.getDestinatario().getNombre() + " no tiene " + demanda[1] + " €");
            }
            else{
                if(esNumerico(oferta[0])){
                    if(tratoaceptar.getPropositor().getFortuna()>=Integer.parseInt(oferta[0])){
                        Propiedad pro1 = (Propiedad) this.tablero.encontrar_casilla(demanda[0]);
                        pro1.setPropietario(tratoaceptar.getPropositor());
                        tratoaceptar.getPropositor().anhadirPropiedad(pro1);
                        tratoaceptar.getDestinatario().eliminarPropiedad(pro1);
                        tratoaceptar.getPropositor().sumarGastos(Float.parseFloat(oferta[0]));
                        tratoaceptar.getDestinatario().sumarFortuna(Float.parseFloat(oferta[0]));
                        tratos.get(this.turno).remove(tratoaceptar);
                        consola.imprimir("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[0] + " €");
                    }
                    else throw new SaldoInsuficienteException("El trato no puede ser aceptado: " + tratoaceptar.getPropositor().getNombre() + " no tiene " + oferta[0]+ " €");
                }
                else if(esNumerico(demanda[0])){

                    if(tratoaceptar.getDestinatario().getFortuna()>=Integer.parseInt(demanda[0])){
                        Propiedad pro1 = (Propiedad) this.tablero.encontrar_casilla(oferta[0]);
                        if(pro1.isHipotecada()){
                            consola.imprimir(pro1.getNombre() + " está hipotecada ¿Quieres continuar aceptando el trato? (si/no)");
                            respuesta = scanner.nextLine();}
                        if(respuesta.equals("si")){
                            pro1.setPropietario(tratoaceptar.getDestinatario());
                            tratoaceptar.getDestinatario().anhadirPropiedad(pro1);
                            tratoaceptar.getPropositor().eliminarPropiedad(pro1);
                            tratoaceptar.getDestinatario().sumarGastos(Float.parseFloat(demanda[0]));
                            tratoaceptar.getPropositor().sumarFortuna(Float.parseFloat(demanda[0]));
                            tratos.get(this.turno).remove(tratoaceptar);
                            consola.imprimir("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " € y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[0]);
                        }
                    else throw new SaldoInsuficienteException("El trato no puede ser aceptado: " + tratoaceptar.getDestinatario().getNombre() + " no tiene " + demanda[0]+ " €");
                }}
                else{
                    Propiedad pro1 = (Propiedad) this.tablero.encontrar_casilla(oferta[0]), pro2 = (Propiedad) this.tablero.encontrar_casilla(demanda[0]);
                    if(pro1.isHipotecada()){
                        consola.imprimir(pro1.getNombre() + " está hipotecada ¿Quieres continuar aceptando el trato? (si/no)");
                        respuesta = scanner.nextLine();}
                    if(respuesta.equals("si")){
                        pro1.setPropietario(tratoaceptar.getDestinatario());
                        tratoaceptar.getDestinatario().anhadirPropiedad(pro1);
                        tratoaceptar.getPropositor().eliminarPropiedad(pro1);
                        pro2.setPropietario(tratoaceptar.getPropositor());
                        tratoaceptar.getPropositor().anhadirPropiedad(pro2);
                        tratoaceptar.getDestinatario().eliminarPropiedad(pro2);
                        tratos.get(this.turno).remove(tratoaceptar);
                        consola.imprimir("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[0]);
                    }
        }}
    }
    }

    public void tratos(){
        for(Trato t: tratos.get(this.turno)){
            consola.imprimir(t.toString());
        }
    }

    public void eliminarTrato(String idtrato) {
        for (ArrayList<Trato> lista : tratos) {
            lista.removeIf(trato -> 
                trato.getPropositor().equals(this.jugadores.get(this.turno)) 
                && trato.getID().equals(idtrato)
            );
        }
    }
    

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public ArrayList<Avatar> getAvatares() {
        return avatares;
    }

    public void setAvatares(ArrayList<Avatar> avatares) {
        this.avatares = avatares;
    }

    public int getTurno() {
        return turno;
    }

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public int getLanzamientos() {
        return lanzamientos;
    }

    public void setLanzamientos(int lanzamientos) {
        this.lanzamientos = lanzamientos;
    }

    public ArrayList<Integer> getLanzamientostotales() {
        return lanzamientostotales;
    }

    public void setLanzamientostotales(ArrayList<Integer> lanzamientostotales) {
        this.lanzamientostotales = lanzamientostotales;
    }

    public ArrayList<Integer> getVueltastotales() {
        return vueltastotales;
    }

    public void setVueltastotales(ArrayList<Integer> vueltastotales) {
        this.vueltastotales = vueltastotales;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    public Dado getDado1() {
        return dado1;
    }

    public void setDado1(Dado dado1) {
        this.dado1 = dado1;
    }

    public Dado getDado2() {
        return dado2;
    }

    public void setDado2(Dado dado2) {
        this.dado2 = dado2;
    }

    public Jugador getBanca() {
        return banca;
    }

    public void setBanca(Jugador banca) {
        this.banca = banca;
    }

    public boolean isTirado() {
        return tirado;
    }

    public void setTirado(boolean tirado) {
        this.tirado = tirado;
    }

    public boolean isSolvente() {
        return solvente;
    }

    public void setSolvente(boolean solvente) {
        this.solvente = solvente;
    }

    public boolean isFturno() {
        return fturno;
    }

    public void setFturno(boolean fturno) {
        this.fturno = fturno;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public ArrayList<Edificio> getEdificiosConstruidos() {
        return edificiosConstruidos;
    }

    public void setEdificiosConstruidos(ArrayList<Edificio> edificiosConstruidos) {
        this.edificiosConstruidos = edificiosConstruidos;
    }

    public int getDoblesContador() {
        return doblesContador;
    }

    public void setDoblesContador(int doblesContador) {
        this.doblesContador = doblesContador;
    }

    public ArrayList<ArrayList<Trato>> getTratos() {
        return tratos;
    }

    public void setTratos(ArrayList<ArrayList<Trato>> tratos) {
        this.tratos = tratos;
    }

    public ConsolaNormal getConsola() {
        return consola;
    }
      
    }