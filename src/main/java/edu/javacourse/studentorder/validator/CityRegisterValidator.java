package edu.javacourse.studentorder.validator;
/*
 *   Created by Kovalyov Anton 31.03.2022
 */

import edu.javacourse.studentorder.domain.Child;
import edu.javacourse.studentorder.domain.Person;
import edu.javacourse.studentorder.domain.StudentOrder;
import edu.javacourse.studentorder.domain.register.AnswerCityRegister;
import edu.javacourse.studentorder.domain.register.AnswerCityRegisterItem;
import edu.javacourse.studentorder.domain.register.CityRegisterResponse;
import edu.javacourse.studentorder.exception.CityRegisterException;
import edu.javacourse.studentorder.validator.register.CityRegisterChecker;
import edu.javacourse.studentorder.validator.register.RealCityRegisterChecker;

public class CityRegisterValidator {

    private static final String IN_CODE = "NO_GRN";
    private static final String UNKNOWN_CODE = "Unknown error";

    private CityRegisterChecker personChecker;

    public CityRegisterValidator() {
        personChecker = new RealCityRegisterChecker();
    }

    public AnswerCityRegister checkCityRegister(StudentOrder studentOrder) {
        AnswerCityRegister answerCityRegister = new AnswerCityRegister();

            answerCityRegister.addItem(checkPerson(studentOrder.getHusband()));
            answerCityRegister.addItem(checkPerson(studentOrder.getWife()));
            for (Child child: studentOrder.getChildren()) {
                answerCityRegister.addItem(checkPerson(child));
            }

        return answerCityRegister;
    }

    private AnswerCityRegisterItem checkPerson(Person person) {
        AnswerCityRegisterItem.CityStatus cityStatus;
        AnswerCityRegisterItem.CityError cityError = null;

        try {
            CityRegisterResponse cityRegisterResponse = personChecker.checkPerson(person);

            cityStatus = cityRegisterResponse.isRegistered() ?
                    AnswerCityRegisterItem.CityStatus.Yes :
                    AnswerCityRegisterItem.CityStatus.No;
        } catch (CityRegisterException ex) {
            ex.printStackTrace(System.out);
            cityStatus = AnswerCityRegisterItem.CityStatus.ERROR;
            cityError = new AnswerCityRegisterItem.CityError(ex.getCode(), ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            cityStatus = AnswerCityRegisterItem.CityStatus.ERROR;
            cityError = new AnswerCityRegisterItem.CityError(UNKNOWN_CODE, ex.getMessage());
        }

        if (cityError == null ) {
            return new AnswerCityRegisterItem(cityStatus, person);
        } else {
            return new AnswerCityRegisterItem(cityStatus, person, cityError);
        }

    }
}
