package do_an_application1;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;


public class CalculatorGUI extends JFrame implements ActionListener, KeyListener {

 private static final long serialVersionUID = 1L;

    private int frameWidth = 300;
    private int frameHeight = 400;
    private int xframeShow = 400;
    private int yframeShow = 100;
    private JTextField tfDisplay;
    private JLabel lbAns, lbStats, lbDOH, lbB;
    private int mode = 0;
    private JFrame frame;

    private JPanel mainPanel;
    private JPanel disPlayPanel;
    private JPanel buttonPanel;

    private JButton btnArr[];

    private JRadioButton radDeg, radRad;
    private JRadioButton radBin, radOct, radDec, radHex;

    private String lbButton[];
    private String mathElement[];
    private double ans = 0;

    // Theme related variables
    private boolean isDarkMode = true;
    private JToggleButton themeToggleButton;
    
    // Colors for dark mode
    private Color colorDisableStats = Color.lightGray;
    private Color colorEnnabaleStar = Color.black;
    private Color operatorButtonColor = new Color(255, 153, 0); // Orange color for operator buttons
    private Color numberButtonColor = new Color(51, 51, 51); // Dark gray for number buttons
    private Color functionButtonColor = new Color(192, 192, 192); // Light gray for function buttons
    private Color backgroundColor = Color.BLACK; // Black background
    private Color textColor = Color.WHITE; // White text
    
    // Colors for light mode
    private Color lightModeBackground = new Color(240, 240, 240);
    private Color lightModeNumberButton = new Color(220, 220, 220);
    private Color lightModeOperatorButton = new Color(255, 204, 102);
    private Color lightModeFunctionButton = new Color(230, 230, 230);
    private Color lightModeTextColor = Color.BLACK;
    
    private Balan balan;
    
    // History related fields
    private List<CalculationHistory> historyList;
    private JPanel historyPanel;
    private JTextField searchField;
    private String historyFilePath;

