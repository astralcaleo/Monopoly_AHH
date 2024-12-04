package monopoly.casillas.propiedades;

import java.util.ArrayList;

import monopoly.edificios.*;
import monopoly.excepciones.LimiteDeEdificacionAlcanzadoException;
import monopoly.excepciones.SaldoInsuficienteException;
import monopoly.Tablero;
import monopoly.Juego;
import monopoly.casillas.Grupo;
import partida.Jugador;

public class Solar extends Propiedad {
    //Atributos
    private float valor;                        // Precio de venta la propiedad
    private float alquiler;                     // Dinero que se cobra como renta
    private Jugador propietario;                // Nombre del propietario actual (null si no tiene)
    private Grupo grupo;                        // Grupo al que pertenece el solar
    private ArrayList<Edificio> edificios;      // Edificios construidos en el solar
    private ArrayList<Integer> numEdificios;    // Cantidad de edificios (0: casas, 1: hoteles, 2: piscinas, 3: pistas deporte)

    // Constructor
    public Solar(String nombre, int posicion, float valorInicial, Jugador propietario) {
        super(nombre, posicion, valorInicial, propietario);       // Llama al constructor de la clase padre Propiedad
        this.valor = super.getValor();
        this.propietario = super.getPropietario();
        this.alquiler = super.getAlquiler();
        this.edificios = new ArrayList<Edificio>();
        this.numEdificios = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {this.numEdificios.add(0);}
    }

    // Getters y Setters
    public Grupo getGrupo() {return grupo;}
    public void setGrupo(Grupo grupo) {this.grupo = grupo;}
    public ArrayList<Edificio> getEdificios() {return edificios;}
    public ArrayList<Integer> getNumEdificios() {return numEdificios;}

    // Métodos de Solar
    // Método para añadir valor a una casilla
    public void sumarValor(float suma) {this.valor += suma;}

    // Método que recalcula el alquiler del Solar teniendo en cuenta los edificios construidos
    public void calcularAlquiler(){
        if(!this.propietario.getNombre().equals("banca")){
            int v = 0;
            for (int i = 0; i < 4; i++) {if(grupo.getEdificios().get(i)!=0){v = 1;}}
            if(grupo.esDuenhoGrupo(propietario) && v==0){
                alquiler = 0.2f * super.getValorInicial();
            } else if(!grupo.esDuenhoGrupo(propietario) && v==0){
                alquiler = 0.1f * super.getValorInicial();
            } else {
                float precio = 0.1f * super.getValorInicial();
                alquiler = 0;
                
                if(numEdificios.get(0)==1){alquiler += 5 * precio;}
                else if(numEdificios.get(0)==2){alquiler += 15 * precio;}
                else if(numEdificios.get(0)==3){alquiler += 35 * precio;}
                else if(numEdificios.get(0)==4){alquiler += 50 * precio;}
                alquiler += numEdificios.get(1) * 70 * precio;
                alquiler += numEdificios.get(2) * 25 * precio;
                alquiler += numEdificios.get(3) * 25 * precio;
            }
        }
    }    

