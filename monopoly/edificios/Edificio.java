package monopoly.edificios;

import partida.*;
import java.util.ArrayList;

import monopoly.casillas.Grupo;
import monopoly.casillas.propiedades.Solar;

public abstract class Edificio {
    // Atributos
    private String ID;              // Identificador único que diferencia los edificios contruídos
    private Jugador propietario;    // Jugador al que le pertenece el edificio
    private Solar ubicacion;        // Solar en el que se construye el edificio
    private Grupo grupo;            // Grupo al que pertenece el solar donde se ha construído el edificio
    private int cantidad = 1;       // Número de edificios de un tipo y un grupo (se usa para la generación del ID)
    private float coste;            // Precio de construcción de cada edificio
    
    // Constructor
    public Edificio(Jugador propietario, Solar ubicacion, ArrayList<Edificio> edificiosConstruidos) {
        this.propietario = propietario;
        this.ubicacion = ubicacion;
        this.grupo = ubicacion.getGrupo();
        generarId(edificiosConstruidos);
    }

    // Getters y Setters
    public String getID() {return ID;}
    public Grupo getGrupo() {return grupo;}
    public int getCantidad() {return cantidad;}
    public Solar getUbicacion() {return ubicacion;}
    public void setCoste(float coste) {this.coste = coste;}

    // Métodos de Edificio
    // Método para generar el identificar único de cada edificio
    private void generarId(ArrayList<Edificio> edificiosConstruidos) {
        int num = 0;
        StringBuilder sb = new StringBuilder();
        String tipo = this.getClass().getSimpleName().toLowerCase();
    
        if (!edificiosConstruidos.isEmpty()) {
            for (Edificio ed : edificiosConstruidos) {
                if (ed.getGrupo().equals(this.grupo) && ed.getClass().equals(this.getClass())) {
                    this.cantidad = ed.getCantidad() + 1;
                    num = 1;
                }
            }
        } if (num == 0) {this.cantidad = 1;}
    
        sb.append(tipo).append("-").append(grupo.colorGrupo()).append(cantidad);
        this.ID = sb.toString();
    }

    //Para imprimir la información del avatar, modificamos el método toString().
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(this.ID).append("\n\n");
        sb.append("\tPropietario: ").append(propietario.getNombre()).append("\n");
        sb.append("\tCasilla: ").append(ubicacion.getNombre()).append("\n");
        sb.append("\tGrupo: ").append(grupo.colorGrupo()).append("\n");
        sb.append("\tCoste: ").append(coste);
        return sb.toString();
    }
}
