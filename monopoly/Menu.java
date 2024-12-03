/*MODIFICACIONES PENDIENTES
 * - Movimientos avanzados
 * - Estadísticas juego / jugador
 * - Mandar a la cárcel si saca 3 dados dobles
 * - POR DETERMINAR
 */

package monopoly;

import java.util.ArrayList;
import java.util.Arrays;

import partida.*;
import partida.avatares.Avatar;

import java.util.Scanner;

import monopoly.casillas.CasillaX;
import monopoly.casillas.Grupo;
import monopoly.edificios.Edificio;

public class Menu {

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
    private ArrayList<Edificio> lista_edificios;
    private int doblesContador = 0;
    private ArrayList<ArrayList<Trato>> tratos;

    public Menu() {
        // Inicializar las listas y otros atributos
        this.jugadores = new ArrayList<Jugador>();
        this.lanzamientostotales = new ArrayList<Integer>();
        this.vueltastotales = new ArrayList<Integer>();
        this.avatares = new ArrayList<Avatar>();
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.scanner = new Scanner(System.in);
        this.lista_edificios = new ArrayList<Edificio>();
        this.tratos = new ArrayList<ArrayList<Trato>>();
        iniciarPartida();
        while(this.fin){
            fturno = false;
            this.Turnos();
        }
        this.scanner.close();
    }

    // Método para inciar una partida: crea los jugadores y avatares.
    private void iniciarPartida() {
        System.out.println("Bienvenido al MONOPOLY ETSE by Antón Expósito, Helena Franco & Hugo Gilsanz\n");
        this.banca = new Jugador();
        this.banca.setNombre("banca");
        this.tablero = new Tablero(banca);
        int jr;
        String js = "0";
        while(!js.equals("2") && !js.equals("3") && !js.equals("4") && !js.equals("5") && !js.equals("6")){
            System.out.println("\nIntroduce el número de jugadores para empezar la partida (entre 2 y 6): ");
            js = this.scanner.nextLine();
            if(!js.equals("2") && !js.equals("3") && !js.equals("4") && !js.equals("5") && !js.equals("6")){
                System.out.println("Introduce un número válido de jugadores.");
            }
        }
        
        jr = Integer.parseInt(js);

        for (int i = 0; i < jr; i++) {
            if (tratos.size() <= i) {
                tratos.add(new ArrayList<Trato>());  
            }
        }

        String comando; //= this.scanner.nextLine();
        while(jr != 0){
            System.out.println("\nJugadores restantes: " + jr);
            System.out.println("Para añadir un jugador escriba: crear jugador \"NOMBRE\" \"TIPO\"\n");
            comando = this.scanner.nextLine();
            String[] partes = comando.split(" ");
            ArrayList<String> tipos = new ArrayList<>(Arrays.asList("sombrero", "coche", "esfinge", "pelota"));
            if (partes.length == 4 && partes[0].equals("crear") && partes[1].equals("jugador") && tipos.contains(partes[3])){
                crearJugador(partes[2],partes[3]);
                --jr;
            } else {
                System.out.println("\nFormato incorrecto\n");
                if(!tipos.contains(partes[3])){System.out.println("Tipos de jugador permitidos: coche, esfinge, pelota y sombrero\n");}
            }
        }
        System.out.println("\nJugadores suficientes.\nComienza la partida...\n");
        verTablero();
    }

    // Método para pedir y analizar los comandos en cada turno
    private void Turnos(){
        String comando;
        do{
            if (this.avatares.get(this.turno).getTurnosBloqueados() > 0) {
                this.avatares.get(this.turno).setTurnosBloqueados(this.avatares.get(this.turno).getTurnosBloqueados()-1);
                System.out.println(this.jugadores.get(this.turno).getNombre() + " no puede lanzar los dados");
                this.tirado=true;
                acabarTurno();
                break;
            }
            System.out.println(this.toString());
            comando = this.scanner.nextLine();
            while(comando.isEmpty()){comando=this.scanner.nextLine();}
            analizarComando(comando);
            if(!this.fin){break;}
        }while(!this.fturno);
        if(this.fin){verTablero();}
    }

