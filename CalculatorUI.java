import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CalculatorUI extends JFrame {
    private final JTextField display = new JTextField("0");
    private final JTextField expressionDisplay = new JTextField("");
    private final JTextArea historyArea = new JTextArea(2, 40);
    private final CalculatorLogic calculator;

    public CalculatorUI() {
        calculator = new CalculatorLogic();
        
        // Thiết lập cửa sổ chính
        setTitle("Máy Tính Casino");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        setMinimumSize(new Dimension(400, 600));
        getContentPane().setBackground(Color.BLACK);

        // Panel chứa màn hình hiển thị
        JPanel displayPanel = createDisplayPanel();
        add(displayPanel, BorderLayout.NORTH);

        // Khu vực lịch sử
        JScrollPane scrollPane = createHistoryPanel();
        add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 700);
        setLocationRelativeTo(null);
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

    private JScrollPane createHistoryPanel() {
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Digital-7", Font.PLAIN, 14));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBackground(Color.BLACK);
        historyArea.setForeground(Color.GRAY);
        historyArea.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(400, 40));
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.BLACK);
        
        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 1, 1));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

        String[] buttonLabels = {
            "AC", "←", "^/√", "÷",
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
        popupMenu.setBackground(new Color(51, 51, 51));

        JMenuItem sqrtItem = new JMenuItem("Căn bậc 2 (√)");
        sqrtItem.setForeground(Color.WHITE);
        sqrtItem.setBackground(new Color(51, 51, 51));
        sqrtItem.addActionListener(e -> new ButtonClickListener().handleOperator("√"));

        JMenuItem powerItem = new JMenuItem("Lũy thừa (^)");
        powerItem.setForeground(Color.WHITE);
        powerItem.setBackground(new Color(51, 51, 51));
        powerItem.addActionListener(e -> new ButtonClickListener().handleOperator("^"));

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
            } else if (command.equals("AC")) {
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
            if (!calculator.getFirstNumber().isEmpty() && !calculator.getOperator().isEmpty()) {
                String secondNumber = display.getText();
                String expression = calculator.getFirstNumber() + " " + calculator.getOperator() + " " + secondNumber;
                String result = calculator.calculateResult(calculator.getFirstNumber(), secondNumber, calculator.getOperator());
                display.setText(result);
                expressionDisplay.setText(expression + " =");
                updateHistory(expression + " = " + result);
                calculator.setResult(true);
                calculator.setFirstNumber("");
                calculator.setOperator("");
            }
        }

        private void handleSquareRoot() {
            String value = display.getText();
            String expression = "√" + value;
            String result = calculator.calculateSquareRoot(value);
            display.setText(result);
            expressionDisplay.setText(expression);
            updateHistory(expression + " = " + result);
            calculator.setResult(true);
            calculator.setStart(true);
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

        private void handleNegative() {
            String currentText = display.getText();
            if (!currentText.equals("0")) {
                if (currentText.startsWith("-")) {
                    display.setText(currentText.substring(1));
                } else {
                    display.setText("-" + currentText);
                }
            }
        }

        private void handleOperator(String operator) {
            if (!display.getText().isEmpty()) {
                if (operator.equals("√")) {
                    handleSquareRoot();
                } else {
                    calculator.setFirstNumber(display.getText());
                    calculator.setOperator(operator);
                    expressionDisplay.setText(calculator.getFirstNumber() + " " + operator);
                    calculator.setStart(true);
                    calculator.setResult(false);
                }
            }
        }
    }

    private void updateHistory(String calculation) {
        calculator.addToHistory(calculation);
        StringBuilder sb = new StringBuilder();
        for (String calc : calculator.getHistory()) {
            sb.append(calc).append("\n");
        }
        historyArea.setText(sb.toString());
    }

    // RoundedButton class remains the same
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalculatorUI calculator = new CalculatorUI();
            calculator.setVisible(true);
        });
    }
} 