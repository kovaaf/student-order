package edu.javacourse.studentorder.dao;
/*
 *   Created by Kovalyov Anton 05.04.2022
 */

import edu.javacourse.studentorder.config.Config;
import edu.javacourse.studentorder.domain.CountryArea;
import edu.javacourse.studentorder.domain.PassportOffice;
import edu.javacourse.studentorder.domain.RegisterOffice;
import edu.javacourse.studentorder.domain.Street;
import edu.javacourse.studentorder.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DictionaryDaoImpl implements DictionaryDao {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryDaoImpl.class);

    private static final String GET_STREET = "SELECT street_code, street_name " +
            "FROM street WHERE UPPER(street_name) LIKE UPPER(?)";
    private static final String GET_PASSPORT = "SELECT * " +
            "FROM passport_office WHERE p_office_area_id = ?";
    private static final String GET_REGISTER = "SELECT * " +
            "FROM register_office WHERE r_office_area_id = ?";
    private static final String GET_AREA = "SELECT * " +
            "FROM country_struct WHERE area_id like ? and area_id <> ?";

    private Connection getConnection() throws SQLException {
        return ConnectionBuilder.getConnection();
    }

    public List<Street> findStreets(String pattern) throws DaoException {
        List<Street> streets = new LinkedList<>();
        /*String GET_STREET = "select street_code, street_name " +
                "from street where UPPER(street_name) like UPPER('%" + pattern + "%')";*/


        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_STREET)) {// PreparedStatement позволяет использовать параметр в запросе

            statement.setString(1, "%" + pattern + "%"); // Вместо вопроса в запросе вставляем параметр pattern

            ResultSet resultSet = statement.executeQuery(); // Это сформированная по запросу таблица
            while (resultSet.next()) { // Следующая строка из таблицы
                streets.add(new Street(resultSet.getLong("street_code"), // Вытаскиваем из строки поле из колонки стрит-код
                        resultSet.getString("street_name"))); // Вытаскиваем из строки поле из колонки стрит-нейм
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }

        return streets;
    }

    @Override
    public List<PassportOffice> findPassportOffices(String areaId) throws DaoException {
        List<PassportOffice> passportOffices = new LinkedList<>();

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_PASSPORT)) {

            statement.setString(1, areaId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                passportOffices.add(new PassportOffice(resultSet.getLong("p_office_id"),
                        resultSet.getString("p_office_area_id"),
                        resultSet.getString("p_office_name")));
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }

        return passportOffices;
    }

    @Override
    public List<RegisterOffice> findRegisterOffices(String areaId) throws DaoException {
        List<RegisterOffice> registerOffices = new LinkedList<>();

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_REGISTER)) {

            statement.setString(1, areaId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                registerOffices.add(new RegisterOffice(resultSet.getLong("r_office_id"),
                        resultSet.getString("r_office_area_id"),
                        resultSet.getString("r_office_name")));
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }

        return registerOffices;
    }

    @Override
    public List<CountryArea> findAreas(String areaId) throws DaoException {
        List<CountryArea> registerOffices = new LinkedList<>();

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(GET_AREA)) {

            String param1 = buildParam(areaId);
            statement.setString(1, param1);
            statement.setString(2,areaId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                registerOffices.add(new CountryArea(
                        resultSet.getString("area_id"),
                        resultSet.getString("area_name")));
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }

        return registerOffices;
    }

    private String buildParam(String areaId) throws SQLException {
        if (areaId == null || areaId.trim().isEmpty()) {
            return "__0000000000";
        } else if (areaId.endsWith("0000000000")) {
            return areaId.substring(0, 2) + "___0000000";
        } else if (areaId.endsWith("0000000")) {
            return areaId.substring(0, 5) + "___0000";
        } else if (areaId.endsWith("0000")) {
            return areaId.substring(0, 8) + "____";
        }
        throw new SQLException("Invalid parameter 'areaId:' " + areaId);
    }


}