    // Método para aumentar el precio cada vez que todos los jugadores dan cuatro vueltas
    private void aumentaPrecio() {
        for(int i=0; i<40; i++){
            if(this.tablero.encontrar_casilla(i).getDuenho().equals(banca) && this.tablero.encontrar_casilla(i).getTipo().equals("Solar")){
                this.tablero.encontrar_casilla(i).setValor(this.tablero.encontrar_casilla(i).getValor()+0.05f*this.tablero.encontrar_casilla(i).getValor());
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        output.append("\nMENÚ\n\n");
        //output.append("crear jugador \"NOMBRE\" \"TIPO\"\n");
        output.append("jugador\n");
        output.append("lanzar dados\n");
        output.append("cambiar modo\n");
        output.append("listar jugadores\n");
        output.append("listar avatares\n");
        output.append("describir jugador \"NOMBRE\"\n");
        output.append("describir avatar \"ID\"\n");
        output.append("describir \"CASILLA\"\n");
        output.append("estadisticas jugador \"NOMBRE\"\n");
        output.append("estadisticas juego\n");
        output.append("listar enventa\n");
        output.append("comprar " + this.avatares.get(this.turno).getLugar().getNombre() + "\n");
        output.append("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre() + "\n");
        output.append("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre() + "\n");
        output.append("edificar \"TIPOEDIFICIO\"\n");
        output.append("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"\n");
        output.append("listar edificios\n");
        output.append("listar edificios \"COLORGRUPO\"\n");
        output.append("trato \"NOMBREJUGADOR\": cambiar (\"____, ____\") \n");
        output.append("info tipos tratos\n");
        output.append("aceptar \"IDTRATO\"\n");
        output.append("declararse en bancarrota\n");
        output.append("salir carcel\n");
        output.append("ver tablero\n");
        output.append("acabar turno\n");
        return output.toString();
    }

    /*Método que interpreta el comando introducido y toma la accion correspondiente.
    * Parámetro: cadena de caracteres (el comando).
    */
    private void analizarComando(String comando) {
        ArrayList<String> edificios = new ArrayList<>(Arrays.asList("casa", "hotel", "piscina", "pista"));
        String[] partes = comando.split(" ");
        /*if (partes.length == 4 && partes[0].equals("crear") && partes[1].equals("jugador")){
            crearJugador(partes[2],partes[3]);
        } else */if(partes[0].equals("jugador")){
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
            salirCarcel();
        } else if(partes[0].equals("describir") && partes.length >= 2){
            if(partes[1].equals("jugador") && partes.length == 3){
                descJugador(partes);
            } else if(partes[1].equals("avatar") && partes.length == 3){
                descAvatar(partes[2]);
            } else if(partes.length == 2){
                descCasilla(partes[1]);
            } else{
                System.out.println("Comando inválido\n");
            }
        } else if(partes.length == 3 && partes[0].equals("estadisticas") && partes[1].equals("jugador")){
            estadisticasjugador(partes[2]);
        } else if(partes.length == 2 && partes[0].equals("listar") && partes[1].equals("enventa")){
            listarVenta();
        } else if(partes.length == 2 && partes[0].equals("comprar") && partes[1].equals(this.avatares.get(this.turno).getLugar().getNombre())){
            comprar(this.avatares.get(this.turno).getLugar().getNombre());
        } else if(partes.length == 2 && partes[0].equals("hipotecar") && partes[1].equals(this.avatares.get(this.turno).getLugar().getNombre())){
            hipotecar(partes[1]);  
        } else if(partes.length == 2 && partes[0].equals("deshipotecar") && partes[1].equals(this.avatares.get(this.turno).getLugar().getNombre())){
            deshipotecar(partes[1]);
        } else if(partes.length == 3 && partes[0].equals("declararse") && partes[1].equals("en") && partes[2].equals("bancarrota")){
            bancarrota(this.jugadores.get(this.turno), this.avatares.get(this.turno).getLugar().getDuenho());
        } else if(partes.length == 2 && partes[0].equals("edificar") && edificios.contains(partes[1])){
            edificarCasilla(partes[1]);
        } else if(partes.length >= 2 && partes[0].equals("listar") && partes[1].equals("edificios")){
            ArrayList<String> grupos = new ArrayList<String>(8);
            grupos.add("negro"); grupos.add("rojo"); grupos.add("verde"); grupos.add("amarillo");
            grupos.add("azul"); grupos.add("rosa"); grupos.add("cian"); grupos.add("blanco");
            
            if(partes.length == 2){
                listarEdificios();
            } else if(grupos.contains(partes[2]) && partes.length == 3){
                listarEdificiosGrupo(partes[2]);
            } else{
                System.out.println("Comando inválido\n");
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
            aceptarTrato(partes[1]);
        } 
        else if(partes.length == 3 && partes[0].equals("trucar")){
            if(!this.jugadores.get(this.turno).getAvatar().getmovAvanzado()) lanzarDados(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
            else lanzarDadosEsp(Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
        } else if(partes[0].equals("exit")){
            System.exit(0);
        } else{
            System.out.println("Comando inválido\n");
        }
        
    }

    private Jugador buscarJugador(String nombre){
        for (Jugador i : this.jugadores) {
            if (i.getNombre().equals(nombre)) {
                return i;
            }
        }
        System.out.println("El jugador " + nombre + " no está en la partida.");
        return banca;
    }
    
    /*Método que realiza las acciones asociadas al comando 'crear jugador'.
    * Parámetros: nombre del jugador y tipo de avatar.
    */
    private void crearJugador(String nombre, String tipo) {
        if (this.jugadores.size()<6){
            CasillaX inicio = this.tablero.encontrar_casilla(0);
            inicio.setPosicion(0);
            Jugador nJugador = new Jugador(nombre, tipo, inicio, avatares);
            this.jugadores.add(nJugador);
            this.lanzamientostotales.add(0);
            this.vueltastotales.add(0);
            this.tablero.encontrar_casilla(0).anhadirAvatar(nJugador.getAvatar());
            for (int i = 0; i < 40; i++) {
                this.tablero.encontrar_casilla(i).getCaidas().add(0);
            }
            System.out.println("nombre: " + nJugador.getNombre() + ",\navatar: " + nJugador.getAvatar().getID());
        } else {
            System.out.println("Número máximo de jugadores alcanzado. No se pueden crear jugadores nuevos.");
        }
    }

    /*Método que realiza las acciones asociadas al comando 'jugador'.*/
    private void turnoJugador() {
        System.out.println("nombre: " + this.jugadores.get(this.turno).getNombre() + ",\navatar: " + this.avatares.get(this.turno).getID());
    }

    /*Método que realiza las acciones asociadas al comando 'describir jugador'.
    * Parámetro: comando introducido
     */
    private void descJugador(String[] partes) {
        String nombreJugador = partes[2];
        for (Jugador i : this.jugadores) {
            if (i.getNombre().equals(nombreJugador)) {
                System.out.println("{\n" + i.toString() + "}"); 
                return;
            }
        }
        System.out.println("El jugador " + nombreJugador + " no está en la partida.");
    }

    /*Método que que muestra la información sobre un jugador.
    * Parámetro: número (índice) que representa su posición en la lista de jugadores
     */
    private void descJugador(int numero) {
        Jugador dJugador = this.jugadores.get(numero);
        System.out.println(dJugador.toString()); 
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    private void listarJugadores() {
        for(int i=0; i<this.jugadores.size(); i++){
            System.out.println("{\n");
            descJugador(i);
            System.out.println((i == (this.jugadores.size()-1) ? "}\n" : "},\n"));
        }
    }

    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
    * Parámetro: id del avatar a describir.
    */
    private void descAvatar(String ID) {
        for (Avatar i : this.avatares) {
            if (i.getID().equals(ID)) {
                System.out.println("{\n" + i.toString() + "}"); 
                return;
            }
        }
        System.out.println("El avatar con ID " + ID + " no está en la partida");
    }

    /*Método que muestra la información sobre un avatar.
    * Parámetro: número (índice) que representa su posición en la lista de jugadores
     */
    private void descAvatar(int numero) {
        Avatar dAvatar = this.avatares.get(numero);
        System.out.println(dAvatar.toString()); 
    }

    // Método que realiza las acciones asociadas al comando 'listar avatares'.
    private void listarAvatares() {
        for(int i=0; i<this.avatares.size(); i++){
            System.out.println("{\n");
            descAvatar(i);
            System.out.println((i == (this.avatares.size()-1) ? "}\n" : "},\n"));
        }
    }

    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
    * Parámetros: nombre de la casilla a describir.
    */
    private void descCasilla(String nombre) {
        CasillaX dCasilla = this.tablero.encontrar_casilla(nombre);
        if(dCasilla!=null){
            System.out.print("\n" + dCasilla.toString() + "\nVeces que han caído en esta casilla: ");
            for (int i = 0; i < jugadores.size(); i++) {
                System.out.print("[" + jugadores.get(i).getNombre() + ", " + dCasilla.getCaidas().get(i) + "]" + (i == (this.jugadores.size()-1) ? "" : ", "));
            } System.out.print("\n");
        }   
    }

    /*Método para cambiar de modo de lanzamiento (normal o avanzado) */
    private void cambiarModo(){
        if(this.jugadores.get(this.turno).getAvatar().getmovAvanzado()){
            System.out.println("A partir de ahora el avatar" + this.jugadores.get(this.turno).getAvatar() + ", de tipo" + this.jugadores.get(this.turno).getAvatar().getTipo() + ", se moverá en modo avanzado.");
        } else{System.out.println("A partir de ahora el avatar " + this.jugadores.get(this.turno).getAvatar().getID() + ", de tipo " + this.jugadores.get(this.turno).getAvatar().getTipo() + ", se moverá en modo normal.");}
        this.jugadores.get(this.turno).getAvatar().setmovAvanzado(this.jugadores.get(this.turno));
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    private void lanzarDados() {
        if(this.tirado){System.out.println("El jugador actual ya ha realizado todas sus tiradas.");}

        if (!this.tirado){
            int d1 = this.dado1.hacerTirada(), d2 = this.dado2.hacerTirada();
            if(!this.jugadores.get(this.turno).getEncarcelado()){
                int desp = d1 + d2;
                int mov = (this.avatares.get(this.turno).getLugar().getPosicion() + desp) % 40;
                
                String origen = this.avatares.get(this.turno).getLugar().getNombre(), destino = this.tablero.encontrar_casilla(mov).getNombre();
                int oint = this.avatares.get(this.turno).getLugar().getPosicion(), dint = this.tablero.encontrar_casilla(mov).getPosicion();

                System.out.println("El avatar " + this.avatares.get(this.turno).getID() + " avanza " + desp + " posiciones, desde " + origen + " hasta " + destino);
                this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                
                //this.tablero.encontrar_casilla(oint).eliminarAvatar(this.avatares.get(this.turno));
                //this.tablero.encontrar_casilla(mov).anhadirAvatar(this.avatares.get(this.turno));
                this.jugadores.get(this.turno).getAvatar().moverAvatar(this.tablero.getPosiciones(), desp);
                this.tablero.encontrar_casilla(destino).getCaidas().set(this.turno, this.tablero.encontrar_casilla(destino).getCaidas().get(this.turno)+1);
                
                this.solvente = this.tablero.encontrar_casilla(destino).evaluarCasilla(this.avatares.get(this.turno).getJugador(), banca, desp);

                if(this.tablero.encontrar_casilla(destino).getComunidad()){
                    this.solvente = this.tablero.encontrar_casilla(destino).cajaComunidad(this, this.avatares.get(this.turno).getJugador(), banca);
                } else if(this.tablero.encontrar_casilla(destino).getSuerte()){
                    this.solvente = this.tablero.encontrar_casilla(destino).casSuerte(this.tablero, this.avatares.get(this.turno).getJugador(), banca, this.turno);
                }
                
                if(this.fin){
                    if(this.tablero.encontrar_casilla(destino).getNombre().equals("IrCarcel") && this.jugadores.get(this.turno).getEncarcelado()){
                        this.jugadores.get(this.turno).encarcelar(this.tablero.getPosiciones());
                    }
                    if(this.tablero.encontrar_casilla(destino).getTipo().equals("Impuesto")){
                        this.tablero.encontrar_casilla("Parking").sumarValor(this.jugadores.get(this.turno).getAvatar().getLugar().getImpuesto());
                    }
                verTablero();
                } else{ System.out.println("El jugador " + this.jugadores.get(this.turno).getNombre() + " está encarcelado. No se desplaza.");}

                if (d1 == d2){
                    ++this.lanzamientos;
                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                    this.tirado = false;
                    if (this.avatares.get(this.turno).getJugador().getEncarcelado()){
                        this.avatares.get(this.turno).getJugador().setEncarcelado(false);
                        this.avatares.get(this.turno).getJugador().setTiradasCarcel(0);
                        System.out.println(this.avatares.get(this.turno).getJugador().getNombre() + " sale de la cárcel. Puede lanzar los dados.");
                    } else{System.out.println("Puede volver a lanzar los dados.");} 

                    if (this.lanzamientos == 3){
                        this.avatares.get(this.turno).getJugador().encarcelar(this.tablero.getPosiciones());
                        System.out.println(this.jugadores.get(this.turno).getNombre() + " ha sacado tres dobles seguidos y va a la cárcel.");
                        this.lanzamientos = 0;
                        this.tirado = true;
                    }
                } else{this.tirado = true;}

                
                if(this.tablero.encontrar_casilla(oint).getPosicion()>this.tablero.encontrar_casilla(dint).getPosicion()){
                    this.avatares.get(this.turno).getJugador().setVueltas(this.avatares.get(this.turno).getJugador().getVueltas()+1);
                    this.avatares.get(this.turno).getJugador().sumarFortuna(Valor.SUMA_VUELTA);
                    this.avatares.get(this.turno).getJugador().getEstadisticas().set(4, this.avatares.get(this.turno).getJugador().getEstadisticas().get(4) + Valor.SUMA_VUELTA);
                    this.vueltastotales.set(this.turno,this.vueltastotales.get(this.turno) + 1);
                    
                    System.out.println("El jugador " + this.jugadores.get(this.turno).getNombre() + " ha cruzado la casilla de Salida. Recibe " + Valor.SUMA_VUELTA + "€.");
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

    private void lanzarDadosEsp(int valor1, int valor2) {
        if (this.tirado) {System.out.println("El jugador actual ya ha realizado todas sus tiradas.");}
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

                            System.out.println("El avatar " + this.avatares.get(this.turno).getID() + " retrocede " + j + " posiciones, desde " + origen + " hasta " + destino);
                            this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                        
                            actualizar(mov,origen,destino,oint,dint,d1,d2,((-j % 40) + 40) % 40);
                            if ((this.avatares.get(this.turno).getLugar().getTipo().equals("Solar") ||
                                this.avatares.get(this.turno).getLugar().getTipo().equals("Transporte") ||
                                this.avatares.get(this.turno).getLugar().getTipo().equals("Servicio"))) {
                                
                                System.out.println("continuar ");
                                System.out.println("comprar " + this.avatares.get(this.turno).getLugar().getNombre());
                                System.out.println("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                System.out.println("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                System.out.println("edificar \"TIPOEDIFICIO\"");
                                System.out.println("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"");

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

                            System.out.println("El avatar " + this.avatares.get(this.turno).getID() + " avanza " + j + " posiciones, desde " + origen + " hasta " + destino);
                            this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);

                            actualizar(mov,origen,destino,oint,dint,d1,d2,j);
                            if ((this.avatares.get(this.turno).getLugar().getTipo().equals("Solar") ||
                                this.avatares.get(this.turno).getLugar().getTipo().equals("Transporte") ||
                                this.avatares.get(this.turno).getLugar().getTipo().equals("Servicio"))) {
                                
                                System.out.println("continuar ");
                                System.out.println("comprar " + this.avatares.get(this.turno).getLugar().getNombre());
                                System.out.println("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                System.out.println("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                System.out.println("edificar \"TIPOEDIFICIO\"");
                                System.out.println("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"");

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
                            System.out.println("El avatar ha sacado dobles tres veces seguidas y va a la cárcel.");
                            //acabarTurno();
                            return;
                        } else{
                            System.out.print("Has sacado doble, puedes volver a lanzar. Valor dados: ");
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

                        System.out.println("El avatar " + this.avatares.get(this.turno).getID() + " retrocede " + desp + " posiciones, desde " + origen + " hasta " + destino);
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
                        
                                System.out.println("El avatar " + this.avatares.get(this.turno).getID() + " avanza " + desp + " posiciones, desde " + origen + " hasta " + destino);
                                this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                        
                                actualizar(mov, origen, destino, oint, dint, d1, d2, desp);
                        
                                if((this.avatares.get(this.turno).getLugar().getTipo().equals("Solar") || this.avatares.get(this.turno).getLugar().getTipo().equals("Transporte") || this.avatares.get(this.turno).getLugar().getTipo().equals("Servicio")) && !compraRealizada) {
                                    System.out.println("continuar ");
                                    System.out.println("comprar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    System.out.println("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    System.out.println("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    System.out.println("edificar \"TIPOEDIFICIO\"");
                                    System.out.println("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"");
                        
                                    String comando = this.scanner.nextLine();
                                    String[] partes = comando.split(" ");
                                    while (comando.isEmpty()) {comando = this.scanner.nextLine();}
                                    analizarComando(comando);
                                    if(partes[0].equals("comprar")) compraRealizada = true;
                        
                                } else if(this.avatares.get(this.turno).getLugar().getTipo().equals("Solar")){
                                    System.out.println("continuar ");
                                    System.out.println("hipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    System.out.println("deshipotecar " + this.avatares.get(this.turno).getLugar().getNombre());
                                    System.out.println("edificar \"TIPOEDIFICIO\"");
                                    System.out.println("vender \"TIPOEDIFICIO\"" + this.avatares.get(this.turno).getLugar().getNombre() + " \"CANTIDAD\"");
                                    String comando = this.scanner.nextLine();
                                    while (comando.isEmpty()) {comando = this.scanner.nextLine();}
                                    analizarComando(comando);
                                }
                        
                                tiradasExtras++;
                        
                                if (tiradasExtras < 3) {
                                    System.out.print("Vuelves a lanzar los dados. Valor dados: ");
                                    d1 = scanner.nextInt();
                                    d2 = scanner.nextInt();
                                    desp = d1 + d2;
                                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                                    doblesPermitido = false;
                                } else if (tiradasExtras == 3) {
                                    System.out.print("Última tirada del turno. Valor dados: ");
                                    d1 = scanner.nextInt();
                                    d2 = scanner.nextInt();
                                    desp = d1 + d2;
                                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                                    if (d1==d2){doblesPermitido = true;}
                                
                                } if (doblesPermitido && d1 == d2 && tiradasExtras==4) {
                                    System.out.print("Has sacado dobles en la última tirada. Se permite una tirada adicional.");
                                    doblesPermitido = false;
                                    System.out.print("Vuelves a lanzar los dados. Valor dados: ");
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

    private void actualizar(int mov, String origen, String destino, int oint, int dint, int d1, int d2, int desp) {
        this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                
        //this.tablero.encontrar_casilla(oint).eliminarAvatar(this.avatares.get(this.turno));
        //this.tablero.encontrar_casilla(mov).anhadirAvatar(this.avatares.get(this.turno));
        this.jugadores.get(this.turno).getAvatar().moverAvatar(this.tablero.getPosiciones(), desp);
        this.tablero.encontrar_casilla(destino).getCaidas().set(this.turno, this.tablero.encontrar_casilla(destino).getCaidas().get(this.turno)+1);
        
        this.solvente = this.tablero.encontrar_casilla(destino).evaluarCasilla(this.avatares.get(this.turno).getJugador(), banca, desp);

        if(this.tablero.encontrar_casilla(destino).getComunidad()){
            this.solvente = this.tablero.encontrar_casilla(destino).cajaComunidad(this, this.avatares.get(this.turno).getJugador(), banca);
        } else if(this.tablero.encontrar_casilla(destino).getSuerte()){
            this.solvente = this.tablero.encontrar_casilla(destino).casSuerte(this.tablero, this.avatares.get(this.turno).getJugador(), banca, this.turno);
        }
        
        if(this.fin){
            if(this.tablero.encontrar_casilla(destino).getNombre().equals("IrCarcel") && this.jugadores.get(this.turno).getEncarcelado()){
                this.jugadores.get(this.turno).encarcelar(this.tablero.getPosiciones());
            }
            if(this.tablero.encontrar_casilla(destino).getTipo().equals("Impuesto")){
                this.tablero.encontrar_casilla("Parking").sumarValor(this.jugadores.get(this.turno).getAvatar().getLugar().getImpuesto());
            }
        verTablero();
        } else{ System.out.println("El jugador " + this.jugadores.get(this.turno).getNombre() + " está encarcelado. No se desplaza.");}
        
        if(this.tablero.encontrar_casilla(oint).getPosicion()>this.tablero.encontrar_casilla(dint).getPosicion() && d1+d2>4){
            this.avatares.get(this.turno).getJugador().setVueltas(this.avatares.get(this.turno).getJugador().getVueltas()+1);
            this.avatares.get(this.turno).getJugador().sumarFortuna(Valor.SUMA_VUELTA);
            this.avatares.get(this.turno).getJugador().getEstadisticas().set(4, this.avatares.get(this.turno).getJugador().getEstadisticas().get(4) + Valor.SUMA_VUELTA);

            System.out.println("El jugador " + this.jugadores.get(this.turno).getNombre() + " ha cruzado la casilla de Salida. Recibe " + Valor.SUMA_VUELTA + "€.");
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
    private void comprar(String nombre){
        if (this.tablero.encontrar_casilla(nombre).getTipo().equals("Solar") || this.tablero.encontrar_casilla(nombre).getTipo().equals("Servicio") || this.tablero.encontrar_casilla(nombre).getTipo().equals("Transporte")){
            this.avatares.get(this.turno).getLugar().comprarCasilla(this.avatares.get(this.turno).getJugador(),banca);
        } else{
            System.out.println("Esta casilla no se puede comprar.");
        }
    }

    private void hipotecar(String nombre){
        if(this.tablero.encontrar_casilla(nombre).getTipo().equals("Solar") || this.tablero.encontrar_casilla(nombre).getTipo().equals("Servicio") || this.tablero.encontrar_casilla(nombre).getTipo().equals("Transporte")){
            this.avatares.get(this.turno).getLugar().hipotecarCasilla(this.jugadores.get(this.turno), banca);
        } else{System.out.println("Esta casilla no se puede hipotecar");}
    }

    private void deshipotecar(String nombre){
        if(this.tablero.encontrar_casilla(nombre).getTipo().equals("Solar") || this.tablero.encontrar_casilla(nombre).getTipo().equals("Servicio") || this.tablero.encontrar_casilla(nombre).getTipo().equals("Transporte")){
            this.avatares.get(this.turno).getLugar().deshipotecarCasilla(this.jugadores.get(this.turno), banca);
        } else{System.out.println("Esta casilla no se puede deshipotecar");}
    }

    private void bancarrota(Jugador solicitante, Jugador demandante){
        if(solicitante.equals((demandante))){demandante = this.banca;}
        ArrayList<CasillaX> propiedades = new ArrayList<>(solicitante.getPropiedades());
        for (CasillaX cas : propiedades) {
            ArrayList<Edificio> edificiosCopia = new ArrayList<>(cas.getLista_edificio());
            for (Edificio ed : edificiosCopia) {
                cas.getLista_edificio().remove(ed); // Eliminar de la lista original
            }

            for (int i = 0; i < cas.getEdificios().size(); i++) {
                cas.getEdificios().set(i, 0);
            }
            for (int j = 0; j < cas.getGrupo().getEdificios().size(); j++) {
                cas.getGrupo().getEdificios().set(j, 0);
            }

            ArrayList<Edificio> listaEdificiosCopia = new ArrayList<>(lista_edificios);
            for (Edificio ed : listaEdificiosCopia) {
                if (ed.getUbicacion().equals(cas)) {
                    lista_edificios.remove(ed);
                    solicitante.venderEdificio(ed);
                }
            }
            
            solicitante.eliminarPropiedad(cas);
            demandante.anhadirPropiedad(cas);
            cas.setDuenho(demandante);
        }

        float deuda = solicitante.getFortuna();
        demandante.sumarFortuna(deuda);
        solicitante.sumarGastos(deuda);
        if (demandante.getNombre().equals("banca")){
            System.out.println("El jugador " + solicitante.getNombre() + " se ha declarado en bancarrota. Sus propiedades pasan a estar de nuevo en venta al precio al que estaban.");
        } else{
            System.out.println("El jugador " + solicitante.getNombre() + " se ha declarado en bancarrota. Sus propiedades y fortuna pasan al jugador " + demandante.getNombre());
        }
        
        System.out.println("El jugador " + solicitante.getNombre() + " sale de la partida.");
        solicitante.getAvatar().getLugar().eliminarAvatar(solicitante.getAvatar()); 
        this.avatares.remove(this.turno);
        this.jugadores.remove(this.turno);
        
        this.fturno = true;
        this.tirado = false;
        
        if(this.turno>=this.jugadores.size()) this.turno = 0;
        this.fin =!((this.jugadores.size() == 1) || (this.jugadores.size() == 0));
        if (this.fin){
            System.out.println("\nEl jugador actual es " + this.avatares.get(this.turno).getJugador().getNombre());
        } else{
            System.out.print("La partida ha terminado. ");
            if(this.jugadores.size()==0){System.out.print("Todos los jugadores han perdido\n");
            } else{System.out.print("El jugador " + this.jugadores.get(0).getNombre() + " ha ganado\n");}
        }
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'. 
    private void salirCarcel() {
        if (this.avatares.get(this.turno).getJugador().getEncarcelado()){
            if(this.avatares.get(this.turno).getJugador().getFortuna() >= (0.25f*Valor.SUMA_VUELTA)){  
                System.out.println(this.avatares.get(this.turno).getJugador().getNombre() + " paga " + (0.25f*Valor.SUMA_VUELTA) + "€ y sale de la cárcel."); 
                this.jugadores.get(this.turno).getEstadisticas().set(1, this.jugadores.get(this.turno).getEstadisticas().get(1) + (0.25f*Valor.SUMA_VUELTA));
                this.avatares.get(this.turno).getJugador().setEncarcelado(false);
            } else{
                System.out.println(this.avatares.get(this.turno).getJugador().getNombre() + " no tiene el dinero suficiente para salir de la cárcel.");
            }  
        } else{
            System.out.println("El jugador no se encuentra en la cárcel");
        }
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    private void listarVenta() {
        ArrayList<CasillaX> lista = new ArrayList<CasillaX>();

        for(int i=0;i<40;i++){
            if(this.tablero.encontrar_casilla(i).getDuenho().equals(banca) && this.tablero.encontrar_casilla(i).getTipo().equals("Solar") || this.tablero.encontrar_casilla(i).getTipo().equals("Servicio") || this.tablero.encontrar_casilla(i).getTipo().equals("Transporte")){
                lista.add(this.tablero.encontrar_casilla(i));
            }
        }
        
        if(lista.size()==0){
            System.out.print("No hay propiedades en venta");
        }
        
        else{
            for(CasillaX cas : lista){
                System.out.println(cas.getNombre() + "{");
                System.out.println("Tipo: " + cas.getTipo());
                if(cas.getTipo().equals("Solar")){
                    System.out.println("Grupo: " + cas.getColorGrupo());
                }
                System.out.println("Valor: " + cas.getValor());
                System.out.println((lista.indexOf(cas) == (lista.size()-1) ? "}\n" : "},\n"));
            }
        }
    }
    
    private void edificarCasilla(String edificio){
        if(!edificio.equals("casa") && !edificio.equals("hotel") && !edificio.equals("piscina") && !edificio.equals("pista")){System.out.println("Comando inválido\n");}
        else{
            if(!avatares.get(this.turno).getLugar().getTipo().equals("Solar")){
                System.out.println("Esta casilla no es edificable.");
            } else if(!avatares.get(this.turno).getLugar().getDuenho().equals(jugadores.get(this.turno))){
                System.out.println("El jugador " + jugadores.get(this.turno).getNombre() + " no puede edificar en esta casilla porque no es de su propiedad.");
            } else if(avatares.get(this.turno).getLugar().getCaidas().get(this.turno)<2 && !avatares.get(this.turno).getLugar().getGrupo().esDuenhoGrupo(avatares.get(this.turno).getJugador())){
                System.out.println("El jugador " + jugadores.get(this.turno).getNombre() + " no puede edificar en esta casilla todavía.");
            } else if(avatares.get(this.turno).getLugar().getGrupo().esDuenhoGrupo(avatares.get(this.turno).getJugador())){
                avatares.get(this.turno).getLugar().construirEdificio(edificio, jugadores.get(this.turno), banca, lista_edificios);
            } else{avatares.get(this.turno).getLugar().construirEdificio(edificio, jugadores.get(this.turno), banca, lista_edificios);}
        }
    }

    private void descEdificio(int numero) {
        Edificio dEdificio = this.lista_edificios.get(numero);
        System.out.println(dEdificio.toString()); 
    }

    private void listarEdificios(){
        if(this.lista_edificios.isEmpty()){System.out.println("No hay edificios construídos.\n");}
        else{
        for(int i=0; i<this.lista_edificios.size(); i++){
            System.out.println("{");
            descEdificio(i);
            System.out.println((i == (this.lista_edificios.size()-1) ? "}\n" : "},\n"));
        }}
    }

    private void listarEdificiosGrupo(String grupo) {
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
                System.out.println("Color de grupo no válido.");
                return;
        }
    
        Grupo grupoActual = this.tablero.getGrupos().get(key);
        ArrayList<CasillaX> casillasGrupo = grupoActual.getMiembros();
        ArrayList<Integer> contadorEdificios = new ArrayList<>(Arrays.asList(0, 0, 0, 0)); 
    
        for (CasillaX casilla : casillasGrupo) {
            System.out.println("{");
            System.out.println("propiedad: " + casilla.getNombre() + ",");
            contadorEdificios.set(0, contadorEdificios.get(0) + listarEdificiosPorTipo(casilla, "casa"));
            contadorEdificios.set(1, contadorEdificios.get(1) + listarEdificiosPorTipo(casilla, "hotel"));
            contadorEdificios.set(2, contadorEdificios.get(2) + listarEdificiosPorTipo(casilla, "piscina"));
            contadorEdificios.set(3, contadorEdificios.get(3) + listarEdificiosPorTipo(casilla, "pista de deporte"));
            System.out.println("alquiler: " + casilla.getImpuesto());
            System.out.println("}\n");
        }
    
        System.out.println("\nEdificios que aún se pueden construir en este grupo:");
        
        if (contadorEdificios.get(0) < grupoActual.getNumCasillas()) {
            System.out.println("\tCasas: " + (grupoActual.getNumCasillas() - contadorEdificios.get(0)));
        } if (contadorEdificios.get(1) < grupoActual.getNumCasillas()) {
            System.out.println("\tHoteles: " + (grupoActual.getNumCasillas() - contadorEdificios.get(1)));
        } if (contadorEdificios.get(2) < grupoActual.getNumCasillas()) {
            System.out.println("\tPiscinas: " + (grupoActual.getNumCasillas() - contadorEdificios.get(2)));
        } if (contadorEdificios.get(3) < grupoActual.getNumCasillas()) {
            System.out.println("\tPistas de deporte: " + (grupoActual.getNumCasillas() - contadorEdificios.get(3)));
        }
    
        if (contadorEdificios.get(0) >= grupoActual.getNumCasillas() && 
            contadorEdificios.get(1) == grupoActual.getNumCasillas() &&
            contadorEdificios.get(2) == grupoActual.getNumCasillas() &&
            contadorEdificios.get(3) == grupoActual.getNumCasillas()) {
            System.out.println("No se pueden construir más edificios en este grupo.");
        }
    }
    
    private int listarEdificiosPorTipo(CasillaX casilla, String tipo) {
        ArrayList<Edificio> edificios = new ArrayList<>();
        for (Edificio edificio : casilla.getLista_edificio()) {
            if (edificio.getTipo().equals(tipo)) {
                edificios.add(edificio);
            }
        }
        System.out.print(tipo + "s: " + (edificios.isEmpty() ? "-" : ""));
        for (Edificio edificio : edificios) {
            System.out.print("[" + edificio.getID() + "]");
        }
        System.out.println();
        return edificios.size();
    }
    
    private void venderEdificios(String tipoedificio, String nombre, String cantidad){
        if(this.tablero.encontrar_casilla(nombre).getTipo().equals("Solar")){
            this.avatares.get(this.turno).getLugar().venderEdificios(this.jugadores.get(this.turno),banca,tipoedificio,cantidad,lista_edificios);
        } else{System.out.println("Esta casilla no puede tener edificios");}
    }

    // Método para mostrar el tablero por pantalla asociado al comando 'ver tablero'.
    private void verTablero() {
        System.out.println("\n" + tablero.toString());
    }

    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    private void acabarTurno() {
        if (this.tirado){
            int turno_act = this.turno + 1;
            if (turno_act == this.avatares.size()){
                turno_act = 0;
            }
            this.turno = turno_act;
            this.fturno = this.tirado && this.solvente;
            this.tirado = false;
            //this.fturno = true;
            System.out.println("\nEl jugador actual es " + this.avatares.get(this.turno).getJugador().getNombre());
        } else {
            System.out.println("\nEl jugador actual no ha tirado. Debe realizar sus tiradas para acabar su turno.");
        }
    }

    private void casillaMasRentable(){
        float masRentable = this.tablero.encontrar_casilla(0).getRentabilidad();
        for(ArrayList<CasillaX> lado : this.tablero.getPosiciones()){
            for(CasillaX cas : lado){if(masRentable<cas.getRentabilidad()) masRentable = cas.getRentabilidad();}
        }

        for(ArrayList<CasillaX> lado : this.tablero.getPosiciones()){
            for(CasillaX cas : lado){if(masRentable==cas.getRentabilidad()) System.out.print(cas.getNombre() + ", ");}
        }
        System.out.println();
    }

    private void grupoMasRentable(){
        ArrayList<Float> masRentable = new ArrayList<Float>(8);
        for(int i=0;i<8;i++){masRentable.add(0f);}
        for(ArrayList<CasillaX> lado : this.tablero.getPosiciones()){
            for(CasillaX cas : lado){
                if(cas.getTipo().equals("Solar")){
                    String color=cas.getColorGrupo();
                    switch(color){
                        case "Negro":
                        masRentable.set(0, masRentable.get(0) + cas.getRentabilidad()); 
                        break;
                        case "Rojo":
                        masRentable.set(1, masRentable.get(1) + cas.getRentabilidad());
                        break;
                        case "Verde":
                        masRentable.set(2, masRentable.get(2) + cas.getRentabilidad());
                        break;
                        case "Amarillo":
                        masRentable.set(3, masRentable.get(3) + cas.getRentabilidad());
                        break;
                        case "Azul":
                        masRentable.set(4, masRentable.get(4) + cas.getRentabilidad());
                        break;
                        case "Rosa":
                        masRentable.set(5, masRentable.get(5) + cas.getRentabilidad());
                        break;
                        case "Cian":
                        masRentable.set(6, masRentable.get(6) + cas.getRentabilidad());
                        break;
                        case "Blanco":
                        masRentable.set(7, masRentable.get(7) + cas.getRentabilidad());
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
                if(i==0){ System.out.print("Negro, ");}  
                else if(i==1){ System.out.print("Rojo, ");}
                else if(i==2){ System.out.print("Verde, ");}
                else if(i==3){ System.out.print("Amarillo, ");}
                else if(i==4){ System.out.print("Azul, ");}
                else if(i==5){ System.out.print("Rosa, ");}
                else if(i==6){ System.out.print("Cian, ");}
                else if(i==7){ System.out.print("Blanco, ");}
            } 
        }
        System.out.println();
    }

    private void casillaMasFrecuentada(){
        int maximo=0;

        for(ArrayList<CasillaX> lado : this.tablero.getPosiciones()){
            for(CasillaX cas : lado){
                int suma = 0;
                for (Integer num : cas.getCaidas()){suma += num;}
                if(suma>maximo){maximo=suma;}
            }
        }

        for(ArrayList<CasillaX> lado : this.tablero.getPosiciones()){
            for(CasillaX cas : lado){
                int suma = 0;
                for (Integer num : cas.getCaidas()) suma += num;
                if(suma==maximo){System.out.print(cas.getNombre() + ", ");}
            }
        } System.out.println();
    }

    private void jugadorMasVueltas(){
        int maximo = this.vueltastotales.get(0);

        for(int i=0;i<this.vueltastotales.size();i++){if(i>maximo) maximo = this.vueltastotales.get(i);}
        for(int i=0;i<this.vueltastotales.size();i++){if(this.vueltastotales.get(i)==maximo) System.out.print(this.jugadores.get(i).getNombre() + ", ");}
        System.out.println();
    }

    private void MasVecesDados(){
        int maximo = this.lanzamientostotales.get(0);

        for(int i=0;i<this.lanzamientostotales.size();i++){
            if(i>maximo) maximo = this.lanzamientostotales.get(i);
        } for(int i=0;i<this.lanzamientostotales.size();i++){
            if(this.lanzamientostotales.get(i)==maximo) System.out.print(this.jugadores.get(i).getNombre() + ", ");
        } System.out.println();
   }

   private void EnCabeza(){
        ArrayList<Float> fortuna = new ArrayList<>(this.jugadores.size());
        for(int i=0;i<this.jugadores.size();i++) fortuna.add(0f);

        for(int i=0;i<this.jugadores.size();i++){
            fortuna.set(i, fortuna.get(i) + this.jugadores.get(i).getFortuna());
            for(CasillaX cas : this.jugadores.get(i).getPropiedades()){
                fortuna.set(i, fortuna.get(i) + cas.getvaloredificios() + cas.getValor());
            }
        }

        float max=0f;

        for(int i=0;i<fortuna.size();i++){if(max<fortuna.get(i)) max=fortuna.get(i);}
        for(int i=0;i<fortuna.size();i++){if(max==fortuna.get(i)) System.out.print(this.jugadores.get(i).getNombre() + ", ");}
        System.out.println();

}
    
    private void estadisticasjuego(){
        System.out.print("estadisticas\n{\n");
        System.out.print("casillaMasRentable: "); casillaMasRentable();
        System.out.print("grupoMasRentable: "); grupoMasRentable();
        System.out.print("casillaMasFrecuentada: "); casillaMasFrecuentada();
        System.out.print("jugadorMasVueltas: "); jugadorMasVueltas();
        System.out.print("jugadorMasVecesDados: "); MasVecesDados();
        System.out.print("jugadorEnCabeza: "); EnCabeza();
        System.out.print("\n}\n");
    }

    private void estadisticasjugador(String nombre){
        for (Jugador i : this.jugadores) {
            if (i.getNombre().equals(nombre)) {
                System.out.println(i.toStringestadisticas());
                return;
            }
        } System.out.println("El jugador " + nombre + " no está en la partida.");
    }

    private void infotratos(){
        System.out.println("Modalidades de trato disponibles");
        System.out.println("cambiar (propiedad_1, propiedad_2)");
        System.out.println("cambiar (propiedad_1, cantidad_dinero)");
        System.out.println("cambiar (cantidad_dinero, propiedad_1)");
        System.out.println("cambiar (propiedad_1, propiedad_2 y cantidad_dinero)");
        System.out.println("cambiar (propiedad_1 y cantidad_dinero, propiedad_2)");
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

    private void trato(String jugador, String inter1, String inter2){
        Jugador jug=buscarJugador(jugador);
        if(esNumerico(inter1)){
            if(this.tablero.encontrar_casilla(inter2).getDuenho().equals(jug)){
                System.out.println(jugador + ", ¿te doy " + inter1 + "€ y tú me das " + inter2 + "?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1,inter2);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else System.out.println("No se puede proponer el trato: " + inter2 + " no pertenece a " + jugador);
        }
        else if(esNumerico(inter2)){
            if(this.tablero.encontrar_casilla(inter1).getDuenho().equals(this.jugadores.get(this.turno))){
                System.out.println(jugador + ", ¿te doy " + inter1 + " y tú me das " + inter2 + " €?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1,inter2);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else System.out.println("No se puede proponer el trato: " + inter1 + " no pertenece a " + this.jugadores.get(this.turno).getNombre());
        }
        else{
            if(this.tablero.encontrar_casilla(inter1).getDuenho().equals(this.jugadores.get(this.turno)) && this.tablero.encontrar_casilla(inter2).getDuenho().equals(jug)){
                System.out.println(jugador + ", ¿te doy " + inter1 + " y tú me das " + inter2 + "?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1,inter2);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else System.out.println("No se puede proponer el trato: Las dos propiedades deben pertenecer a ambos jugadores");
        }

        } 
    private void trato(String jugador, String inter1, String inter2, String inter3, String inter4){
        Jugador jug = buscarJugador(jugador);
        if(inter2.equals("y")){
            if(this.tablero.encontrar_casilla(inter4).getDuenho().equals(jug) && this.tablero.encontrar_casilla(inter1).getDuenho().equals(this.jugadores.get(this.turno))){
                System.out.println(jugador + ", ¿te doy " + inter1 + " y " + inter3 + " € y tú me das " + inter4 + "?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1 + " " + inter3,inter4);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else System.out.println("No se puede proponer el trato: Las dos propiedades deben pertenecer a ambos jugadores");}
        else {
            if(this.tablero.encontrar_casilla(inter2).getDuenho().equals(jug) && this.tablero.encontrar_casilla(inter1).getDuenho().equals(this.jugadores.get(this.turno))){
                System.out.println(jugador + ", ¿te doy " + inter1 + " y tú me das " + inter2 + " y " + inter4 + " €?");
                Trato trato = new Trato(this.jugadores.get(this.turno),jug,inter1,inter2 + " "+ inter4);
                tratos.get(this.jugadores.indexOf(jug)).add(trato);
            }
            else System.out.println("No se puede proponer el trato: Las dos propiedades deben pertenecer a ambos jugadores");
        }
        }

    private void aceptarTrato(String idtrato){
        Trato tratoaceptar = null;
        for(Trato trato : tratos.get(this.turno)){
            if(trato.getID().equals(idtrato)){
                tratoaceptar = trato; 
            }
        }
        String respuesta = "si";
        if(tratoaceptar == null) System.out.println("No se ha podido encontrar el trato introducido"); 
        else{
            String oferta[] = tratoaceptar.getOferta().split(" "), demanda[] = tratoaceptar.getDemanda().split(" ");
            if(oferta.length == 2){
                
                if(tratoaceptar.getPropositor().getFortuna()>=Integer.parseInt(oferta[1])){
                    CasillaX pro1 = this.tablero.encontrar_casilla(oferta[0]),pro2=this.tablero.encontrar_casilla(demanda[0]);
                    if(pro1.getHipotecada()){
                        System.out.println(pro1.getNombre() + " está hipotecada ¿Quieres continuar aceptando el trato? (si/no)");
                        respuesta = scanner.nextLine();}
                    if(respuesta.equals("si")){
                        pro1.setDuenho(tratoaceptar.getDestinatario());
                        tratoaceptar.getDestinatario().anhadirPropiedad(pro1);
                        tratoaceptar.getPropositor().eliminarPropiedad(pro1);
                        pro2.setDuenho(tratoaceptar.getPropositor());
                        tratoaceptar.getPropositor().anhadirPropiedad(pro2);
                        tratoaceptar.getDestinatario().eliminarPropiedad(pro2);
                        tratoaceptar.getPropositor().sumarGastos(Float.parseFloat(oferta[1]));
                        tratoaceptar.getDestinatario().sumarFortuna(Float.parseFloat(oferta[1]));
                        tratos.get(this.turno).remove(tratoaceptar);
                        System.out.println("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[1] + " € y " + oferta[0]);       
                }
                }
                else System.out.println("El trato no puede ser aceptado: " + tratoaceptar.getPropositor().getNombre() + " no tiene " + oferta[1]+ " €");
        
            }
            else if(demanda.length == 2){
                if(tratoaceptar.getDestinatario().getFortuna()>=Integer.parseInt(demanda[1])){
                    CasillaX pro1 = this.tablero.encontrar_casilla(oferta[0]),pro2=this.tablero.encontrar_casilla(demanda[0]);
                    if(pro1.getHipotecada()){
                        System.out.println(pro1.getNombre() + " está hipotecada ¿Quieres continuar aceptando el trato? (si/no)");
                        respuesta = scanner.nextLine();}
                        if(respuesta.equals("si")){
                            pro1.setDuenho(tratoaceptar.getDestinatario());
                            tratoaceptar.getDestinatario().anhadirPropiedad(pro1);
                            tratoaceptar.getPropositor().eliminarPropiedad(pro1);
                            pro2.setDuenho(tratoaceptar.getPropositor());
                            tratoaceptar.getPropositor().anhadirPropiedad(pro2);
                            tratoaceptar.getDestinatario().eliminarPropiedad(pro2);
                            tratoaceptar.getDestinatario().sumarGastos(Float.parseFloat(demanda[1]));
                            tratoaceptar.getPropositor().sumarFortuna(Float.parseFloat(demanda[1]));
                            tratos.get(this.turno).remove(tratoaceptar);
                            System.out.println("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " y " + demanda[1]+ " € y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[0]);
                        }
                }
                else System.out.println("El trato no puede ser aceptado: " + tratoaceptar.getDestinatario().getNombre() + " no tiene " + demanda[1] + " €");
            }
            else{
                if(esNumerico(oferta[0])){
                    if(tratoaceptar.getPropositor().getFortuna()>=Integer.parseInt(oferta[0])){
                        CasillaX pro1 = this.tablero.encontrar_casilla(demanda[0]);
                        pro1.setDuenho(tratoaceptar.getPropositor());
                        tratoaceptar.getPropositor().anhadirPropiedad(pro1);
                        tratoaceptar.getDestinatario().eliminarPropiedad(pro1);
                        tratoaceptar.getPropositor().sumarGastos(Float.parseFloat(oferta[0]));
                        tratoaceptar.getDestinatario().sumarFortuna(Float.parseFloat(oferta[0]));
                        tratos.get(this.turno).remove(tratoaceptar);
                        System.out.println("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[0] + " €");
                    }
                    else System.out.println("El trato no puede ser aceptado: " + tratoaceptar.getPropositor().getNombre() + " no tiene " + oferta[0]+ " €");
                }
                else if(esNumerico(demanda[0])){

                    if(tratoaceptar.getDestinatario().getFortuna()>=Integer.parseInt(demanda[0])){
                        CasillaX pro1 = this.tablero.encontrar_casilla(oferta[0]);
                        if(pro1.getHipotecada()){
                            System.out.println(pro1.getNombre() + " está hipotecada ¿Quieres continuar aceptando el trato? (si/no)");
                            respuesta = scanner.nextLine();}
                        if(respuesta.equals("si")){
                            pro1.setDuenho(tratoaceptar.getDestinatario());
                            tratoaceptar.getDestinatario().anhadirPropiedad(pro1);
                            tratoaceptar.getPropositor().eliminarPropiedad(pro1);
                            tratoaceptar.getDestinatario().sumarGastos(Float.parseFloat(demanda[0]));
                            tratoaceptar.getPropositor().sumarFortuna(Float.parseFloat(demanda[0]));
                            tratos.get(this.turno).remove(tratoaceptar);
                            System.out.println("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " € y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[0]);
                        }
                    else System.out.println("El trato no puede ser aceptado: " + tratoaceptar.getDestinatario().getNombre() + " no tiene " + demanda[0]+ " €");
                }}
                else{
                    CasillaX pro1 = this.tablero.encontrar_casilla(oferta[0]), pro2 = this.tablero.encontrar_casilla(demanda[0]);
                    if(pro1.getHipotecada()){
                        System.out.println(pro1.getNombre() + " está hipotecada ¿Quieres continuar aceptando el trato? (si/no)");
                        respuesta = scanner.nextLine();}
                    if(respuesta.equals("si")){
                        pro1.setDuenho(tratoaceptar.getDestinatario());
                        tratoaceptar.getDestinatario().anhadirPropiedad(pro1);
                        tratoaceptar.getPropositor().eliminarPropiedad(pro1);
                        pro2.setDuenho(tratoaceptar.getPropositor());
                        tratoaceptar.getPropositor().anhadirPropiedad(pro2);
                        tratoaceptar.getDestinatario().eliminarPropiedad(pro2);
                        tratos.get(this.turno).remove(tratoaceptar);
                        System.out.println("Se ha aceptado el siguiente trato con " + tratoaceptar.getPropositor().getNombre() + ": le doy " + demanda[0] + " y " + tratoaceptar.getPropositor().getNombre() + " me da " + oferta[0]);
                    }
        }}
    }
    }

    private void eliminarTrato(String idtrato){
        for(Trato trato : tratos.get(this.turno)){
            if(trato.getPropositor().equals(this.jugadores.get(this.turno))){
                if(trato.getID().equals(idtrato)) tratos.get(this.turno).remove(trato);
            }
        }
    }
    

    //GETTERS
    /*Método para obtener la lista de jugadores*/
    public ArrayList<Jugador> getJugadores() {
        return this.jugadores;
    }

    /*Método para obtener la lista de avatares*/
    public ArrayList<Avatar> getAvatares() {
        return this.avatares;
    }

    /*Método para obtener el turno actual*/
    public int getTurno() {
        return this.turno;
    }

    /*Método para obtener el número de lanzamientos del jugador actual*/
    public int getLanzamientos() {
        return this.lanzamientos;
    }

    /*Método para obtener el tablero*/
    public Tablero getTablero() {
        return this.tablero;
    }

    /*Método para obtener el primer dado*/
    public Dado getDado1() {
        return this.dado1;
    }

    /*Método para obtener el segundo dado*/
    public Dado getDado2() {
        return this.dado2;
    }
    
    /*Método para obtener el jugador "banca"*/
    public Jugador getBanca() {
        return this.banca;
    }
    
    /*Método para saber si el jugador ha tirado o no*/
    public boolean getTirado() {
        return this.tirado;
    }

    /*Método para saber si el jugador es solvente o no*/
    public boolean getSolvente() {
        return this.solvente;
    }

    /*Método para comprobar si el jugador ha terminado o no*/
    public boolean getFturno(){
        return this.fturno;
    }
    
    /*Método para obtener si el juego ha terminado o no. */
    public boolean getFin() {
        return this.fin;
    }

    /*Método para obtener la lectura */
    public Scanner getScanner() {
        return this.scanner;
    }

    //SETTERS
    /*Método para modificar la lista de jugadores*/
    public void setJugadores(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }
    
    /*Método para modificar la lista de avatares*/
    public void setAvatares(ArrayList<Avatar> avatares) {
        this.avatares = avatares;
    }

    /*Método para modificar el turno actual*/
    public void setTurno(int turno) {
        this.turno = turno;
    }

    /*Método para modificar el número de lannzamientos del jugador actual*/
    public void setLanzamientos(int lanzamientos) {
        this.lanzamientos = lanzamientos;
    }

    /*Método para modificar el tablero*/
    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    /*Método para modificar el primer dado*/
    public void setDado1(Dado dado1) {
        this.dado1 = dado1;
    }

    /*Método para modificar el segundo dado*/
    public void setDado2(Dado dado2) {
        this.dado2 = dado2;
    }

    /*Método para modificar al jugador "banca"*/
    public void setBanca(Jugador banca) {
        this.banca = banca;
    }

    /*Método para cambiar el estado de tirada del jugador actual*/
    public void setTirado(boolean tirado) {
        this.tirado = tirado;
    }

    /*Método para cambiar el estado de solvencia del jugador actual*/
    public void setSolvente(boolean solvente) {
        this.solvente = solvente;
    }

    /*Método para modificar si el turno del jugador ha terminado*/
    public void setFturno(boolean fturno) {
        this.fturno = fturno;
    }

    /*Método para modificar si el juego ha terminado o no.*/
    public void setFin(boolean fin) {
        this.fin = fin;
    }   

    /*Método para modificar la lectura por línea de comandos*/
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /*Función de dados trucados */
    private void lanzarDados(int valor1, int valor2) {
        if(this.tirado){System.out.println("El jugador actual ya ha realizado todas sus tiradas.");}

        if (!this.tirado){
            int d1 = valor1, d2 = valor2;
            if(!this.jugadores.get(this.turno).getEncarcelado()){
                int desp = d1 + d2;
                int mov = (this.avatares.get(this.turno).getLugar().getPosicion() + desp) % 40;
                
                String origen = this.avatares.get(this.turno).getLugar().getNombre(), destino = this.tablero.encontrar_casilla(mov).getNombre();
                int oint = this.avatares.get(this.turno).getLugar().getPosicion(), dint = this.tablero.encontrar_casilla(mov).getPosicion();

                System.out.println("El avatar " + this.avatares.get(this.turno).getID() + " avanza " + desp + " posiciones, desde " + origen + " hasta " + destino);
                this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                
                //this.tablero.encontrar_casilla(oint).eliminarAvatar(this.avatares.get(this.turno));
                //this.tablero.encontrar_casilla(mov).anhadirAvatar(this.avatares.get(this.turno));
                this.jugadores.get(this.turno).getAvatar().moverAvatar(this.tablero.getPosiciones(), desp);
                this.tablero.encontrar_casilla(destino).getCaidas().set(this.turno, this.tablero.encontrar_casilla(destino).getCaidas().get(this.turno)+1);
                
                this.solvente = this.tablero.encontrar_casilla(destino).evaluarCasilla(this.avatares.get(this.turno).getJugador(), banca, desp);

                if(this.tablero.encontrar_casilla(destino).getComunidad()){
                    this.solvente = this.tablero.encontrar_casilla(destino).cajaComunidad(this, this.avatares.get(this.turno).getJugador(), banca);
                } else if(this.tablero.encontrar_casilla(destino).getSuerte()){
                    this.solvente = this.tablero.encontrar_casilla(destino).casSuerte(this.tablero, this.avatares.get(this.turno).getJugador(), banca, this.turno);
                }
                
                if(this.fin){
                    if(this.tablero.encontrar_casilla(destino).getNombre().equals("IrCarcel") && this.jugadores.get(this.turno).getEncarcelado()){
                        this.jugadores.get(this.turno).encarcelar(this.tablero.getPosiciones());
                    }
                    if(this.tablero.encontrar_casilla(destino).getTipo().equals("Impuesto")){
                        this.tablero.encontrar_casilla("Parking").sumarValor(this.jugadores.get(this.turno).getAvatar().getLugar().getImpuesto());
                    }
                verTablero();
                } else{ System.out.println("El jugador " + this.jugadores.get(this.turno).getNombre() + " está encarcelado. No se desplaza.");}

                if (d1 == d2){
                    ++this.lanzamientos;
                    this.lanzamientostotales.set(this.turno, this.lanzamientostotales.get(this.turno) + 1);
                    this.tirado = false;
                    if (this.avatares.get(this.turno).getJugador().getEncarcelado()){
                        this.avatares.get(this.turno).getJugador().setEncarcelado(false);
                        this.avatares.get(this.turno).getJugador().setTiradasCarcel(0);
                        System.out.println(this.avatares.get(this.turno).getJugador().getNombre() + " sale de la cárcel. Puede lanzar los dados.");
                    } else{System.out.println("Puede volver a lanzar los dados.");} 

                    if (this.lanzamientos == 3){
                        this.avatares.get(this.turno).getJugador().encarcelar(this.tablero.getPosiciones());
                        System.out.println(this.jugadores.get(this.turno).getNombre() + " ha sacado tres dobles seguidos y va a la cárcel.");
                        this.lanzamientos = 0;
                        this.tirado = true;
                    }
                } else{this.tirado = true;}

                
                if(this.tablero.encontrar_casilla(oint).getPosicion()>this.tablero.encontrar_casilla(dint).getPosicion()){
                    this.avatares.get(this.turno).getJugador().setVueltas(this.avatares.get(this.turno).getJugador().getVueltas()+1);
                    this.avatares.get(this.turno).getJugador().sumarFortuna(Valor.SUMA_VUELTA);
                    this.avatares.get(this.turno).getJugador().getEstadisticas().set(4, this.avatares.get(this.turno).getJugador().getEstadisticas().get(4) + Valor.SUMA_VUELTA);

                    System.out.println("El jugador " + this.jugadores.get(this.turno).getNombre() + " ha cruzado la casilla de Salida. Recibe " + Valor.SUMA_VUELTA + "€.");
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
}