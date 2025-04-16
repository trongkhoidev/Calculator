import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * CalculatorPanel class represents the main calculator interface.
 * It handles the display and button layout for the calculator.
 */
public class CalculatorPanel extends JPanel {
    private final JTextField display;           // Main number display
    private final JTextField expressionDisplay; // Expression display
    private final CalculatorLogic calculator;   // Reference to calculator logic
    private boolean isDarkMode = true;          // Theme mode flag
    private final Preferences prefs;            // For storing user preferences
    
    // Theme colors
    private Color backgroundColor;
    private Color textColor;
    private Color displayColor;
    private Color operatorColor;
    private Color scientificColor;
    private Color numberColor;
    private Color controlColor;

    // Fonts
    private final Font displayFont = new Font("Segoe UI", Font.PLAIN, 24);
    private final Font buttonFont = new Font("Segoe UI", Font.PLAIN, 18);

    /**
     * Constructor initializes the calculator panel and sets up the UI components
     * @param calculator Reference to the calculator logic
     */
    public CalculatorPanel(CalculatorLogic calculator) {
        this.calculator = calculator;
        this.display = new JTextField("0");
        this.expressionDisplay = new JTextField("");
        this.prefs = Preferences.userNodeForPackage(CalculatorPanel.class);
        
        // Load saved preferences
        isDarkMode = prefs.getBoolean("darkMode", true);
        updateTheme();
        
        setLayout(new BorderLayout(0, 0));
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create display panel
        JPanel displayPanel = createDisplayPanel();
        add(displayPanel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Updates the theme colors based on the current mode
     */
    private void updateTheme() {
        if (isDarkMode) {
            backgroundColor = new Color(30, 30, 30);
            textColor = Color.WHITE;
            displayColor = new Color(40, 40, 40);
            operatorColor = new Color(0, 120, 212);
            scientificColor = new Color(0, 150, 136);
            numberColor = new Color(60, 60, 60);
            controlColor = new Color(80, 80, 80);
        } else {
            backgroundColor = new Color(240, 240, 240);
            textColor = Color.BLACK;
            displayColor = new Color(250, 250, 250);
            operatorColor = new Color(0, 90, 158);
            scientificColor = new Color(0, 120, 107);
            numberColor = new Color(200, 200, 200);
            controlColor = new Color(180, 180, 180);
        }
        setBackground(backgroundColor);
    }

    /**
     * Creates and configures the display panel containing the calculator displays
     * @return Configured display panel
     */
    private JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout(0, 0));
        displayPanel.setBackground(backgroundColor);
        displayPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Configure expression display
        expressionDisplay.setHorizontalAlignment(JTextField.RIGHT);
        expressionDisplay.setEditable(false);
        expressionDisplay.setFont(displayFont);
        expressionDisplay.setPreferredSize(new Dimension(400, 30));
        expressionDisplay.setBackground(displayColor);
        expressionDisplay.setForeground(textColor);
        expressionDisplay.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Configure main display
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setFont(displayFont);
        display.setPreferredSize(new Dimension(400, 50));
        display.setBackground(displayColor);
        display.setForeground(textColor);
        display.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Add displays to panel
        displayPanel.add(expressionDisplay, BorderLayout.NORTH);
        displayPanel.add(display, BorderLayout.CENTER);

        return displayPanel;
    }

