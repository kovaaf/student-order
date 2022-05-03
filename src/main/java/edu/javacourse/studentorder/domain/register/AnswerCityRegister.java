package edu.javacourse.studentorder.domain.register;
/*
 *   Created by Kovalyov Anton 31.03.2022
 */

import java.util.ArrayList;
import java.util.List;

public class AnswerCityRegister {
    private List<AnswerCityRegisterItem> items;

    public void addItem(AnswerCityRegisterItem answerCityRegisterItem) {
        if (items == null) items = new ArrayList<>(5);
        items.add(answerCityRegisterItem);
    }

    public List<AnswerCityRegisterItem> getItems() {
        return items;
    }
}
