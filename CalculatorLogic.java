import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;

/**
 * CalculatorLogic class handles all the mathematical operations and state management
 * for the calculator application. It includes basic arithmetic operations,
 * advanced operations like square root and power, and maintains calculation history.
 */
public class CalculatorLogic {
    // Constants for formatting and limits
    private static final int MAX_DIGITS = 10;
    private static final double MAX_VALUE = 1e100;
    private static final double MIN_VALUE = 1e-100;
    
    // Calculator state variables
    private String displayValue = "0";   // Current value shown on display
    private boolean isNewNumber = true;  // Flag for starting a new number input
    private boolean hasDecimal = false;  // Flag for decimal point presence
    private boolean isStart = true;      // Flag for calculator's initial state
    private boolean isResult = false;    // Flag for result state
    private String firstNumber = "";     // First operand in calculation
    private String operator = "";        // Current operator
    private boolean isNegative = false;  // Flag for negative number input
    private boolean waitForSquareRoot = false; // Flag for square root operation
    private boolean negativeResult = false;    // Flag for negative result
    private String lastResult = "";      // Stores last calculation result
    private String pendingOperation = ""; // Stores pending operation
    private boolean isDegreeMode = true; // Flag for angle mode (true = degrees, false = radians)
    
    // History storage
    private final ArrayList<String> history = new ArrayList<>();
    
    // Number formatters for display
    private final DecimalFormat standardFormat = new DecimalFormat("#,##0.########");
    private final DecimalFormat scientificFormat = new DecimalFormat("0.########E0");

    /**
     * Handles number input and decimal point
     * @param input The number or decimal point to be processed
     */
    public void inputNumber(String input) {
        if (isResult) {
            // If there's a previous result and new input, reset for new calculation
            displayValue = "0";
            firstNumber = "";
            operator = "";
            isResult = false;
            isNegative = false;
            negativeResult = false;
        }

        // Handle decimal point input
        if (input.equals(",")) {
            if (isNewNumber) {
                displayValue = "0,";
                isNewNumber = false;
            } else if (!hasDecimal) {
                displayValue += ",";
            }
            hasDecimal = true;
            return;
        }

        // Handle number input
        if (isNewNumber) {
            displayValue = isNegative ? "-" + input : input;
            isNewNumber = false;
        } else if (displayValue.length() < MAX_DIGITS || hasDecimal) {
            displayValue += input;
        }
        
        isStart = false;
    }

    /**
     * Handles mathematical operations (+, -, ×, ÷, %, ^)
     */
    public String inputOperator(String newOperator) {
        // Handle special negative sign for negative numbers
        if (newOperator.equals("-")) {
            if (isStart || isNewNumber || waitForSquareRoot) {
                isNegative = !isNegative;
                negativeResult = !negativeResult;
                if (isNegative) {
                    displayValue = "-" + displayValue.replace("-", "");
                } else {
                    displayValue = displayValue.replace("-", "");
                }
                return displayValue;
            }
        }

        // If waiting for input to calculate square root, calculate immediately
        if (waitForSquareRoot) {
            String result = calculateSquareRoot(displayValue);
            if (result.equals("Math ERROR")) return result;
            displayValue = result;
            firstNumber = result;
            waitForSquareRoot = false;
        }

        // If there's a previous operation and not a new number, perform the calculation
        if (!operator.isEmpty() && !isNewNumber) {
            String result = calculateResult(firstNumber, displayValue, operator);
            if (result.equals("Math ERROR")) return result;
            displayValue = result;
            firstNumber = result;
        } else if (isResult || !firstNumber.isEmpty()) {
            // Use current number as first operand for new calculation
            firstNumber = displayValue;
        } else {
            // Use current number as first operand
            firstNumber = displayValue;
        }

        // Update state
        operator = newOperator;
        isNewNumber = true;
        hasDecimal = false;
        isResult = false;
        isNegative = false;
        return displayValue;
    }