    /**
     * Creates and configures the button panel containing all calculator buttons
     * @return Configured button panel
     */
    private JPanel createButtonPanel() {
        JPanel mainButtonPanel = new JPanel(new BorderLayout(10, 10));
        mainButtonPanel.setBackground(backgroundColor);

        // Create scientific functions panel
        JPanel scientificPanel = new JPanel(new GridLayout(3, 5, 5, 5));
        scientificPanel.setBackground(backgroundColor);
        String[] scientificButtons = {
            "sin", "cos", "tan", "cot", "log",
            "ln", "x²", "x³", "√", "x^y",
            "n!", "π", "e", "(", ")"
        };
        for (String label : scientificButtons) {
            CircularButton button = createCircularButton(label);
            button.setBackground(scientificColor);
            scientificPanel.add(button);
        }

        // Create number pad panel
        JPanel numberPadPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        numberPadPanel.setBackground(backgroundColor);
        String[] numberButtons = {
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            "0", ".", "±", "+"
        };
        for (String label : numberButtons) {
            CircularButton button = createCircularButton(label);
            if (label.matches("[0-9.]")) {
                button.setBackground(numberColor);
            } else {
                button.setBackground(operatorColor);
            }
            numberPadPanel.add(button);
        }

        // Create control panel
        JPanel controlPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        controlPanel.setBackground(backgroundColor);
        String[] controlButtons = {"CE", "C", "←"};
        for (String label : controlButtons) {
            CircularButton button = createCircularButton(label);
            button.setBackground(controlColor);
            controlPanel.add(button);
        }

        // Add theme toggle button
        CircularButton themeButton = createCircularButton("☀");
        themeButton.addActionListener(e -> {
            isDarkMode = !isDarkMode;
            prefs.putBoolean("darkMode", isDarkMode);
            updateTheme();
            updateUI();
        });
        controlPanel.add(themeButton);

        // Add equals button
        CircularButton equalsButton = createCircularButton("=");
        equalsButton.setBackground(operatorColor);
        controlPanel.add(equalsButton);

        // Add all panels to main button panel
        mainButtonPanel.add(scientificPanel, BorderLayout.NORTH);
        mainButtonPanel.add(numberPadPanel, BorderLayout.CENTER);
        mainButtonPanel.add(controlPanel, BorderLayout.SOUTH);

        return mainButtonPanel;
    }

