package riquelme_quiz;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Riquelme_quiz extends JFrame {
    private ArrayList<Question> quest;
    private int current_quest_index = 0;
    private int total_pts = 0;
    
    private JLabel quest_label;
    private JLabel quest_num_label;
    private JRadioButton[] btn_option;
    private ButtonGroup group_option;
    private JButton btn_next;
    private JButton btn_submit;
    private JProgressBar bar_progress;
    private JPanel main_panel;
    
    public Riquelme_quiz() {
        setTitle("Fakta Unik Dunia");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        quest_initialize();
        UI_initialize();
        quest_display();
    }
    
    private void quest_initialize() {
        quest = new ArrayList<>();
        
        quest.add(new Question("Negara dengan jumlah pulau terbanyak di dunia adalah negara?", 
                new String[]{"A. Indonesia", "B. Inggris", "C. Amerika Serikat", "D. Australia", "E. Swedia"}, 4
                ));
        
        quest.add(new Question("Salah satu pahlawan Finlandia adalah?", 
                new String[]{"A. Simo Hayha", "B. Ir. Soekarno", "C. Adolf Hitler", "D. Ghadaffi", "E. Victor Gyokeres"}, 0
                ));
        
        quest.add(new Question("Negara penghasil beras ketan terbesar di dunia adalah?", 
                new String[]{"A. Kamboja", "B. Laos", "C. Indonesia", "D. Tiongkok", "E. Vietnam"}, 3
                ));
    }
    
    private void UI_initialize() {
        main_panel = new JPanel();
        main_panel.setLayout(new BorderLayout(20, 20));
        main_panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        main_panel.setBackground(new Color(240, 240, 250));
        
        JPanel panel_header = new JPanel(new BorderLayout());
        panel_header.setBackground(new Color(75, 135, 185));
        panel_header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label_title = new JLabel("Pertanyaan", SwingConstants.CENTER);
        label_title.setFont(new Font("Arial", Font.BOLD, 26));
        label_title.setForeground(Color.WHITE);
        
        quest_num_label = new JLabel("", SwingConstants.LEFT);
        quest_num_label.setFont(new Font("Arial", Font.BOLD, 13));
        quest_num_label.setForeground(Color.WHITE);
        
        panel_header.add(label_title, BorderLayout.CENTER);
        panel_header.add(quest_num_label, BorderLayout.WEST);
        
        bar_progress = new JProgressBar(0, quest.size());
        bar_progress.setValue(0);
        bar_progress.setStringPainted(true);
        bar_progress.setForeground(new Color(75, 135, 185));
        
        JPanel panel_quest = new JPanel(new BorderLayout());
        panel_quest.setBackground(Color.WHITE);
        panel_quest.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        quest_label = new JLabel();
        quest_label.setFont(new Font("Arial", Font.BOLD, 18));
        quest_label.setForeground(new Color(60, 60, 60));
        panel_quest.add(quest_label, BorderLayout.NORTH);
        
        JPanel panel_option = new JPanel();
        panel_option.setLayout(new GridLayout(5, 1, 10, 10));
        panel_option.setBackground(Color.WHITE);
        panel_option.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        btn_option = new JRadioButton[5];
        group_option = new ButtonGroup();
        
        for (int i = 0; i < 5; i++) {
            btn_option[i] = new JRadioButton();
            btn_option[i].setFont(new Font("Arial", Font.BOLD, 14));
            btn_option[i].setBackground(Color.WHITE);
            btn_option[i].setFocusPainted(false);
            group_option.add(btn_option[i]);
            panel_option.add(btn_option[i]);
        }
        
        panel_quest.add(panel_option, BorderLayout.CENTER);
        
        JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        panel_btn.setBackground(new Color(240, 240, 250));
        
        btn_next = new JButton("Next");
        btn_next.setFont(new Font("Arial", Font.BOLD, 16));
        btn_next.setBackground(new Color(80, 140, 190));
        btn_next.setForeground(Color.WHITE);
        btn_next.setFocusPainted(false);
        btn_next.setPreferredSize(new Dimension(100, 35));
        btn_next.addActionListener(e -> next_quest());
        
        btn_submit = new JButton("Submit");
        btn_submit.setFont(new Font("Arial", Font.BOLD, 16));
        btn_submit.setBackground(new Color(65, 165, 85));
        btn_submit.setForeground(Color.WHITE);
        btn_submit.setFocusPainted(false);
        btn_submit.setPreferredSize(new Dimension(100, 35));
        btn_submit.addActionListener(e -> quiz_submit());
        btn_submit.setVisible(false);
        
        panel_btn.add(btn_next);
        panel_btn.add(btn_submit);
        
        main_panel.add(panel_header, BorderLayout.NORTH);
        main_panel.add(bar_progress, BorderLayout.AFTER_LINE_ENDS);
        main_panel.add(panel_quest, BorderLayout.CENTER);
        main_panel.add(panel_btn, BorderLayout.SOUTH);
        
        add(main_panel);
    }
    
    private void quest_display() {
        if (current_quest_index < quest.size()) {
            Question q = quest.get(current_quest_index);
            quest_num_label.setText("Nomor " + (current_quest_index + 1));
            quest_label.setText("<html>" + q.get_quest() + "</html>");
            
            String[] option = q.get_option();
            for (int i = 0; i < 5; i++) {
                btn_option[i].setText(option[i]);
                btn_option[i].setSelected(false);
            }
            
            bar_progress.setValue(current_quest_index);
            
            if (current_quest_index == quest.size() - 1) {
                btn_next.setVisible(false);
                btn_submit.setVisible(true);
            }
        }
    }
    
    private void next_quest() {
        if (check_answer()) {
            total_pts++;
        }
        
        current_quest_index++;
        quest_display();
    }
    
    private boolean check_answer() {
        for (int i = 0; i < 5; i++) {
            if (btn_option[i].isSelected()) {
                return i == quest.get(current_quest_index).get_correct_answer();
            }
        }
        return false;
    }
    
    private void quiz_submit() {
        if (check_answer()) {
            total_pts++;
        }
        
        bar_progress.setValue(quest.size());
        
        double percentage = (total_pts * 100.0) / quest.size();
        String grade;
        
        if (percentage == 100) grade = "A - Perfect!";
        else if (percentage >= 80) grade = "A - Excellent!";
        else if (percentage >= 70) grade = "B - Not Bad, but OK :)";
        else if (percentage >= 60) grade = "C - Don't cry, let's try again!";
        else grade = "D - Need try again!";
        
        String msg = String.format("Quiz Completed!\n\n" + "Your Score: %d / %d\n" 
                + "Percentage: %.1f%%\n" + "Grade: %s", total_pts, quest.size(), percentage, grade);
        
        JOptionPane.showMessageDialog(this, msg, "Quiz Results", JOptionPane.INFORMATION_MESSAGE);
        
        int response = JOptionPane.showConfirmDialog(
                this, "Would you like to retry this quiz?", "Retry Quiz", JOptionPane.YES_NO_OPTION
        );
        
        if (response == JOptionPane.YES_OPTION) {
            reset_quiz();
        } else {
            System.exit(0);
        }
    }
    
    private void reset_quiz() {
        current_quest_index = 0;
        total_pts = 0;
        btn_next.setVisible(true);
        btn_submit.setVisible(false);
        quest_display();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Riquelme_quiz app = new Riquelme_quiz();
            app.setVisible(true);
        });
    }
    
    class Question {
        private String quest;
        private String[] options;
        private int correct_answer;
        
        public Question(String quest, String[] options, int correct_answer) {
            this.quest = quest;
            this.options = options;
            this.correct_answer = correct_answer;
        }
        
        public String get_quest() {
            return quest;
        }
        
        public String[] get_option() {
            return options;
        }
        
        public int get_correct_answer() {
            return correct_answer;
        }
    }
}