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
public class MultipleChoiceQuestion extends Question {
    
    private ArrayList<String> choices;

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

    public void addChoice(String choice) {
        choices.add(choice);
    }
    
    public MultipleChoiceQuestion() {
        super();
        type = Question.QUESTION_TYPE_MULTIPLE_CHOICE;
        choices = new ArrayList();
    }
    

    
}
