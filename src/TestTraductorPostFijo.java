public class TestTraductorPostFijo {

    public static void main(String[] args) {
        //Traduccion
        String expresion = "(25 * (2 + 2)) / 2 * 3";

        TraductorExpresionPostFijo expr = new TraductorExpresionPostFijo(new Lexico(expresion));

        System.out.println("La expresion " + expresion
                + " en notacion postfija es " + expr.postfijo()
                + " y su valor es " + expr.valor());

        //Analisis Lexico

        ComponenteLexico etiquetaLexica;
        String programa = "(25*(2+2))/2*3";

        Lexico lexico = new Lexico(programa);


        System.out.println("Test lexico basico \t" + programa + "\n");

        do {

            etiquetaLexica = lexico.getComponenteLexico();

            System.out.println(etiquetaLexica.toString());

        } while (!etiquetaLexica.getEtiqueta().equals("end_program"));

    }

}