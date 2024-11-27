package monopoly.cartas;

import java.util.ArrayList;

public class Carta {

    //Atributos:
    private static ArrayList<Carta> cartasSuerte; //Colección de cartas obtenibles en las casillas tipo Suerte
    private static ArrayList<Carta> cartasCaja; //Colección de cartas obtenibles en las casillas tipo Comunidad

    private String nombre; //Nombre de la carta
    private String tipo; //Tipo de carta (suerte o caja)
    private String accion; //Acción que se debe realizar al invocar la carta

    // Constructor que inicializa cartas si no han sido inicializadas
    public Carta() {
        if (cartasSuerte == null || cartasCaja == null) {
            inicializarCartas();
        }
    }

    //Constructor principal que requiere los parámetros nombre, tipo y acción e inicializa las cartas
    public Carta(String nombre, String tipo, String accion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.accion = accion;
        if (cartasSuerte == null || cartasCaja == null) {
            inicializarCartas();
        }
    }

    /*Método que inicializa las barajas añadiendo las cartas correspondientes de Suerte y Comunidad*/
    private static void inicializarCartas() {
        cartasSuerte = new ArrayList<>();
        cartasCaja = new ArrayList<>();

        cartasSuerte.add(new Carta("suerte1", "suerte", "Ve al Transportes1 y coge un avión. Si pasas por la casilla de Salida, cobra la cantidad habitual."));
        cartasSuerte.add(new Carta("suerte2", "suerte", "Decides hacer un viaje de placer. Avanza hasta Solar15 directamente, sin pasar por la casilla de Salida y sin cobrar la cantidad habitual."));
        cartasSuerte.add(new Carta("suerte3", "suerte", "Vendes tu billete de avión para Solar17 en una subasta por Internet. Cobra 500000€"));
        cartasSuerte.add(new Carta("suerte4", "suerte", "Ve a Solar3. Si pasas por la casilla de Salida, cobra la cantidad habitual."));
        cartasSuerte.add(new Carta("suerte5", "suerte", "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar la cantidad habitual."));
        cartasSuerte.add(new Carta("suerte6", "suerte", "¡Has ganado el bote de la lotería! Recibe 1000000€."));

        cartasCaja.add(new Carta("caja1", "caja", "Paga 500000€ por un fin de semana en un balneario de 5 estrellas."));
        cartasCaja.add(new Carta("caja2", "caja", "Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar la cantidad habitual."));
        cartasCaja.add(new Carta("caja3", "caja", "Colócate en la casilla de Salida. Cobra la cantidad habitual."));
        cartasCaja.add(new Carta("caja4", "caja", "Tu compañía de Internet obtiene beneficios. Recibe 2000000€."));
        cartasCaja.add(new Carta("caja5", "caja", "Paga 1000000€ por invitar a todos tus amigos a un viaje a Solar14."));
        cartasCaja.add(new Carta("caja6", "caja", "Alquilas a tus compañeros una villa en Solar7 durante una semana. Paga 200000€ a cada jugador."));
    }

    /*Método que permite barajar las cartas de Suerte */
    public Carta barajarSuerte(int numero) {
        //Collections.shuffle(cartasSuerte);
        //return cartasSuerte.get(numero - 1);
        return cartasSuerte.get(numero - 1);
    }

    /*Método que permite barajar las cartas de Caja de Comunidad */
    public Carta barajarCaja(int numero) {
        //Collections.shuffle(cartasCaja);
        //return cartasCaja.get(numero - 1);
        return cartasCaja.get(numero -1);
    }

    // GETTERS
    /*Método que devuelve el nombre de la carta */
    public String getNombre() {
        return this.nombre;
    }

    /*Método que devuelve el tipo de la carta (suerte o caja) */
    public String getTipo() {
        return this.tipo;
    }

    /*Método que devuelve la acción asignada a la carta */
    public String getAccion() {
        return this.accion;
    }
}
