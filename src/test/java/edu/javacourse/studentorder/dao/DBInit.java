package edu.javacourse.studentorder.dao;
/*
 *   Created by Kovalyov Anton 17.04.2022
 */

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public class DBInit {
    public static void startUp() throws URISyntaxException, IOException, SQLException {
        URL urlDBStructure = DictionaryDaoImplTest.class.getClassLoader().getResource("db_structure.sql");
        URL urlDBData = DictionaryDaoImplTest.class.getClassLoader().getResource("student_data.sql");

        List<String> stringsDBStructure = Files.readAllLines(Paths.get(urlDBStructure.toURI()));
        List<String> stringsDBData = Files.readAllLines(Paths.get(urlDBData.toURI()));

        String sqlDBStructure = String.join("", stringsDBStructure);
        String sqlDBData = String.join("", stringsDBData);
        try (Connection connection = ConnectionBuilder.getConnection(); Statement statement = connection.createStatement()){
            statement.executeUpdate(sqlDBStructure);
            statement.executeUpdate(sqlDBData);
        }
    }
}
