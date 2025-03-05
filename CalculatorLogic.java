import java.text.DecimalFormat;
import java.util.ArrayList;

public class CalculatorLogic {
    // Constants for formatting
    private static final int MAX_DIGITS = 10;
    private static final double MAX_VALUE = 1e100;
    private static final double MIN_VALUE = 1e-100;
    
    // Calculator state
    private String displayValue = "0";   // Giá trị hiển thị
    private boolean isNewNumber = true;  // Đánh dấu bắt đầu nhập số mới
    private boolean hasDecimal = false;  // Đánh dấu đã có dấu thập phân
    private boolean isStart = true;      // Đánh dấu trạng thái bắt đầu
    private boolean isResult = false;    // Đánh dấu vừa tính kết quả
    private String firstNumber = "";     // Số thứ nhất trong phép tính
    private String operator = "";        // Toán tử hiện tại
    private boolean isNegative = false;  // Đánh dấu đang nhập số âm
    private boolean waitForSquareRoot = false; // Đánh dấu đang đợi nhập số để tính căn
    private boolean negativeResult = false;    // Đánh dấu kết quả sẽ là số âm
    private String lastResult = "";      // Lưu kết quả cuối cùng để tính tiếp
    private String pendingOperation = ""; // Phép toán đang chờ thực hiện
    
    // History
    private final ArrayList<String> history = new ArrayList<>();
    
    // Number formatters
    private final DecimalFormat standardFormat = new DecimalFormat("#,##0.########");
    private final DecimalFormat scientificFormat = new DecimalFormat("0.########E0");

    /**
     * Nhập số hoặc dấu thập phân
     */
    public void inputNumber(String input) {
        if (isResult) {
            // Nếu vừa có kết quả và nhập số mới, reset để bắt đầu phép tính mới
            displayValue = "0";
            firstNumber = "";
            operator = "";
            isResult = false;
            isNegative = false;
            negativeResult = false;
        }

        // Xử lý dấu thập phân
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

        // Xử lý nhập số
        if (isNewNumber) {
            displayValue = isNegative ? "-" + input : input;
            isNewNumber = false;
        } else if (displayValue.length() < MAX_DIGITS || hasDecimal) {
            displayValue += input;
        }
        
        isStart = false;
    }

    /**
     * Xử lý các phép toán (+, -, ×, ÷, %, ^)
     */
    public String inputOperator(String newOperator) {
        // Xử lý dấu trừ đặc biệt cho số âm
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

        // Nếu đang đợi nhập số để tính căn, tính ngay
        if (waitForSquareRoot) {
            String result = calculateSquareRoot(displayValue);
            if (result.equals("Math ERROR")) return result;
            displayValue = result;
            firstNumber = result;
            waitForSquareRoot = false;
        }

        // Nếu đã có phép toán trước đó và không phải số mới, thực hiện phép tính
        if (!operator.isEmpty() && !isNewNumber) {
            String result = calculateResult(firstNumber, displayValue, operator);
            if (result.equals("Math ERROR")) return result;
            displayValue = result;
            firstNumber = result;
        } else if (isResult || !firstNumber.isEmpty()) {
            // Sử dụng số hiện tại làm số đầu cho phép tính mới
            firstNumber = displayValue;
        } else {
            // Lưu số hiện tại làm số đầu
            firstNumber = displayValue;
        }

        // Cập nhật trạng thái
        operator = newOperator;
        isNewNumber = true;
        hasDecimal = false;
        isResult = false;
        isNegative = false;
        return displayValue;
    }

    /**
     * Tính toán kết quả khi nhấn dấu =
     */
    public String calculateEquals() {
        // Nếu đang đợi nhập số để tính căn, tính ngay
        if (waitForSquareRoot) {
            String result = calculateSquareRoot(displayValue);
            if (result.equals("Math ERROR")) return result;
            displayValue = result;
            firstNumber = result;  // Lưu kết quả làm số đầu cho phép tính tiếp
            waitForSquareRoot = false;
            clearExpression();  // Xóa biểu thức hiển thị
            return displayValue;
        }

        // Nếu không có phép toán hoặc chưa nhập số thứ hai, trả về số hiện tại
        if (operator.isEmpty() || isStart) {
            return displayValue;
        }

        try {
            // Lấy số thứ hai cho phép tính
            String secondNumber = displayValue;

            // Thực hiện phép tính
            String result = calculateResult(firstNumber, secondNumber, operator);
            if (result.equals("Math ERROR")) return result;

            // Lưu lại phép tính vào history
            String calculation = firstNumber + " " + operator + " " + secondNumber + " = " + result;
            addToHistory(calculation);

            // Cập nhật trạng thái
            displayValue = result;
            firstNumber = result;  // Lưu kết quả làm số đầu cho phép tính tiếp theo
            operator = "";         // Reset operator
            isNewNumber = true;
            hasDecimal = result.contains(",");
            isResult = true;
            isNegative = result.startsWith("-");
            negativeResult = false;
            waitForSquareRoot = false;  // Reset trạng thái căn bậc hai
            
            // Xóa biểu thức hiển thị
            clearExpression();

            return displayValue;

        } catch (ArithmeticException e) {
            return handleError();
        }
    }

