import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║          Tic-Tac-Toe  —  Java Swing                     ║
 * ║  2-Player & vs AI (Minimax + Alpha-Beta Pruning)         ║
 * ║  Score tracking · Ghost preview · Win highlighting       ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * Requires: Java 8 or later
 * Compile : javac TicTacToe.java
 * Run     : java TicTacToe
 */
public class TicTacToe extends JFrame {

    // ── Layout ────────────────────────────────────────────────────────
    private static final int CELL = 110;   // cell side in pixels
    private static final int GAP  =   8;   // gap between cells

    // ── Colour palette (Catppuccin-Mocha inspired) ────────────────────
    private static final Color C_BG      = new Color( 17,  17,  27);
    private static final Color C_SURFACE = new Color( 30,  30,  46);
    private static final Color C_OVERLAY = new Color( 49,  50,  68);
    private static final Color C_HOVER   = new Color( 69,  71,  90);
    private static final Color C_X       = new Color(243, 139, 168);  // pink
    private static final Color C_O       = new Color(137, 220, 235);  // cyan
    private static final Color C_WIN     = new Color(166, 227, 161);  // green
    private static final Color C_TEXT    = new Color(205, 214, 244);
    private static final Color C_DIM     = new Color(108, 112, 134);
    private static final Color C_ACCENT  = new Color(180, 190, 254);  // lavender

    // ── Game state ────────────────────────────────────────────────────
    private final char[][] board = new char[3][3];
    private char    turn   = 'X';
    private boolean over   = false;
    private boolean aiMode = false;
    private int     sX, sO, sD;           // session scores

    // ── Swing refs ────────────────────────────────────────────────────
    private final Cell[][]  cells   = new Cell[3][3];
    private JLabel   statusLbl;
    private ScoreBox boxX, boxO, boxD;
    private JButton  modeBtn;

