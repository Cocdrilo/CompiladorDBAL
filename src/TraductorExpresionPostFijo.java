/*
 * Uso de la traduccion dirigida por sintaxis para traducir una expresion aritmetica en notacion
 * infija a postfija (notacion polaca)
 */

import java.util.Stack; // para la pila

public class TraductorExpresionPostFijo {
    private ComponenteLexico componenteLexico;
    private Lexico lexico;
    private Stack<String> pila;
    private String postfijo;

    public TraductorExpresionPostFijo(Lexico lexico) {
        this.lexico = lexico;
        this.componenteLexico = this.lexico.getComponenteLexico();
        this.pila = new Stack<String>();
        this.postfijo = "";
    }

    public String postfijo() {
        expresion();
        // si se hace con print en vez de con pila, no se necesita este while
        while (!pila.isEmpty()) {
            postfijo += pila.remove(0) + " ";
        }
        return postfijo;
    }

    private void expresion() {
        termino();
        masTerminos();
    }

    public void factor() {
        if (this.componenteLexico.getEtiqueta().equals("open_parenthesis")) {
            compara("open_parenthesis");
            expresion();
            compara("closed_parenthesis");
        } else if (this.componenteLexico.getEtiqueta().equals("int")) {
            pila.push(componenteLexico.getValor());
            this.componenteLexico = lexico.getComponenteLexico();
        } else {
            System.out.println("Error. Se esperaba un número o paréntesis.");
        }
    }

    public void termino() {
        factor();
        masFactores();
    }

    private void masTerminos() {
        if (this.componenteLexico.getEtiqueta().equals("add")){
            compara("add");
            termino();
            pila.push("+");
            masTerminos();
        }
        else if(this.componenteLexico.getEtiqueta().equals("substract")){
            compara("substract");
            termino();
            pila.push("-");
            masTerminos();

        }
    }

    private void masFactores() {
        if (this.componenteLexico.getEtiqueta().equals("multiply")){
            compara("multiply");
            factor();
            pila.push("*");
            masFactores();
        }
        else if(this.componenteLexico.getEtiqueta().equals("divide")){
            compara("divide");
            factor();
            pila.push("/");
            masFactores();

        }
    }

    public void compara(String etiquetaLexica) {
        if (this.componenteLexico.getEtiqueta().equals(etiquetaLexica)) {
            this.componenteLexico = lexico.getComponenteLexico();

        } else {
            System.out.println("Error. Se esperaba: " + etiquetaLexica);
        }

    }

    // parq calcular el valor de esta expresion
    public int valor() {
        Stack<Integer> pilaValor = new Stack<>();
        String[] tokens = postfijo.trim().split("\\s+"); // Dividir por espacios

        for (String token : tokens) {
            if (isNumeric(token)) {
                pilaValor.push(Integer.parseInt(token));
            } else {
                int num2 = pilaValor.pop(); // Segundo numero
                int num1 = pilaValor.pop(); // Primer numero
                switch (token) {
                    case "+":
                        pilaValor.push(num1 + num2);
                        break;
                    case "-":
                        pilaValor.push(num1 - num2);
                        break;
                    case "*":
                        pilaValor.push(num1 * num2);
                        break;
                    case "/":
                        if (num2 != 0) {
                            pilaValor.push(num1 / num2);
                        } else {
                            System.out.println("ERROR: División por cero.");
                            return -1; // O cualquier otro valor de error.
                        }
                        break;
                    default:
                        System.out.println("ERROR: Operador desconocido " + token);
                        return -1; // O cualquier otro valor de error.
                }
            }
        }

        return pilaValor.pop();
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}