    /**
     * Xử lý phép căn bậc hai
     */
    public String inputSquareRoot() {
        // Nếu có lastResult và đang bắt đầu phép tính mới
        if (!lastResult.isEmpty() && isStart) {
            displayValue = lastResult;
        }
        
        waitForSquareRoot = true;
        if (!displayValue.equals("0")) {
            String result = calculateSquareRoot(displayValue);
            if (!result.equals("Math ERROR")) {
                // Lưu kết quả để sử dụng trong các phép tính
                displayValue = result;
                if (!operator.isEmpty()) {
                    // Nếu đang trong phép tính, cập nhật số thứ hai
                    isNewNumber = true;
                } else {
                    // Nếu là phép tính độc lập, cập nhật firstNumber
                    firstNumber = result;
                }
            }
            return result;
        }
        return displayValue;
    }

    /**
     * Tính căn bậc hai của một số
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

            // Lưu lại phép tính vào history
            addToHistory("√(" + number + ") = " + formattedResult);
            
            return formattedResult;
        } catch (Exception e) {
            return handleError();
        }
    }

    /**
     * Tính kết quả của một phép tính với hai số và một toán tử
     */
    public String calculateResult(String num1, String num2, String op) {
        try {
            double x = parseDisplayValue(num1);
            double y = parseDisplayValue(num2);
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
     * Format số theo quy tắc hiển thị của máy tính
     */
    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "Math ERROR";
        }
        
        if (value == 0) return "0";
        
        double absValue = Math.abs(value);
        // Sử dụng định dạng khoa học cho số rất lớn hoặc rất nhỏ
        if (absValue >= 1e9 || (absValue < 1e-7 && absValue > 0)) {
            try {
                String formatted = scientificFormat.format(value);
                // Kiểm tra xem số có quá lớn không
                if (formatted.contains("E") && Integer.parseInt(formatted.substring(formatted.indexOf("E") + 1)) > 99) {
                    return "Math ERROR";
                }
                return formatted.replace("E", "e").replace(".", ",");
            } catch (Exception e) {
                return "Math ERROR";
            }
        }
        
        String result = standardFormat.format(value);
        if (result.contains(",")) {
            result = result.replaceAll("0+$", "").replaceAll(",$", "");
        }
        
        return result;
    }

    /**
     * Chuyển đổi chuỗi hiển thị sang số
     */
    private double parseDisplayValue(String value) {
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Thực hiện các phép tính
     */
    private double performOperation(double x, double y, String operator) {
        double result;
        try {
            result = switch (operator) {
                case "+" -> x + y;
                case "-" -> x - y;
                case "×" -> x * y;
                case "÷" -> {
                    if (y == 0) throw new ArithmeticException("Division by zero");
                    yield x / y;
                }
                case "%" -> {
                    if (y == 0) throw new ArithmeticException("Modulo by zero");
                    double quotient = x / y;
                    double remainder = x >= 0 ? 
                        x - (Math.floor(quotient) * y) : 
                        x - (Math.ceil(quotient) * y);
                    if (remainder < 0) remainder += Math.abs(y);
                    yield remainder;
                }
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

            // Kiểm tra kết quả sau khi thực hiện phép tính
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new ArithmeticException("Result out of range");
            }

            return result;
        } catch (Exception e) {
            throw new ArithmeticException(e.getMessage());
        }
    }

    /**
     * Xử lý lỗi và reset máy tính
     */
    private String handleError() {
        reset();
        return "Math ERROR";
    }

    /**
     * Reset máy tính về trạng thái ban đầu
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
     * Thêm một phép tính vào lịch sử
     */
    public void addToHistory(String calculation) {
        history.add(calculation);
    }

    // Getter và setter methods
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
     * Xóa biểu thức hiển thị
     */
    public void clearExpression() {
        // Phương thức này sẽ được gọi từ CalculatorPanel để xóa expressionDisplay
        // Thêm code ở đây nếu cần xử lý thêm
    }
}
