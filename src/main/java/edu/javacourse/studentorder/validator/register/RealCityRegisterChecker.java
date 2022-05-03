package edu.javacourse.studentorder.validator.register;
/*
 *   Created by Kovalyov Anton 31.03.2022
 */

import edu.javacourse.studentorder.config.Config;
import edu.javacourse.studentorder.domain.Person;
import edu.javacourse.studentorder.domain.register.CityRegisterRequest;
import edu.javacourse.studentorder.domain.register.CityRegisterResponse;
import edu.javacourse.studentorder.exception.CityRegisterException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

public class RealCityRegisterChecker implements CityRegisterChecker {
    public CityRegisterResponse checkPerson(Person person)
            throws CityRegisterException {


        try {
            CityRegisterRequest cityRegisterRequest = new CityRegisterRequest(person);

            Client client = ClientBuilder.newClient();
            CityRegisterResponse cityRegisterResponse = client.target(Config.getProperty(Config.CR_URL))
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(cityRegisterRequest, MediaType.APPLICATION_JSON))
                    .readEntity(CityRegisterResponse.class);

            return cityRegisterResponse;
        } catch (Exception e) {
            throw new CityRegisterException("1", e.getMessage(), e);
        }
    }
}
