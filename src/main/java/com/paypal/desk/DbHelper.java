package com.paypal.desk;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHelper {

    private static final Connection connection = getConnection();

    private static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/paypal",
                    "root",
                    "proton55"
            );

            System.out.println("Connection successful");
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static int createUser(String firstName, String lastName) {
        String sql = "insert into users " +
                "(first_name, last_name)" +
                " values (" +
                "'" + firstName + "'" +
                ", " +
                "'" + lastName + "'" +
                ")";

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);

            String idSql = "select max(id) from users";
            Statement idStatement = connection.createStatement();
            ResultSet resultSet = idStatement.executeQuery(idSql);

            resultSet.next();

            if(!statement.isClosed()){
                statement.close();
            }

            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Updates the user balance in database
     * Sets balance = balance + amount
     *
     * @param userId id of the user in users table
     * @param amount double value of the amount to insert
     */
    static void cashFlow(int userId, double amount) {
        String sql="update users set balance=balance+"+amount +"where id="+userId;

        try {
            Statement statement=connection.createStatement();
            statement.executeUpdate(sql);
            if(!statement.isClosed()){
                statement.close();
            }
            System.out.println("Balance is changed");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Emulates a transaction between 2 users
     * Takes money from one account and adds to another account
     *
     * @param userFrom source user id
     * @param userTo   target user id
     * @param amount   transaction amount
     */
    static void transaction(int userFrom, int userTo, double amount) throws Exception {

        String sql="select balance from users where id="+userFrom;
        String sql2="insert into transactions(user_from,user_to,transaction_amount) values(" +
                userFrom+","+
                userTo+","+
                amount+
                ")";
        try {
            Statement statement=connection.createStatement();

            ResultSet resultSet=statement.executeQuery(sql);
            resultSet.next();
            int balance=resultSet.getInt(1);
            if(!resultSet.isClosed()) {
                resultSet.close();
            }
            if(balance>amount) {
                cashFlow(userFrom, -amount);
                cashFlow(userTo, amount);
                statement.executeUpdate(sql2);
            }else{
                System.out.println("Sorry, your balance isn't enough to complete transaction");
                throw new Exception("You don't have enough money on your balance");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    static List<User> listUsers() {

        String sql = "select * from users where id";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            List<User> userList = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                double balance = resultSet.getDouble("balance");

                userList.add(new User(
                        id, firstName, lastName, balance
                ));
            }
            if(!statement.isClosed()){
                statement.close();
            }
            return userList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

   /* public static void  test(){
        String sql="select * from users";
        try {
            Statement statement=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultSet=statement.executeQuery(sql);
            while (resultSet.next()){
                print(resultSet);
            }
          *//*  if(resultSet.previous()){
                resultSet.previous();  //last-1; with 2 resultSet.previous();
                print(resultSet);
            }*//*
         //   resultSet.first();
        //    print(resultSet);

           resultSet.first();
           resultSet.updateString("first_name","name");
           resultSet.updateRow();

            print(resultSet);



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
    /*private static void print(ResultSet resultSet){
        try {
            System.out.print(resultSet.getInt("id")+" ");
            System.out.print(resultSet.getString("first_name")+" ");
            System.out.print(resultSet.getString("last_name")+" ");
            System.out.print(resultSet.getDouble("balance")+" ");
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }*/
}

