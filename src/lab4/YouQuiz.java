/*
 *
 *  Copyright (C) 2014  Anwar Mohamed <anwarelmakrahy[at]gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to Anwar Mohamed
 *  anwarelmakrahy[at]gmail.com
 *
 */

package lab4;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.gtranslate.Audio;
import com.gtranslate.Language;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
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
import javaFlacEncoder.FLACFileWriter;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javazoom.jl.decoder.JavaLayerException;
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

                    JSONArray jsonChoices = jsonQuestion.getJSONArray("choices");
                    for (int j = 0; j < jsonChoices.length(); ++j) {
                        ((MultipleChoiceQuestion) question).addChoice(
                                jsonChoices.get(j).toString());
                    }

                    break;
                case Question.QUESTION_TYPE_SHORT_ANSWER:
                    question = new ShortAnswerQuestion();
                    break;
                case Question.QUESTION_TYPE_TRUE_FALSE:
                    question = new TrueFalseQuestion();
                    break;
            }

            if (question != null) {
                question.setQuestion(jsonQuestion.getString("question"));
                question.setAnswer(jsonQuestion.getString("answer"));
                questionsArray.add(question);
            }
        }

        shuffleQuestions();
    }

    private JButton refreshButton = new JButton(
            new ImageIcon(getClass().getClassLoader().
                    getResource("resources/refresh.png")));
    private JButton backButton = new JButton(
            new ImageIcon(getClass().getClassLoader().
                    getResource("resources/backward.png")));
    private JButton forwardButton = new JButton(
            new ImageIcon(getClass().getClassLoader().
                    getResource("resources/forward.png")));
    private JButton speakButton = new JButton(
            new ImageIcon(getClass().getClassLoader().
                    getResource("resources/speaker.png")));
    private JButton micButton = new JButton(
            new ImageIcon(getClass().getClassLoader().
                    getResource("resources/mic.png")));
    private int questionIndex;

    private void switchToQuizMode() {

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        backButton.setFocusable(false);
        toolbar.add(backButton);
        toolbar.setAlignmentX(0);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                contentPanel.setQuestion(questionsArray.get(--questionIndex));
                forwardButton.setEnabled(true);

                if (questionIndex - 1 < 0) {
                    backButton.setEnabled(false);
                }

                updateAnswerForm();
            }
        });

        forwardButton.setFocusable(false);
        toolbar.add(forwardButton);
        toolbar.setAlignmentX(0);

        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                contentPanel.setQuestion(questionsArray.get(++questionIndex));
                backButton.setEnabled(true);

                if (questionsArray.size() == questionIndex + 1) {
                    forwardButton.setEnabled(false);
                }

                updateAnswerForm();
            }
        });

        speakButton.setFocusable(false);
        toolbar.add(speakButton);
        toolbar.setAlignmentX(0);

        speakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Audio audio = Audio.getInstance();
                InputStream sound = null;
                try {
                    sound = audio.getAudio(questionsArray.get(questionIndex)
                            .getQuestion(), Language.ENGLISH);
                } catch (IOException ex) {
                    Logger.getLogger(YouQuiz.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
                try {
                    audio.play(sound);
                } catch (JavaLayerException ex) {
                    Logger.getLogger(
                            YouQuiz.class.getName()).log(
                                    Level.SEVERE, null, ex);
                }
            }
        });

        refreshButton.setFocusable(false);
        toolbar.add(refreshButton);
        toolbar.setAlignmentX(0);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                shuffleQuestions();
                questionIndex = 0;
                backButton.setEnabled(false);

                if (questionsArray.size() == 1) {
                    forwardButton.setEnabled(false);
                } else {
                    forwardButton.setEnabled(true);
                }

                contentPanel.setQuestion(questionsArray.get(questionIndex));
                updateAnswerForm();
            }
        });

        micButton.setFocusable(false);
        toolbar.add(micButton);
        toolbar.setAlignmentX(0);

        micButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                GSpeechDuplex dup = new GSpeechDuplex();
                dup.addResponseListener(new GSpeechResponseListener() {
                    @Override
                    public void onResponse(GoogleResponse gr) {
                        if (questionsArray.get(questionIndex).checkAnswer(
                                gr.getResponse())) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Thats Great, Correct Answer",
                                    "Excellent",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Oops! '" + gr.getResponse()
                                    + "' is a wrong Answer. Its '"
                                    + questionsArray.get(questionIndex)
                                    .getAnswer().get(0) + "'",
                                    "Sorry",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                Microphone mic = new Microphone(FLACFileWriter.FLAC);
                File file = new File("CRAudioTest.flac");

                try {
                    mic.captureAudioToFile(file);
                    Thread.sleep(5000);
                    mic.close();

                    byte[] data = Files.readAllBytes(
                            mic.getAudioFile().toPath());
                    dup.recognize(
                            data, (int) mic.getAudioFormat().getSampleRate());
                    mic.getAudioFile().delete();

                } catch (LineUnavailableException | InterruptedException | 
                        IOException ex) {
                }

            }
        });

        contentPanel = new ContentPanel();

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(toolbar, BorderLayout.NORTH);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        contentPanel.add(contentPanel.questionLabel);

        add(contentPanel, BorderLayout.CENTER);

        if (questionsArray.isEmpty()) {
            refreshButton.setEnabled(false);
            backButton.setEnabled(false);
            forwardButton.setEnabled(false);
            speakButton.setEnabled(false);
        } else {
            questionIndex = 0;
            backButton.setEnabled(false);

            if (questionsArray.size() == 1) {
                forwardButton.setEnabled(false);
            }

            contentPanel.setQuestion(questionsArray.get(questionIndex));
            contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            contentPanel.add(contentPanel.answerPanel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            contentPanel.add(contentPanel.checkButton);
            updateAnswerForm();
        }

    }

    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements();
                buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    private void updateAnswerForm() {
        contentPanel.answerPanel.removeAll();

        final Question question = questionsArray.get(questionIndex);

        switch (question.type) {
            case Question.QUESTION_TYPE_MULTIPLE_CHOICE:
            case Question.QUESTION_TYPE_TRUE_FALSE:
                JRadioButton radioButton;
                final ButtonGroup radioGroup = new ButtonGroup();

                for (int i = 0; i < ((MultipleChoiceQuestion) question)
                        .getChoices().size(); ++i) {
                    radioButton = new JRadioButton(
                            ((MultipleChoiceQuestion) question)
                            .getChoices().get(i));
                    radioButton.setFont(new Font("Consolas", Font.PLAIN, 20));
                    radioGroup.add(radioButton);
                    radioButton.setFocusable(false);
                    contentPanel.answerPanel.add(radioButton);
                }

                for (ActionListener al
                        : contentPanel.checkButton.getActionListeners()) {
                    contentPanel.checkButton.removeActionListener(al);
                }

                contentPanel.checkButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (question.checkAnswer(getSelectedButtonText(
                                radioGroup))) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Thats Great, Correct Answer",
                                    "Excellent",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Oops! Wrong Answer. Its '"
                                    + question.getAnswer().get(0) + "'",
                                    "Sorry",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                break;
            case Question.QUESTION_TYPE_SHORT_ANSWER:
                final JTextField answerText = new JTextField(25);
                answerText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
                contentPanel.answerPanel.add(answerText);

                for (ActionListener al
                        : contentPanel.checkButton.getActionListeners()) {
                    contentPanel.checkButton.removeActionListener(al);
                }

                contentPanel.checkButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        if (question.checkAnswer(answerText.getText())) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Thats Great, Correct Answer",
                                    "Excellent",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Oops! Wrong Answer. Its '"
                                    + question.getAnswer().get(0) + "'",
                                    "Sorry",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                break;
        }

        contentPanel.answerPanel.invalidate();
    }

    private void shuffleQuestions() {
        Collections.shuffle(questionsArray, new Random(System.nanoTime()));
        for (int i = 0; i < questionsArray.size(); ++i) {
            if (questionsArray.get(i).type
                    == Question.QUESTION_TYPE_MULTIPLE_CHOICE
                    || questionsArray.get(i).type
                    == Question.QUESTION_TYPE_TRUE_FALSE) {
                ((MultipleChoiceQuestion) questionsArray
                        .get(i)).shuffleChoices();
            }
        }
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

    class ContentPanel extends JPanel {

        private Image bgimage = null;

        ContentPanel() {
            MediaTracker mt = new MediaTracker(this);
            bgimage = (new ImageIcon(getClass().getClassLoader()
                    .getResource("resources/back.png"))).getImage();
            mt.addImage(bgimage, 0);
            try {
                mt.waitForAll();
            } catch (InterruptedException e) {
            }

            setLayout(new VerticalLayout());
            answerPanel.setLayout(new VerticalLayout());
            checkButton.setFocusable(false);
        }

        private Question question = null;
        public JLabel questionLabel = new JLabel("", JLabel.CENTER);
        public JPanel answerPanel = new JPanel();
        public JButton checkButton = new JButton("Check My Answer");

        public void setQuestion(Question question) {
            this.question = question;
            questionLabel.setText(questionsArray.indexOf(question) + 1 + ". "
                    + question.getQuestion());
            questionLabel.setFont(new Font("Consolas", Font.BOLD, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int x = (this.getWidth() - bgimage.getWidth(null)) / 2;
            int y = (this.getHeight() - bgimage.getHeight(null)) / 2;
            g2d.drawImage(bgimage, x, y, null);

            if (question == null) {
                Font font = new Font("Consolas", Font.PLAIN, 28);
                g.setFont(font);
                g.drawString(
                        "No Questions Available",
                        (int) (getWidth() - g2d.getFontMetrics().
                        getStringBounds("No Questions Available", g2d)
                        .getWidth()) / 2, getHeight() / 2);
            }
        }
    }
}