    public CalculatorGUI() {
        frame = new JFrame("Calculator Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBounds(xframeShow, yframeShow, frameWidth, frameHeight);
        frame.setJMenuBar(createMenuBar());
        
        // Initialize history
        historyFilePath = "calculation_history.txt";
        historyList = new ArrayList<>();
        loadHistory();
        
        resetValue(); // dat lai cac gia tri
        changeMode(); // che do hien thi
    }

    private void changeMode() {
        if (mode == 0) {
            frameWidth = 300;
            frameHeight = 380;
            frame.setTitle("Calculator - Basic");
        }
        if (mode == 1) {
            frameWidth = 340;
            frameHeight = 560;
            frame.setTitle("Calculator - Advanced");
        }
        if (mode == 2) {
            frameWidth = 400;
            frameHeight = 500;
            frame.setTitle("Calculator - History");
        }

        createListLabelButton(mode);
        balan.setDegOrRad(true);
        balan.setRadix(10);

        frame.getContentPane().removeAll();
        frame.setSize(frameWidth, frameHeight);
        mainPanel = createMainPanel();
        frame.add(mainPanel);

        frame.getContentPane().validate();
        frame.setVisible(true);
        if (mode != 2) {
            tfDisplay.requestFocus();
        }
    }
    
    // Apply the current theme (light/dark) to UI components
    private void applyTheme() {
        if (isDarkMode) {
            // Dark mode colors
            backgroundColor = Color.BLACK;
            textColor = Color.WHITE;
            operatorButtonColor = new Color(255, 153, 0);
            numberButtonColor = new Color(51, 51, 51);
            functionButtonColor = new Color(192, 192, 192);
            themeToggleButton.setText("☀️");
            themeToggleButton.setToolTipText("Switch to Light Mode");
        } else {
            // Light mode colors
            backgroundColor = lightModeBackground;
            textColor = lightModeTextColor;
            operatorButtonColor = lightModeOperatorButton;
            numberButtonColor = lightModeNumberButton;
            functionButtonColor = lightModeFunctionButton;
            themeToggleButton.setText("🌙");
            themeToggleButton.setToolTipText("Switch to Dark Mode");
        }
        
        // Update UI
        changeMode();
    }
    
    // Toggle between light and dark mode
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        if (mode == 2) {
            // History mode
            mainPanel = createHistoryPanel();
        } else {
            // Calculator modes
            disPlayPanel = createDisplayPanel();
            mainPanel.add(disPlayPanel, BorderLayout.NORTH);

            if (mode == 0) {
                buttonPanel = createButtonBasicPanel();
            }
            if (mode == 1) {
                buttonPanel = createButtonAdvancedPanel();
            }

            mainPanel.add(buttonPanel, BorderLayout.CENTER);
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        mainPanel.setVisible(true);
        return mainPanel;
    }
    
    // Create history panel for mode 2
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(isDarkMode ? backgroundColor : lightModeBackground);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with search functionality
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(isDarkMode ? backgroundColor : lightModeBackground);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(isDarkMode ? textColor : lightModeTextColor);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        searchButton.addActionListener(e -> searchHistory());
        
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        
        // History list panel
        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBackground(isDarkMode ? backgroundColor : Color.WHITE);
        
        refreshHistoryPanel();
        
        JScrollPane scrollPane = new JScrollPane(historyPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Bottom panel with Clear All button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(isDarkMode ? backgroundColor : lightModeBackground);
        
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> clearAllHistory());
        bottomPanel.add(clearAllButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Refresh the history panel with current history items
    private void refreshHistoryPanel() {
        historyPanel.removeAll();
        
        if (historyList.isEmpty()) {
            JLabel emptyLabel = new JLabel("No calculation history available");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            emptyLabel.setForeground(isDarkMode ? textColor : lightModeTextColor);
            historyPanel.add(emptyLabel);
        } else {
            String searchTerm = searchField != null ? searchField.getText().toLowerCase() : "";
            
            for (CalculationHistory history : historyList) {
                if (searchTerm.isEmpty() || 
                    history.getExpression().toLowerCase().contains(searchTerm) ||
                    history.getResult().toLowerCase().contains(searchTerm) ||
                    history.getTimestamp().toLowerCase().contains(searchTerm)) {
                    
                    JPanel itemPanel = createHistoryItemPanel(history);
                    historyPanel.add(itemPanel);
                }
            }
        }
        
        historyPanel.revalidate();
        historyPanel.repaint();
    }
    
    // Create panel for a single history item
    private JPanel createHistoryItemPanel(CalculationHistory history) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(isDarkMode ? new Color(30, 30, 30) : Color.WHITE);
        
        JLabel expressionLabel = new JLabel(history.getExpression());
        expressionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        expressionLabel.setForeground(isDarkMode ? textColor : lightModeTextColor);
        
        JLabel resultLabel = new JLabel("= " + history.getResult());
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(isDarkMode ? textColor : lightModeTextColor);
        
        JLabel timeLabel = new JLabel(history.getTimestamp());
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        timeLabel.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(isDarkMode ? new Color(30, 30, 30) : Color.WHITE);
        textPanel.add(expressionLabel);
        textPanel.add(resultLabel);
        textPanel.add(timeLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(isDarkMode ? new Color(30, 30, 30) : Color.WHITE);
        
        JButton useButton = new JButton("Use");
        useButton.addActionListener(e -> useHistoryItem(history));
        
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteHistoryItem(history));
        
        buttonPanel.add(useButton);
        buttonPanel.add(deleteButton);
        
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    // Search history with the term in search field
    private void searchHistory() {
        refreshHistoryPanel();
    }
    
    // Clear all history after confirmation
    private void clearAllHistory() {
        int result = JOptionPane.showConfirmDialog(frame, 
            "Are you sure you want to clear all calculation history?", 
            "Confirm Clear History", 
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            historyList.clear();
            saveHistory();
            refreshHistoryPanel();
        }
    }
    
    // Delete a specific history item
    private void deleteHistoryItem(CalculationHistory history) {
        historyList.remove(history);
        saveHistory();
        refreshHistoryPanel();
    }
    
    // Use a history item in calculator
    private void useHistoryItem(CalculationHistory history) {
        // Switch to appropriate calculator mode
        if (history.getExpression().contains("sin") || 
            history.getExpression().contains("cos") || 
            history.getExpression().contains("tan") ||
            history.getExpression().contains("log")) {
            mode = 1; // Advanced mode
        } else {
            mode = 0; // Basic mode
        }
        
        changeMode();
        
        // Set the expression in the calculator
        tfDisplay.setText(history.getExpression());
        result(); // Calculate result
    }

    // ----------------------- main panel content ----------------------- //
    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();

        // menu mode
        JMenu mm = createMenu("Mode", KeyEvent.VK_M);
        mm.add(createMenuItem("Basic", KeyEvent.VK_B));
        mm.add(createMenuItem("Advanced", KeyEvent.VK_A));
        mm.add(createMenuItem("History", KeyEvent.VK_H));
        mm.add(createMenuItem("Exit", KeyEvent.VK_X));
        mb.add(mm);
        
        // Theme toggle button
        themeToggleButton = new JToggleButton(isDarkMode ? "☀️" : "🌙");
        themeToggleButton.setToolTipText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.addActionListener(e -> toggleTheme());
        mb.add(themeToggleButton);

        return mb;
    }

    // create Display Panel
    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        
        if (mode == 1) {
            lbStats = new JLabel("");
            Font fontStats = lbStats.getFont().deriveFont(Font.PLAIN, 12f);
            lbStats.setFont(fontStats);
            lbStats.setForeground(colorDisableStats);
            lbStats.setBackground(backgroundColor);
            lbStats.setOpaque(true);
            panel.add(lbStats, BorderLayout.NORTH);
        }

        tfDisplay = new JTextField(frameWidth);
        Font fontDisplay = tfDisplay.getFont().deriveFont(Font.PLAIN, 20f);
        tfDisplay.setFont(fontDisplay);
        tfDisplay.setHorizontalAlignment(JTextField.RIGHT);
        tfDisplay.setBorder(null);
        tfDisplay.setForeground(textColor);
        tfDisplay.setBackground(backgroundColor);
        tfDisplay.addKeyListener(this);// bac su kien khi an phim
        panel.add(tfDisplay, BorderLayout.CENTER);

        lbAns = new JLabel("0");
        Font fontAns = lbAns.getFont().deriveFont(Font.PLAIN, 35f);
        lbAns.setFont(fontAns);
        lbAns.setHorizontalAlignment(JLabel.RIGHT);
        lbAns.setBackground(backgroundColor);
        lbAns.setForeground(textColor);
        lbAns.setOpaque(true);

        panel.add(lbAns, BorderLayout.SOUTH);
        panel.setBorder(new EmptyBorder(0, 0, 10, 0));

        return panel;
    }

