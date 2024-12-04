package monopoly.cartas;

public abstract class Carta {
    // Atributos
    private String nombre;      // Nombre de la carta
    private String accion;      // Acción que se debe realizar al invocar la carta

    // Constructores
    public Carta() {}

    public Carta(String nombre, String accion) {
        this.nombre = nombre;
        this.accion = accion;
        //inicializarCartas();
    }

    // Getters y Setters
    public String getNombre() {return this.nombre;}
    public String getAccion() {return this.accion;}

    // Métodos de Carta
    // Método abstracto que inicializa las barajas añadiendo las cartas correspondientes de Suerte y Comunidad
    public abstract void inicializarCartas();
}
