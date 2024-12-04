package monopoly.interfaz;

import java.util.Scanner;

public class ConsolaNormal implements Consola {
    private final Scanner scanner = new Scanner(System.in); // Mantén un único Scanner abierto

    @Override
    public int leerEnIntervalo(String mensaje, int min, int max) {
        int numero = -1;
        boolean valido = false;

        while (!valido) {
            try {
                System.out.println(mensaje);
                String entrada = System.console().readLine(); // Lee la entrada
                numero = Integer.parseInt(entrada);

                if (numero >= min && numero <= max) {
                    valido = true;
                } else {
                    System.out.println("El número debe estar entre " + min + " y " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingresa un número.");
            }
        }

        return numero;
    }
    
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

    @Override
    public int leer(){
        int numero = scanner.nextInt();
        return numero;
    }
}
