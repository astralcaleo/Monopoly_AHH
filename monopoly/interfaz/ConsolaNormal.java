package monopoly.interfaz;

import java.util.Scanner;

public class ConsolaNormal implements Consola {
    private final Scanner scanner = new Scanner(System.in); // Mantén un único Scanner abierto

    @Override
    public void imprimir(String mensaje) {
        System.out.println(mensaje);
    }

    @Override
    public String leer(String descripcion) {
        System.out.println(descripcion);
        String mensaje = scanner.nextLine();
        return mensaje;
    }

    public int leer(){
        int numero = scanner.nextInt();
        return numero;
    }
}
