import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class AnalizadorSintactico {

    private Lexico lexico;
    private ComponenteLexico componenteLexico;
    private Hashtable<String, String> simbolos;
    private ArrayList<String> errores;
    private String valorAsignacion;
    private String tipo;
    private int tamaño;

    public AnalizadorSintactico(Lexico lexico) {
        this.lexico = lexico;
        this.componenteLexico = this.lexico.getComponenteLexico();
        this.simbolos = new Hashtable<String, String>();
        this.errores = new ArrayList<String>();
    }

    public void analisisSintactico() {
        analizaInicioFin();
        declaraciones();
        instrucciones();
        procesarCierrePrograma();
        if(!errores.isEmpty()){
            System.out.println("Errores encontrados en el programa");
            printErrores();
        }
    }

    public void analizaInicioFin() {
        compara("void");
        compara("main");
        compara("open_bracket");
    }

    private void procesarCierrePrograma() {
        if (componenteLexico.getEtiqueta().equals("closed_bracket")) {
            compara("closed_bracket");

            if (componenteLexico.getEtiqueta().equals("end_program")) {
                System.out.println("Programa compilado correctamente\n");
            }
        }
    }
    public void printErrores(){
        for (String error : errores) {
            System.out.println(error);
        }
    }


    public void declaraciones() {
        declaracion();
        if(componenteLexico.getEtiqueta().equals("semicolon")) {
            compara("semicolon");
            declaraciones();
        }

    }


    public void declaracion() {
        tipoVector();
    }


    public void listaIdentificadores() {
        String nombreIdentificador = componenteLexico.getValor();

        if (simbolos.containsKey(nombreIdentificador)) {
            errores.add("Error in line  " + lexico.getLineas() + " : Variable '" + nombreIdentificador + "' already declared ");
        } else {
            simbolos.put(nombreIdentificador, tipo);
        }
        compara("id");
        asignacionDeclaracion();
        masIdentificadores();
    }

    public void masIdentificadores() {
        if (componenteLexico.getEtiqueta().equals("comma")) {
            compara("comma");
            String nombreIdentificador = componenteLexico.getValor();
            simbolos.put(nombreIdentificador, tipo);
            compara("id");
            asignacionDeclaracion();
            masIdentificadores();
        }
    }

    public void compara(String token) {
        if (this.componenteLexico.getEtiqueta().equals(token)) {
            this.componenteLexico = this.lexico.getComponenteLexico();
        } else {
            System.out.println("Error: Expected " + token + ", but found " + componenteLexico.getEtiqueta());
            // Skip to the next token
            this.componenteLexico = this.lexico.getComponenteLexico();
            // Reset the tipo variable to avoid subsequent errors
            this.tipo = null;
        }
    }

    public void tipoPrimitivo() {
        listaIdentificadores();
    }

    public void tipoVector() {
        if (componenteLexico.getValor().equals("int") || componenteLexico.getValor().equals("float") || componenteLexico.getValor().equals("bool")) {
            tipo = componenteLexico.getValor();
            componenteLexico = lexico.getComponenteLexico();
            if (componenteLexico.getEtiqueta().equals("open_square_bracket")) {
                compara("open_square_bracket");
                System.out.println("Estamos en el open bracket del vector");
                tamaño = Integer.parseInt(componenteLexico.getValor());
                componenteLexico = lexico.getComponenteLexico();
                compara("closed_square_bracket");
                simbolos.put(componenteLexico.getValor(), "array(" + tipo + ", " + tamaño + ")");
                compara("id");
            }else{
                System.out.println("entro a tipo primitivo" + " " +lexico.getLineas());
                tipoPrimitivo();
            }
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

    public void instrucciones() {
        instruccion();
        while (componenteLexico.getEtiqueta().equals("semicolon")) {
            compara("semicolon");
            instrucciones();
        }
    }

    public void instruccion() {
        System.out.println("Estamos en instruccion " +componenteLexico.getEtiqueta() + componenteLexico.getValor());
        String valorActual = componenteLexico.getEtiqueta();
        switch (valorActual) {
            case "id" -> {
                variable();
                System.out.println("Salgo de Variable " + componenteLexico.getEtiqueta() + componenteLexico.getValor());
                compara("assignment");
                expresionLogica();
            }
            case "int", "float", "bool" -> {
                declaracion();
            }
            case "if" -> {
                compara("if");
                compara("open_parenthesis");
                expresionLogica();
                compara("closed_parenthesis");
                instruccion();
            }
            case "else" -> {
                compara("else");
                instruccion();
            }

            case "open_bracket" -> {
                compara("open_bracket");
                instrucciones();
                if(componenteLexico.getEtiqueta().equals("closed_bracket")){
                    compara("closed_bracket");
                    instrucciones();
                }
            }
            case "while" -> {
                compara("while");
                compara("open_parenthesis");
                expresionLogica();
                compara("closed_parenthesis");
                instruccion();
            }
            case "do" -> {
                compara("do");
                instruccion();
            }
            case "print" -> {
                compara("print");
                compara("open_parenthesis");
                variable();
                compara("closed_parenthesis");
            }
        }
    }

    public void expresionLogica() {
        terminoLogico();
        while (componenteLexico.getEtiqueta().equals("or")) {
            compara("or");
            terminoLogico();
        }
    }

    public void expresion() {
        termino();
        if(esOperadorAditivo(componenteLexico.getEtiqueta())){
            componenteLexico = lexico.getComponenteLexico();
            termino();
            if(esOperadorAditivo(componenteLexico.getEtiqueta())){
                componenteLexico = lexico.getComponenteLexico();
                expresion();
            }
        }
    }

    public void termino() {
        factor();
        if(esOperadorMultiplicativo(componenteLexico.getEtiqueta())){
            componenteLexico = lexico.getComponenteLexico();
            factor();
            if(esOperadorAditivo(componenteLexico.getEtiqueta())) {
                expresion();
            } else if(esOperadorMultiplicativo(componenteLexico.getEtiqueta())){
                expresion();
            }
        }
    }

    public void factor() {
        if(componenteLexico.getEtiqueta().equals("id") || componenteLexico.getEtiqueta().equals("int") || componenteLexico.getEtiqueta().equals("float") || componenteLexico.getEtiqueta().equals("bool")){
            variable();
        } else if(componenteLexico.getEtiqueta().equals("open_parenthesis")){
            compara("open_parenthesis");
            expresion();
            compara("closed_parenthesis");

        }
    }

    public void variable() {

        if (componenteLexico.getEtiqueta().equals("id")) {
            String nombreVariable = componenteLexico.getValor();

            if (!simbolos.containsKey(nombreVariable)) {
                errores.add("Error in line " + lexico.getLineas() + ": Variable '" + nombreVariable + "' not declared");
            }
            compara("id");
            if (componenteLexico.getEtiqueta().equals("open_square_bracket")) {
                compara("open_square_bracket");
                expresion();
                compara("closed_square_bracket");
            }
        } else {
            componenteLexico = lexico.getComponenteLexico();
        }
    }

    public void terminoLogico() {
        factorLogico();
        if (componenteLexico.getEtiqueta().equals("and")) {
            compara("and");
            factorLogico();
        }
    }

    public void factorLogico() {
        expresionRelacional();
    }

    public void expresionRelacional() {
        expresion();
        if (esOperadorRelacional(componenteLexico.getEtiqueta())) {
            componenteLexico = lexico.getComponenteLexico();
            expresion();
        }
    }

    public boolean esOperadorRelacional(String valor) {
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
        if (componenteLexico.getEtiqueta().equals("assignment")) {
            compara("assignment");
            expresionLogica();
        }
    }

}