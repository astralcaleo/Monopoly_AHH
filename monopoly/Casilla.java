package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Casilla {

    //Atributos:
    private String nombre; //Nombre de la casilla
    private String tipo; //Tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte, Impuesto).
    private float valor; //Valor de esa casilla (en la mayoría será valor de compra, en la casilla parking se usará como el bote).
    private float valor_inicial; //Valor inicial de esa casilla
    private int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Jugador duenho; //Dueño de la casilla (por defecto sería la banca).
    private Grupo grupo; //Grupo al que pertenece la casilla (si es solar).
    private float impuesto; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private float hipoteca; //Valor otorgado por hipotecar una casilla
    private boolean hipotecada; //Valor que indica que la casilla ya está hipotecada
    private ArrayList<Avatar> avatares; //Avatares que están situados en la casilla. 
    private ArrayList<Integer> caidas;
    private ArrayList<Integer> edificios;
    private ArrayList<Edificio> lista_edificio;
    private Scanner scanner;
    private boolean comunidad = false;
    private boolean suerte = false;
    private float rentabilidad = 0f;
    private float valoredificios = 0f;
    
    //Constructores:
    //Constructor vacío
    public Casilla() {}

    /*Constructor para casillas tipo Solar, Servicios o Transporte:
    * Parámetros: nombre casilla, tipo (debe ser solar, serv. o transporte), posición en el tablero, valor y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
        this.valor_inicial = this.valor;
        this.posicion = posicion;
        this.duenho = duenho;
        this.hipoteca = (this.valor_inicial)/2f;
        this.avatares = new ArrayList<Avatar>();
        this.caidas = new ArrayList<Integer>();
        this.edificios = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            this.edificios.add(0);
        }
        this.lista_edificio = new ArrayList<Edificio>();
        this.hipoteca = (this.valor_inicial)/2f;
    }

    /*Constructor utilizado para inicializar las casillas de tipo IMPUESTOS.
    * Parámetros: nombre, posición en el tablero, impuesto establecido y dueño.
     */
    public Casilla(String nombre, int posicion, float impuesto, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = "Impuesto";
        this.posicion = posicion;
        this.impuesto = impuesto;
        this.duenho = duenho;
        this.avatares = new ArrayList<Avatar>();
        this.caidas = new ArrayList<Integer>();
    }

    /*Constructor utilizado para crear las otras casillas (Suerte, Caja de comunidad y Especiales):
    * Parámetros: nombre, tipo de la casilla (será uno de los que queda), posición en el tablero y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = duenho;
        this.avatares = new ArrayList<Avatar>();
        this.caidas = new ArrayList<Integer>();
    }

    //Método utilizado para añadir un avatar al array de avatares en casilla.
    public void anhadirAvatar(Avatar av) {
        this.avatares.add(av);
    }

    //Método utilizado para eliminar un avatar del array de avatares en casilla.
    public void eliminarAvatar(Avatar av) {
        this.avatares.remove(av);
    }

    /*Método para obtener la lista de jugadores en partida */ //POR DETERMINAR
    public ArrayList<Jugador> listaJugadores(Tablero tablero){
        ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
        for (ArrayList<Casilla> fila : tablero.getPosiciones()) {
            for (Casilla casilla : fila) {
                //System.out.println("Casilla: " + casilla.getNombre() + " tiene avatares: " + casilla.getAvatares());
                for (Avatar avatar : casilla.getAvatares()) {
                    Jugador jugador = avatar.getJugador();
                    if (!jugadores.contains(jugador)) {
                        jugadores.add(jugador);
                    }
                }
            }
        }
        return jugadores;
    }

    /*Método para evaluar qué hacer en una casilla concreta. Parámetros:
    * - Jugador cuyo avatar está en esa casilla.
    * - La banca (para ciertas comprobaciones).
    * - El valor de la tirada: para determinar impuesto a pagar en casillas de servicios.
    * Valor devuelto: true en caso de ser solvente (es decir, de cumplir las deudas), y false
    * en caso de no cumplirlas.*/
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        if (this.tipo.equals("Solar")) {
            if (!this.duenho.equals(banca) && !this.duenho.equals(actual)) {
                if (!this.hipotecada){
                    calcularImpuesto();
                    if (actual.getFortuna() < this.impuesto){
                        System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                        System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota");
                        return false; 
                    } else {
                        actual.sumarGastos(this.impuesto);
                        actual.getEstadisticas().set(2, actual.getEstadisticas().get(2) + this.impuesto);
                        
                        this.duenho.sumarFortuna(this.impuesto);
                        this.duenho.getEstadisticas().set(3, this.duenho.getEstadisticas().get(3) + this.impuesto);
                        
                        System.out.println("El jugador " + actual.getNombre() + " paga " + this.impuesto + "€ al jugador " + this.duenho.getNombre());
                        this.rentabilidad += this.impuesto;
                        return true;
                    }
                } else{System.out.println("La casilla está hipotecada. No se pagan ni reciben alquileres.");
                    return true;
                }
            }

        } else if (this.tipo.equals("Transporte")) {
            if (!this.duenho.equals(banca) && !this.duenho.equals(actual)) {
                if(!this.hipotecada){
                    float prop = 0f;
                    for(Casilla c:this.duenho.getPropiedades()){
                        if(c.tipo.equals("Transporte")){
                            prop = prop + 0.25f;
                        }
                    }
                    this.impuesto = Valor.SUMA_VUELTA*prop;
                    if (actual.getFortuna() < this.impuesto) {
                        System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                        System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                        return false; 
                    } else {
                        actual.sumarGastos(this.impuesto);
                        actual.getEstadisticas().set(2, actual.getEstadisticas().get(2) + this.impuesto);

                        this.duenho.sumarFortuna(this.impuesto);
                        this.duenho.getEstadisticas().set(3, this.duenho.getEstadisticas().get(3) + this.impuesto);

                        System.out.println("El jugador " + actual.getNombre() + " paga " + this.impuesto + "€ al jugador " + this.duenho.getNombre());
                        this.rentabilidad += this.impuesto;
                        return true;
                    }
                } else{
                    System.out.println("La casilla está hipotecada. No pagan ni reciben alquileres.");
                    return true;
                }
            }
        
        } else if (this.tipo.equals("Servicios")) { 
            if (!this.duenho.equals(banca) && !this.duenho.equals(actual)) {
                if(!this.hipotecada){
                    float factor = this.valor_inicial/200f;
                    Casilla imp1 = new Casilla("Serv1", "Servicio", 12, 0.75f*(Valor.SUMA_VUELTA), banca); 
                    Casilla imp2 = new Casilla("Serv2", "Servicio", 28, 0.75f*(Valor.SUMA_VUELTA), banca);
                    if (this.duenho.getPropiedades().contains(imp1)){
                        if(this.duenho.getPropiedades().contains(imp2)){
                            this.impuesto = 10*tirada*factor;
                        } else{this.impuesto = 4*tirada*factor;}
                    } else{this.impuesto = factor;}
                    if (actual.getFortuna() < this.impuesto) {
                        System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                        System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                        return false; 
                    } else {
                        actual.sumarGastos(this.impuesto);
                        actual.getEstadisticas().set(2, actual.getEstadisticas().get(2) + this.impuesto);
                        
                        this.duenho.sumarFortuna(this.impuesto);
                        this.duenho.getEstadisticas().set(3, this.duenho.getEstadisticas().get(3) + this.impuesto);

                        System.out.println("El jugador " + actual.getNombre() + " paga " + this.impuesto + "€ al jugador " + this.duenho.getNombre());
                        this.rentabilidad += this.impuesto;
                        return true;
                    }
                } else{
                }
            }

        } else if (this.tipo.equals("Impuesto")) {
            if (actual.getFortuna() < this.impuesto) {
                System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                return false; 
            } else {
                actual.sumarGastos(this.impuesto);
                banca.sumarFortuna(this.impuesto);
                actual.getEstadisticas().set(1, actual.getEstadisticas().get(1) + this.impuesto);
                
                System.out.println("El jugador " + actual.getNombre() + " paga " + this.impuesto + "€ en impuestos a la banca.");
                return true;
            }

        } else if (this.nombre.equals("IrCarcel")){
            //actual.encarcelar(pos);
            System.out.println("El jugador " + actual.getNombre() + " se dirige a la cárcel.");
            actual.setEncarcelado(true);
            actual.getEstadisticas().set(6, actual.getEstadisticas().get(6) + 1f);
            return true;

        } else if (this.nombre.equals("Parking")){
            //float bote = this.duenho.getFortuna();
            actual.sumarFortuna(this.valor);
            //duenho.sumarGastos(bote);
            actual.getEstadisticas().set(5, actual.getEstadisticas().get(5) + this.valor);

            System.out.println("El jugador " + actual.getNombre() + " recibe un bote de " + this.valor + "€.");
            this.valor = 0;
            return true;

        } else if (this.nombre.equals("Suerte")){
            this.suerte = true;
            return true;
        
        } else if (this.nombre.equals("Caja")){
            this.comunidad = true;
            return true;

        } return true; // Para otros tipos de casillas que no implican pago
    }
    
    public boolean cajaComunidad(Menu menu, Jugador actual, Jugador banca){
        int numero = 0;
        while (numero < 1 || numero > 6) {
            System.out.println(actual.getNombre() + ", elige una carta entre 1 y 6:");
            this.scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                numero = scanner.nextInt();
            } else {
                System.out.println("Entrada inválida. Debes ingresar un número entre 1 y 6.");
                scanner.next();
            }
        }

        Carta carta = new Carta();
        Carta cartaSeleccionada=carta.barajarCaja(numero);
        System.out.println("Acción: " + cartaSeleccionada.getAccion());
        if(cartaSeleccionada.getNombre().equals("caja1")){
            if(actual.getFortuna() < 500000f){
                System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                return false;
            } else{
                actual.sumarGastos(500000f);
                menu.getTablero().encontrar_casilla("Parking").sumarValor(500000f);
                actual.getEstadisticas().set(1, actual.getEstadisticas().get(1) + 500000);
            }
        
        } else if(cartaSeleccionada.getNombre().equals("caja2")){
            System.out.println("El jugador " + actual.getNombre() + " se dirige a la cárcel.");
            actual.encarcelar(menu.getTablero().getPosiciones());
            menu.getTablero().encontrar_casilla(actual.getAvatar().getLugar().getNombre()).eliminarAvatar(menu.getAvatares().get(menu.getTurno()));
            menu.getTablero().encontrar_casilla("Cárcel").anhadirAvatar(menu.getAvatares().get(menu.getTurno()));
            actual.getAvatar().setLugar(menu.getTablero().encontrar_casilla("Cárcel"));
            menu.getTablero().encontrar_casilla("Cárcel").getCaidas().set(menu.getTurno(), menu.getTablero().encontrar_casilla("Cárcel").getCaidas().get(menu.getTurno())+1);
            actual.getEstadisticas().set(6, actual.getEstadisticas().get(6) + 1f);
        
        } else if(cartaSeleccionada.getNombre().equals("caja3")){
            actual.getAvatar().moverAvatar(menu.getTablero().getPosiciones(),"Salida");
            menu.getTablero().encontrar_casilla("Salida").getCaidas().set(menu.getTurno(), menu.getTablero().encontrar_casilla("Salida").getCaidas().get(menu.getTurno())+1);
            actual.getAvatar().setLugar(menu.getTablero().encontrar_casilla("Salida"));
            actual.sumarFortuna(Valor.SUMA_VUELTA);
            actual.getEstadisticas().set(4, actual.getEstadisticas().get(4) + Valor.SUMA_VUELTA);
        
        } else if(cartaSeleccionada.getNombre().equals("caja4")){
            actual.sumarFortuna(2000000f);
            actual.getEstadisticas().set(5, actual.getEstadisticas().get(5) + 2000000f);
        
        } else if(cartaSeleccionada.getNombre().equals("caja5")){
            if(actual.getFortuna() < 1000000){
                System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                return false;
            } else{
                actual.sumarGastos(1000000f);
                menu.getTablero().encontrar_casilla("Parking").sumarValor(1000000f);
                actual.getEstadisticas().set(1, actual.getEstadisticas().get(1) + 1000000f);
            }
        
        } else if(cartaSeleccionada.getNombre().equals("caja6")){
            if(actual.getFortuna() < 200000*(menu.getJugadores().size())){ 
                System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota.");
                return false;
            } else{
                for(Jugador jugador : menu.getJugadores()){
                    if(!jugador.getNombre().equals(actual.getNombre())){
                        actual.sumarGastos(200000);
                        jugador.sumarFortuna(200000);
                        actual.getEstadisticas().set(1, actual.getEstadisticas().get(1) + 200000);
                    }
                }
            }
        } this.comunidad = false;
        return true;
    }

    public boolean casSuerte(Tablero tablero, Jugador actual, Jugador banca, int turno){
        int numero = 0;
        while (numero < 1 || numero > 6) {
            System.out.println(actual.getNombre() + ", elige una carta entre 1 y 6: ");
            this.scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                numero = scanner.nextInt();
            } else {
                System.out.println("Entrada inválida. Debes ingresar un número entre 1 y 6.");
                scanner.next();
            }
        }
        
        Carta carta = new Carta();
        Carta cartaSeleccionada=carta.barajarSuerte(numero);
        System.out.println("Acción: " + cartaSeleccionada.getAccion());
        if(cartaSeleccionada.getNombre().equals("suerte1")){
            actual.getAvatar().moverAvatar(tablero.getPosiciones(),"Trans1");
            tablero.encontrar_casilla("Trans1").getCaidas().set(turno, tablero.encontrar_casilla("Trans1").getCaidas().get(turno)+1);
            actual.getAvatar().setLugar(tablero.encontrar_casilla("Trans1"));
        
        } else if(cartaSeleccionada.getNombre().equals("suerte2")){
            actual.getAvatar().moverAvatar(tablero.getPosiciones(),"Solar15");
            tablero.encontrar_casilla("Solar15").getCaidas().set(turno, tablero.encontrar_casilla("Solar15").getCaidas().get(turno)+1);
            actual.getAvatar().setLugar(tablero.encontrar_casilla("Solar15"));
        
        } else if(cartaSeleccionada.getNombre().equals("suerte3")){
            actual.sumarFortuna(500000);
            actual.getEstadisticas().set(5, actual.getEstadisticas().get(5) + 500000);
        
        } else if(cartaSeleccionada.getNombre().equals("suerte4")){
            actual.getAvatar().moverAvatar(tablero.getPosiciones(),"Solar3");
            tablero.encontrar_casilla("Solar3").getCaidas().set(turno, tablero.encontrar_casilla("Solar3").getCaidas().get(turno)+1);
            actual.getAvatar().setLugar(tablero.encontrar_casilla("Solar3"));
        
        } else if(cartaSeleccionada.getNombre().equals("suerte5")){
            System.out.println("El jugador " + actual.getNombre() + " se dirige a la cárcel.");
            actual.encarcelar(tablero.getPosiciones());
            tablero.encontrar_casilla(actual.getAvatar().getLugar().getNombre()).eliminarAvatar(this.avatares.get(turno));
            tablero.encontrar_casilla("Cárcel").anhadirAvatar(this.avatares.get(turno));
            actual.getAvatar().setLugar(tablero.encontrar_casilla("Cárcel"));
            tablero.encontrar_casilla("Cárcel").getCaidas().set(turno, tablero.encontrar_casilla("Cárcel").getCaidas().get(turno)+1);
            actual.getEstadisticas().set(6, actual.getEstadisticas().get(6) + 1f);
        
        } else if(cartaSeleccionada.getNombre().equals("suerte6")){
            actual.sumarFortuna(1000000f);
            actual.getEstadisticas().set(5, actual.getEstadisticas().get(5) + 1000000f);
        } this.suerte = false; 
        return true;
    }

    /*Método usado para comprar una casilla determinada. Parámetros:
    * - Jugador que solicita la compra de la casilla.
    * - Banca del monopoly (es el dueño de las casillas no compradas aún).*/
    public void comprarCasilla(Jugador solicitante, Jugador banca) {
        if (this.duenho.equals(banca)) {
            if (solicitante.getFortuna() >= this.valor) {
                solicitante.sumarGastos(this.valor);
                this.duenho = solicitante;
                solicitante.anhadirPropiedad(this);
                banca.sumarFortuna(this.valor);
                solicitante.getEstadisticas().set(0, solicitante.getEstadisticas().get(0) + this.valor);
                System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + this.nombre + " por " + this.valor + "€. Su fortuna actual es " + solicitante.getFortuna() + "€.");
            }
        } else{
            System.out.println("La casilla no está en venta.");
        }
    }

    /*Método para hipotecar una casilla. */
    public void hipotecarCasilla(Jugador solicitante, Jugador banca){
        if(this.duenho.equals(solicitante)){
            if(this.hipotecada){System.out.println(solicitante.getNombre() + " no puede hipotecar " + this.getNombre() +". Ya está hipotecada.");
            } else{
                if(this.getTipo().equals("Solar")){
                    int cont=0;
                    for(int i : this.edificios){
                        if(i != 0){cont=1;}  
                    } 
                    if(cont==1){System.out.println("Antes de hipotecar se deben vender todos los edificios");
                    } else{
                        System.out.println(solicitante.getNombre() + " recibe " + this.hipoteca + "€. No puede recibir alquileres ni edificar en el grupo "+ this.getColorGrupo());
                        this.hipotecada = true;
                        solicitante.sumarFortuna(this.hipoteca);
                        //banca.sumarGastos(this.hipoteca);
                    }
                } else{
                    System.out.println(solicitante.getNombre() + " recibe " + this.hipoteca + "€. No puede recibir alquileres en "+ this.getNombre());
                    this.hipotecada = true;
                    solicitante.sumarFortuna(this.hipoteca);
                    //banca.sumarGastos(this.hipoteca);
                }
            }
        } else{System.out.println(solicitante.getNombre() + " no puede hipotecar " + this.getNombre() + ". No es una propiedad que le pertenece.\n");}
    }

    /*Método para deshipotecar una casilla. */
    public void deshipotecarCasilla(Jugador solicitante, Jugador banca){
        if(this.duenho.equals(solicitante)){
            if(!this.hipotecada){System.out.println(solicitante.getNombre() + " no puede deshipotecar " + this.getNombre() + ". Ya está deshipotecada.");
            } else{
                float deuda = 1.1f*(this.hipoteca);
                if(solicitante.getFortuna()>=deuda){
                    if(this.getTipo().equals("Solar")){System.out.println(solicitante.getNombre() + " paga " + deuda + " por deshipotecar " + this.getNombre() + ". Ahora puede recibir alquileres y edificar en el grupo " + this.getColorGrupo() + ".");
                    } else{System.out.println(solicitante.getNombre() + " paga " + deuda + " por deshipotecar " + this.getNombre() + ". Ahora puede recibir alquileres en" + this.getNombre());}
                    
                    this.hipotecada=false;
                    solicitante.sumarGastos(deuda);
                    //banca.sumarFortuna(deuda);
                } else{System.out.println(solicitante.getNombre() + " no tiene dinero suficiente para deshipotecar " + this.getNombre() + ".");}
            }
        } else{System.out.println(solicitante.getNombre() + " no puede deshipotecar " + this.getNombre() + ". No es una propiedad que le pertenece.\n");}
    }

    /*Método para vender una cantidad concreta de edificios de un tipo. */
    public void venderEdificios(Jugador solicitante, Jugador banca, String tipo, String cantidad, ArrayList<Edificio> lista_edificios){
        int numvender = Integer.parseInt(cantidad), num, res;
        if(this.getDuenho().equals(solicitante)){
            if(tipo.equals("casa")){
                num = edificios.get(0);
                if(num==0){System.out.println("No hay casas que se puedan vender en esta casilla.");}
                else{
                    if(num<numvender) System.out.println("Solamente se puede vender " + num + " casa/s, recibiendo " + (0.6f*(this.valor_inicial))/2f + "€ por edificio.");
                    else{
                        solicitante.sumarFortuna(numvender*(0.6f*(this.valor_inicial))/2f);
                        this.valoredificios -= (numvender*(0.6f*(this.valor_inicial))/2f);
                        //banca.sumarGastos((0.6f*(this.valor_inicial))/2f);
                        res = this.edificios.get(0)-numvender;    
                        this.edificios.set(0, res);
                        this.grupo.getEdificios().set(0, res);
                        for(int i=0;i<numvender;i++){
                            eliminarEdificio("casa", lista_edificios, this, solicitante);
                            eliminarEdificioCas("casa");
                        }
                        System.out.println(solicitante.getNombre() + " ha vendido " + numvender + " casa/s en " + this.getNombre() + ", recibiendo " +  numvender*(0.6f*(this.valor_inicial))/2f + "€. Casas restantes en la propiedad: " + res);
                    }
                }
            }
            if(tipo.equals("hotel")){
                num = edificios.get(1);
                if(num==0){System.out.println("No hay hoteles que se puedan vender en esta casilla.");}
                else{
                if(num<numvender) System.out.println("Solamente se puede vender " + num + " hotel/es, recibiendo " + (0.6f*(this.valor_inicial))/2f  + "€ por edificio.");
                else{
                        solicitante.sumarFortuna(numvender*(0.6f*(this.valor_inicial))/2f);
                        this.valoredificios -= (numvender*(0.6f*(this.valor_inicial))/2f);
                        //banca.sumarGastos((0.6f*(this.valor_inicial))/2f);
                        res = this.edificios.get(1)-numvender;    
                        this.edificios.set(1, res);
                        this.grupo.getEdificios().set(1, res);
                        for(int i=0;i<numvender;i++){
                            eliminarEdificio("hotel", lista_edificios, this, solicitante);
                            eliminarEdificioCas("hotel");
                        }
                        System.out.println(solicitante.getNombre() + " ha vendido " + numvender + " hotel/es en " + this.getNombre() + ", recibiendo " +  numvender*(0.6f*(this.valor_inicial))/2f + "€. Hoteles restantes en la propiedad: " + res);
                    }
                }
            }
            if(tipo.equals("piscina")){
                num = edificios.get(2);
                if(num==0){System.out.println("No hay piscinas que se puedan vender en esta casilla.");}
                else{
                    if(num<numvender) System.out.println("Solamente se puede vender " + num + " piscina/s, recibiendo " + (0.4f*(this.valor_inicial))/2f  + "€ por edificio.");
                    else{
                        solicitante.sumarFortuna(numvender*(0.4f*this.valor_inicial)/2f);
                        this.valoredificios -= (numvender*(0.4f*(this.valor_inicial))/2f);
                        //banca.sumarGastos(0.4f*(this.valor_inicial)/2f);
                        res = this.edificios.get(2)-numvender;    
                        this.edificios.set(2, res);
                        this.grupo.getEdificios().set(2, res);
                        for(int i=0;i<numvender;i++){
                            eliminarEdificio("piscina", lista_edificios, this, solicitante);
                            eliminarEdificioCas("piscina");
                        }
                        System.out.println(solicitante.getNombre() + " ha vendido " + numvender + " piscina/s en " + this.getNombre() + ", recibiendo " + numvender*(0.4f*(this.valor_inicial))/2f + "€. Piscinas restantes en la propiedad: " + res);
                    }
                }
            }
            if(tipo.equals("pista")){
                num = edificios.get(3);
                if(num==0){System.out.println("No hay pistas de deporte que se puedan vender en esta casilla.");}
                else{
                    if(num<numvender) System.out.println("Solamente se puede vender " + num + " pista/s de deporte, recibiendo " + (1.25f*(this.valor_inicial))/2f + "€ por edificio.");
                    else{
                        solicitante.sumarFortuna(numvender*(1.25f*(this.valor_inicial))/2f);
                        this.valoredificios -= (numvender*(1.25f*(this.valor_inicial))/2f);
                        //banca.sumarGastos((1.25f*(this.valor_inicial))/2f);
                        res = this.edificios.get(3)-numvender;    
                        this.edificios.set(3, res);
                        this.grupo.getEdificios().set(3, res);
                        for(int i=0;i<numvender;i++){
                            eliminarEdificio("pista",lista_edificios,this,solicitante);
                            eliminarEdificioCas("pista");
                        }
                        System.out.println(solicitante.getNombre() + " ha vendido " + numvender + " pista/s de deporte en " + this.getNombre() + ", recibiendo " +  numvender*(1.25f*(this.valor_inicial))/2f + "€. Pistas de deporte restantes en la propiedad: " + res);
                    }
                }
            }
        }
        else{
            System.out.println("No se pueden vender " + tipo + " en " + this.getNombre() + ". Esta propiedad no le pertenece a " + solicitante.getNombre());
        }
    }

    /*Método para eliminar un edificio de un tipo y una casilla de la lista general de edificios. */
    private void eliminarEdificio(String tipo, ArrayList<Edificio> lista_edificios, Casilla cas, Jugador solicitante){
        for(Edificio ed : lista_edificios){
            if(ed.getTipo().equals(tipo) && ed.getUbicacion().equals(cas)){
                lista_edificios.remove(ed);
                solicitante.venderEdificio(ed);
                return;
            }
        }
    }

    /*Método para eliminar un edificio de un tipo de una casilla. */
    private void eliminarEdificioCas(String tipo){
        for(Edificio ed : lista_edificio){
            if(ed.getTipo().equals(tipo)){
                lista_edificio.remove(ed);
                return;
            }
        }
    } 

    /*Método para añadir valor a una casilla. Utilidad:
    * - Sumar valor a la casilla de parking.
    * - Sumar valor a las casillas de solar al no comprarlas tras cuatro vueltas de todos los jugadores.
    * Este método toma como argumento la cantidad a añadir del valor de la casilla.*/
    public void sumarValor(float suma) {
        this.valor += suma;
    }

    /*Método para mostrar información sobre una casilla.
    * Devuelve una cadena con información específica de cada tipo de casilla.*/
    public String infoCasilla() {
        String info = "Nombre: " + this.nombre + "\n" + "Tipo: " + this.tipo + "\n";
        if (this.tipo.equals("Impuesto")) {
            info += "Impuesto: " + this.impuesto + "\n";}
        info += "Posición: " + this.posicion + "\n" + "Valor: " + this.valor + "\n" + "Dueño: " + (this.duenho == null ? "Banca" : this.duenho.getNombre()) + "\n";
        
        return info;
    }

    /* Método para mostrar información de una casilla en venta.
     * Valor devuelto: texto con esa información.
     */
    public String casEnVenta(Jugador banca) {
        if (this.duenho.equals(banca)) {
            return this.nombre + " en venta\nValor: " + this.valor;
        } else {
            return this.nombre + " NO está en venta";
        }
    }

    public void construirEdificio(String tipoedificio, Jugador solicitante, Jugador banca, ArrayList<Edificio> lista_edificios){
        int v = 0;
        for (int i = 1; i < 4; i++) {
            if(this.grupo.getEdificios().get(i)!=this.grupo.getNumCasillas()){v = 1;}
        } if(v==0){if(this.grupo.getEdificios().get(0)<this.grupo.getNumCasillas()){v = 1;}}
        
        if(v==1){
            if(tipoedificio.equals("casa")){
                if (this.grupo.getEdificios().get(1)==this.grupo.getNumCasillas() && this.edificios.get(0)<this.grupo.getNumCasillas()){
                    if (solicitante.getFortuna() >= this.valor_inicial*0.6f) {
                        solicitante.sumarGastos(this.valor_inicial*0.6f);
                        banca.sumarFortuna(this.valor_inicial*0.6f);
                        
                        System.out.println("Se ha edificado una casa en " + this.nombre + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + this.valor_inicial*0.6f + "€.");
                        this.valoredificios += 0.6f*(this.valor_inicial);
                        this.edificios.set(0, this.edificios.get(0)+1);
                        this.grupo.getEdificios().set(0, this.grupo.getEdificios().get(0)+1);
                        Edificio casa = new Edificio("casa", solicitante, this, lista_edificios);
                        solicitante.comprarEdificio(casa);
                        lista_edificios.add(casa);
                        lista_edificio.add(casa);


                    } else {System.out.println("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar una casa en la casilla " + this.nombre + ".");}
                } else if (this.grupo.getEdificios().get(1)<this.grupo.getNumCasillas() && this.edificios.get(0)<4){
                    if (solicitante.getFortuna() >= this.valor_inicial*0.6f) {
                        solicitante.sumarGastos(this.valor_inicial*0.6f);
                        banca.sumarFortuna(this.valor_inicial*0.6f);
                        
                        System.out.println("Se ha edificado una casa en " + this.nombre + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + this.valor_inicial*0.6f + "€.");
                        this.valoredificios += 0.6f*(this.valor_inicial);
                        this.edificios.set(0, this.edificios.get(0)+1);
                        this.grupo.getEdificios().set(0, this.grupo.getEdificios().get(0)+1);
                        Edificio casa = new Edificio("casa", solicitante, this, lista_edificios);
                        solicitante.comprarEdificio(casa);
                        lista_edificios.add(casa);
                        lista_edificio.add(casa);

                    } else {System.out.println("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar una casa en la casilla " + this.nombre + ".");}
                } else {System.out.println("No se pueden edificar más casas en este solar.");}
            
            } else if(tipoedificio.equals("hotel")){
                if(this.grupo.getEdificios().get(1)<this.grupo.getNumCasillas()){
                    if(this.grupo.getEdificios().get(1)==this.grupo.getNumCasillas()-1 && this.grupo.getEdificios().get(0)>this.grupo.getNumCasillas()){
                        System.out.println("No puedes edificar el hotel. Debes vender antes otras casas para tener como máximo " + this.grupo.getNumCasillas() + " hoteles y " + this.grupo.getNumCasillas() + " casas.");
                    } else {
                        if(this.edificios.get(0)==4){
                            if (solicitante.getFortuna() >= this.valor_inicial*0.6f) {
                                solicitante.sumarGastos(this.valor_inicial*0.6f);
                                banca.sumarFortuna(this.valor_inicial*0.6f);
                                
                                System.out.println("Se ha edificado un hotel en " + this.nombre + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + this.valor_inicial*0.6f + "€.");
                                this.valoredificios += 0.6f*(this.valor_inicial);
                                this.edificios.set(1, this.edificios.get(1)+1);
                                this.grupo.getEdificios().set(1, this.grupo.getEdificios().get(1)+1);
                                this.edificios.set(0, 0);
                                this.grupo.getEdificios().set(0, this.grupo.getEdificios().get(1)-4);
                                Edificio hotel = new Edificio("hotel", solicitante, this, lista_edificios);
                                
                                ArrayList<Edificio> casasAEliminar = new ArrayList<>();
                                for (Edificio casa : lista_edificio) {
                                    if (casa.getTipo().equals("casa")) {
                                        casasAEliminar.add(casa);
                                        solicitante.venderEdificio(casa);
                                    }
                                } lista_edificio.removeAll(casasAEliminar); lista_edificios.removeAll(casasAEliminar);
                                solicitante.comprarEdificio(hotel);
                                lista_edificios.add(hotel);
                                lista_edificio.add(hotel);

                            } else {System.out.println("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar un hotel en la casilla " + this.nombre + ".");}
                        } else {System.out.println("No se puede edificar un hotel, ya que no se dispone de cuatro casas en este solar.");}
                    }
                } else {System.out.println("No se pueden edificar más hoteles en este solar.");}
            
            } else if(tipoedificio.equals("piscina")){
                if(this.grupo.getEdificios().get(2)<this.grupo.getNumCasillas()){
                    if(this.edificios.get(0)>=2 && this.edificios.get(1)>=1){
                        if (solicitante.getFortuna() >= this.valor_inicial*0.4f) {
                            solicitante.sumarGastos(this.valor_inicial*0.4f);
                            banca.sumarFortuna(this.valor_inicial*0.4f);
                            
                            System.out.println("Se ha edificado una piscina en " + this.nombre + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + this.valor_inicial*0.4f + "€.");
                            this.valoredificios += 0.4f*(this.valor_inicial);
                            this.edificios.set(2, this.edificios.get(2)+1);
                            this.grupo.getEdificios().set(2, this.grupo.getEdificios().get(2)+1);
                            Edificio piscina = new Edificio("piscina", solicitante, this, lista_edificios);
                            solicitante.comprarEdificio(piscina);
                            lista_edificios.add(piscina);
                            lista_edificio.add(piscina);

                        } else {System.out.println("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar una piscina en la casilla " + this.nombre + ".");}
                    } else {System.out.println("No se puede edificar una piscina, ya que no se dispone de un hotel y dos casas.");}
                } else {System.out.println("No se pueden edificar más piscinas en este solar.");}
            
            } else if(tipoedificio.equals("pista")){
                if(this.grupo.getEdificios().get(3)<this.grupo.getNumCasillas()){
                    if(this.edificios.get(1)>=2){
                        if (solicitante.getFortuna() >= this.valor_inicial*1.25f) {
                            solicitante.sumarGastos(this.valor_inicial*1.25f);
                            banca.sumarFortuna(this.valor_inicial*1.25f);
                            
                            System.out.println("Se ha edificado una pista de deporte en " + this.nombre + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + this.valor_inicial*1.25f + "€.");
                            this.valoredificios += 1.25f*(this.valor_inicial);
                            this.edificios.set(3, this.edificios.get(3)+1);
                            this.grupo.getEdificios().set(3, this.grupo.getEdificios().get(3)+1);
                            Edificio pista = new Edificio("pista", solicitante, this, lista_edificios);
                            solicitante.comprarEdificio(pista);
                            lista_edificios.add(pista);
                            lista_edificio.add(pista);

                        } else {System.out.println("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar una pista de deporte en la casilla " + this.nombre + ".");}
                    } else {System.out.println("No se puede edificar una pista de deportes, ya que no se dispone de dos hoteles.");}
                } else {System.out.println("No se pueden edificar más piscinas en este solar.");}
            } //else {System.out.println("Edificio no reconocido.");}
        } else {System.out.println("No se pueden construir más edificios en este grupo.");}
        this.calcularImpuesto();
    }

    public void calcularImpuesto(){
        if(!this.duenho.getNombre().equals("banca")){
            int v = 0;
            for (int i = 0; i < 4; i++) {if(this.grupo.getEdificios().get(i)!=0){v = 1;}}
            if(this.grupo.esDuenhoGrupo(this.duenho) && v==0){
                this.impuesto = 0.2f*(this.valor_inicial);
            } else if(!this.grupo.esDuenhoGrupo(this.duenho) && v==0){
                this.impuesto = 0.1f*(this.valor_inicial);
            } else {
                this.impuesto = 0;
                if(this.edificios.get(0)==1){this.impuesto += 5*0.1f*(this.valor_inicial);}
                else if(this.edificios.get(0)==2){this.impuesto += 15*0.1f*(this.valor_inicial);}
                else if(this.edificios.get(0)==3){this.impuesto += 35*0.1f*(this.valor_inicial);}
                else if(this.edificios.get(0)==4){this.impuesto += 50*0.1f*(this.valor_inicial);}
                this.impuesto += this.edificios.get(1)*70*0.1f*(this.valor_inicial);
                this.impuesto += this.edificios.get(2)*25*0.1f*(this.valor_inicial);
                this.impuesto += this.edificios.get(3)*25*0.1f*(this.valor_inicial);
            }
        }
    }
    
    //Para imprimir la información de la casilla, modificamos el método toString().
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (this.tipo) {
            case "Solar":
                calcularImpuesto();
                sb.append("tipo: ").append(this.tipo).append(",\n");
                sb.append("grupo: ").append(this.getColorGrupo()).append(",\n");
                sb.append("propietario: ").append(this.duenho.getNombre()).append(",\n");
                sb.append("valor: ").append(this.valor).append(",\n");
                
                sb.append("alquiler: ").append(0.1f*(this.valor_inicial)).append(",\n");
                sb.append("valor casa: ").append(0.6f*(this.valor_inicial)).append(",\n");
                sb.append("valor hotel: ").append(0.6f*(this.valor_inicial)).append(",\n");
                sb.append("valor piscina: ").append(0.4f*(this.valor_inicial)).append(",\n");
                sb.append("valor pista de deporte: ").append(this.valor_inicial + 1.25f*(this.valor_inicial)).append(",\n");
                sb.append("alquiler una casa: ").append(5f*(0.1f*(this.valor_inicial))).append(",\n");
                sb.append("alquiler dos casas: ").append(15f*(0.1f*(this.valor_inicial))).append(",\n");
                sb.append("alquiler tres casas: ").append(35f*(0.1f*(this.valor_inicial))).append(",\n");
                sb.append("alquiler cuatro casas: ").append(50f*(0.1f*(this.valor_inicial))).append(",\n");
                sb.append("alquiler hotel: ").append(70f*(0.1f*(this.valor_inicial))).append(",\n");
                sb.append("alquiler piscina: ").append(25f*(0.1f*(this.valor_inicial))).append(",\n");
                sb.append("alquiler pista de deporte: ").append(25f*(0.1f*(this.valor_inicial))).append(",\n");

                sb.append("número de casas edificadas: ").append(this.edificios.get(0)).append(",\n");
                sb.append("número de hoteles edificados: ").append(this.edificios.get(1)).append(",\n");
                sb.append("número de piscinas edificadas: ").append(this.edificios.get(2)).append(",\n");
                sb.append("número de pistas de deporte edificadas: ").append(this.edificios.get(3)).append(",\n");
                sb.append("alquiler actual: ").append(this.impuesto).append("\n");
                break;
            
            case "Transporte":
                sb.append("tipo: ").append(this.tipo).append(",\n");
                sb.append("propietario: ").append(this.duenho.getNombre()).append(",\n");
                sb.append("valor: ").append(this.valor).append(",\n");
                sb.append("alquiler: ").append(Valor.SUMA_VUELTA).append("\n");
                break;

            case "Servicio":
                sb.append("tipo: ").append(this.tipo).append(",\n");
                sb.append("propietario: ").append(this.duenho.getNombre()).append(",\n");
                sb.append("valor: ").append(this.valor).append(",\n");
                sb.append("alquiler: ").append(Valor.SUMA_VUELTA/200f).append("\n");
                break;
            
            case "Impuesto":
                sb.append("tipo: ").append(this.tipo).append(",\n");
                sb.append("a pagar: ").append(this.impuesto).append("\n");
                break;
        
            default:
                switch (this.nombre) {
                    case "Parking":
                        sb.append("bote: ").append(this.valor).append(",\n");
                        sb.append("jugadores: [");
                        for(Avatar i:this.avatares){
                            sb.append(i.getJugador().getNombre()).append(", ");
                        }
                        if (this.avatares.size()>0){
                            sb.deleteCharAt(sb.length() - 1);
                            sb.deleteCharAt(sb.length() - 1);
                        }
                        sb.append("]").append("\n");
                        break;
                    
                    case "Cárcel":
                        sb.append("salir: ").append(0.25*Valor.SUMA_VUELTA).append(",\n");
                        sb.append("jugadores: ");
                        for(Avatar i:this.avatares){
                            if(i.getJugador().getEncarcelado()){
                                sb.append("[").append(i.getJugador().getNombre()).append(", ").append(i.getJugador().getTurnosCarcel()).append("] ");
                            }
                        } break;

                    default:
                        sb.append("Esta casilla no tiene características especiales");
                        break;
                }
                break;
        }
        return sb.toString();
    }

    //GETTERS
    /*Método para obtener el nombre de la casilla*/
    public String getNombre(){
        return this.nombre;
    }

    /*Método para obtener el tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte o Impuesto)*/
    public String getTipo(){
        return this.tipo;
    }

    /*Método para obtener el valor de compra (o bote del Parking) de la casilla*/
    public float getValor() {
        return this.valor;
    }

    /*Método para obtener la posición de la casilla*/
    public int getPosicion(){
        return this.posicion;
    }
    
    /*Método para obtener el dueño de la casilla*/
    public Jugador getDuenho(){
        return this.duenho;
    }

    /*Método para obtener el grupo al que pertenece la casilla*/
    public Grupo getGrupo() {
        return this.grupo;
    }

    /*Método para obtener la cantidad a pagar al caer en la casilla*/
    public float getImpuesto() {
        return this.impuesto;
    }
    
    public float getHipoteca() {
        return this.hipoteca;
    }

    /*Método para obtener la lista de avatares situados en la casilla*/
    public ArrayList<Avatar> getAvatares() {
        return this.avatares;
    }

    public String getColorGrupo(){
        switch (this.getGrupo().getColor()) {
            case Valor.BLACK:
                return "Negro";
            case Valor.RED:
                return "Rojo";
            case Valor.GREEN:
                return "Verde";
            case Valor.YELLOW:
                return "Amarillo";
            case Valor.BLUE:
                return "Azul";
            case Valor.PURPLE:
                return "Rosa";
            case Valor.CYAN:
                return "Cian";
            case Valor.WHITE:
                return "Blanco";
            default:
                return "La casilla no pertenece a ningún grupo.";
        }

    }

    public ArrayList<Integer> getCaidas() {
        return this.caidas;
    }

    public ArrayList<Integer> getEdificios() {
        return this.edificios;
    }

    public ArrayList<Edificio> getLista_edificio() {
        return this.lista_edificio;
    }

    public float getValor_inicial() {
        return this.valor_inicial;
    }

    public boolean getComunidad(){
        return this.comunidad;
    }

    public boolean getSuerte(){
        return this.suerte;
    }

    public float getRentabilidad() {
        return this.rentabilidad;
    }

    public float getvaloredificios() {
        return this.valoredificios;
    }

    public boolean getHipotecada(){
        return this.hipotecada;
    }

    //SETTERS
    /*Método para modificar el nombre de la casilla*/
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /*Método para modificar el tipo de la casilla*/
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /*Método para obtener el valor de compra (o bote del Parking) de la casilla*/
    public void setValor(float valor) {
        this.valor = valor;
    }

    /*Método para modificar la posición de la casilla*/
    public void setPosicion(int posicion){
        this.posicion = posicion;
    }

    /*Método para modificar el dueño de la casilla*/
    public void setDuenho(Jugador duenho) {
        this.duenho = duenho;
    }

    /*Método para modificar el grupo de la casilla*/
    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    /*Método para modificar la cantidad a pagar al caer en la casilla*/
    public void setImpuesto(float impuesto) {
        this.impuesto = impuesto;
    }

    public void setHipoteca(float hipoteca) {
        this.hipoteca = hipoteca;
    }

    /*Método para obtener la lista de avatares situados en la casilla*/
    public void setAvatares(ArrayList<Avatar> avatares) {
        this.avatares = avatares;
    }

    public void setCaidas(ArrayList<Integer> caidas) {
        this.caidas = caidas;
    }

    public void setEdificios(ArrayList<Integer> edificios) {
        this.edificios = edificios;
    }

    public void setRentabilidad(float rentabilidad){
        this.rentabilidad=rentabilidad;
    }
}