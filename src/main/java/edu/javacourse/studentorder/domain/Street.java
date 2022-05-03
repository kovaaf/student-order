package edu.javacourse.studentorder.domain;
/*
 *   Created by Kovalyov Anton 02.04.2022
 */

public class Street {
    private Long streetCode;
    private String streetName;

    public Street() {
    }

    public Street(Long streetCode, String streetName) {
        this.streetCode = streetCode;
        this.streetName = streetName;
    }

    public Long getStreetCode() {
        return streetCode;
    }

    public void setStreetCode(Long streetCode) {
        this.streetCode = streetCode;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @Override
    public String toString() {
        return "Street{" +
                "streetCode=" + streetCode +
                ", streetName='" + streetName + '\'' +
                '}';
    }
}