    // Método para construir un edificio de un tipo en el solar
    public void construirEdificio(String tipoedificio, Jugador solicitante, ArrayList<Edificio> edificiosConstruidos) throws Exception{
        int v = 0;
        for (int i = 1; i < 4; i++) {
            if(grupo.getEdificios().get(i) != grupo.getNumCasillas()){v = 1;}
        } if(v==0){if(grupo.getEdificios().get(0) < grupo.getNumCasillas()){v = 1;}}
        
        if(v==1){
            if(tipoedificio.equals("casa")){
                float precio = super.getValorInicial() * 0.6f;
                if ((grupo.getEdificios().get(1) == grupo.getNumCasillas() && numEdificios.get(0) < grupo.getNumCasillas()) || (grupo.getEdificios().get(1) < grupo.getNumCasillas() && numEdificios.get(0) < 4)){
                    if (solicitante.getFortuna() >= precio) {
                        solicitante.sumarGastos(precio);
                        System.out.println("Se ha edificado una casa en " + super.getNombre() + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + precio + "€.");
                        numEdificios.set(0, numEdificios.get(0)+1);
                        grupo.getEdificios().set(0, grupo.getEdificios().get(0)+1);
                        Casa casa = new Casa(solicitante, this, edificiosConstruidos);
                        solicitante.comprarEdificio(casa);
                        edificios.add(casa);
                        edificiosConstruidos.add(casa);

                    } else {throw new SaldoInsuficienteException("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar una casa en la casilla " + super.getNombre() + ".");}
                
                } else {throw new LimiteDeEdificacionAlcanzadoException("No se pueden edificar más casas en este solar.");}
            
            } else if(tipoedificio.equals("hotel")){
                float precio = super.getValorInicial() * 0.6f;
                if(grupo.getEdificios().get(1) < grupo.getNumCasillas()){
                    if(grupo.getEdificios().get(1) == grupo.getNumCasillas()-1 && grupo.getEdificios().get(0) > grupo.getNumCasillas()){
                        throw new LimiteDeEdificacionAlcanzadoException("No puedes edificar el hotel. Debes vender antes otras casas para tener como máximo " + grupo.getNumCasillas() + " hoteles y " + grupo.getNumCasillas() + " casas.");
                    } else {
                        if(numEdificios.get(0) == 4){
                            if (solicitante.getFortuna() >= precio) {
                                ArrayList<Edificio> casasAEliminar = new ArrayList<>();
                                for (Edificio casa : edificios) {
                                    if (casa instanceof Casa) { 
                                        casasAEliminar.add(casa);
                                        solicitante.venderEdificio(casa);
                                    }
                                } edificios.removeAll(casasAEliminar); edificiosConstruidos.removeAll(casasAEliminar);
                                numEdificios.set(0, 0);
                                grupo.getEdificios().set(0, grupo.getEdificios().get(1)-4);

                                solicitante.sumarGastos(precio);
                                System.out.println("Se ha edificado un hotel en " + super.getNombre() + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + precio + "€.");
                                numEdificios.set(1, numEdificios.get(1)+1);
                                grupo.getEdificios().set(1, grupo.getEdificios().get(1)+1);
                                Hotel hotel = new Hotel(solicitante, this, edificiosConstruidos);
                                solicitante.comprarEdificio(hotel);
                                edificios.add(hotel);
                                edificiosConstruidos.add(hotel);

                            } else {throw new SaldoInsuficienteException("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar un hotel en la casilla " + super.getNombre() + ".");}
                        } else {throw new LimiteDeEdificacionAlcanzadoException("No se puede edificar un hotel, ya que no se dispone de cuatro casas en este solar.");}
                    }
                } else {throw new LimiteDeEdificacionAlcanzadoException("No se pueden edificar más hoteles en este solar.");}
            
            } else if(tipoedificio.equals("piscina")){
                float precio = super.getValorInicial() * 0.4f;
                if(grupo.getEdificios().get(2) < grupo.getNumCasillas()){
                    if(numEdificios.get(0) >= 2 && numEdificios.get(1) >= 1){
                        if (solicitante.getFortuna() >= precio) {
                            solicitante.sumarGastos(precio);
                            System.out.println("Se ha edificado una piscina en " + super.getNombre() + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + precio + "€.");
                            numEdificios.set(2, numEdificios.get(2)+1);
                            grupo.getEdificios().set(2, grupo.getEdificios().get(2)+1);
                            Piscina piscina = new Piscina(solicitante, this, edificiosConstruidos);
                            solicitante.comprarEdificio(piscina);
                            edificios.add(piscina);
                            edificiosConstruidos.add(piscina);

                        } else {throw new SaldoInsuficienteException("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar una piscina en la casilla " + getNombre() + ".");}
                    } else {throw new LimiteDeEdificacionAlcanzadoException("No se puede edificar una piscina, ya que no se dispone de al menos un hotel y dos casas.");}
                } else {throw new LimiteDeEdificacionAlcanzadoException("No se pueden edificar más piscinas en este solar.");}
            
            } else if(tipoedificio.equals("pista")){
                float precio = super.getValorInicial() * 1.25f;
                if(grupo.getEdificios().get(3)<grupo.getNumCasillas()){
                    if(numEdificios.get(1)>=2){
                        if (solicitante.getFortuna() >= precio) {
                            solicitante.sumarGastos(precio);                            
                            System.out.println("Se ha edificado una pista de deporte en " + super.getNombre() + ".\nLa fortuna de " + solicitante.getNombre() + " se reduce en " + precio + "€.");
                            numEdificios.set(3, numEdificios.get(3)+1);
                            grupo.getEdificios().set(3, grupo.getEdificios().get(3)+1);
                            PistaDeporte pista = new PistaDeporte(solicitante, this, edificiosConstruidos);
                            solicitante.comprarEdificio(pista);
                            edificios.add(pista);
                            edificiosConstruidos.add(pista);

                        } else {throw new SaldoInsuficienteException("La fortuna de " + solicitante.getNombre() + " no es suficiente para edificar una pista de deporte en la casilla " + super.getNombre() + ".");}
                    } else {throw new LimiteDeEdificacionAlcanzadoException("No se puede edificar una pista de deportes, ya que no se dispone de dos hoteles.");}
                } else {throw new LimiteDeEdificacionAlcanzadoException("No se pueden edificar más piscinas en este solar.");}
            } //else {System.out.println("Edificio no reconocido.");}
        } else {throw new LimiteDeEdificacionAlcanzadoException("No se pueden construir más edificios en este grupo.");}
        this.calcularAlquiler();
    }
   
