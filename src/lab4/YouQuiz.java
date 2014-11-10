/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Anwar Mohamed
 */
public class YouQuiz extends JFrame {

    public YouQuiz() throws IOException {
        initComponents();

        loadQuestions();
        switchToQuizMode();
    }

    private JSONArray jsonArray;
    private ArrayList<Question> questionsArray;

    private void loadQuestions() throws IOException {
        String jsonContents = readFile(getClass().getClassLoader().
                getResource("resources/questions.json").getFile().substring(1));
        jsonArray = new JSONArray(jsonContents);
        questionsArray = new ArrayList();

        Question question = null;
        JSONObject jsonQuestion;

        for (int i = 0; i < jsonArray.length(); ++i) {
            jsonQuestion = jsonArray.getJSONObject(i);

            switch (jsonQuestion.getInt("type")) {
                case Question.QUESTION_TYPE_MULTIPLE_CHOICE:
                    question = new MultipleChoiceQuestion();
                    break;
                case Question.QUESTION_TYPE_SHORT_ANSWER:
                    question = new ShortAnswerQuestion();
                    break;
            }

            if (question != null) {
                question.setQuestion(jsonQuestion.getString("question"));
                question.setAnswer(jsonQuestion.getString("answer"));
                questionsArray.add(question);
            }
        }

        Collections.shuffle(questionsArray, new Random(System.nanoTime()));
    }

    private void switchToQuizMode() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton backButton = new JButton(
                new ImageIcon(getClass().getClassLoader().
                        getResource("resources/backward.png")));
        toolbar.add(backButton);
        toolbar.setAlignmentX(0);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        JButton refreshButton = new JButton(
                new ImageIcon(getClass().getClassLoader().
                        getResource("resources/refresh.png")));
        toolbar.add(refreshButton);
        toolbar.setAlignmentX(0);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        panel.add(toolbar);
        add(panel, BorderLayout.NORTH);
    }

    private void initComponents() {

        menuBar = new JMenuBar();
        menuBarFile = new JMenu();
        menuBarExit = new JMenuItem();
        menuBarMode = new JMenu();
        menuBarEdit = new JRadioButtonMenuItem();
        menuBarQuiz = new JRadioButtonMenuItem();
        menuBarHelp = new JMenu();
        menuBarAbout = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("YouQuiz | Your Quiz Factory");
        setIconImage((new ImageIcon(getClass().getClassLoader().
                getResource("resources/icon.png"))).getImage());
        setMinimumSize(new Dimension(800, 500));
        setResizable(false);

        menuBarFile.setText("File");

        menuBarExit.setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F4,
                java.awt.event.InputEvent.ALT_MASK));
        menuBarExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuBarExit.setText("Exit");
        menuBarFile.add(menuBarExit);

        menuBar.add(menuBarFile);

        menuBarMode.setText("Mode");

        menuBarEdit.setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_E,
                java.awt.event.InputEvent.CTRL_MASK));
        menuBarEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Execute when button is pressed
                System.out.println("You clicked the button");
            }
        });
        menuBarEdit.setText("Edit Mode");

        menuBarQuiz.setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_Q,
                java.awt.event.InputEvent.CTRL_MASK));
        menuBarQuiz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Execute when button is pressed
                System.out.println("You clicked the button");
            }
        });
        menuBarQuiz.setSelected(true);
        menuBarQuiz.setText("Quiz Mode");

        ButtonGroup menuBarGroup = new ButtonGroup();
        menuBarGroup.add(menuBarEdit);
        menuBarGroup.add(menuBarQuiz);

        menuBarMode.add(menuBarEdit);
        menuBarMode.add(menuBarQuiz);

        menuBar.add(menuBarMode);

        menuBarHelp.setText("Help");

        menuBarAbout.setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_H,
                java.awt.event.InputEvent.CTRL_MASK));

        menuBarAbout.setText("About");
        menuBarAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JOptionPane.showMessageDialog(
                        null, "By Anwar Mohamed ~ 2491", "YouQuiz",
                        JOptionPane.INFORMATION_MESSAGE,
                        new ImageIcon(getClass().getClassLoader()
                                .getResource("resources/icon-48.png")));
            }
        });
        menuBarHelp.add(menuBarAbout);

        menuBar.add(menuBarHelp);
        setJMenuBar(menuBar);

        contentPanel = new ContentPanel();
        setContentPane(contentPanel);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info
                    : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(
                    YouQuiz.class.getName()).log(Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new YouQuiz().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(YouQuiz.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    // Variables declaration - do not modify
    private JMenuBar menuBar;
    private JMenuItem menuBarAbout;
    private JRadioButtonMenuItem menuBarEdit;
    private JMenuItem menuBarExit;
    private JMenu menuBarFile;
    private JMenu menuBarHelp;
    private JMenu menuBarMode;
    private JRadioButtonMenuItem menuBarQuiz;
    private ContentPanel contentPanel;
    // End of variables declaration

    class QuestionPanel extends JPanel {

        private Question question;

        public QuestionPanel(Question question) {
            this.question = question;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("Tahoma", Font.PLAIN, 20));
            g.setColor(Color.red);
            g.drawString(question.getQuestion(), 10, 20);
        }
    }

    class ContentPanel extends JPanel {

        Image bgimage = null;

        ContentPanel() {
            MediaTracker mt = new MediaTracker(this);
            bgimage = (new ImageIcon(getClass().getClassLoader()
                    .getResource("resources/back.png"))).getImage();
            mt.addImage(bgimage, 0);
            try {
                mt.waitForAll();
            } catch (InterruptedException e) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int x = (this.getWidth() - bgimage.getWidth(null)) / 2;
            int y = (this.getHeight() - bgimage.getHeight(null)) / 2;
            g2d.drawImage(bgimage, x, y, null);
        }
    }
}
