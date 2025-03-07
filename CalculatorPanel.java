import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class CalculatorPanel extends JPanel {
    private final JTextField display;
    private final JTextField expressionDisplay;
    private final CalculatorLogic calculator;

    public CalculatorPanel(CalculatorLogic calculator) {
        this.calculator = calculator;
        this.display = new JTextField("0");
        this.expressionDisplay = new JTextField("");
        
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.BLACK);

        // Panel chứa màn hình hiển thị
        JPanel displayPanel = createDisplayPanel();
        add(displayPanel, BorderLayout.NORTH);

        // Panel chứa các nút
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout(0, 0));
        displayPanel.setBackground(Color.BLACK);

        expressionDisplay.setHorizontalAlignment(JTextField.RIGHT);
        expressionDisplay.setEditable(false);
        expressionDisplay.setFont(new Font("Digital-7", Font.PLAIN, 24));
        expressionDisplay.setPreferredSize(new Dimension(400, 25));
        expressionDisplay.setBackground(Color.BLACK);
        expressionDisplay.setForeground(Color.GRAY);
        expressionDisplay.setBorder(null);
        displayPanel.add(expressionDisplay, BorderLayout.NORTH);

        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setFont(new Font("Digital-7", Font.BOLD, 60));
        display.setPreferredSize(new Dimension(400, 80));
        display.setBackground(Color.BLACK);
        display.setForeground(Color.WHITE);
        display.setBorder(null);
        displayPanel.add(display, BorderLayout.CENTER);

        return displayPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 1, 1));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        String[] buttonLabels = {
            "C", "←", "^/√", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ",", "%", "="
        };

        for (String label : buttonLabels) {
            RoundedButton button = createButton(label);
            if (label.equals("^/√")) {
                addPowerSqrtPopupMenu(button);
            }
            buttonPanel.add(button);
        }

        return buttonPanel;
    }

    private void addPowerSqrtPopupMenu(RoundedButton button) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(new Color(40, 40, 40));
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));

        JMenuItem sqrtItem = new JMenuItem("Căn bậc 2 (√)");
        sqrtItem.setFont(new Font("Arial", Font.BOLD, 14));
        sqrtItem.setForeground(Color.BLACK);
        sqrtItem.setBackground(new Color(40, 40, 40));
        sqrtItem.addActionListener(e -> new ButtonClickListener().handleOperator("√"));
        sqrtItem.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                sqrtItem.setBackground(new Color(60, 60, 60));
            }
            public void mouseExited(MouseEvent e) {
                sqrtItem.setBackground(new Color(40, 40, 40));
            }
        });

        JMenuItem powerItem = new JMenuItem("Lũy thừa (^)");
        powerItem.setFont(new Font("Arial", Font.BOLD, 14));
        powerItem.setForeground(Color.BLACK);
        powerItem.setBackground(new Color(40, 40, 40));
        powerItem.addActionListener(e -> new ButtonClickListener().handleOperator("^"));
        powerItem.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                powerItem.setBackground(new Color(60, 60, 60));
            }
            public void mouseExited(MouseEvent e) {
                powerItem.setBackground(new Color(40, 40, 40));
            }
        });

        popupMenu.add(sqrtItem);
        popupMenu.add(powerItem);

        button.addActionListener(e -> {
            popupMenu.show(button, 0, button.getHeight());
        });
    }

    private RoundedButton createButton(String label) {
        RoundedButton button = new RoundedButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 28));
        button.setPreferredSize(new Dimension(90, 90));

        if (label.matches("[0-9]")) {
            button.setBackground(new Color(51, 51, 51));
            button.setForeground(Color.WHITE);
        } else if (label.matches("[+\\-×÷]") || label.equals("=")) {
            button.setBackground(new Color(255, 149, 0));
            button.setForeground(Color.WHITE);
        } else if (label.matches("[,]")) {
            button.setBackground(new Color(51, 51, 51));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(new Color(165, 165, 165));
            button.setForeground(Color.BLACK);
        }

        addButtonHoverEffect(button);
        if (!label.equals("^/√")) {
            button.addActionListener(new ButtonClickListener());
        }

        return button;
    }

    private void addButtonHoverEffect(RoundedButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground().equals(new Color(51, 51, 51))) {
                    button.setBackground(new Color(80, 80, 80));
                } else if (button.getBackground().equals(new Color(255, 149, 0))) {
                    button.setBackground(new Color(255, 170, 50));
                } else {
                    button.setBackground(new Color(190, 190, 190));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getBackground().equals(new Color(80, 80, 80))) {
                    button.setBackground(new Color(51, 51, 51));
                } else if (button.getBackground().equals(new Color(255, 170, 50))) {
                    button.setBackground(new Color(255, 149, 0));
                } else {
                    button.setBackground(new Color(165, 165, 165));
                }
            }
        });
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();

            if (command.equals("←")) {
                handleBackspace();
            } else if (command.matches("[0-9]")) {
                handleNumber(command);
            } else if (command.equals("C")) {
                handleClear();
            } else if (command.equals("=")) {
                handleEquals();
            } else if (command.equals("^/√")) {
                return;
            } else if (command.equals(",")) {
                handleDecimalPoint();
            } else {
                handleOperator(command);
            }
        }

        private void handleBackspace() {
            String currentText = display.getText();
            if (currentText.startsWith("Error")) {
                handleClear();
                return;
            }
            if (currentText.length() > 0) {
                String newText = currentText.substring(0, currentText.length() - 1);
                display.setText(newText.isEmpty() ? "0" : newText);
            }
        }

        private void handleNumber(String number) {
            if (calculator.isResult()) {
                display.setText(number);
                expressionDisplay.setText("");
                calculator.setFirstNumber("");
                calculator.setOperator("");
                calculator.setResult(false);
            } else if (calculator.isStart()) {
                display.setText(number);
                calculator.setStart(false);
            } else {
                String currentText = display.getText();
                if (currentText.equals("0")) {
                    display.setText(number);
                } else {
                    display.setText(currentText + number);
                }
            }
        }

        private void handleClear() {
            display.setText("0");
            expressionDisplay.setText("");
            calculator.setFirstNumber("");
            calculator.setOperator("");
            calculator.setStart(true);
            calculator.setResult(false);
        }

        private void handleEquals() {
            String secondNumber = display.getText();
            String expression;
            String result;

            // Nếu chưa có phép tính hoặc chưa nhập gì, không làm gì cả
            if (calculator.isStart() && calculator.getOperator().isEmpty()) {
                return;
            }

            // Nếu đã có kết quả và không có phép tính mới, không làm gì cả
            if (calculator.isResult() && calculator.getOperator().isEmpty()) {
                return;
            }

            // Cập nhật biểu thức hiện tại với số cuối cùng
            String currentExpression = expressionDisplay.getText();
            if (!currentExpression.isEmpty()) {
                if (currentExpression.equals("√")) {
                    // Nếu chỉ có phép tính căn
                    expression = "√" + secondNumber;
                    expressionDisplay.setText(expression);
                } else if (currentExpression.endsWith("√")) {
                    // Nếu có phép tính với căn
                    expression = currentExpression + secondNumber;
                    expressionDisplay.setText(expression);
                } else if (currentExpression.endsWith("^")) {
                    expression = currentExpression + " " + secondNumber;
                    expressionDisplay.setText(expression);
                } else {
                    expression = currentExpression + " " + secondNumber;
                    expressionDisplay.setText(expression);
                }
            } else {
                expression = secondNumber;
                expressionDisplay.setText(expression);
            }

            // Tính toán kết quả
            result = evaluator.evaluate(expression);
            display.setText(result);
            
            calculator.addToHistory(expression + " = " + result);
            calculator.setResult(true);
            calculator.setFirstNumber(result);
            calculator.setOperator("");
        }

        private void handleDecimalPoint() {
            String currentText = display.getText();
            if (calculator.isResult()) {
                display.setText("0,");
                expressionDisplay.setText("");
                calculator.setFirstNumber("");
                calculator.setOperator("");
                calculator.setResult(false);
                calculator.setStart(false);
            } else if (calculator.isStart()) {
                display.setText("0,");
                calculator.setStart(false);
            } else if (!currentText.contains(",")) {
                display.setText(currentText + ",");
            }
        }

        private void handleOperator(String operator) {
            String currentExpression = expressionDisplay.getText();
            String currentDisplay = display.getText();

            // Xử lý đặc biệt cho dấu trừ
            if (operator.equals("-")) {
                // Trường hợp 1: Đang ở trạng thái bắt đầu hoặc màn hình hiển thị "0"
                if (calculator.isStart() || currentDisplay.equals("0")) {
                    // Kiểm tra nếu biểu thức không rỗng và kết thúc bằng phép toán
                    if (!currentExpression.isEmpty() && currentExpression.matches(".*[×÷+\\-]\\s*$")) {
                        // Nếu đã có dấu trừ, không cho phép thêm nữa
                        if (currentExpression.endsWith(" -") || currentDisplay.startsWith("-")) {
                            return;
                        }
                    }
                    // Cho phép thêm dấu trừ nếu chưa có
                    if (!currentDisplay.startsWith("-")) {
                        display.setText("-");
                        calculator.setStart(false);
                    }
                    return;
                }
                // Trường hợp 2: Đang nhập số
                else if (!calculator.isStart()) {
                    // Nếu đã có dấu trừ, không cho phép thêm nữa
                    if (currentDisplay.startsWith("-") || (currentExpression.endsWith(" -"))) {
                        return;
                    }
                }
            }

            // Xử lý các phép toán khác và cập nhật biểu thức
            if (!currentDisplay.isEmpty()) {
                if (currentExpression.isEmpty()) {
                    // Bắt đầu biểu thức mới
                    if (operator.equals("√")) {
                        expressionDisplay.setText("√");
                    } else {
                        expressionDisplay.setText(currentDisplay + " " + operator);
                    }
                } else {
                    // Thêm vào biểu thức hiện tại
                    if (operator.equals("√")) {
                        if (calculator.isResult()) {
                            expressionDisplay.setText("√");
                        } else if (!calculator.isStart()) {
                            if (currentExpression.endsWith(" ")) {
                                expressionDisplay.setText(currentExpression + "√");
                            } else {
                                expressionDisplay.setText(currentExpression + " " + currentDisplay + " " + operator);
                            }
                        } else {
                            expressionDisplay.setText(currentExpression + " √");
                        }
                    } else {
                        if (calculator.isResult()) {
                            expressionDisplay.setText(currentDisplay + " " + operator);
                        } else if (!calculator.isStart()) {
                            // Nếu biểu thức kết thúc bằng phép toán, thay thế nó
                            if (currentExpression.matches(".*[×÷+\\-]\\s*$")) {
                                expressionDisplay.setText(currentExpression.replaceAll("[×÷+\\-]\\s*$", operator));
                            } else {
                                expressionDisplay.setText(currentExpression + " " + currentDisplay + " " + operator);
                            }
                        } else {
                            // Thay đổi phép toán cuối cùng
                            expressionDisplay.setText(currentExpression.substring(0, currentExpression.length() - 1) + operator);
                        }
                    }
                }

                display.setText("0");
                calculator.setStart(true);
                calculator.setResult(false);
            }
        }
    }

    private class ExpressionEvaluator {
        private final ArrayList<String> numbers = new ArrayList<>();
        private final ArrayList<String> operators = new ArrayList<>();

        public String evaluate(String expression) {
            // Tách biểu thức thành các số và phép tính
            String[] parts = expression.split(" ");
            numbers.clear();
            operators.clear();

            // Xử lý từng phần của biểu thức
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.matches("[+\\-×÷^%]")) {
                    operators.add(part);
                } else if (part.startsWith("√")) {
                    // Xử lý phép căn
                    String numberToSqrt;
                    if (part.length() > 1) {
                        // Nếu số đi liền với dấu căn (√9)
                        numberToSqrt = part.substring(1);
                    } else if (i + 1 < parts.length) {
                        // Nếu số đứng riêng (√ 9)
                        numberToSqrt = parts[++i];
                    } else {
                        return "Math ERROR";
                    }
                    // Tính căn bậc hai của số
                    String sqrtResult = calculator.calculateResult("0", numberToSqrt, "√");
                    if (sqrtResult.equals("Math ERROR")) return sqrtResult;
                    numbers.add(sqrtResult);
                } else if (!part.equals("=")) {
                    numbers.add(part);
                }
            }

            // Xử lý các phép tính còn lại theo thứ tự ưu tiên
            // Lũy thừa
            for (int i = 0; i < operators.size(); i++) {
                if (operators.get(i).equals("^")) {
                    String result = calculator.calculateResult(numbers.get(i), numbers.get(i + 1), "^");
                    if (result.equals("Math ERROR")) return result;
                    numbers.set(i, result);
                    numbers.remove(i + 1);
                    operators.remove(i);
                    i--;
                }
            }

            // Nhân, chia và chia lấy dư
            for (int i = 0; i < operators.size(); i++) {
                if (operators.get(i).equals("×") || operators.get(i).equals("÷") || operators.get(i).equals("%")) {
                    String result = calculator.calculateResult(numbers.get(i), numbers.get(i + 1), operators.get(i));
                    if (result.equals("Math ERROR")) return result;
                    numbers.set(i, result);
                    numbers.remove(i + 1);
                    operators.remove(i);
                    i--;
                }
            }

            // Cộng và trừ
            while (!operators.isEmpty()) {
                String result = calculator.calculateResult(numbers.get(0), numbers.get(1), operators.get(0));
                if (result.equals("Math ERROR")) return result;
                numbers.set(0, result);
                numbers.remove(1);
                operators.remove(0);
            }

            return numbers.get(0);
        }
    }

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    // RoundedButton class
    class RoundedButton extends JButton {
        private final int radius = 100;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground().darker());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }
} 