    // Método para vender una cantidad concreta de edificios de un tipo
    public void venderEdificios(Jugador solicitante, String tipo, String cantidad, ArrayList<Edificio> edificiosConstruidos){
        int numvender = Integer.parseInt(cantidad), num, res;
        if(propietario.equals(solicitante)){
            if(tipo.equals("casa")){
                num = numEdificios.get(0);
                if(num == 0){System.out.println("No hay casas que se puedan vender en esta casilla.");}
                else{
                    float precio = 0.6f * super.getValorInicial() / 2f;
                    if(num < numvender){System.out.println("Solamente se puede vender " + num + " casa/s, recibiendo " + precio + "€ por edificio.");
                    } else{
                        solicitante.sumarFortuna(numvender * precio);
                        res = numEdificios.get(0) - numvender;    
                        numEdificios.set(0, res);
                        grupo.getEdificios().set(0, res);
                        for(int i=0;i<numvender;i++){
                            eliminarEdificio("casa", edificiosConstruidos, this, solicitante);
                            eliminarEdificioCas("casa");
                        }
                        System.out.println(solicitante.getNombre() + " ha vendido " + numvender + " casa/s en " + this.getNombre() + ", recibiendo " +  numvender * precio + "€. Casas restantes en la propiedad: " + res);
                    }
                }
            }
            if(tipo.equals("hotel")){
                num = numEdificios.get(1);
                if(num == 0){System.out.println("No hay hoteles que se puedan vender en esta casilla.");}
                else{
                    float precio = 0.6f * super.getValorInicial() / 2f;
                    if(num < numvender){System.out.println("Solamente se puede vender " + num + " hotel/es, recibiendo " + precio  + "€ por edificio.");
                    } else{
                        solicitante.sumarFortuna(numvender * precio);
                        res = numEdificios.get(1) - numvender;    
                        numEdificios.set(1, res);
                        this.grupo.getEdificios().set(1, res);
                        for(int i=0;i<numvender;i++){
                            eliminarEdificio("hotel", edificiosConstruidos, this, solicitante);
                            eliminarEdificioCas("hotel");
                        }
                        System.out.println(solicitante.getNombre() + " ha vendido " + numvender + " hotel/es en " + this.getNombre() + ", recibiendo " +  numvender * precio + "€. Hoteles restantes en la propiedad: " + res);
                    }
                }
            }
            if(tipo.equals("piscina")){
                num = numEdificios.get(2);
                if(num==0){System.out.println("No hay piscinas que se puedan vender en esta casilla.");}
                else{
                    float precio = 0.4f * super.getValorInicial() / 2f;
                    if(num < numvender){System.out.println("Solamente se puede vender " + num + " piscina/s, recibiendo " + precio  + "€ por edificio.");
                    } else{
                        solicitante.sumarFortuna(numvender * precio);
                        res = numEdificios.get(2) - numvender;    
                        numEdificios.set(2, res);
                        grupo.getEdificios().set(2, res);
                        for(int i=0;i<numvender;i++){
                            eliminarEdificio("piscina", edificiosConstruidos, this, solicitante);
                            eliminarEdificioCas("piscina");
                        }
                        System.out.println(solicitante.getNombre() + " ha vendido " + numvender + " piscina/s en " + this.getNombre() + ", recibiendo " + numvender * precio + "€. Piscinas restantes en la propiedad: " + res);
                    }
                }
            }
            if(tipo.equals("pista")){
                num = numEdificios.get(3);
                if(num==0){System.out.println("No hay pistas de deporte que se puedan vender en esta casilla.");}
                else{
                    float precio = 1.25f * super.getValorInicial() / 2f;
                    if(num < numvender) System.out.println("Solamente se puede vender " + num + " pista/s de deporte, recibiendo " + precio + "€ por edificio.");
                    else{
                        solicitante.sumarFortuna(numvender * precio);
                        res = numEdificios.get(3) - numvender;    
                        numEdificios.set(3, res);
                        this.grupo.getEdificios().set(3, res);
                        for(int i=0;i<numvender;i++){
                            eliminarEdificio("pista", edificiosConstruidos, this, solicitante);
                            eliminarEdificioCas("pista");
                        }
                        System.out.println(solicitante.getNombre() + " ha vendido " + numvender + " pista/s de deporte en " + this.getNombre() + ", recibiendo " +  numvender * precio + "€. Pistas de deporte restantes en la propiedad: " + res);
                    }
                }
            }
        } else{System.out.println("No se pueden vender " + tipo + " en " + this.getNombre() + ". Esta propiedad no le pertenece a " + solicitante.getNombre());}
    }

