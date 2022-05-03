package edu.javacourse.studentorder.domain.register;
/*
 *   Created by Kovalyov Anton 31.03.2022
 */

public class CityRegisterResponse {
    private boolean isRegistered;
    private boolean isTemporal;

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        this.isRegistered = registered;
    }

    public boolean isTemporal() {
        return isTemporal;
    }

    public void setTemporal(boolean temporal) {
        this.isTemporal = temporal;
    }

    @Override
    public String toString() {
        return "CityRegisterCheckerResponse{" +
                "isRegistered=" + isRegistered +
                ", isTemporal=" + isTemporal +
                '}';
    }
}
