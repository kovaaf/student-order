package edu.javacourse.studentorder.validator.register;
/*
 *   Created by Kovalyov Anton 31.03.2022
 */

import edu.javacourse.studentorder.domain.Adult;
import edu.javacourse.studentorder.domain.Child;
import edu.javacourse.studentorder.domain.Person;
import edu.javacourse.studentorder.domain.register.CityRegisterResponse;
import edu.javacourse.studentorder.exception.CityRegisterException;

public class FakeCityRegisterChecker implements CityRegisterChecker {
    private static final String GOOD_1 = "1000"; // Все прописаны, ошибок нет
    private static final String GOOD_2 = "2000";
    private static final String BAD_1 = "1001"; // Не прописаны, ошибок нет
    private static final String BAD_2 = "2001";
    private static final String ERROR_1 = "1002"; // Ошибка ГРН
    private static final String ERROR_2 = "2002";
    private static final String ERROR_T_1 = "1003"; // Ошибка соединения
    private static final String ERROR_T_2 = "2003";

    public CityRegisterResponse checkPerson(Person person)
            throws CityRegisterException {
        CityRegisterResponse result = new CityRegisterResponse();

        if (person instanceof Adult adult) {
            String passportSeria = adult.getPassportSeria();

            if (passportSeria.equals(GOOD_1) || passportSeria.equals(GOOD_2)) {
                result.setRegistered(true);
                result.setTemporal(false);
            }
            if (passportSeria.equals(BAD_1) || passportSeria.equals(BAD_2)) {
                result.setRegistered(false);
            }
            if (passportSeria.equals(ERROR_1) || passportSeria.equals(ERROR_2)) {
                throw new CityRegisterException("1", "GRN ERROR " + passportSeria);
            }
        }

        if (person instanceof Child) {
            result.setRegistered(true);
            result.setTemporal(true);
        }

        System.out.println(result);

        return result;
    }
}