    // Método para eliminar un edificio de un tipo y un solar de la lista general de edificios
    private void eliminarEdificio(String tipo, ArrayList<Edificio> edificiosConstruidos, Solar cas, Jugador solicitante){
        switch (tipo) {
            case "casa":
                for(Edificio ed : edificios){
                    if(ed.getUbicacion().equals(cas)){
                        if(ed instanceof Casa){edificiosConstruidos.remove(ed); solicitante.venderEdificio(ed); return;}
                    }
                } break;

            case "hotel":
                for(Edificio ed : edificios){
                    if(ed.getUbicacion().equals(cas)){
                        if(ed instanceof Hotel){edificiosConstruidos.remove(ed); solicitante.venderEdificio(ed); return;}
                    }
                } break;

            case "piscina":
                for(Edificio ed : edificios){
                    if(ed.getUbicacion().equals(cas)){
                        if(ed instanceof Piscina){edificiosConstruidos.remove(ed); solicitante.venderEdificio(ed); return;}
                    }
                } break;

            case "pista":
                for(Edificio ed : edificios){
                    if(ed.getUbicacion().equals(cas)){
                        if(ed instanceof PistaDeporte){edificiosConstruidos.remove(ed); solicitante.venderEdificio(ed); return;}
                    }
                } break;
        
            default:
                System.out.println("No existe el tipo de edificio.");
                break;
        }
    }

    // Método para eliminar un edificio de un tipo de un solar
    private void eliminarEdificioCas(String tipo){
        switch (tipo) {
            case "casa":
                for(Edificio ed : edificios){if(ed instanceof Casa){edificios.remove(ed); return;}}
                break;

            case "hotel":
                for(Edificio ed : edificios){if(ed instanceof Hotel){edificios.remove(ed); return;}}
                break;

            case "piscina":
                for(Edificio ed : edificios){if(ed instanceof Piscina){edificios.remove(ed); return;}}
                break;

            case "pista":
                for(Edificio ed : edificios){if(ed instanceof PistaDeporte){edificios.remove(ed); return;}}
                break;
        
            default:
                System.out.println("No existe el tipo de edificio.");
                break;
        }
    } 
    
    // Método para saber el valor total de los edificios
    public float valorEdificios(){
        float valoredificios = 0f;
        valoredificios += numEdificios.get(0) * 0.6f * super.getValorInicial();
        valoredificios += numEdificios.get(1) * 0.6f * super.getValorInicial();
        valoredificios += numEdificios.get(2) * 0.4f * super.getValorInicial();
        valoredificios += numEdificios.get(3) * 1.25f * super.getValorInicial();
        return valoredificios;
    }

