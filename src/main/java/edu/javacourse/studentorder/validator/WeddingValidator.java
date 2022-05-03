package edu.javacourse.studentorder.validator;
/*
 *   Created by Kovalyov Anton 31.03.2022
 */

import edu.javacourse.studentorder.domain.StudentOrder;
import edu.javacourse.studentorder.domain.wedding.AnswerWedding;

public class WeddingValidator {
    String hostName;
    String login;
    String password;

    public AnswerWedding checkWedding(StudentOrder studentOrder) {
        System.out.println("WeddingCheck is running: "
                + hostName + ", " + login + ", " + password);
        return new AnswerWedding();
    }
}
