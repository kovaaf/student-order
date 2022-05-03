package edu.javacourse.studentorder.domain.register;
/*
 *   Created by Kovalyov Anton 04.04.2022
 */

import edu.javacourse.studentorder.domain.Person;

public class AnswerCityRegisterItem {

    public enum CityStatus {
        Yes, No, ERROR
    }

    public static class CityError {
        private String code;
        private String text;

        public CityError(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }
    }

    private CityStatus cityStatus;
    private Person person;
    private CityError error;

    public AnswerCityRegisterItem(CityStatus cityStatus, Person person) {
        this.cityStatus = cityStatus;
        this.person = person;
    }

    public AnswerCityRegisterItem(CityStatus cityStatus, Person person, CityError error) {
        this.cityStatus = cityStatus;
        this.person = person;
        this.error = error;
    }

    public CityStatus getCityStatus() {
        return cityStatus;
    }

    public Person getPerson() {
        return person;
    }

    public CityError getError() {
        return error;
    }
}
