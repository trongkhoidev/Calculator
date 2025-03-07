import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * MainUI class represents the main window of the calculator application.
 * It manages the tabbed interface containing the calculator and history panels.
 */
public class MainUI extends JFrame {
    private final CalculatorLogic calculator;    // Core calculator logic
    private final CalculatorPanel calculatorPanel; // Main calculator interface
    private final HistoryPanel historyPanel;      // History view interface

    /**
     * Constructor initializes the main window and sets up the UI components
     */
    public MainUI() {
        calculator = new CalculatorLogic();
        calculatorPanel = new CalculatorPanel(calculator);
        historyPanel = new HistoryPanel(calculator);

        // Set up main window properties
        setTitle("Calculator Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 600));
        getContentPane().setBackground(Color.BLACK);

        // Create custom TabPane with custom UI
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            public void updateUI() {
                setUI(new BasicTabbedPaneUI() {
                    protected Color selectColor;

                    @Override
                    protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                        return 40; // Tab height
                    }

                    @Override
                    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                        return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 20; // Add spacing between tabs
                    }
                    
                    @Override
                    protected void installDefaults() {
                        super.installDefaults();
                        shadow = Color.BLACK;
                        darkShadow = Color.BLACK;
                        selectColor = new Color(40, 40, 40);
                        contentBorderInsets = new Insets(0, 0, 0, 0);
                        tabAreaInsets = new Insets(0, 4, 0, 4);
                        selectedTabPadInsets = new Insets(0, 0, 0, 0);
                    }

                    @Override
                    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                        // Don't paint content border
                    }

                    @Override
                    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                        // Don't paint tab border
                    }

                    @Override
                    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setColor(isSelected ? new Color(40, 40, 40) : Color.BLACK);
                        g2d.fillRect(x, y, w, h);
                        g2d.dispose();
                    }

                    @Override
                    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
                        // Don't paint focus indicator
                    }
                });
            }
        };

        // Set up TabPane appearance
        tabbedPane.setBackground(Color.BLACK);
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setBorder(null);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));

        // Create icons from image files
        ImageIcon calculatorIcon = new ImageIcon("Calculator.png");
        ImageIcon historyIcon = new ImageIcon("history.png");

        // Scale icons to appropriate size (24x24)
        Image calcImg = calculatorIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        Image histImg = historyIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        calculatorIcon = new ImageIcon(calcImg);
        historyIcon = new ImageIcon(histImg);

        // Add tabs with icons
        tabbedPane.addTab("", calculatorIcon, calculatorPanel);
        tabbedPane.addTab("", historyIcon, historyPanel);

        // Add TabPane to frame
        add(tabbedPane);

        setSize(400, 700);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }
            MainUI calculator = new MainUI();
            calculator.setVisible(true);
        });
    }
} 