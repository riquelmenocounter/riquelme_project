package tictactoe;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TicTacToe extends JFrame {
    private JButton[][] btn = new JButton[3][3];
    private boolean isXTurn = true;
    private JLabel status_label;
    private JButton btn_reset;
    private int xWon = 0;
    private int oWon = 0;
    private JLabel label_score;
    
    public TicTacToe() {
        setTitle("Tic Tac Toe Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));
        setResizable(false);
        
        JPanel panel_game = new JPanel();
        panel_game.setLayout(new GridLayout(3, 3, 6, 6));
        panel_game.setBackground(new Color(55, 55, 55));
        panel_game.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btn[i][j] = new JButton("");
                btn[i][j].setFont(new Font("Arial", Font.BOLD, 65));
                btn[i][j].setFocusPainted(false);
                btn[i][j].setBackground(Color.white);
                btn[i][j].setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85), 2));
                
                final int row = i;
                final int col = j;
                
                btn[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        btn_clicked(row, col);
                    }
                });
                
                btn[i][j].addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (btn[row][col].getText().equals("") && btn[row][col].isEnabled()) {
                            btn[row][col].setBackground(new Color(240, 240, 240));
                        }
                    }
                    
                    public void mouseExited(MouseEvent e) {
                        if (btn[row][col].getText().equals("") && btn[row][col].isEnabled()) {
                            btn[row][col].setBackground(Color.white);
                        }
                    }
                });
                
                panel_game.add(btn[i][j]);
            }
        }
        
        JPanel panel_control = new JPanel();
        panel_control.setLayout(new BorderLayout(20, 20));
        panel_control.setBackground(new Color(40, 40, 40));
        panel_control.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        status_label = new JLabel("Giliran: X", SwingConstants.CENTER);
        status_label.setFont(new Font("Arial", Font.BOLD, 25));
        status_label.setForeground(new Color(150, 250, 150));
        
        label_score = new JLabel("X: 0 | O: 0", SwingConstants.CENTER);
        label_score.setFont(new Font("Arial", Font.BOLD, 25));
        label_score.setForeground(Color.white);
        
        btn_reset = new JButton("Main Lagi");
        btn_reset.setFont(new Font("Arial", Font.BOLD, 25));
        btn_reset.setFocusPainted(false);
        btn_reset.setBackground(new Color(75, 135, 185));
        btn_reset.setForeground(Color.white);
        btn_reset.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        btn_reset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game_reset();
            }
        });
        
        btn_reset.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn_reset.setBackground(new Color(95, 155, 205));
            }
            
            public void mouseExited(MouseEvent e) {
                btn_reset.setBackground(new Color(75, 135, 185));
            }
        });
        
        JPanel panel_status = new JPanel();
        panel_status.setLayout(new GridLayout(2, 1, 10, 10));
        panel_status.setBackground(new Color(40, 40, 40));
        panel_status.add(status_label);
        panel_status.add(label_score);
        
        panel_control.add(panel_status, BorderLayout.CENTER);
        panel_control.add(btn_reset, BorderLayout.SOUTH);
        
        add(panel_game, BorderLayout.CENTER);
        add(panel_control, BorderLayout.SOUTH);
        
        pack();
        setSize(500, 600);
        setLocationRelativeTo(null);
    }
    
    private void btn_clicked(int row, int col) {
        if (!btn[row][col].getText().equals("")) {
            return;
        }
        
        if (isXTurn) {
            btn[row][col].setText("X");
            btn[row][col].setForeground(new Color(100, 200, 255));
        } else {
            btn[row][col].setText("O");
            btn[row][col].setForeground(new Color(255, 100, 100));
        }
        
        btn[row][col].setBackground(new Color(55, 55, 55));
        
        if (chk_won()) {
            String win = isXTurn ? "X" : "O";
            status_label.setText(win + " Menang!");
            status_label.setForeground(isXTurn ? new Color(100, 200, 255) : new Color(255, 100, 100));
            
            if (isXTurn) {
                xWon++;
            } else {
                oWon++;
            }
            score_update();
            btn_disable();
            return;
        }
        
        if (isBoardFull()) {
            status_label.setText("Seri!");
            status_label.setForeground(new Color(255, 250, 150));
            return;
        }
        
        isXTurn = !isXTurn;
        status_label.setText("Giliran: " + (isXTurn ? "X" : "O"));
        status_label.setForeground(isXTurn ? new Color(100, 200, 255) : new Color(255, 100, 100));
    }
    
    private boolean chk_won() {
        String symbol = isXTurn ? "X" : "O";
        
        // Cek baris
        for (int i = 0; i < 3; i++) {
            if (btn[i][0].getText().equals(symbol) && 
                btn[i][1].getText().equals(symbol) && 
                btn[i][2].getText().equals(symbol)) {
                highlightWinningLine(i, 0, i, 1, i, 2);
                return true;
            }
        }
        
        // Cek kolom
        for (int i = 0; i < 3; i++) {
            if (btn[0][i].getText().equals(symbol) && 
                btn[1][i].getText().equals(symbol) && 
                btn[2][i].getText().equals(symbol)) {
                highlightWinningLine(0, i, 1, i, 2, i);
                return true;
            }
        }
        
        // Cek diagonal kiri atas ke kanan bawah
        if (btn[0][0].getText().equals(symbol) && 
            btn[1][1].getText().equals(symbol) && 
            btn[2][2].getText().equals(symbol)) {
            highlightWinningLine(0, 0, 1, 1, 2, 2);
            return true;
        }
        
        // Cek diagonal kanan atas ke kiri bawah
        if (btn[0][2].getText().equals(symbol) && 
            btn[1][1].getText().equals(symbol) && 
            btn[2][0].getText().equals(symbol)) {
            highlightWinningLine(0, 2, 1, 1, 2, 0);
            return true;
        }
        
        return false;
    }
        
    private void highlightWinningLine(int r1, int c1, int r2, int c2, int r3, int c3) {
        Color color_won = new Color(55, 185, 105);
        btn[r1][c1].setBackground(color_won);
        btn[r2][c2].setBackground(color_won);
        btn[r3][c3].setBackground(color_won);
    }
    
    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (btn[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void btn_disable() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btn[i][j].setEnabled(false);
            }
        }
    }
    
    private void game_reset() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btn[i][j].setText("");
                btn[i][j].setEnabled(true);
                btn[i][j].setBackground(Color.white);
            }
        }
        isXTurn = true;
        status_label.setText("Giliran: X");
        status_label.setForeground(new Color(100, 200, 255));
    }
    
    private void score_update() {
        label_score.setText("X: " + xWon + " | O: " + oWon);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TicTacToe game = new TicTacToe();
                game.setVisible(true);
            }
        });
    }
}