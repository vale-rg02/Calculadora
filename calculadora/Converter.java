import java.util.*;

public class Converter {
    public static void main(String[] args) {
        String inputString;
        Scanner keyb = new Scanner(System.in);

        while (true) {
            System.out.println("Introduce una expresión en notación infija (o 'quit' para salir):");
            System.out.print("> ");
            inputString = keyb.nextLine();
            if (inputString.equalsIgnoreCase("quit")) {
                break;
            }

            List<String> tokens = getTokens(inputString);
            List<String> postfix = toPostfix(tokens);

            try {
                double result = evaluatePostfix(postfix);
                System.out.println("Resultado: " + result);
            } catch (Exception e) {
                System.out.println("Error al evaluar la expresión: " + e.getMessage());
            }
        }
        keyb.close();
    }

    // Regresa true si el token es un operador válido
    public static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || 
               token.equals("*") || token.equals("/") || token.equals("^");
    }

    // Convertir una lista de tokens a su representación como cadena de caracteres
    public static String toString(List<String> list) {
        StringBuilder output = new StringBuilder();
        for (String token : list) {
            output.append(token).append(" ");
        }
        return output.toString().trim();
    }

    // Convierte los tokens de una expresión infija a postfija
    public static ArrayList<String> toPostfix(List<String> input) {
        Stack<String> stack = new Stack<>();
        ArrayList<String> output = new ArrayList<>();

        for (String token : input) {
            if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop(); // Remover el paréntesis izquierdo
            } else if (isOperand(token)) {
                output.add(token);
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && isOperator(stack.peek()) &&
                        getPrec(token) <= getPrec(stack.peek())) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    // Evaluar una expresión postfija
    public static double evaluatePostfix(List<String> postfix) throws Exception {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isOperand(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new Exception("Expresión inválida.");
                }
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(token, a, b));
            }
        }

        if (stack.size() != 1) {
            throw new Exception("Expresión inválida.");
        }

        return stack.pop();
    }

    // Aplicar el operador a los operandos
    public static double applyOperator(String operator, double a, double b) throws Exception {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) throw new Exception("División entre cero.");
                return a / b;
            case "^":
                return Math.pow(a, b);
            default:
                throw new Exception("Operador desconocido: " + operator);
        }
    }

    // Verifica si el token es un operando (número)
    public static boolean isOperand(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Obtiene la lista de tokens a partir de una cadena que contiene una expresión en notación infija
    public static List<String> getTokens(String input) {
        StringTokenizer st = new StringTokenizer(input, " ()+-*/^", true);
        ArrayList<String> tokenList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (!token.isEmpty()) {
                tokenList.add(token);
            }
        }

        return tokenList;
    }

    // Obtener la prioridad de cada operador
    public static int getPrec(String token) {
        switch (token) {
            case "^":
                return 3;
            case "*":
            case "/":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return 0;
        }
    }
}
