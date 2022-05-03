package edu.javacourse.studentorder.dao;
/*
 *   Created by Kovalyov Anton 09.04.2022
 */

import edu.javacourse.studentorder.domain.StudentOrder;
import edu.javacourse.studentorder.exception.DaoException;

import java.util.List;

public interface StudentOrderDao {
    Long saveStudentOrder(StudentOrder studentOrder) throws DaoException;
    List<StudentOrder> getStudentOrders() throws DaoException;
}
