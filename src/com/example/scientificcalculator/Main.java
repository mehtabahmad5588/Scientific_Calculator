package com.example.scientificcalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Random;

/**
 * Working Java Swing scientific calculator inspired by the supplied calculator UI.
 * Compile:
 *   javac -d out src/com/example/scientificcalculator/Main.java
 * Run:
 *   java -cp out com.example.scientificcalculator.Main
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new CalculatorFrame().setVisible(true);
        });
    }
}

class CalculatorFrame extends JFrame {
    private final JTextField display = new JTextField("0");
    private final JLabel status = new JLabel("S   A   M   STO   RCL   STAT   CMPLX   MAT   VCT");
    private final JLabel angleLabel = new JLabel("RAD");
    private final JLabel modeLabel = new JLabel("PRO");

    private boolean shiftOn;
    private boolean alphaOn;
    private boolean degreeMode;
    private boolean scientificDisplay;
    private boolean justEvaluated;

    private double ans;
    private double memory;
    private final DecimalFormat normal = new DecimalFormat("0.###############");
    private final Random random = new Random();

    private final Color BG = new Color(10, 15, 25);
    private final Color DISPLAY_BG = new Color(216, 238, 236);
    private final Color FUNCTION_BG = new Color(55, 66, 86);
    private final Color NUMBER_BG = new Color(82, 94, 115);
    private final Color TOP_BG = new Color(78, 89, 108);
    private final Color SHIFT_BG = new Color(246, 190, 0);
    private final Color ALPHA_BG = new Color(126, 88, 172);
    private final Color ORANGE = new Color(245, 103, 15);

    CalculatorFrame() {
        setTitle("Scientific Calculator PRO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 450));
        setSize(400, 400);
        setLocationRelativeTo(null);
        buildUI();
        installKeyboard();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(10, 12, 14, 12));

        // Header
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel menu = new JLabel("☰");
        menu.setForeground(new Color(180, 190, 205));
        menu.setFont(new Font("SansSerif", Font.PLAIN, 28));

        styleBadge(modeLabel, new Color(128, 91, 170));
        modeLabel.setText("PRO");
        modeLabel.setToolTipText("Current calculator mode");

        left.add(menu);
        left.add(modeLabel);
        header.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        styleOutline(angleLabel);
        angleLabel.setToolTipText("Click to switch RAD / DEG");
        angleLabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { toggleAngle(); }
        });

        JLabel scan = new JLabel("▣ SCAN");
        styleOutline(scan);
        scan.setToolTipText("Scan is not required for desktop calculations");
        right.add(angleLabel);
        right.add(scan);
        header.add(right, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);

        status.setForeground(new Color(120, 130, 145));
        status.setFont(new Font("Monospaced", Font.BOLD, 12));
        center.add(status, BorderLayout.NORTH);

        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("Monospaced", Font.BOLD, 34));
        display.setForeground(new Color(20, 30, 38));
        display.setBackground(DISPLAY_BG);
        display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 130, 145), 2),
                new EmptyBorder(14, 12, 14, 12)
        ));
        center.add(display, BorderLayout.CENTER);

        JPanel keys = new JPanel(new GridLayout(0, 6, 6, 6));
        keys.setOpaque(false);

        // Top row
        addKey(keys, "SHIFT", "", SHIFT_BG, e -> toggleShift(), true);
        addKey(keys, "ALPHA", "", ALPHA_BG, e -> toggleAlpha(), true);
        addBlank(keys);
        addBlank(keys);
        addKey(keys, "MODE", "", TOP_BG, e -> chooseMode(), true);
        addKey(keys, "2nd", "", TOP_BG, e -> toggleShift(), true);

        // Scientific rows. Main text / SHIFT text.
        addKey(keys, "SOLVE\nCALC", "SOLVE", FUNCTION_BG, e -> solveSimple(), true);
        addKey(keys, "d/dx\n∫dx", "∫", FUNCTION_BG, e -> calculusInfo(), true);
        addKey(keys, "x⁻¹\nx!", "x!", FUNCTION_BG, e -> unaryFactorOrInverse(), true);
        addKey(keys, "Σ Π\nLogₓy", "log", FUNCTION_BG, e -> logBase(), true);
        addKey(keys, "10ˣ\nLog", "10^", FUNCTION_BG, e -> logOrTenPower(), true);
        addKey(keys, "eˣ\nLn", "e^", FUNCTION_BG, e -> lnOrExp(), true);

        addKey(keys, "x²", "x³", FUNCTION_BG, e -> powerSquareOrCube(), true);
        addKey(keys, "ʸ√x", "√", FUNCTION_BG, e -> rootOrSqrt(), true);
        addKey(keys, "xʸ", "x²", FUNCTION_BG, e -> append("^"), true);
        addKey(keys, "A\n(−)", "", FUNCTION_BG, e -> toggleSign(), true);
        addKey(keys, "FACT\n0!", "", FUNCTION_BG, e -> factorial(), true);
        addKey(keys, "|x|\nhyp", "hyp", FUNCTION_BG, e -> appendFunction("abs"), true);

        addKey(keys, "sin⁻¹\nsin", "sin⁻¹", FUNCTION_BG, e -> trig("sin"), true);
        addKey(keys, "cos⁻¹\ncos", "cos⁻¹", FUNCTION_BG, e -> trig("cos"), true);
        addKey(keys, "tan⁻¹\ntan", "tan⁻¹", FUNCTION_BG, e -> trig("tan"), true);
        addKey(keys, "STO\nRCL", "", FUNCTION_BG, e -> storeRecall(), true);
        addKey(keys, "←\nENG", "ENG", FUNCTION_BG, e -> backspaceOrEng(), true);
        addKey(keys, "%\n(", ")", FUNCTION_BG, e -> percentOrParen(), true);

        // Main number/operator rows
        addKey(keys, "x)", "", NUMBER_BG, e -> append(")"), false);
        addKey(keys, "S↔D", "", NUMBER_BG, e -> toggleFormat(), false);
        addKey(keys, "M+", "", NUMBER_BG, e -> memoryAdd(), false);
        addKey(keys, "a b/c\nx/y", "", NUMBER_BG, e -> fractionToDecimal(), false);
        addKey(keys, "√\nAbs", "", NUMBER_BG, e -> appendFunction("sqrt"), false);
        addKey(keys, "e\nπ", "", NUMBER_BG, e -> append("π"), false);

        addKey(keys, "CONST\n7", "", NUMBER_BG, e -> append("7"), false);
        addKey(keys, "CONV\n8", "", NUMBER_BG, e -> append("8"), false);
        addKey(keys, "CLR\n9", "", NUMBER_BG, e -> append("9"), false);
        addKey(keys, "DEL", "", ORANGE, e -> backspace(), false);
        addKey(keys, "AC", "", ORANGE, e -> clearAll(), false);
        addKey(keys, "M−\nM+", "", NUMBER_BG, e -> memoryMinusOrAdd(), false);

        addKey(keys, "MATRIX\n4", "", NUMBER_BG, e -> append("4"), false);
        addKey(keys, "VECTOR\n5", "", NUMBER_BG, e -> append("5"), false);
        addKey(keys, "FUNC\n6", "", NUMBER_BG, e -> append("6"), false);
        addKey(keys, "nPr\n×", "nPr", NUMBER_BG, e -> append("*"), false);
        addKey(keys, "nCr\n÷", "nCr", NUMBER_BG, e -> append("/"), false);
        addKey(keys, "=", "", NUMBER_BG, e -> evaluate(), false);

        addKey(keys, "STAT\n1", "", NUMBER_BG, e -> append("1"), false);
        addKey(keys, "CMPLX\n2", "", NUMBER_BG, e -> append("2"), false);
        addKey(keys, "BASE\n3", "", NUMBER_BG, e -> append("3"), false);
        addKey(keys, "Pol\n+", "", NUMBER_BG, e -> append("+"), false);
        addKey(keys, "Rec\n−", "", NUMBER_BG, e -> append("-"), false);
        addKey(keys, "(", "", NUMBER_BG, e -> append("("), false);

        addKey(keys, "RND\n0", "", NUMBER_BG, e -> append("0"), false);
        addKey(keys, "Ran#\n·", "", NUMBER_BG, e -> append("."), false);
        addKey(keys, "π\nExp", "", NUMBER_BG, e -> append("π"), false);
        addKey(keys, "DRG\nAns", "", NUMBER_BG, e -> append("Ans"), false);
        addKey(keys, "=", "", NUMBER_BG, e -> evaluate(), false);
        addKey(keys, "CE", "", NUMBER_BG, e -> clearEntry(), false);

        center.add(keys, BorderLayout.SOUTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void styleBadge(JLabel l, Color bg) {
        l.setOpaque(true);
        l.setBackground(bg);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setBorder(new EmptyBorder(5, 12, 5, 12));
    }

    private void styleOutline(JLabel l) {
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Monospaced", Font.BOLD, 13));
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150,160,175), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void addBlank(JPanel p) {
        JLabel b = new JLabel();
        b.setOpaque(false);
        p.add(b);
    }

    private void addKey(JPanel p, String main, String shift, Color bg, ActionListener action, boolean supportsShift) {
        String shown = main.replace("\n", "<br>");
        JButton b = new JButton("<html><center>" + shown + "</center></html>");
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, main.contains("\n") ? 13 : 15));
        b.setBorder(new EmptyBorder(8, 4, 8, 4));
        b.setToolTipText(shift.isEmpty() ? main.replace("\n", " / ") : "Normal: " + main.replace("\n", " / ") + "   |   SHIFT: " + shift);
        b.addActionListener(e -> {
            // Buttons that have a dedicated SHIFT action use their same action;
            // the action itself reads shiftOn where appropriate.
            action.actionPerformed(e);
        });
        p.add(b);
    }

    private void installKeyboard() {
        getRootPane().registerKeyboardAction(e -> clearAll(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> evaluate(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        String[][] keys = {
                {"0","0"},{"1","1"},{"2","2"},{"3","3"},{"4","4"},{"5","5"},{"6","6"},{"7","7"},{"8","8"},{"9","9"},
                {"+","+"},{"-","-"},{"*","*"},{"/","/"},{"^","^"},{".","."},{"(", "("},{")",")"}
        };
        for (String[] k : keys) {
            getRootPane().registerKeyboardAction(e -> append(k[1]),
                    KeyStroke.getKeyStroke(k[0].charAt(0)), JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
    }

    private void toggleShift() {
        shiftOn = !shiftOn;
        updateStatus();
    }

    private void toggleAlpha() {
        alphaOn = !alphaOn;
        updateStatus();
    }

    private void updateStatus() {
        status.setText((shiftOn ? "SHIFT*   " : "") + (alphaOn ? "ALPHA*   " : "")
                + "S   A   M   STO   RCL   STAT   CMPLX   MAT   VCT");
    }

    private void toggleAngle() {
        degreeMode = !degreeMode;
        angleLabel.setText(degreeMode ? "DEG" : "RAD");
    }

    private void chooseMode() {
        String[] options = {"PRO", "COMP", "STAT", "TABLE", "EQN"};
        String s = (String) JOptionPane.showInputDialog(this, "Select mode", "MODE",
                JOptionPane.PLAIN_MESSAGE, null, options, modeLabel.getText());
        if (s != null) modeLabel.setText(s);
    }

    private void append(String s) {
        if (display.getText().equals("Error") || justEvaluated) {
            display.setText("0");
            justEvaluated = false;
        }
        String current = display.getText();
        if (current.equals("0") && !s.equals(".") && !s.equals(")") && !s.equals("π") && !s.equals("Ans")) {
            display.setText(s);
        } else {
            display.setText(current + s);
        }
    }

    private void appendFunction(String fn) {
        if (display.getText().equals("Error") || justEvaluated) {
            display.setText("");
            justEvaluated = false;
        }
        String current = display.getText();
        if (current.equals("0")) current = "";
        display.setText(current + fn + "(");
    }

    private void append(String operator, boolean allowEmpty) {
        append(operator);
    }

    private void clearAll() {
        display.setText("0");
        justEvaluated = false;
    }

    private void clearEntry() {
        display.setText("0");
        justEvaluated = false;
    }

    private void backspace() {
        String s = display.getText();
        if (s.equals("Error") || s.length() <= 1) {
            clearAll();
            return;
        }
        display.setText(s.substring(0, s.length() - 1));
    }

    private void backspaceOrEng() {
        if (shiftOn) {
            engineeringNotation();
            shiftOn = false;
            updateStatus();
        } else {
            backspace();
        }
    }

    private void toggleSign() {
        try {
            double x = parseDisplay();
            display.setText(format(-x));
            justEvaluated = true;
        } catch (Exception e) {
            if (!display.getText().startsWith("-")) display.setText("-" + display.getText());
            else display.setText(display.getText().substring(1));
        }
    }

    private void powerSquareOrCube() {
        try {
            double x = parseDisplay();
            display.setText(format(Math.pow(x, shiftOn ? 3 : 2)));
            finishUnary();
        } catch (Exception e) {
            error();
        }
    }

    private void rootOrSqrt() {
        if (shiftOn) {
            appendFunction("sqrt");
        } else {
            append("^(");
            // A closing parenthesis can be entered normally; this makes y√x
            // usable as x^(1/y) via ordinary expression editing.
        }
        shiftOn = false;
        updateStatus();
    }

    private void unaryFactorOrInverse() {
        try {
            double x = parseDisplay();
            double r;
            if (shiftOn) {
                r = factorialValue(x);
            } else {
                if (x == 0) throw new ArithmeticException("Division by zero");
                r = 1.0 / x;
            }
            display.setText(format(r));
            finishUnary();
        } catch (Exception e) {
            error();
        }
    }

    private void factorial() {
        try {
            double x = parseDisplay();
            display.setText(format(factorialValue(x)));
            finishUnary();
        } catch (Exception e) {
            error();
        }
    }

    private double factorialValue(double x) {
        if (x < 0 || x != Math.rint(x) || x > 170)
            throw new ArithmeticException("Invalid factorial");
        double r = 1;
        for (int i = 2; i <= (int)x; i++) r *= i;
        return r;
    }

    private void percentOrParen() {
        if (shiftOn) {
            append(")");
            shiftOn = false;
            updateStatus();
        } else {
            append("%");
        }
    }

    private void toggleFormat() {
        try {
            double x = parseDisplay();
            scientificDisplay = !scientificDisplay;
            display.setText(format(x));
            justEvaluated = true;
        } catch (Exception e) {
            error();
        }
    }

    private void engineeringNotation() {
        try {
            double x = parseDisplay();
            if (x == 0) {
                display.setText("0");
                return;
            }
            int exponent = (int)(Math.floor(Math.log10(Math.abs(x)) / 3.0) * 3);
            double mantissa = x / Math.pow(10, exponent);
            display.setText(String.format(Locale.US, "%.12fE%d", mantissa, exponent));
            justEvaluated = true;
        } catch (Exception e) {
            error();
        }
    }

    private void fractionToDecimal() {
        try {
            display.setText(format(parseDisplay()));
            justEvaluated = true;
        } catch (Exception e) {
            error();
        }
    }

    private void memoryAdd() {
        try {
            memory += parseDisplay();
            display.setText(format(memory));
            justEvaluated = true;
        } catch (Exception e) { error(); }
    }

    private void memoryMinusOrAdd() {
        try {
            double x = parseDisplay();
            if (shiftOn) memory += x;
            else memory -= x;
            display.setText(format(memory));
            shiftOn = false;
            updateStatus();
            justEvaluated = true;
        } catch (Exception e) { error(); }
    }

    private void storeRecall() {
        try {
            if (shiftOn) {
                memory = parseDisplay();
            } else {
                display.setText(format(memory));
                justEvaluated = true;
            }
            shiftOn = false;
            updateStatus();
        } catch (Exception e) { error(); }
    }

    private void trig(String name) {
        try {
            double x = parseDisplay();
            double r;
            if (shiftOn) {
                if (name.equals("sin")) r = Math.asin(x);
                else if (name.equals("cos")) r = Math.acos(x);
                else r = Math.atan(x);
                if (degreeMode) r = Math.toDegrees(r);
            } else {
                double rad = degreeMode ? Math.toRadians(x) : x;
                if (name.equals("sin")) r = Math.sin(rad);
                else if (name.equals("cos")) r = Math.cos(rad);
                else r = Math.tan(rad);
            }
            display.setText(format(r));
            finishUnary();
        } catch (Exception e) { error(); }
    }

    private void logOrTenPower() {
        try {
            double x = parseDisplay();
            double r = shiftOn ? Math.pow(10, x) : Math.log10(x);
            display.setText(format(r));
            finishUnary();
        } catch (Exception e) { error(); }
    }

    private void lnOrExp() {
        try {
            double x = parseDisplay();
            double r = shiftOn ? Math.exp(x) : Math.log(x);
            display.setText(format(r));
            finishUnary();
        } catch (Exception e) { error(); }
    }

    private void logBase() {
        if (shiftOn) {
            try {
                double y = parseDisplay();
                Object inputObj = JOptionPane.showInputDialog(this, "Enter base:", "Log base", JOptionPane.PLAIN_MESSAGE, null, null, "10");
                if (inputObj == null) return;
                String input = inputObj.toString();
                double base = Double.parseDouble(input);
                if (base <= 0 || base == 1 || y <= 0) throw new ArithmeticException();
                display.setText(format(Math.log(y) / Math.log(base)));
                finishUnary();
            } catch (Exception e) { error(); }
        } else {
            appendFunction("log");
        }
        shiftOn = false;
        updateStatus();
    }

    private void solveSimple() {
        // Practical SOLVE button: evaluates the current expression.
        evaluate();
    }

    private void calculusInfo() {
        JOptionPane.showMessageDialog(this,
                "Use ordinary expressions for calculation.\n" +
                        "d/dx and ∫dx are UI placeholders; symbolic calculus is not implemented.",
                "Calculus", JOptionPane.INFORMATION_MESSAGE);
    }

    private void finishUnary() {
        justEvaluated = true;
        shiftOn = false;
        updateStatus();
    }

    private double parseDisplay() {
        return new ExpressionParser(display.getText(), ans, degreeMode).parse();
    }

    private void evaluate() {
        try {
            double r = parseDisplay();
            if (Double.isNaN(r) || Double.isInfinite(r)) throw new ArithmeticException();
            ans = r;
            display.setText(format(r));
            justEvaluated = true;
            shiftOn = false;
            updateStatus();
        } catch (Exception e) {
            error();
        }
    }

    private void error() {
        display.setText("Error");
        justEvaluated = true;
    }

    private String format(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return "Error";
        if (scientificDisplay && x != 0) return String.format(Locale.US, "%.12E", x);
        return normal.format(x);
    }
}

class ExpressionParser {
    private final String s;
    private final double ans;
    private final boolean degreeMode;
    private int pos;

    ExpressionParser(String raw, double ans, boolean degreeMode) {
        this.s = raw.replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .replace("π", "pi")
                .replace("Ans", "ans")
                .replace(" ", "");
        this.ans = ans;
        this.degreeMode = degreeMode;
    }

    double parse() {
        double v = expression();
        skip();
        if (pos != s.length()) throw new IllegalArgumentException("Unexpected input");
        return v;
    }

    private double expression() {
        double v = term();
        while (true) {
            skip();
            if (match('+')) v += term();
            else if (match('-')) v -= term();
            else return v;
        }
    }

    private double term() {
        double v = power();
        while (true) {
            skip();
            if (match('*')) v *= power();
            else if (match('/')) {
                double d = power();
                if (d == 0) throw new ArithmeticException("Division by zero");
                v /= d;
            } else if (match('%')) {
                v %= power();
            } else if (startsImplicitMultiplication()) {
                v *= power();
            } else return v;
        }
    }

    private boolean startsImplicitMultiplication() {
        if (pos >= s.length()) return false;
        char c = s.charAt(pos);
        return c == '(' || c == '.' || Character.isDigit(c) || Character.isLetter(c);
    }

    private double power() {
        double v = unary();
        skip();
        if (match('^')) v = Math.pow(v, power());
        return v;
    }

    private double unary() {
        skip();
        if (match('+')) return unary();
        if (match('-')) return -unary();
        return postfix();
    }

    private double postfix() {
        double v = primary();
        while (true) {
            skip();
            if (match('!')) {
                if (v < 0 || v != Math.rint(v) || v > 170)
                    throw new ArithmeticException("Invalid factorial");
                double r = 1;
                for (int i = 2; i <= (int)v; i++) r *= i;
                v = r;
            } else if (match('%')) {
                v /= 100.0;
            } else break;
        }
        return v;
    }

    private double primary() {
        skip();
        if (match('(')) {
            double v = expression();
            if (!match(')')) throw new IllegalArgumentException("Missing )");
            return v;
        }
        if (pos >= s.length()) throw new IllegalArgumentException("Missing value");

        char c = s.charAt(pos);
        if (Character.isDigit(c) || c == '.') return number();

        if (Character.isLetter(c)) {
            String name = identifier();
            if (name.equals("ans")) return ans;
            if (name.equals("pi")) return Math.PI;
            if (name.equals("e")) return Math.E;

            skip();
            if (match('(')) {
                double x = expression();
                if (!match(')')) throw new IllegalArgumentException("Missing )");
                return apply(name, x);
            }
            throw new IllegalArgumentException("Function requires ()");
        }

        throw new IllegalArgumentException("Invalid character");
    }

    private double number() {
        int start = pos;
        boolean dot = false;
        while (pos < s.length()) {
            char c = s.charAt(pos);
            if (Character.isDigit(c)) {
                pos++;
            } else if (c == '.' && !dot) {
                dot = true;
                pos++;
            } else {
                break;
            }
        }

        if (pos < s.length() && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
            pos++;
            if (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) pos++;
            int expStart = pos;
            while (pos < s.length() && Character.isDigit(s.charAt(pos))) pos++;
            if (expStart == pos) throw new IllegalArgumentException("Bad exponent");
        }

        return Double.parseDouble(s.substring(start, pos));
    }

    private String identifier() {
        int start = pos;
        while (pos < s.length() && Character.isLetter(s.charAt(pos))) pos++;
        return s.substring(start, pos).toLowerCase(Locale.US);
    }

    private double apply(String n, double x) {
        double rad = degreeMode ? Math.toRadians(x) : x;
        switch (n) {
            case "sin": return Math.sin(rad);
            case "cos": return Math.cos(rad);
            case "tan": return Math.tan(rad);
            case "asin": return degreeMode ? Math.toDegrees(Math.asin(x)) : Math.asin(x);
            case "acos": return degreeMode ? Math.toDegrees(Math.acos(x)) : Math.acos(x);
            case "atan": return degreeMode ? Math.toDegrees(Math.atan(x)) : Math.atan(x);
            case "sqrt": return Math.sqrt(x);
            case "abs": return Math.abs(x);
            case "log": return Math.log10(x);
            case "ln": return Math.log(x);
            case "exp": return Math.exp(x);
            case "hyp": return Math.hypot(x, 0);
            default: throw new IllegalArgumentException("Unknown function: " + n);
        }
    }

    private boolean match(char c) {
        if (pos < s.length() && s.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    private void skip() {
        while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
    }
}
