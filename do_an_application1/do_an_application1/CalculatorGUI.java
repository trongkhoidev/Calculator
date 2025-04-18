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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
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

    private Color colorDisableStats = Color.lightGray, colorEnnabaleStar = Color.black;
    private Color operatorButtonColor = new Color(255, 153, 0); // Orange color for operator buttons
    private Color numberButtonColor = new Color(51, 51, 51); // Dark gray for number buttons
    private Color functionButtonColor = new Color(192, 192, 192); // Light gray for function buttons
    private Color backgroundColor = Color.BLACK; // Black background
    private Balan balan;

    public CalculatorGUI() {
        frame = new JFrame("Calculator Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBounds(xframeShow, yframeShow, frameWidth, frameHeight);
        frame.setJMenuBar(createMenuBar());
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
            frameWidth = 460;
            frameHeight = 440;
            frame.setTitle("Calculator - Advanced");
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
        tfDisplay.requestFocus();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
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
        // mainPanel

        mainPanel.setVisible(true);
        return mainPanel;
    }

    // ----------------------- main panel content ----------------------- //
    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();

        // menu mode
        JMenu mm = createMenu("Mode", KeyEvent.VK_M);
        mm.add(createMenuItem("Basic", KeyEvent.VK_B));
        mm.add(createMenuItem("Advanced", KeyEvent.VK_A));
        mm.add(createMenuItem("Exit", KeyEvent.VK_X));
        mb.add(mm);

        // Removed Help menu
        return mb;
    }

    // create Display Panel
    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        
        if (mode == 1) {
            lbStats = new JLabel("sto");
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
        tfDisplay.setForeground(Color.WHITE);
        tfDisplay.setBackground(backgroundColor);
        tfDisplay.addKeyListener(this);// bac su kien khi an phim
        panel.add(tfDisplay, BorderLayout.CENTER);

        lbAns = new JLabel("0");
        Font fontAns = lbAns.getFont().deriveFont(Font.PLAIN, 35f);
        lbAns.setFont(fontAns);
        lbAns.setHorizontalAlignment(JLabel.RIGHT);
        lbAns.setBackground(backgroundColor);
        lbAns.setForeground(Color.WHITE);
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
            btnArr[i].setForeground(Color.WHITE);
            
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

        // Calculate the number of columns and rows for a grid layout
        int numButtons = lbButton.length;
        int columns = 8; // 8 columns to match the layout in the image
        int rows = (int) Math.ceil((double) numButtons / columns);
        
        // panel bottom with grid layout
        JPanel panel2 = new JPanel(new GridLayout(rows, columns, 8, 8));
        panel2.setBackground(backgroundColor);
        
        btnArr = new JButton[lbButton.length];
        
        for (int i = 0; i < lbButton.length; i++) {
            btnArr[i] = createButton(lbButton[i]);
            
            // Set button appearance
            btnArr[i].setFocusPainted(false);
            btnArr[i].setBorderPainted(false);
            btnArr[i].setFont(new Font("Arial", Font.BOLD, 16));
            btnArr[i].setForeground(Color.WHITE);
            
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
            
            panel2.add(btnArr[i]);
        }

        JPanel buttonAdvancedPanel = new JPanel(new BorderLayout());
        buttonAdvancedPanel.setBackground(backgroundColor);
        buttonAdvancedPanel.add(panel1, BorderLayout.NORTH);
        buttonAdvancedPanel.add(panel2, BorderLayout.CENTER);
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
        rad.setForeground(Color.WHITE);
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
            String s[] = {"C", "CE", "←", "(", ")", "!", "log", "ln", 
                "7", "8", "9", "/", "√", "x²", "sin", "π", 
                "4", "5", "6", "*", "cos", "e", "tan", "1/x", 
                "1", "2", "3", "–", "Ans", "°", "%", "+", 
                "0", "•", "=", ""};
            lbButton = s;

            String s1[] = {"", "", "", "(", ")", "!", "log ", "ln ", 
                "7", "8", "9", "/", "√", "²", "sin ", "π", 
                "4", "5", "6", "*", "cos ", "e", "tan ", "1/", 
                "1", "2", "3", "-", "Ans", "°", "%", "+", 
                "0", ".", "", ""};
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
        ans = balan.valueMath(tfDisplay.getText());
        if (!balan.isError()) {
            balan.var[0] = ans;
            lbAns.setText(balan.numberToString(ans, balan.getRadix(),
                    balan.getSizeRound()));
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
        if (command.equals("Exit")) {
            System.exit(0);
        }

        // Xử lý nút C (Clear All)
        if (evt.getSource() == btnArr[0]) { // btnArr[0] là vị trí nút "C"
            resetValue();
            actionCE();
            return;
        }

        // Xử lý nút CE (Clear Entry)
        if (evt.getSource() == btnArr[1]) { // btnArr[1] là vị trí nút "CE"
            actionCE();
            return;
        }

        // Xử lý nút `←` (Backspace)
        if (evt.getSource() == btnArr[2]) { // btnArr[2] là vị trí nút "←"
            actionDel();
            return;
        }

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
}