    // ══════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToe::new);
    }

    public TicTacToe() {
        super("Tic-Tac-Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(C_BG);
        resetState();
        buildUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────
    //  State helpers
    // ─────────────────────────────────────────────────────────────────
    private void resetState() {
        for (char[] row : board) Arrays.fill(row, ' ');
        turn = 'X';
        over = false;
    }

    /** Returns winning player char, or ' ' if none. Works on any board. */
    private char winner(char[][] b) {
        for (int r = 0; r < 3; r++)
            if (b[r][0] != ' ' && b[r][0] == b[r][1] && b[r][1] == b[r][2]) return b[r][0];
        for (int c = 0; c < 3; c++)
            if (b[0][c] != ' ' && b[0][c] == b[1][c] && b[1][c] == b[2][c]) return b[0][c];
        if (b[0][0] != ' ' && b[0][0] == b[1][1] && b[1][1] == b[2][2]) return b[0][0];
        if (b[0][2] != ' ' && b[0][2] == b[1][1] && b[1][1] == b[2][0]) return b[0][2];
        return ' ';
    }

    /** Returns {r0,c0,r1,c1,r2,c2} of the winning line on main board, or null. */
    private int[] winLine() {
        for (int r = 0; r < 3; r++)
            if (board[r][0] != ' ' && board[r][0] == board[r][1] && board[r][1] == board[r][2])
                return new int[]{r,0,r,1,r,2};
        for (int c = 0; c < 3; c++)
            if (board[0][c] != ' ' && board[0][c] == board[1][c] && board[1][c] == board[2][c])
                return new int[]{0,c,1,c,2,c};
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2])
            return new int[]{0,0,1,1,2,2};
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0])
            return new int[]{0,2,1,1,2,0};
        return null;
    }

    private boolean full(char[][] b) {
        for (char[] row : b) for (char v : row) if (v == ' ') return false;
        return true;
    }

    // ─────────────────────────────────────────────────────────────────
    //  UI Construction
    // ─────────────────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildNorth(),  BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildSouth(),  BorderLayout.SOUTH);
    }

    private JPanel buildNorth() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(22, 20, 8, 20));

        JLabel title = new JLabel("TIC  \u00B7  TAC  \u00B7  TOE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(C_ACCENT);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JPanel scoreRow = new JPanel(new GridLayout(1, 3, 8, 0));
        scoreRow.setBackground(C_BG);
        scoreRow.setMaximumSize(new Dimension(370, 64));
        scoreRow.setAlignmentX(CENTER_ALIGNMENT);
        boxX = new ScoreBox("X",    C_X);
        boxD = new ScoreBox("Draw", C_DIM);
        boxO = new ScoreBox("O",    C_O);
        scoreRow.add(boxX);
        scoreRow.add(boxD);
        scoreRow.add(boxO);

        p.add(title);
        p.add(Box.createVerticalStrut(12));
        p.add(scoreRow);
        return p;
    }

    private JPanel buildCenter() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(C_BG);
        wrap.setBorder(new EmptyBorder(8, 20, 8, 20));

        int side = CELL * 3 + GAP * 2;
        JPanel grid = new JPanel(new GridLayout(3, 3, GAP, GAP));
        grid.setBackground(C_BG);
        grid.setPreferredSize(new Dimension(side, side));

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                cells[r][c] = new Cell(r, c);
                grid.add(cells[r][c]);
            }
        wrap.add(grid);
        return wrap;
    }

    private JPanel buildSouth() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_BG);
        p.setBorder(new EmptyBorder(8, 20, 22, 20));

        statusLbl = new JLabel("Player X's Turn");
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLbl.setForeground(C_X);
        statusLbl.setAlignmentX(CENTER_ALIGNMENT);

        JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0));
        btns.setBackground(C_BG);
        btns.setMaximumSize(new Dimension(370, 42));
        btns.setAlignmentX(CENTER_ALIGNMENT);

        JButton newBtn = roundBtn("New Game", C_ACCENT, C_BG);
        modeBtn        = roundBtn("vs Human", C_OVERLAY, C_TEXT);
        newBtn.addActionListener(e -> newGame());
        modeBtn.addActionListener(e -> toggleMode());

        btns.add(newBtn);
        btns.add(modeBtn);

        p.add(statusLbl);
        p.add(Box.createVerticalStrut(12));
        p.add(btns);
        return p;
    }

    /** Pill-shaped button with custom paint (no default borders). */
    private JButton roundBtn(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color col = bg;
                if (getModel().isRollover()) col = col.brighter();
                if (getModel().isPressed())  col = col.darker();
                g2.setColor(col);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t,
                    (getWidth()  - fm.stringWidth(t)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(130, 40));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ─────────────────────────────────────────────────────────────────
    //  Game Flow
    // ─────────────────────────────────────────────────────────────────
    void onClick(int r, int c) {
        if (over || board[r][c] != ' ') return;
        place(r, c, turn);
        // After human move, trigger AI if applicable
        if (!over && aiMode && turn == 'O') {
            boardEnabled(false);
            new Timer(320, e -> {
                aiMove();
                boardEnabled(true);
                ((Timer) e.getSource()).stop();
            }).start();
        }
    }

    private void place(int r, int c, char p) {
        board[r][c] = p;
        cells[r][c].mark(p);

        char w = winner(board);
        if (w != ' ') {
            over = true;
            int[] line = winLine();
            if (line != null)
                for (int i = 0; i < 6; i += 2)
                    cells[line[i]][line[i + 1]].setWin(true);
            if (w == 'X') { sX++; boxX.set(sX); }
            else           { sO++; boxO.set(sO); }
            String who = (aiMode && w == 'O') ? "AI (O)" : "Player " + w;
            setStatus(who + " Wins! \uD83C\uDF89", C_WIN);

        } else if (full(board)) {
            over = true;
            sD++; boxD.set(sD);
            setStatus("It's a Draw! \uD83E\uDD1D", C_DIM);

        } else {
            turn = (p == 'X') ? 'O' : 'X';
            String who = (aiMode && turn == 'O') ? "AI" : "Player " + turn;
            setStatus(who + "'s Turn", turn == 'X' ? C_X : C_O);
        }
    }

    private void newGame() {
        resetState();
        for (Cell[] row : cells) for (Cell cl : row) cl.reset();
        setStatus("Player X's Turn", C_X);
        boardEnabled(true);
    }

    private void toggleMode() {
        aiMode = !aiMode;
        modeBtn.setText(aiMode ? "vs AI  \u2713" : "vs Human");
        modeBtn.repaint();
        newGame();
    }

    private void boardEnabled(boolean en) {
        for (Cell[] row : cells) for (Cell cl : row) cl.setEnabled(en);
    }

    private void setStatus(String text, Color c) {
        statusLbl.setText(text);
        statusLbl.setForeground(c);
    }

    // ─────────────────────────────────────────────────────────────────
    //  AI — Minimax with Alpha-Beta Pruning  (O = maximiser)
    // ─────────────────────────────────────────────────────────────────
    private void aiMove() {
        int[] res = minimax(board, 0, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (res[1] >= 0) place(res[1], res[2], 'O');
    }

    /**
     * @return {score, bestRow, bestCol}
     * isMax = true  →  AI's turn  (O, maximising)
     * isMax = false →  human's turn (X, minimising)
     */
    private int[] minimax(char[][] b, int depth, boolean isMax, int alpha, int beta) {
        char w = winner(b);
        if (w == 'O') return new int[]{ 10 - depth, -1, -1 };
        if (w == 'X') return new int[]{ depth - 10, -1, -1 };
        if (full(b))  return new int[]{ 0, -1, -1 };

        int best = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int br = -1, bc = -1;

        outer:
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (b[r][c] != ' ') continue;
                b[r][c] = isMax ? 'O' : 'X';
                int s = minimax(b, depth + 1, !isMax, alpha, beta)[0];
                b[r][c] = ' ';
                if (isMax) {
                    if (s > best) { best = s; br = r; bc = c; }
                    alpha = Math.max(alpha, best);
                } else {
                    if (s < best) { best = s; br = r; bc = c; }
                    beta = Math.min(beta, best);
                }
                if (beta <= alpha) break outer;   // prune
            }
        }
        return new int[]{ best, br, bc };
    }

    // ─────────────────────────────────────────────────────────────────
    //  Inner class: Cell — clickable game cell with custom rendering
    // ─────────────────────────────────────────────────────────────────
    private class Cell extends JPanel {
        private final int r, c;
        private char    val = ' ';
        private boolean win = false;
        private boolean hov = false;

        Cell(int r, int c) {
            this.r = r;
            this.c = c;
            setPreferredSize(new Dimension(CELL, CELL));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { onClick(r, c); }
                @Override public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            });
        }

        void mark(char v)      { val = v;  repaint(); }
        void setWin(boolean w) { win = w;  repaint(); }
        void reset()           { val = ' '; win = false; hov = false; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth(), h = getHeight();
            boolean canHover = hov && val == ' ' && !over && isEnabled();

            // ── Background
            Color bg = win
                ? new Color(166, 227, 161, 55)
                : (canHover ? C_HOVER : C_OVERLAY);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, 14, 14);

            // ── Win border
            if (win) {
                g2.setColor(C_WIN);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, 14, 14);
            }

            // ── Symbol
            int pad  = Math.max(w / 5, 18);
            int sw   = w - 2 * pad;
            int sh   = h - 2 * pad;
            Stroke thick = new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            if (val == 'X') {
                g2.setColor(win ? C_WIN : C_X);
                g2.setStroke(thick);
                g2.drawLine(pad,      pad,      pad + sw, pad + sh);
                g2.drawLine(pad + sw, pad,      pad,      pad + sh);

            } else if (val == 'O') {
                g2.setColor(win ? C_WIN : C_O);
                g2.setStroke(thick);
                g2.drawOval(pad, pad, sw, sh);

            } else if (canHover) {
                // Ghost preview of next move
                Color hint = turn == 'X' ? C_X : C_O;
                g2.setColor(new Color(hint.getRed(), hint.getGreen(), hint.getBlue(), 40));
                g2.setStroke(thick);
                if (turn == 'X') {
                    g2.drawLine(pad,      pad,      pad + sw, pad + sh);
                    g2.drawLine(pad + sw, pad,      pad,      pad + sh);
                } else {
                    g2.drawOval(pad, pad, sw, sh);
                }
            }
            g2.dispose();
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Inner class: ScoreBox — compact score card per player
    // ─────────────────────────────────────────────────────────────────
    private class ScoreBox extends JPanel {
        private final JLabel num;

        ScoreBox(String label, Color clr) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setBorder(new EmptyBorder(6, 8, 6, 8));

            num = new JLabel("0");
            num.setFont(new Font("Segoe UI", Font.BOLD, 22));
            num.setForeground(C_TEXT);
            num.setAlignmentX(CENTER_ALIGNMENT);

            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(clr);
            lbl.setAlignmentX(CENTER_ALIGNMENT);

            add(Box.createVerticalGlue());
            add(num);
            add(lbl);
            add(Box.createVerticalGlue());
        }

        void set(int v) { num.setText(String.valueOf(v)); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(C_SURFACE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
        }
    }
}
