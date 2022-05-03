package edu.javacourse.studentorder.validator;
/*
 *   Created by Kovalyov Anton 31.03.2022
 */

import edu.javacourse.studentorder.domain.StudentOrder;
import edu.javacourse.studentorder.domain.children.AnswerChildren;

public class ChildrenValidator {
    String hostName;
    String login;
    String password;

    public AnswerChildren checkChildren(StudentOrder studentOrder) {
        System.out.println("ChildrenCheck is running: "
                + hostName + ", " + login + ", " + password);
        return new AnswerChildren();
    }
}
