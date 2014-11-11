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
    public final static int QUESTION_TYPE_TRUE_FALSE = 3;

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
        this.answer.add(answer.toLowerCase());
    }

    public void setAnswer(ArrayList<String> answer) {
        this.answer.addAll(answer);
    }

    public boolean checkAnswer(String answer) {
        return this.answer.contains(answer.toLowerCase());
    }
}
