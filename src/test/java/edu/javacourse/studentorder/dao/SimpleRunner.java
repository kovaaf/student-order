package edu.javacourse.studentorder.dao;
/*
 *   Created by Kovalyov Anton 17.04.2022
 */

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleRunner {
    public static void main(String[] args) {
        SimpleRunner simpleRunner = new SimpleRunner();

        simpleRunner.runTests();
    }

    private void runTests() {

        try {
            Class<?> cl = Class.forName("edu.javacourse.studentorder.dao.DictionaryDaoImplTest");

            Constructor<?> constructor = cl.getConstructor();
            Object entity = constructor.newInstance();

            Method[] methods = cl.getMethods();
            for(Method method : methods) {
                Test annotation = method.getAnnotation(Test.class);
                if (annotation != null) {
                    method.invoke(entity);
                }
            }

        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }


    }
}