    // create button Basic Panel
    private JPanel createButtonBasicPanel() {
        JPanel buttonBasicPanel = new JPanel(new GridLayout(5, 4, 8, 8));
        buttonBasicPanel.setBackground(backgroundColor);
        
        btnArr = new JButton[lbButton.length];
        
        for (int i = 0; i < lbButton.length; i++) {
            btnArr[i] = createButton(lbButton[i]);
            
            // Set button appearance
            btnArr[i].setFocusPainted(false);
            btnArr[i].setBorderPainted(false);
            btnArr[i].setFont(new Font("Arial", Font.BOLD, 18));
            btnArr[i].setForeground(textColor);
            
            // Set round shape and color based on button type
            if (lbButton[i].equals("+") || lbButton[i].equals("–") || 
                lbButton[i].equals("*") || lbButton[i].equals("/") || 
                lbButton[i].equals("=")) {
                btnArr[i].setBackground(operatorButtonColor);
            } else if (lbButton[i].equals("0") || lbButton[i].equals("1") || 
                      lbButton[i].equals("2") || lbButton[i].equals("3") || 
                      lbButton[i].equals("4") || lbButton[i].equals("5") || 
                      lbButton[i].equals("6") || lbButton[i].equals("7") || 
                      lbButton[i].equals("8") || lbButton[i].equals("9") ||
                      lbButton[i].equals("•") || lbButton[i].equals(",")) {
                btnArr[i].setBackground(numberButtonColor);
            } else {
                btnArr[i].setBackground(functionButtonColor);
            }
            
            buttonBasicPanel.add(btnArr[i]);
        }

        return buttonBasicPanel;
    }

