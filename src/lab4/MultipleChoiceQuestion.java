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
import java.util.Collections;
import java.util.Random;

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
    
    public void shuffleChoices() {
        Collections.shuffle(choices, new Random(System.nanoTime()));
    }
    
}
