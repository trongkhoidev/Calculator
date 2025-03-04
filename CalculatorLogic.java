import java.text.DecimalFormat;
import java.util.ArrayList;

public class CalculatorLogic {
    private double result = 0;
    private String firstNumber = "";
    private String operator = "";
    private final ArrayList<String> history = new ArrayList<>();
    private boolean isResult = false;
    private boolean start = true;
    private final DecimalFormat df = new DecimalFormat("#.########"); // Format for 8 decimal places

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
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

    public void setFirstNumber(String firstNumber) {
        this.firstNumber = firstNumber;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void addToHistory(String calculation) {
        history.add(calculation);
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    private String formatResult(double value) {
        String formatted = df.format(value);
        // Remove trailing zeros after decimal point if not needed
        if (formatted.contains(",")) {
            formatted = formatted.replaceAll("0+$", "").replaceAll(",$", "");
        }
        return formatted;
    }

    public String calculateResult(String firstNum, String secondNum, String operator) {
        try {
            // Handle empty first number as 0
            if (firstNum.isEmpty()) {
                firstNum = "0";
            }
            
            // Check for empty second number or missing operator
            if (secondNum.isEmpty() || operator.isEmpty()) {
                return "Syntax ERROR";
            }

            // Replace comma with dot for parsing
            firstNum = firstNum.replace(",", ".");
            secondNum = secondNum.replace(",", ".");

            double x = Double.parseDouble(firstNum);
            double y = Double.parseDouble(secondNum);

            result = switch (operator) {
                case "+" -> x + y;
                case "-" -> x - y;
                case "×" -> x * y;
                case "÷" -> {
                    if (y == 0) {
                        yield Double.NaN;
                    }
                    yield x / y;
                }
                case "%" -> {
                    if (y == 0) {
                        yield Double.NaN;
                    }
                    double remainder = x % y;
                    if (x < 0 || y < 0) {
                        remainder = (remainder + Math.abs(y)) % Math.abs(y);
                    }
                    yield remainder;
                }
                case "^" -> {
                    // Xử lý các trường hợp đặc biệt của lũy thừa theo Casio fx-580VN
                    if (x == 0) {
                        if (y == 0) yield 1; // 0^0 = 1
                        if (y < 0) yield Double.NaN; // 0^(-n) = Math ERROR
                        yield 0; // 0^n = 0
                    }
                    if (x < 0) {
                        // Số âm chỉ có thể mũ số nguyên
                        if (Math.floor(y) != y) {
                            yield Double.NaN; // (-n)^(không nguyên) = Math ERROR
                        }
                        // Nếu số mũ là số lẻ, kết quả sẽ âm
                        if (y % 2 != 0) {
                            yield -Math.pow(-x, y);
                        }
                    }
                    double power = Math.pow(x, y);
                    // Kiểm tra kết quả có quá lớn không
                    if (Double.isInfinite(power) || Math.abs(power) > 1e100) {
                        yield Double.NaN;
                    }
                    yield power;
                }
                case "√" -> {
                    // Xử lý căn bậc 2 theo Casio fx-580VN
                    if (y < 0) {
                        yield Double.NaN; // Căn bậc 2 của số âm = Math ERROR
                    }
                    if (y == 0) {
                        yield 0; // √0 = 0
                    }
                    double sqrt = Math.sqrt(y);
                    if (Double.isInfinite(sqrt) || sqrt > 1e100) {
                        yield Double.NaN;
                    }
                    if (sqrt < 1e-99) {
                        yield 0;
                    }
                    yield sqrt;
                }
                default -> Double.NaN;
            };

            if (Double.isNaN(result)) {
                return "Math ERROR";
            }
            if (Double.isInfinite(result)) {
                return "Math ERROR";
            }

            // Format result and replace dot with comma
            return formatResult(result).replace(".", ",");

        } catch (NumberFormatException e) {
            return "Syntax ERROR";
        }
    }
} 