    // create button Advanced panel
    private JPanel createButtonAdvancedPanel() {
        // panel top
        JPanel panelLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLeft.setBackground(backgroundColor);
        ButtonGroup btnGroup = new ButtonGroup();
        radDeg = createRadio("Deg", true, panelLeft);
        btnGroup.add(radDeg);
        radRad = createRadio("Rad", false, panelLeft);
        btnGroup.add(radRad);

        // Panel for the top area
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.setBackground(backgroundColor);
        panel1.add(panelLeft, BorderLayout.WEST);

        // Create all buttons first
        btnArr = new JButton[lbButton.length];
        for (int i = 0; i < lbButton.length; i++) {
            btnArr[i] = createButton(lbButton[i]);
            
            // Set button appearance
            btnArr[i].setFocusPainted(false);
            btnArr[i].setBorderPainted(false);
            btnArr[i].setFont(new Font("Arial", Font.BOLD, 16));
            btnArr[i].setForeground(textColor);
            
            // Set round shape and color based on button type
            if (lbButton[i].equals("+") || lbButton[i].equals("–") || 
                lbButton[i].equals("*") || lbButton[i].equals("/") || 
                lbButton[i].equals("=")) {
                btnArr[i].setBackground(operatorButtonColor);
            } else if (lbButton[i].equals("0") || lbButton[i].equals("1") || 
                      lbButton[i].equals("2") || lbButton[i].equals("3") || 
                      lbButton[i].equals("4") || lbButton[i].equals("5") || 
                      lbButton[i].equals("6") || lbButton[i].equals("7") || 
                      lbButton[i].equals("8") || lbButton[i].equals("9") ||
                      lbButton[i].equals("•") || lbButton[i].equals(",")) {
                btnArr[i].setBackground(numberButtonColor);
            } else {
                btnArr[i].setBackground(functionButtonColor);
            }
        }

        // Create a main panel with 7 rows and 5 columns with even spacing
        JPanel buttonGrid = new JPanel(new GridLayout(7, 5, 10, 10));
        buttonGrid.setBackground(backgroundColor);
        
        // Row 1: C, CE, ←, (, )
        buttonGrid.add(btnArr[0]); // C
        buttonGrid.add(btnArr[1]); // CE
        buttonGrid.add(btnArr[2]); // ←
        buttonGrid.add(btnArr[3]); // (
        buttonGrid.add(btnArr[4]); // )
        
        // Row 2: !, log, x², sin, √
        buttonGrid.add(btnArr[5]); // !
        buttonGrid.add(btnArr[6]); // log
        buttonGrid.add(btnArr[7]); // x²
        buttonGrid.add(btnArr[13]); // sin
        buttonGrid.add(btnArr[12]); // √
        
        // Row 3: 7, 8, 9, /, cos
        buttonGrid.add(btnArr[8]); // 7
        buttonGrid.add(btnArr[9]); // 8
        buttonGrid.add(btnArr[10]); // 9
        buttonGrid.add(btnArr[11]); // /
        buttonGrid.add(btnArr[14]); // cos
        
        // Row 4: 4, 5, 6, *, tan
        buttonGrid.add(btnArr[16]); // 4
        buttonGrid.add(btnArr[17]); // 5
        buttonGrid.add(btnArr[18]); // 6
        buttonGrid.add(btnArr[19]); // *
        buttonGrid.add(btnArr[15]); // tan
        
        // Row 5: 1, 2, 3, -, Ans
        buttonGrid.add(btnArr[24]); // 1
        buttonGrid.add(btnArr[25]); // 2
        buttonGrid.add(btnArr[26]); // 3
        buttonGrid.add(btnArr[23]); // -
        buttonGrid.add(btnArr[20]); // Ans
        
        // Row 6: 0, •, =, +, °
        buttonGrid.add(btnArr[28]); // 0
        buttonGrid.add(btnArr[29]); // •
        buttonGrid.add(btnArr[27]); // =
        buttonGrid.add(btnArr[34]); // +
        buttonGrid.add(btnArr[21]); // °
        
        // Row 7: ln, π, e, 1/x, %
        buttonGrid.add(btnArr[30]); // ln
        buttonGrid.add(btnArr[31]); // π
        buttonGrid.add(btnArr[32]); // e
        buttonGrid.add(btnArr[33]); // 1/x
        buttonGrid.add(btnArr[22]); // %
        
        // Final panel combining all elements
        JPanel buttonAdvancedPanel = new JPanel(new BorderLayout(0, 10));
        buttonAdvancedPanel.setBackground(backgroundColor);
        buttonAdvancedPanel.add(panel1, BorderLayout.NORTH);
        buttonAdvancedPanel.add(buttonGrid, BorderLayout.CENTER);
        buttonAdvancedPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        return buttonAdvancedPanel;
    }

