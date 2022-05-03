package edu.javacourse.studentorder.dao;
/*
 *   Created by Kovalyov Anton 09.04.2022
 */

import edu.javacourse.studentorder.config.Config;
import edu.javacourse.studentorder.domain.*;
import edu.javacourse.studentorder.exception.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentOrderDaoImpl implements StudentOrderDao {

    private static final Logger logger = LoggerFactory.getLogger(StudentOrderDaoImpl.class);

    private static final String INSERT_ORDER = "INSERT INTO STUDENT_ORDER(" +
            "STUDENT_ORDER_STATUS," +
            "STUDENT_ORDER_DATE," +
            "H_SUR_NAME," +
            "H_GIVEN_NAME," +
            "H_PATRONYMIC," +
            "H_DATE_OF_BIRTH," +
            "H_PASSPORT_SERIA," +
            "H_PASSPORT_NUMBER," +
            "H_PASSPORT_DATE," +
            "H_PASSPORT_OFFICE_ID," +
            "H_POST_INDEX," +
            "H_STREET_CODE," +
            "H_BUILDING," +
            "H_EXTENSION," +
            "H_APARTMENT," +
            "H_UNIVERSITY_ID," +
            "H_STUDENT_NUMBER," +
            "W_SUR_NAME," +
            "W_GIVEN_NAME," +
            "W_PATRONYMIC," +
            "W_DATE_OF_BIRTH," +
            "W_PASSPORT_SERIA," +
            "W_PASSPORT_NUMBER," +
            "W_PASSPORT_DATE," +
            "W_PASSPORT_OFFICE_ID," +
            "W_POST_INDEX," +
            "W_STREET_CODE," +
            "W_BUILDING," +
            "W_EXTENSION," +
            "W_APARTMENT," +
            "W_UNIVERSITY_ID," +
            "W_STUDENT_NUMBER," +
            "CERTIFICATE_ID," +
            "REGISTER_OFFICE_ID," +
            "MARRIAGE_DATE)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    
    private static final String INSERT_CHILD = "INSERT INTO STUDENT_CHILD(" +
            "STUDENT_ORDER_ID," +
            "C_SUR_NAME," +
            "C_GIVEN_NAME," +
            "C_PATRONYMIC," +
            "C_DATE_OF_BIRTH," +
            "C_CERTIFICATE_NUMBER," +
            "C_CERTIFICATE_DATE," +
            "C_REGISTER_OFFICE_ID," +
            "C_POST_INDEX," +
            "C_STREET_CODE," +
            "C_BUILDING," +
            "C_EXTENSION," +
            "C_APARTMENT)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String SELECT_ORDERS =
            "SELECT so.*, " + // All student order's columns
                    "ro.r_office_area_id, ro.r_office_name, " + // Plus ЗАГС
                    "po_h.p_office_area_id as h_p_office_area_id, po_h.p_office_name as h_p_office_name, " + // Plus Passport office of husband (area_id + name columns)
                    "po_w.p_office_area_id as w_p_office_area_id, po_w.p_office_name as w_p_office_name " + // Plus Passport office of wife (area_id + name columns)
                    "FROM student_order so " + // From Student order table
                    "INNER JOIN register_office ro ON ro.r_office_id = so.register_office_id " + // Join to result register office table with id reference
                    "INNER JOIN passport_office po_h ON po_h.p_office_id = so.h_passport_office_id " + // Join to result passport office of husband table with husband's id reference
                    "INNER JOIN passport_office po_w ON po_w.p_office_id = so.w_passport_office_id " + // Join to result passport office of wife table with wife's id reference
                    "WHERE student_order_status = ? ORDER BY student_order_date LIMIT ?"; // Where status == parameter_value and sort by order creation date

    public static final String SELECT_CHILD =
            "SELECT soc.*,  " +
            "ro.r_office_area_id, ro.r_office_name " +
//            "so.h_sur_name, so.h_given_name, so.h_patronymic, so.h_date_of_birth" +
//            ", so.w_sur_name, so.w_given_name, so.w_patronymic, so.w_date_of_birth" +
            "FROM student_child soc " +
            "INNER JOIN register_office ro ON ro.r_office_id = soc.c_register_office_id " +
//            "INNER JOIN student_order so ON so.student_order_id = soc.student_order_id " +
            "WHERE soc.student_order_id IN ";

    private static final String SELECT_ORDERS_FULL =
            "SELECT so.*, " +
                    "ro.r_office_area_id, ro.r_office_name, " +
                    "po_h.p_office_area_id as h_p_office_area_id, po_h.p_office_name as h_p_office_name, " +
                    "po_w.p_office_area_id as w_p_office_area_id, po_w.p_office_name as w_p_office_name, " +
                    "soc.*,  " +
                    "roc.r_office_area_id as c_r_office_area_id, roc.r_office_name as c_r_office_name " +
                    "FROM student_order so " +
                    "INNER JOIN register_office ro ON ro.r_office_id = so.register_office_id " +
                    "INNER JOIN passport_office po_h ON po_h.p_office_id = so.h_passport_office_id " +
                    "INNER JOIN passport_office po_w ON po_w.p_office_id = so.w_passport_office_id " +
                    "INNER JOIN student_child soc ON soc.student_order_id = so.student_order_id " +
                    "INNER JOIN register_office roc ON roc.r_office_id = soc.c_register_office_id " +
                    "WHERE student_order_status = ? ORDER BY so.student_order_id LIMIT ?";

    private Connection getConnection() throws SQLException {
        return ConnectionBuilder.getConnection();
    }

    @Override
    public Long saveStudentOrder(StudentOrder studentOrder) throws DaoException {
        Long result = -1L;

        logger.debug("SO:{}", studentOrder);

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_ORDER, new String[] {"student_order_id"})) {

            connection.setAutoCommit(false);
            try {
                // Header
                statement.setInt(1, StudentOrderStatus.START.ordinal());
                statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

                // Husband
                setParamsForAdult(statement, 3, studentOrder.getHusband());

                // Wife
                int startValueWife = INSERT_ORDER.substring(0, INSERT_ORDER.indexOf("W_SUR_NAME,")).split(",").length + 1; // 18
                setParamsForAdult(statement, startValueWife, studentOrder.getWife());


                // Marriage data
                int startValueMarriage = INSERT_ORDER.substring(INSERT_ORDER.indexOf("CERTIFICATE_ID,")).split(",").length - 4; // 33
                statement.setString(startValueMarriage, studentOrder.getMarriageCertificateId());
                statement.setLong(startValueMarriage + 1, studentOrder.getMarriageOffice().getOfficeId());
                statement.setDate(startValueMarriage + 2, Date.valueOf(studentOrder.getMarriageDate()));

                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) result = generatedKeys.getLong(1);

                saveChildren(connection, studentOrder, result);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }

        return result;
    }

   private void saveChildren(Connection connection, StudentOrder studentOrder, Long studentOrderId) throws SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CHILD)) {
            for (Child child : studentOrder.getChildren()) {
                preparedStatement.setLong(1, studentOrderId);
                setParamsForChild(child, preparedStatement);
                preparedStatement.addBatch(); // Буферизация
            }
            preparedStatement.executeBatch(); // Отправка буфера, когда транзакции кончились, а буфер не заполнился до 1000

            // Пример с буферизацией. Здесь она не нужна - коунтеры под удаление, только executeBatch() после блока for оставить
            /*int counter = 0; // Кол-во записей в буфере
            for (Child child : studentOrder.getChildren()) {
                preparedStatement.setLong(1, studentOrderId);
                setParamsForChild(child, preparedStatement);
                preparedStatement.addBatch(); // Буферизация
                counter++;
                if (counter > 1000) {
                    preparedStatement.executeBatch(); // Отправка буфера при заполнении до 1000
                    counter = 0;
                }
            }
            if (counter > 0) {
                preparedStatement.executeBatch(); // Отправка буфера, когда транзакции кончились, а буфер не заполнился до 1000
            }*/
        }
    }

    private void setParamsForAdult(PreparedStatement statement, int start, Adult adult) throws SQLException {
        setParamsForPerson(statement, start, adult);

        statement.setString(start + 4, adult.getPassportSeria());
        statement.setString(start + 5, adult.getPassportNumber());
        statement.setDate(start + 6, Date.valueOf(adult.getIssueDate()));
        statement.setLong(start + 7, adult.getIssueDepartment().getOfficeId());
        
        setParamsForAddress(statement, start + 8, adult);

        statement.setLong(start + 13, adult.getUniversity().getUniversityId());
        statement.setString(start + 14, adult.getStudentId());
    }

    private void setParamsForChild(Child child, PreparedStatement preparedStatement) throws SQLException {
        setParamsForPerson(preparedStatement, 2, child);

        preparedStatement.setString(6, child.getCertificateNumber());
        preparedStatement.setDate(7, java.sql.Date.valueOf(child.getIssueDate()));
        preparedStatement.setLong(8, child.getIssueDepartment().getOfficeId());

        setParamsForAddress(preparedStatement, 9, child);


    }

    private void setParamsForPerson(PreparedStatement statement, int start, Person person) throws SQLException {
        statement.setString(start, person.getSurName());
        statement.setString(start + 1, person.getGivenName());
        statement.setString(start + 2, person.getPatronymic());
        statement.setDate(start + 3, Date.valueOf(person.getDateOfBirth()));
    }

    private void setParamsForAddress(PreparedStatement statement, int start, Person person) throws SQLException {
        Address address = person.getAddress();

        statement.setString(start, address.getPostCode());
        statement.setLong(start + 1, address.getStreet().getStreetCode());
        statement.setString(start + 2, address.getBuilding());
        statement.setString(start + 3, address.getExtension());
        statement.setString(start + 4, address.getApartment());
    }

    @Override
    public List<StudentOrder> getStudentOrders() throws DaoException {
        return getStudentOrdersOneSelect();
//        return getStudentOrdersTwoSelect();
    }

    private List<StudentOrder> getStudentOrdersOneSelect() throws DaoException {
        List<StudentOrder> result = new LinkedList<>();

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ORDERS_FULL)) {
            // Учитываем, что в таблице теперь записи дублируются т.к. двое детей
            // Для этого создаём мапу и в качестве уникального ключа выступает айди заявки, по которому отсеиваем дубли
            Map<Long, StudentOrder> studentOrderMap = new HashMap<>();

            statement.setInt(1, StudentOrderStatus.START.ordinal());
            // Внимательно! Может быть больше одного ребёнка, а у нас на каждого ребёнка по этому запросу дублируется заявка
            int limit = Integer.parseInt(Config.getProperty(Config.DB_LIMIT));
            statement.setInt(2, limit);

            ResultSet resultSet = statement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                Long soId = resultSet.getLong("student_order_id");
                // Если такой заявки нет ещё, то добавляем её
                if (!studentOrderMap.containsKey(soId)) {
                    StudentOrder studentOrder = getFullStudentOrder(resultSet);

                    result.add(studentOrder);
                    studentOrderMap.put(soId, studentOrder);
                }
                // Если она есть, то мы не создаём новую в возвращаемом списке заявок, а только добавляем в заявку ребёнка
                StudentOrder studentOrder = studentOrderMap.get(soId);
                studentOrder.addChild(fillChild(resultSet)); // Передаём текущую строку цикла по резалтсету из запроса в метод
                counter++;
            }
            // Удаляем из результирующего списка не полностью сформированные заявки
            if (counter >= limit) {
                result.remove(result.size() - 1);
            }

            resultSet.close();

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }

        return result;
    }

    private List<StudentOrder> getStudentOrdersTwoSelect() throws DaoException {
        List<StudentOrder> result = new LinkedList<>();

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ORDERS)) {

            statement.setInt(1, StudentOrderStatus.START.ordinal());
            statement.setInt(2, Integer.parseInt(Config.getProperty(Config.DB_LIMIT)));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                StudentOrder studentOrder = getFullStudentOrder(resultSet);

                result.add(studentOrder);
            }
            findChildren(connection, result);

            resultSet.close();

        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }

        return result;
    }

    private StudentOrder getFullStudentOrder(ResultSet resultSet) throws SQLException {
        StudentOrder studentOrder = new StudentOrder();

        fillStudentOrder(resultSet, studentOrder);
        fillMarriage(resultSet, studentOrder);

        studentOrder.setHusband(fillAdult(resultSet, "h_"));
        studentOrder.setWife(fillAdult(resultSet, "w_"));
        return studentOrder;
    }

    private void findChildren(Connection connection, List<StudentOrder> studentOrderList) throws SQLException {
        String range = "(" + studentOrderList.stream().map(so -> String.valueOf(so.getStudentOrderId()))
                .collect(Collectors.joining(",")) + ")"; // Сформировали для запроса "range" детей

        Map<Long, StudentOrder> studentOrderMap = studentOrderList.stream().collect(Collectors
                .toMap(StudentOrder::getStudentOrderId, so -> so)); // Для скорости обращения по айди как идентификатору заявки сделали ассоциативный массив айди-заявка

        // Рассовываем детей по заявкам через ключ-айди
        try(PreparedStatement statement = connection.prepareStatement(SELECT_CHILD + range)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Child child = fillChild(resultSet);
                StudentOrder studentOrder = studentOrderMap.get(resultSet.getLong("student_order_id"));
                studentOrder.addChild(child);
            }
        }
    }

    private Adult fillAdult(ResultSet resultSet, String pref) throws SQLException {
        Adult adult = new Adult();

        adult.setSurName(resultSet.getString(pref + "sur_name"));
        adult.setGivenName(resultSet.getString(pref + "given_name"));
        adult.setPatronymic(resultSet.getString(pref + "patronymic"));
        adult.setDateOfBirth(resultSet.getDate(pref + "date_of_birth").toLocalDate());
        adult.setPassportSeria(resultSet.getString(pref + "passport_seria"));
        adult.setPassportNumber(resultSet.getString(pref + "passport_number"));
        adult.setIssueDate(resultSet.getDate(pref + "passport_date").toLocalDate());

        String areaId = resultSet.getString(pref + "p_office_area_id");
        String officeName = resultSet.getString(pref + "p_office_name");
        Long poId = resultSet.getLong(pref + "passport_office_id");
        PassportOffice passportOffice = new PassportOffice(poId, areaId, officeName);
        adult.setIssueDepartment(passportOffice);

        Address address = new Address();
        address.setPostCode(resultSet.getString(pref + "post_index"));
        address.setBuilding(resultSet.getString(pref + "building"));
        address.setExtension(resultSet.getString(pref + "extension"));
        address.setApartment(resultSet.getString(pref + "apartment"));
        Street street = new Street(resultSet.getLong(pref + "street_code"), "");
        address.setStreet(street);
        adult.setAddress(address);

        University university = new University(resultSet.getLong(pref + "university_id"), "");
        adult.setUniversity(university);
        adult.setStudentId(resultSet.getString(pref + "student_number"));

        return adult;
    }

    private Child fillChild(ResultSet resultSet) throws SQLException {
        String surName = resultSet.getString("c_sur_name");
        String givenName = resultSet.getString("c_given_name");
        String patronymic = resultSet.getString("c_patronymic");
        LocalDate dateOfBirth = resultSet.getDate("c_date_of_birth").toLocalDate();

        Child child = new Child(surName, givenName, patronymic, dateOfBirth);

        child.setCertificateNumber(resultSet.getString("c_certificate_number"));
        child.setIssueDate(resultSet.getDate("c_certificate_date").toLocalDate());

        RegisterOffice registerOffice = new RegisterOffice();
        registerOffice.setOfficeId(resultSet.getLong("c_register_office_id"));
        registerOffice.setOfficeAreaId("c_r_office_area_id"); // Мы заджоинили эти столбцы
        registerOffice.setOfficeName("c_r_office_name"); // Мы заджоинили эти столбцы

        child.setIssueDepartment(registerOffice);

        Address address = new Address();
        address.setPostCode(resultSet.getString("c_post_index"));
        address.setBuilding(resultSet.getString("c_building"));
        address.setExtension(resultSet.getString("c_extension"));
        address.setApartment(resultSet.getString("c_apartment"));
        Street street = new Street(resultSet.getLong("c_street_code"), "");
        address.setStreet(street);

        child.setAddress(address);

        return child;
    }

    private void fillStudentOrder(ResultSet resultSet, StudentOrder studentOrder) throws SQLException {
        studentOrder.setStudentOrderId(resultSet.getLong("student_order_id"));
        studentOrder.setStudentOrderStatus(StudentOrderStatus.fromValue(resultSet.getInt("student_order_status")));
        studentOrder.setStudentOrderDate(resultSet.getTimestamp("student_order_date").toLocalDateTime());
    }

    private void fillMarriage(ResultSet resultSet, StudentOrder studentOrder) throws SQLException {
        studentOrder.setMarriageCertificateId(resultSet.getString("certificate_id"));
        studentOrder.setMarriageDate(resultSet.getDate("marriage_date").toLocalDate());

        Long roId = resultSet.getLong("register_office_id");
        String areaId = resultSet.getString("r_office_area_id");
        String officeName = resultSet.getString("r_office_name");
        RegisterOffice registerOffice = new RegisterOffice(roId, areaId, officeName);
        studentOrder.setMarriageOffice(registerOffice);
    }
}
