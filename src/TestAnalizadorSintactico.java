public class TestAnalizadorSintactico {

    public static void main (String[] args) {

        boolean mostrarComponentesLexicos = true; //poner a false y no se quieren mostrar los tokens <id, a> ...

        String expresion = "void main {int alex, b05, cec9, d; float x; int [105] v98;int alex;}";

        ComponenteLexico etiquetaLexica;
        Lexico lexico = new Lexico(expresion);

        if(mostrarComponentesLexicos) {

            do {
                etiquetaLexica = lexico.getComponenteLexico();
                System.out.println("<" + etiquetaLexica.toString() + ">"); //System.out.println(etiquetaLexica.toString());

            }while(!etiquetaLexica.getEtiqueta().equals("end_program"));

            System.out.println("");
        }

        AnalizadorSintactico compilador = new AnalizadorSintactico (new Lexico(expresion));

        System.out.println("Compilacion de sentencia de declaraciones de variables");
        System.out.println(expresion + "\n");

        compilador.analisisSintactico();

        System.out.println("Tabla de simbolos \n\n" );
        String simbolos = compilador.tablaSimbolos();
        System.out.println(simbolos);

    }

}