    // ----------------------- item of menu ----------------------- //
    // create menu
    private JMenu createMenu(String title, int key) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(key);
        return menu;
    }

    // create menu item
    private JMenuItem createMenuItem(String title, int key) {
        JMenuItem mi = new JMenuItem(title, key);
        mi.addActionListener(this);
        return mi;
    }

    // create button
    private JButton createButton(String title) {
        JButton btn = new JButton(title) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(getBackground().darker());
                } else {
                    g.setColor(getBackground());
                }
                g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(java.awt.Graphics g) {
                // No border
            }

            @Override
            public boolean contains(int x, int y) {
                if (shape == null || !shape.getBounds().equals(getBounds())) {
                    shape = new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight());
                }
                return shape.contains(x, y);
            }
            private java.awt.Shape shape;
        };
        btn.setContentAreaFilled(false);
        btn.addActionListener(this);
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }

    // create button and add to panel
    private JButton createButton(String title, JPanel panel) {
        JButton btn = createButton(title);
        panel.add(btn);
        return btn;
    }

    // create Radio button
    private JRadioButton createRadio(String title, boolean isSelect,
            JPanel panel) {
        JRadioButton rad = new JRadioButton(title);
        rad.addActionListener(this);
        rad.setSelected(isSelect);
        rad.setForeground(textColor);
        rad.setBackground(backgroundColor);
        panel.add(rad);
        return rad;
    }

    // create List label Button
    private void createListLabelButton(int mode) {
        if (mode == 0) {
            String s[] = {"C", "←", "^/√", "÷", 
                         "7", "8", "9", "×", 
                         "4", "5", "6", "-", 
                         "1", "2", "3", "+", 
                         "0", ",", "%", "="};
            lbButton = s;

            String s1[] = {"", "", "", "/", 
                         "7", "8", "9", "*", 
                         "4", "5", "6", "-", 
                         "1", "2", "3", "+", 
                         "0", ".", "%", ""};
            mathElement = s1;

            return;
        }
        if (mode == 1) {
            // The first 28 buttons (excluding the bottom row)
            String s[] = {"C", "CE", "←", "(", ")", "!", "log", "x²", 
                "7", "8", "9", "/", "√", "sin", "cos", "tan", 
                "4", "5", "6", "*", "Ans", "°", "%", "–", 
                "1", "2", "3", "=", 
                "0", "•", "ln", "π", "e", "1/x", "+"};
            lbButton = s;

            String s1[] = {"", "", "", "(", ")", "!", "log ", "²", 
                "7", "8", "9", "/", "√", "sin ", "cos ", "tan ", 
                "4", "5", "6", "*", "Ans", "°", "%", "-", 
                "1", "2", "3", "", 
                "0", ".", "ln ", "π", "e", "1/", "+"};
            mathElement = s1;
            return;
        }
    }

    private JButton[] addListButtonToPanel(String lbArr[], JPanel panel) {
        JButton arr[] = new JButton[lbArr.length];
        for (int i = 0; i < lbArr.length; i++) {
            arr[i] = createButton(lbArr[i], panel);
        }
        return arr;
    }

    // ----------------------- Action ----------------------- //
    // dat lai gia tri cho balan
    private void resetValue() {
        balan = new Balan();
        balan.setError(false);
        if (mode == 2) {
            setRadix();
        }
        if (mode == 1) {
            setDegOrRad();
        }
    }

    // cho phep chen ky tu vao vi tri con tro
    private void insertMathString(String str) {
        int index = tfDisplay.getCaretPosition();
        StringBuilder s = new StringBuilder(tfDisplay.getText() + ""); // copy
        s.insert(index, str); // insert text at index control
        String s1 = new String(s); // convert to string
        tfDisplay.setText(s1); // set text for jtextField
        tfDisplay.requestFocus(); // focus jtextFiedl
        tfDisplay.setCaretPosition(index + str.length());
    }

    // tra ve ket qua
    private void result() {
        balan.setError(false);
        String expression = tfDisplay.getText();
        ans = balan.valueMath(expression);
        
        if (!balan.isError()) {
            balan.var[0] = ans;
            String result = balan.numberToString(ans, balan.getRadix(), balan.getSizeRound());
            lbAns.setText(result);
            
            // Add to history
            addToHistory(expression, result);
        } else {
            lbAns.setText("Math error!");
        }
    }

    private void actionCE() {
        balan.setError(false);
        if (mode == 1) {
            lbStats.setForeground(colorDisableStats);
        }
        tfDisplay.setText("");
        tfDisplay.requestFocus();

        lbAns.setText("0");

    }

    private void actionDel() {
        int index = tfDisplay.getCaretPosition();
        StringBuilder s = new StringBuilder(tfDisplay.getText() + ""); // copy
        if (index > 0) {
            s.deleteCharAt(index - 1);
            String s1 = new String(s); // convert to string
            tfDisplay.setText(s1); // set text for jtextField
            tfDisplay.setCaretPosition(index - 1);
        }
        tfDisplay.requestFocus(); // focus jtextFiedl
    }

    private void setDegOrRad() {
        if (radRad.isSelected()) {
            balan.setDegOrRad(false);
        }
        if (radDeg.isSelected()) {
            balan.setDegOrRad(true);
        }
        tfDisplay.requestFocus();
    }

    private void setRadix() {
        if (radBin.isSelected()) {
            balan.setRadix(2);
        }
        if (radOct.isSelected()) {
            balan.setRadix(8);
        }
        if (radDec.isSelected()) {
            balan.setRadix(10);
        }
        if (radHex.isSelected()) {
            balan.setRadix(16);
        }
        tfDisplay.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();

        // Chuyển chế độ
        if (command.equals("Basic")) {
            mode = 0;
            changeMode();
            return;
        }
        if (command.equals("Advanced")) {
            mode = 1;
            changeMode();
            setDegOrRad();
            return;
        }
        if (command.equals("History")) {
            mode = 2;
            changeMode();
            return;
        }
        if (command.equals("Exit")) {
            System.exit(0);
        }

        // Xử lý nút C (Clear All)
        if (mode != 2 && evt.getSource() == btnArr[0]) { // btnArr[0] là vị trí nút "C"
            resetValue();
            actionCE();
            return;
        }

        // Xử lý nút CE (Clear Entry)
        if (mode != 2 && evt.getSource() == btnArr[1]) { // btnArr[1] là vị trí nút "CE"
            actionCE();
            return;
        }

        // Xử lý nút `←` (Backspace)
        if (mode != 2 && evt.getSource() == btnArr[2]) { // btnArr[2] là vị trí nút "←"
            actionDel();
            return;
        }

        if (mode != 2) {
            for (int i = 0; i < btnArr.length; i++) {
                if (evt.getSource() == btnArr[i] && !mathElement[i].equals("")) {
                    insertMathString(mathElement[i]);
                    return;
                }
            }
            if (command.equals("=")) {
                result();
                return;
            }
            if (command.equals("ln")) {
                result();
                if (!balan.isError()) {
                    balan.var[0] = Math.log(ans);
                    lbAns.setText(balan.numberToString(Math.log(ans), balan.getRadix(),
                            balan.getSizeRound()));
                }
                return;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
            result();
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {

    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    // Add calculation to history
    private void addToHistory(String expression, String result) {
        if (expression.trim().isEmpty() || result.trim().isEmpty()) {
            return; // Don't add empty calculations
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());
        
        CalculationHistory history = new CalculationHistory(expression, result, timestamp);
        historyList.add(0, history); // Add to the beginning of the list
        
        // Limit history size to 100 entries
        if (historyList.size() > 100) {
            historyList.remove(historyList.size() - 1);
        }
        
        // Save history to file
        saveHistory();
    }
    
    // Save history to file
    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFilePath))) {
            for (CalculationHistory history : historyList) {
                // Write in format: expression|result|timestamp
                writer.write(history.getExpression() + "|" + 
                             history.getResult() + "|" + 
                             history.getTimestamp());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving history: " + e.getMessage());
        }
    }
    
    // Load history from file
    private void loadHistory() {
        File file = new File(historyFilePath);
        if (!file.exists()) {
            return; // No history file yet
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(historyFilePath))) {
            String line;
            historyList.clear();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    CalculationHistory history = new CalculationHistory(
                        parts[0], parts[1], parts[2]);
                    historyList.add(history);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading history: " + e.getMessage());
        }
    }
}

// Class to represent a calculation history item
class CalculationHistory {
    private String expression;
    private String result;
    private String timestamp;
    
    public CalculationHistory(String expression, String result, String timestamp) {
        this.expression = expression;
        this.result = result;
        this.timestamp = timestamp;
    }
    
    public String getExpression() {
        return expression;
    }
    
    public String getResult() {
        return result;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
}
