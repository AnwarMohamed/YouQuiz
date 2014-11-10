/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab4;

import java.util.ArrayList;

/**
 *
 * @author Anwar Mohamed
 */
public abstract class Question {

    protected String question;
    protected ArrayList<String> answer;
    protected int type;
    protected boolean answered;

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public Question() {
        answer = new ArrayList();
        type = QUESTION_TYPE_ABSTRACT;
        answered = false;
    }

    public final static int QUESTION_TYPE_ABSTRACT = 0;
    public final static int QUESTION_TYPE_SHORT_ANSWER = 1;
    public final static int QUESTION_TYPE_MULTIPLE_CHOICE = 2;

    public int getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer.add(answer);
    }

    public void setAnswer(ArrayList<String> answer) {
        this.answer.addAll(answer);
    }

    public boolean checkAnswer(String answer) {
        return this.answer.contains(answer);
    }
}
