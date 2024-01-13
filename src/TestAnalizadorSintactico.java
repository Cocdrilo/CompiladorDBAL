import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestAnalizadorSintactico {

    public static void main(String[] args) {

        boolean mostrarComponentesLexicos = true;

        String nombreArchivo = "res/prueba1.txt"; // Nombre del archivo que contiene la expresión
        String expresion = leerExpresionDesdeArchivo(nombreArchivo);

        ComponenteLexico etiquetaLexica;
        Lexico lexico = new Lexico(expresion);

        if (mostrarComponentesLexicos) {
            do {
                etiquetaLexica = lexico.getComponenteLexico();
                System.out.println("<" + etiquetaLexica.toString() + ">");
            } while (!etiquetaLexica.getEtiqueta().equals("end_program"));

            System.out.println("");
        }

        AnalizadorSintactico compilador = new AnalizadorSintactico(new Lexico(expresion));

        System.out.println("Compilacion de sentencia de declaraciones de variables");
        System.out.println(expresion + "\n");



        System.out.println("Lineas: " + lexico.getLineas());

        compilador.analisisSintactico();

        System.out.println("Tabla de simbolos \n\n");
        String simbolos = compilador.tablaSimbolos();
        System.out.println(simbolos);

    }

    private static String leerExpresionDesdeArchivo(String nombreArchivo) {
        String contenido = "";
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido += linea + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenido;
    }
}