    /**
     * Creates a circular button with the specified label
     * @param label The text to display on the button
     * @return Configured CircularButton
     */
    private CircularButton createCircularButton(String label) {
        CircularButton button = new CircularButton(label);
        button.setFont(buttonFont);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(60, 60));
        button.addActionListener(new ButtonClickListener());
        return button;
    }

    /**
     * Custom button class that renders as a perfect circle
     */
    private class CircularButton extends JButton {
        public CircularButton(String text) {
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

            // Draw circular background
            int diameter = Math.min(getWidth(), getHeight());
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
            g2.fillOval(x, y, diameter, diameter);

            // Draw text
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getHeight();
            g2.drawString(getText(), 
                (getWidth() - textWidth) / 2,
                (getHeight() + textHeight) / 2 - fm.getDescent());

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // No border
        }
    }

    /**
     * Inner class that handles button click events
     */
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            
            switch (command) {
                case "sin", "cos", "tan", "cot" -> handleTrigonometric(command);
                case "log" -> handleLogarithm(10);
                case "ln" -> handleLogarithm(Math.E);
                case "x²" -> handlePower(2);
                case "x³" -> handlePower(3);
                case "x^y" -> handlePower();
                case "n!" -> handleFactorial();
                case "π" -> handleConstant(Math.PI);
                case "e" -> handleConstant(Math.E);
                case "CE" -> handleClearEntry();
                case "C" -> handleClear();
                case "←" -> handleBackspace();
                case "±" -> handleSignChange();
                case "=" -> handleEquals();
                case "." -> handleDecimalPoint();
                default -> {
                    if (command.matches("[0-9]")) {
                        handleNumber(command);
                    } else if (command.matches("[+\\-×÷]")) {
                        handleOperator(command);
                    }
                }
            }
        }

        private void handleTrigonometric(String func) {
            try {
                double value = Double.parseDouble(display.getText());
                double result = calculator.trigonometricFunction(value, func);
                display.setText(calculator.formatNumber(result));
            } catch (Exception e) {
                display.setText("Error");
            }
        }

        private void handleLogarithm(double base) {
            try {
                double value = Double.parseDouble(display.getText());
                if (value <= 0) {
                    display.setText("Error");
                    return;
                }
                double result = Math.log(value) / Math.log(base);
                display.setText(calculator.formatNumber(result));
            } catch (Exception e) {
                display.setText("Error");
            }
        }

        private void handlePower(double exponent) {
            try {
                double value = Double.parseDouble(display.getText());
                double result = Math.pow(value, exponent);
                display.setText(calculator.formatNumber(result));
            } catch (Exception e) {
                display.setText("Error");
            }
        }

        private void handlePower() {
            // Store current value and wait for exponent
            calculator.setFirstNumber(display.getText());
            calculator.setOperator("^");
            expressionDisplay.setText(display.getText() + " ^ ");
            display.setText("0");
        }

        private void handleFactorial() {
            try {
                double value = Double.parseDouble(display.getText());
                double result = calculator.factorial(value);
                display.setText(calculator.formatNumber(result));
            } catch (Exception e) {
                display.setText("Error");
            }
        }

        private void handleConstant(double constant) {
            display.setText(calculator.formatNumber(constant));
        }

        private void handleClearEntry() {
            display.setText("0");
            calculator.setNewNumber(true);
        }

        private void handleSignChange() {
            String current = display.getText();
            if (current.startsWith("-")) {
                display.setText(current.substring(1));
            } else {
                display.setText("-" + current);
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

            // If there's no calculation or nothing entered, do nothing
            if (calculator.isStart() && calculator.getOperator().isEmpty()) {
                return;
            }

            // If there's already a result and no new calculation, do nothing
            if (calculator.isResult() && calculator.getOperator().isEmpty()) {
                return;
            }

            // Update current expression with the last number
            String currentExpression = expressionDisplay.getText();
            if (!currentExpression.isEmpty()) {
                if (currentExpression.equals("√")) {
                    // If there's only a square root calculation
                    expression = "√" + secondNumber;
                    expressionDisplay.setText(expression);
                } else if (currentExpression.endsWith("√")) {
                    // If there's a calculation with square root
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

            // Calculate result
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

            // Special handling for minus sign
            if (operator.equals("-")) {
                // Case 1: Starting state or display "0"
                if (calculator.isStart() || currentDisplay.equals("0")) {
                    // Check if expression is not empty and ends with operation
                    if (!currentExpression.isEmpty() && currentExpression.matches(".*[×÷+\\-]\\s*$")) {
                        // If there's already a minus, no need to add more
                        if (currentExpression.endsWith(" -") || currentDisplay.startsWith("-")) {
                            return;
                        }
                    }
                    // Allow adding minus if not already started with
                    if (!currentDisplay.startsWith("-")) {
                        display.setText("-");
                        calculator.setStart(false);
                    }
                    return;
                }
                // Case 2: Entering a number
                else if (!calculator.isStart()) {
                    // If there's already a minus, no need to add more
                    if (currentDisplay.startsWith("-") || (currentExpression.endsWith(" -"))) {
                        return;
                    }
                }
            }

            // Handle other operations and update expression
            if (!currentDisplay.isEmpty()) {
                if (currentExpression.isEmpty()) {
                    // Start new expression
                    if (operator.equals("√")) {
                        expressionDisplay.setText("√");
                    } else {
                        expressionDisplay.setText(currentDisplay + " " + operator);
                    }
                } else {
                    // Add to current expression
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
                            // If expression ends with operation, replace it
                            if (currentExpression.matches(".*[×÷+\\-]\\s*$")) {
                                expressionDisplay.setText(currentExpression.replaceAll("[×÷+\\-]\\s*$", operator));
                            } else {
                                expressionDisplay.setText(currentExpression + " " + currentDisplay + " " + operator);
                            }
                        } else {
                            // Change last operation
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

    /**
     * Inner class that evaluates mathematical expressions
     */
    private class ExpressionEvaluator {
        private final ArrayList<String> numbers = new ArrayList<>();
        private final ArrayList<String> operators = new ArrayList<>();

        public String evaluate(String expression) {
            // Split expression into numbers and operations
            String[] parts = expression.split(" ");
            numbers.clear();
            operators.clear();

            // Process each part of the expression
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.matches("[+\\-×÷^%]")) {
                    operators.add(part);
                } else if (part.startsWith("√")) {
                    // Process square root
                    String numberToSqrt;
                    if (part.length() > 1) {
                        // If number is next to square root (√9)
                        numberToSqrt = part.substring(1);
                    } else if (i + 1 < parts.length) {
                        // If number is standalone (√ 9)
                        numberToSqrt = parts[++i];
                    } else {
                        return "Math ERROR";
                    }
                    // Calculate square root of number
                    String sqrtResult = calculator.calculateResult("0", numberToSqrt, "√");
                    if (sqrtResult.equals("Math ERROR")) return sqrtResult;
                    numbers.add(sqrtResult);
                } else if (!part.equals("=")) {
                    numbers.add(part);
                }
            }

            // Process remaining operations in order of precedence
            // Exponentiation
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

            // Multiplication, division, and modulo
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

            // Addition and subtraction
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
} 