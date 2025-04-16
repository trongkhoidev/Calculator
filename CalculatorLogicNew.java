import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

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
     * Formats a number for display
     * @param value The number to format
     * @return Formatted number string
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
     * Sets the new number flag
     * @param isNew Flag indicating if this is a new number
     */
    public void setNewNumber(boolean isNew) {
        this.isNewNumber = isNew;
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

    // ... rest of existing methods ...
} 