    /**
     * Calculates result when equals button is pressed
     */
    public String calculateEquals() {
        // If waiting for input to calculate square root, calculate immediately
        if (waitForSquareRoot) {
            String result = calculateSquareRoot(displayValue);
            if (result.equals("Math ERROR")) return result;
            displayValue = result;
            firstNumber = result;
            waitForSquareRoot = false;
            clearExpression();
            return displayValue;
        }

        // Handle direct percentage (no operator)
        if (displayValue.endsWith("%")) {
            String numStr = displayValue.substring(0, displayValue.length() - 1);
            try {
                double num = parseDisplayValue(numStr);
                double result = num / 100.0;
                String formattedResult = formatNumber(result);
                displayValue = formattedResult;
                firstNumber = formattedResult;
                isResult = true;
                return formattedResult;
            } catch (Exception e) {
                return handleError();
            }
        }

        // If no operation or first number not entered, return current number
        if (operator.isEmpty() || isStart) {
            return displayValue;
        }

        try {
            // Get second number for calculation
            String secondNumber = displayValue;

            // Perform calculation
            String result = calculateResult(firstNumber, secondNumber, operator);
            if (result.equals("Math ERROR")) return result;

            // Save calculation to history
            String calculation = firstNumber + " " + operator + " " + secondNumber + " = " + result;
            addToHistory(calculation);

            // Update state
            displayValue = result;
            firstNumber = result;
            operator = "";
            isNewNumber = true;
            hasDecimal = result.contains(",");
            isResult = true;
            isNegative = result.startsWith("-");
            negativeResult = false;
            waitForSquareRoot = false;
            
            clearExpression();
            return displayValue;

        } catch (ArithmeticException e) {
            return handleError();
        }
    }

    /**
     * Handles square root operation
     */
    public String inputSquareRoot() {
        // If there's a lastResult and starting new calculation
        if (!lastResult.isEmpty() && isStart) {
            displayValue = lastResult;
        }
        
        waitForSquareRoot = true;
        if (!displayValue.equals("0")) {
            String result = calculateSquareRoot(displayValue);
            if (!result.equals("Math ERROR")) {
                // Save result for use in further calculations
                displayValue = result;
                if (!operator.isEmpty()) {
                    // If in calculation, update second number
                    isNewNumber = true;
                } else {
                    // If standalone calculation, update firstNumber
                    firstNumber = result;
                }
            }
            return result;
        }
        return displayValue;
    }

    /**
     * Calculates square root of a number
     */
    private String calculateSquareRoot(String number) {
        try {
            double value = parseDisplayValue(number);
            if (value < 0) throw new ArithmeticException("Square root of negative");
            
            double result = Math.sqrt(value);
            if (Double.isInfinite(result) || result > MAX_VALUE) {
                throw new ArithmeticException("Result too large");
            }

            String formattedResult = formatNumber(result);
            if (negativeResult) {
                formattedResult = "-" + formattedResult;
                negativeResult = false;
            }

            // Save calculation to history
            addToHistory("√(" + number + ") = " + formattedResult);
            
            return formattedResult;
        } catch (Exception e) {
            return handleError();
        }
    }

    /**
     * Calculates result of a calculation with two numbers and an operator
     */
    public String calculateResult(String num1, String num2, String op) {
        try {
            double x = parseDisplayValue(num1);
            double y;

            // Handle percentage in second operand
            if (num2.endsWith("%")) {
                String numStr = num2.substring(0, num2.length() - 1);
                y = parseDisplayValue(numStr);
                switch (op) {
                    case "+" -> y = (y / 100.0) * x;  // Add y% of x to x
                    case "-" -> y = (y / 100.0) * x;  // Subtract y% of x from x
                    default -> y = y / 100.0;  // Just convert to decimal for other operations
                }
            } else {
                y = parseDisplayValue(num2);
            }

            double result = performOperation(x, y, op);
            if (Double.isNaN(result) || Double.isInfinite(result) || Math.abs(result) > MAX_VALUE) {
                throw new ArithmeticException("Result out of range");
            }

            return formatNumber(result);
        } catch (Exception e) {
            return handleError();
        }
    }

