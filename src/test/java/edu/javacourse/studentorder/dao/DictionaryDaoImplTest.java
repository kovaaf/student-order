package edu.javacourse.studentorder.dao;

/*
 *   Created by Kovalyov Anton 17.04.2022
 */

import edu.javacourse.studentorder.domain.CountryArea;
import edu.javacourse.studentorder.domain.PassportOffice;
import edu.javacourse.studentorder.domain.RegisterOffice;
import edu.javacourse.studentorder.domain.Street;
import edu.javacourse.studentorder.exception.DaoException;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DictionaryDaoImplTest {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryDaoImplTest.class);

    @BeforeClass
    public static void startUp() throws URISyntaxException, IOException, SQLException {
        DBInit.startUp();
    }

    @Test
    public void testStreet() throws DaoException {
        LocalDateTime localDateTime1 = LocalDateTime.now();
        LocalDateTime localDateTime2 = LocalDateTime.now();
//        logger.info("TEST " + localDateTime); // Так неправильно, слишком много времени тратится на преобразования
        logger.info("TEST {} {}", localDateTime1, localDateTime2); // Так правильно
        List<Street> streets = new DictionaryDaoImpl().findStreets("про");
        Assert.assertTrue(streets.size() == 2);
    }
    @Test
    public void testPasportOffice() throws DaoException {
        List<PassportOffice> passportOffices = new DictionaryDaoImpl().findPassportOffices("010020000000");
        Assert.assertTrue(passportOffices.size() == 2);
    }
    @Test
    public void testRegisterOffice() throws DaoException {
        List<RegisterOffice> registerOffices = new DictionaryDaoImpl().findRegisterOffices("010010000000");
        Assert.assertTrue(registerOffices.size() == 2);
    }
    @Test
    public void testArea() throws DaoException {
        List<CountryArea> areas1 = new DictionaryDaoImpl().findAreas("");
        List<CountryArea> areas2 = new DictionaryDaoImpl().findAreas("020000000000");
        List<CountryArea> areas3 = new DictionaryDaoImpl().findAreas("020010000000");
        List<CountryArea> areas4 = new DictionaryDaoImpl().findAreas("020010010000");

        Assert.assertTrue(areas1.size() == 2);
        Assert.assertTrue(areas2.size() == 2);
        Assert.assertTrue(areas3.size() == 2);
        Assert.assertTrue(areas4.size() == 2);
    }
}