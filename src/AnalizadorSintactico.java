import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class AnalizadorSintactico {

    private Lexico lexico;
    private ComponenteLexico componenteLexico;
    private Hashtable<String, String> simbolos;
    private String tipo;
    private int tamaño;

    public AnalizadorSintactico(Lexico lexico) {
        this.lexico = lexico;
        this.componenteLexico = this.lexico.getComponenteLexico();
        this.simbolos = new Hashtable<String, String>();
    }

    public void analisisSintactico() {
        analizaInicioFin();
        declaraciones();
        //new
    }

    public void declaraciones() {
        declaracion();
        while (componenteLexico.getEtiqueta().equals("comma")) {
            compara("comma");
            declaracion();
        }
        if (componenteLexico.getEtiqueta().equals("semicolon")) {
            compara("semicolon");
            declaraciones();
        }
    }

    public void declaracion() {
        System.out.println("Declaracion");
        if (componenteLexico.getValor().equals("int") || componenteLexico.getValor().equals("float") || componenteLexico.getValor().equals("bool")){
            tipo();
        }
        if (componenteLexico.getEtiqueta().equals("while") || componenteLexico.getEtiqueta().equals("if") || componenteLexico.getEtiqueta().equals("do") || componenteLexico.getEtiqueta().equals("print")) {
            System.out.println(componenteLexico.getEtiqueta());
            instruccion();
        }
        String tipoActual = componenteLexico.getEtiqueta(); // Almacena el tipo actual antes de avanzar al siguiente componente léxico
        identificadores();
        if (componenteLexico.getEtiqueta().equals("assignment")) {
            asignacionDeclaracion(); // Llamada a asignacionDeclaracion solo cuando hay una asignación
        }
    }

    public void analizaInicioFin() {
        compara("void");
        compara("main");
        compara("open_brace");
    }

    public void identificadores() {
        if (componenteLexico.getEtiqueta().equals("id")) {
            String nombreIdentificador = componenteLexico.getValor();
            if (!nombreIdentificador.equals("main") && !nombreIdentificador.equals("void")) {
                if (simbolos.containsKey(nombreIdentificador)) {
                    //System.out.println("Error: El identificador '" + nombreIdentificador + "' ya ha sido declarado.");
                } else {
                    System.out.println("Identificador: " + nombreIdentificador);
                    simbolos.put(nombreIdentificador, tipo);
                }
                componenteLexico = lexico.getComponenteLexico();
                masIdentificadores();
                asignacionDeclaracion(); // Llamada a asignacionDeclaracion con el nombre del identificador
            } else {
                componenteLexico = lexico.getComponenteLexico();
                masIdentificadores();
            }
        } else if (componenteLexico.getEtiqueta().equals("end_program")) {
            System.out.println("Fin Programa");
            return;
        } else if (componenteLexico.getEtiqueta().equals(("open_bracket"))) {
            System.out.println("Entre a OpenBracket:");
            vector();
        } else if (componenteLexico.getEtiqueta().equals("close_brace")) {
            System.out.println("Salgo de Encapsulamiento");
        } else {
            System.out.println("No soy un identificador" + componenteLexico.getEtiqueta());
        }
    }

    public void masIdentificadores() {
        while (componenteLexico.getValor().equals("comma")) {
            compara("comma");
            if (componenteLexico.getEtiqueta().equals("id")) {
                String nombreIdentificador = componenteLexico.getValor();
                if (simbolos.containsKey(nombreIdentificador)) {
                    System.out.println("Error: El identificador '" + nombreIdentificador + "' ya ha sido declarado.");
                } else {
                    simbolos.put(nombreIdentificador, tipo);
                }
                componenteLexico = lexico.getComponenteLexico();
                asignacionDeclaracion(); // Llamada a asignacionDeclaracion con el nombre del identificador
            } else {
                System.out.println("Error: Se esperaba un identificador");
            }
        }
    }

    public void compara(String token) {
        if (this.componenteLexico.getEtiqueta().equals(token)) {
            this.componenteLexico = this.lexico.getComponenteLexico();
        } else {
            System.out.println("Error: Expected " + token + ", but found " + this.componenteLexico.getEtiqueta());
            // Skip to the next token
            this.componenteLexico = this.lexico.getComponenteLexico();
            // Reset the tipo variable to avoid subsequent errors
            this.tipo = null;
        }
    }

    public void tipo() {
        if (componenteLexico.getValor().equals("int") || componenteLexico.getValor().equals("float") || componenteLexico.getValor().equals("bool")){
            tipo = componenteLexico.getValor();
            componenteLexico = lexico.getComponenteLexico();
        } else {
            System.out.println(componenteLexico.getValor());
            System.out.println("Error: Se esperaba int o float o bool");
        }
    }

    public String tablaSimbolos() {
        String simbolos = "";

        Set<Map.Entry<String, String>> s = this.simbolos.entrySet();
        if (s.isEmpty()) System.out.println("La tabla de simbolos esta vacia\n");
        for (Map.Entry<String, String> m : s) {
            simbolos = simbolos + "<'" + m.getKey() + "', " +
                    m.getValue() + "> \n";
        }

        return simbolos;
    }

    public void vector() {
        componenteLexico = lexico.getComponenteLexico();
        if (componenteLexico.getEtiqueta().equals("int")||componenteLexico.getEtiqueta().equals("float")||componenteLexico.getEtiqueta().equals("bool")) {
            tamaño = Integer.parseInt(componenteLexico.getValor());
            compara(componenteLexico.getEtiqueta());
        } else {
            System.out.println("Error: Se esperaba un número para el tamaño del vector");
        }
        componenteLexico = lexico.getComponenteLexico();
        // Actualiza la tabla de símbolos para el identificador actual como un vector
        String nombreIdentificador = componenteLexico.getValor();
        if (simbolos.containsKey(nombreIdentificador)) {
            System.out.println("Error: El identificador '" + nombreIdentificador + "' ya ha sido declarado.");
        } else {
            simbolos.put(nombreIdentificador, "array(" + tipo + ", " + tamaño + ")");
        }
        componenteLexico = lexico.getComponenteLexico();
    }

    public void instrucciones() {
        instruccion();
        while (componenteLexico.getEtiqueta().equals("semicolon")) {
            compara("semicolon");
            instruccion();
        }
    }

    public void instruccion() {
        String valorActual = componenteLexico.getEtiqueta();
        System.out.println("Instruccion: " + valorActual);

        switch (valorActual) {
            case "if" -> {
                compara("if");
                System.out.println("if comparado");
                compara("open_parenthesis");
                System.out.println("open_parenthesis comparado");
                expresionLogica();
                System.out.println("expresion logica comparada");
                compara("closed_parenthesis");
                instruccion();
                if (componenteLexico.getEtiqueta().equals("else")) {
                    compara("else");
                    instruccion();
                }
            }
            case "while" -> {
                System.out.println("Entre a while instruccion");
                compara("while");
                System.out.println("while comparado");
                compara("open_parenthesis");
                System.out.println("open_parenthesis comparado");
                expresionLogica();
                compara("closed_parenthesis");
                System.out.println("closed_parenthesis comparado");
                compara("open_brace");
                System.out.println("Empieza instruccion");
                instrucciones();
                compara("close_brace");
            }
            case "do" -> {
                compara("do");
                instruccion();
                compara("while");
                compara("open_parenthesis");
                expresionLogica();
                compara("closed_parenthesis");
                compara("semicolon");
            }
            case "print" -> {
                System.out.println("Entre a print");
                compara("print");
                System.out.println("print comparado");
                compara("open_parenthesis");
                expresion();
                compara("closed_parenthesis");
                compara("semicolon");
            }
            case "open_brace" -> {
                compara("open_brace");
                instrucciones();
                compara("close_brace");
            }
            default -> {
                // Trata como asignación
                variable();
                compara("assignment");
                expresionLogica();
                compara("semicolon");
            }
        }
    }

    public void expresionLogica() {
        System.out.println("Entre a expresion logica " + componenteLexico.getEtiqueta());
        terminoLogico();
        while (componenteLexico.getEtiqueta().equals("or")) {
            compara("or");
            terminoLogico();
        }
    }

    public void expresion() {
        System.out.println("Entre a expresion " + componenteLexico.getEtiqueta());
        termino();
        while (esOperadorAditivo(componenteLexico.getEtiqueta())) {
            System.out.println("Entre a operador aditivo");
            componenteLexico = lexico.getComponenteLexico();
            termino();
        }
    }

    public void termino() {
        System.out.println("Entre a termino" + componenteLexico.getEtiqueta());
        factor();
        while (esOperadorMultiplicativo(componenteLexico.getEtiqueta())) {
            componenteLexico = lexico.getComponenteLexico();
            factor();
        }
    }

    public void factor() {
        System.out.println("Entre a factor" + componenteLexico.getEtiqueta());
        if (componenteLexico.getValor().equals("(")) {
            compara("open_parenthesis");
            expresion();
            compara("closed_parenthesis");
        } else if (componenteLexico.getEtiqueta().equals("id")) {
            variable();
        } else if (componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float") || componenteLexico.getEtiqueta().equals("bool")) {
            componenteLexico = lexico.getComponenteLexico();
            System.out.println("Entre a factor numerico");
        } else {
            System.out.println("Error: Factor mal formado");
        }
    }

    public void variable() {
        System.out.println("Entre a variable" + componenteLexico.getEtiqueta());
        if (componenteLexico.getEtiqueta().equals("id")) {
            String nombreIdentificador = componenteLexico.getValor();
            // Verificar si es un arreglo
            //componenteLexico = lexico.getComponenteLexico();
            if (componenteLexico.getEtiqueta().equals("open_bracket")) {
                compara("open_bracket");
                expresion();
                compara("closed_bracket");
                // Actualizar la tabla de símbolos para el identificador como un arreglo
                if (simbolos.containsKey(nombreIdentificador)) {
                    asignacionDeclaracion();
                } else {
                    simbolos.put(nombreIdentificador, "array(" + tipo + ")");
                }
                componenteLexico = lexico.getComponenteLexico();
            } else {
                System.out.println("Entre a variable simple" + componenteLexico.getEtiqueta());
                componenteLexico = lexico.getComponenteLexico();
                if (simbolos.containsKey(nombreIdentificador)) {
                    System.out.println("Identificador: " + nombreIdentificador);
                } else {
                    System.out.println("Error: El identificador '" + nombreIdentificador + "' no ha sido declarado.");
                }
            }
        } else {
            System.out.println("Error: Se esperaba un identificador");
        }
    }

    public void terminoLogico() {
        System.out.println("Entre a termino logico" + componenteLexico.getEtiqueta());
        factorLogico();
        while (componenteLexico.getEtiqueta().equals("and")) {
            compara("and");
            factorLogico();
        }
    }

    public void factorLogico() {
        System.out.println("Entre a factor logico" + componenteLexico.getEtiqueta());
        if (componenteLexico.getEtiqueta().equals("not")) {
            compara("not");
            factorLogico();
        } else if (componenteLexico.getValor().equals("true") || componenteLexico.getValor().equals("false")) {
            componenteLexico = lexico.getComponenteLexico();
        } else {
            expresionRelacional();
        }
    }

    public void expresionRelacional() {
        System.out.println("Entre a expresion relacional" + componenteLexico.getEtiqueta());
        expresion();
        System.out.println("Expresion"+ componenteLexico.getEtiqueta());
        if (esOperadorRelacional(componenteLexico.getEtiqueta())) {
            componenteLexico = lexico.getComponenteLexico();
            expresion();
        }
    }

    public boolean esOperadorRelacional(String valor) {
        System.out.println("Entre a operador relacional"+ valor);
        return valor.equals("less_than") || valor.equals("less_equals") || valor.equals("greater_than") ||
                valor.equals("greater_equals") || valor.equals("equals") || valor.equals("not_equals");
    }



    public boolean esOperadorAditivo(String valor) {
        //System.out.println("He Adicionado");
        return valor.equals("add") || valor.equals("substract");
    }

    public boolean esOperadorMultiplicativo(String valor) {
        return valor.equals("multiply") || valor.equals("divide") || valor.equals("remainder");
    }

    public void asignacionDeclaracion() {
        System.out.println("AsignacionDeclaracion");
        System.out.println("Etiqueta actual: " + componenteLexico.getEtiqueta());
        if (componenteLexico.getEtiqueta().equals("open_bracket")) {
            compara("open_bracket");
            expresion();
            compara("closed_bracket");
            System.out.println("Salgo de vector");
        }
        System.out.println("Etiqueta actual: " + componenteLexico.getEtiqueta());
        if (componenteLexico.getEtiqueta().equals("assignment")) {
            System.out.println("Entre a igual");
            compara("assignment");
            System.out.println("Entre a expresionLogica"+componenteLexico.getEtiqueta());
            expresionLogica();

        } else {
            compara("semicolon");
        }
    }

}