    /**
     * Formats number according to calculator display rules
     */
    public String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "Error";
        }
        
        // Handle very large or very small numbers
        if (Math.abs(value) > MAX_VALUE || (Math.abs(value) < MIN_VALUE && value != 0)) {
            return scientificFormat.format(value);
        }
        
        // Format regular numbers
        return standardFormat.format(value);
    }

    /**
     * Converts display string to number
     */
    private double parseDisplayValue(String value) {
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Performs calculations
     */
    private double performOperation(double x, double y, String operator) {
        try {
            return switch (operator) {
                case "+" -> x + y;
                case "-" -> x - y;
                case "×" -> x * y;
                case "÷" -> {
                    if (y == 0) throw new ArithmeticException("Division by zero");
                    yield x / y;
                }
                case "%" -> y / 100.0;  // Direct percentage conversion
                case "^" -> {
                    if (x == 0 && y == 0) yield 1;
                    if (x == 0 && y < 0) throw new ArithmeticException("Zero power negative");
                    if (x < 0 && y % 1 == 0) {
                        double absResult = Math.pow(Math.abs(x), y);
                        yield y % 2 == 0 ? absResult : -absResult;
                    }
                    if (x < 0) throw new ArithmeticException("Negative base fractional power");
                    yield Math.pow(x, y);
                }
                case "√" -> {
                    if (y < 0) throw new ArithmeticException("Square root of negative");
                    yield Math.sqrt(y);
                }
                default -> throw new ArithmeticException("Invalid operator");
            };
        } catch (Exception e) {
            throw new ArithmeticException(e.getMessage());
        }
    }

    /**
     * Handles error and resets calculator
     */
    private String handleError() {
        reset();
        return "Math ERROR";
    }

    /**
     * Resets calculator to initial state
     */
    public void reset() {
        displayValue = "0";
        firstNumber = "";
        operator = "";
        pendingOperation = "";
        lastResult = "";
        isNewNumber = true;
        hasDecimal = false;
        isStart = true;
        isResult = false;
        isNegative = false;
        waitForSquareRoot = false;
        negativeResult = false;
    }

    /**
     * Adds a calculation to history
     */
    public void addToHistory(String calculation) {
        history.add(calculation);
    }

    // Getter and setter methods
    public String getDisplayValue() {
        return displayValue;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void clearHistory() {
        history.clear();
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public boolean isResult() {
        return isResult;
    }

    public void setResult(boolean result) {
        isResult = result;
    }

    public String getFirstNumber() {
        return firstNumber;
    }

    public void setFirstNumber(String number) {
        firstNumber = number;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String op) {
        operator = op;
    }

    @Override
    public String toString() {
        return displayValue;
    }

    /**
     * Clears expression display
     */
    public void clearExpression() {
        // This method will be called from CalculatorPanel to handle additional processing
        // Add code here if needed
    }

    /**
     * Calculates factorial of a number
     * @param n The number to calculate factorial for
     * @return The factorial result
     */
    public double factorial(double n) {
        if (n < 0 || n != Math.floor(n)) {
            throw new IllegalArgumentException("Factorial is only defined for non-negative integers");
        }
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * Performs trigonometric functions based on current angle mode
     * @param x The angle value
     * @param func The trigonometric function to apply
     * @return The result of the trigonometric function
     */
    public double trigonometricFunction(double x, String func) {
        double angle = isDegreeMode ? Math.toRadians(x) : x;
        return switch (func) {
            case "sin" -> Math.sin(angle);
            case "cos" -> Math.cos(angle);
            case "tan" -> Math.tan(angle);
            case "cot" -> 1.0 / Math.tan(angle);
            default -> throw new IllegalArgumentException("Invalid trigonometric function");
        };
    }

    /**
     * Evaluates a complex mathematical expression
     * @param expression The expression to evaluate
     * @return The result of the evaluation
     */
    public String evaluateExpression(String expression) {
        try {
            // Remove whitespace and convert to lowercase
            expression = expression.replaceAll("\\s+", "").toLowerCase();
            
            // Handle parentheses
            while (expression.contains("(")) {
                int start = expression.lastIndexOf("(");
                int end = expression.indexOf(")", start);
                if (end == -1) throw new IllegalArgumentException("Mismatched parentheses");
                
                String subExpr = expression.substring(start + 1, end);
                String result = evaluateSimpleExpression(subExpr);
                expression = expression.substring(0, start) + result + expression.substring(end + 1);
            }
            
            return evaluateSimpleExpression(expression);
        } catch (Exception e) {
            return handleError();
        }
    }

    /**
     * Evaluates a simple expression without parentheses
     * @param expression The expression to evaluate
     * @return The result of the evaluation
     */
    private String evaluateSimpleExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<String> operators = new Stack<>();
        
        String[] tokens = expression.split("(?<=[-+*/^])|(?=[-+*/^])");
        
        for (String token : tokens) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                numbers.push(Double.parseDouble(token));
            } else if (token.matches("[+\\-*/^]")) {
                while (!operators.isEmpty() && hasPrecedence(token, operators.peek())) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(token);
            }
        }
        
        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }
        
        return formatNumber(numbers.pop());
    }

    private boolean hasPrecedence(String op1, String op2) {
        if (op2.equals("(") || op2.equals(")")) return false;
        return (op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"));
    }

    private double applyOperator(String operator, double b, double a) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new ArithmeticException("Division by zero");
                yield a / b;
            }
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("Invalid operator");
        };
    }

    /**
     * Toggles between degree and radian mode
     */
    public void toggleAngleMode() {
        isDegreeMode = !isDegreeMode;
    }

    /**
     * Gets the current angle mode
     * @return true if in degree mode, false if in radian mode
     */
    public boolean isDegreeMode() {
        return isDegreeMode;
    }

    /**
     * Sets the new number flag
     * @param isNew Flag indicating if this is a new number
     */
    public void setNewNumber(boolean isNew) {
        this.isNewNumber = isNew;
    }
}