     // Métodos heredados
    @Override
    public void hipotecar(Jugador solicitante) {
        if (propietario.equals(solicitante)) {
            if (!super.isHipotecada()) {
                int cont=0;
                for(int i : this.numEdificios){if(i != 0){cont=1;}} 
                
                if(cont==1){System.out.println("Antes de hipotecar se deben vender todos los edificios");
                } else {
                    super.setHipotecada(true);
                    System.out.println(solicitante.getNombre() + " recibe " + super.getHipoteca() + "€. No puede recibir alquileres ni edificar en el grupo " + grupo.colorGrupo() + ".");
                    solicitante.sumarFortuna(super.getHipoteca());
                }
            } else {System.out.println("La propiedad " + getNombre() + " ya está hipotecada.");}
        } else {System.out.println("La propiedad no pertenece al jugador " + solicitante.getNombre() + ".");}
    }

    // Método para ejecutar acciones específicas de cada tipo de casilla
    @Override
    public boolean evaluarCasilla(Jugador actual, int tirada, Tablero tablero, int turno, Juego menu){
        if (!this.propietario.equals(tablero.getBanca()) && !this.propietario.equals(actual)) {
            if (!super.isHipotecada()){
                calcularAlquiler();
                
                if (actual.getFortuna() < alquiler){
                    System.out.println("El jugador " + actual.getNombre() + " no puede pagar la deuda.");
                    System.out.println("El jugador debe o bien hipotecar o bien declararse en bancarrota");
                    return false; 
                } else {
                    actual.sumarGastos(alquiler);
                    actual.getEstadisticas().set(2, actual.getEstadisticas().get(2) + alquiler);

                    this.propietario.sumarFortuna(alquiler);
                    this.propietario.getEstadisticas().set(3, this.propietario.getEstadisticas().get(3) + alquiler);

                    System.out.println("El jugador " + actual.getNombre() + " paga " + alquiler + "€ al jugador " + this.propietario.getNombre());
                    super.setRentabilidad(super.getRentabilidad() + alquiler);
                }
            } else{System.out.println("Este solar se encuentra hipotecado. No se cobrarán alquileres.");}
        } return true;
    }

    // Método toString para representar la propiedad
    @Override
    public String toString() {
        calcularAlquiler();
        float alquilerOriginal = 0.1f * getValorInicial();
        return super.getNombre() + 
               "\n\tGrupo: " + grupo.colorGrupo() + 
               "\n\tValor de compra: " + super.getValor() + 
               "\n\tPropietario: " + ((propietario.getNombre().equals("banca")) ? "Banca" : propietario.getNombre()) + 
               "\n\tHipotecada: " + (super.isHipotecada() ? "SI" : "NO") +
               "\n\tAlquiler sin edificar: " + alquilerOriginal + "\n" +

               "\n\tValor por casa: " + 0.6f * super.getValorInicial() + 
               "\n\tValor por hotel: " + 0.6f * super.getValorInicial() + 
               "\n\tValor por piscina: " + 0.4f * super.getValorInicial() + 
               "\n\tValor por pista de deporte: " + 1.25f * super.getValorInicial() +  "\n" +

               "\n\tAlquiler de una casa: " + 5f*alquilerOriginal + 
               "\n\tAlquiler de dos casas: " + 15f*alquilerOriginal + 
               "\n\tAlquiler de tres casas: " + 35f*alquilerOriginal + 
               "\n\tAlquiler de cuatro casas: " + 50f*alquilerOriginal + 
               "\n\tAlquiler por hotel: " + 70f*alquilerOriginal + 
               "\n\tAlquiler por piscina: " + 25f*alquilerOriginal + 
               "\n\tAlquiler por pista de deporte: " + 25f*alquilerOriginal + "\n" +

               "\n\tCasas edificadas: " + numEdificios.get(0) + 
               "\n\tHoteles edificados: " + numEdificios.get(1) + 
               "\n\tPiscinas edificadas: " + numEdificios.get(2) + 
               "\n\tPistas de deporte edificadas: " + numEdificios.get(3) + "\n" +

               "\n\tAlquiler actual: " + alquiler;
    }

    // Método para imprimir la información de cada tipo de casilla
    @Override
    public void infoCasilla(){toString();}
}
