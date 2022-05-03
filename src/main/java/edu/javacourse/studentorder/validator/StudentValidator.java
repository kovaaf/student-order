package edu.javacourse.studentorder.validator;
/*
 *   Created by Kovalyov Anton 31.03.2022
 */

import edu.javacourse.studentorder.domain.StudentOrder;
import edu.javacourse.studentorder.domain.student.AnswerStudent;

public class StudentValidator {
    String hostName;
    String login;
    String password;

    public AnswerStudent checkStudent(StudentOrder studentOrder) {
        System.out.println("StudentCheck is running: "
                + hostName + ", " + login + ", " + password);
        return new AnswerStudent();
    }
}
