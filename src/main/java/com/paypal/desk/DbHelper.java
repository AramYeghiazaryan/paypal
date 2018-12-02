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
                " values (?,?)";

        try {
          //  Statement statement = connection.createStatement();
           // statement.execute(sql);
            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setString(1,firstName);
            preparedStatement.setString(2,lastName);
            preparedStatement.execute();

            String idSql = "select max(id) from users";
            Statement idStatement = connection.createStatement();
            ResultSet resultSet = idStatement.executeQuery(idSql);

            resultSet.next();
            if(!preparedStatement.isClosed()){
                preparedStatement.close();
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
        String sql="update users set balance=balance+? where id=?";

        try {

            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setDouble(1,amount);
            preparedStatement.setInt(2,userId);
            preparedStatement.executeUpdate();
            if(!preparedStatement.isClosed()){
                preparedStatement.close();
            }

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

        String sql="select balance from users where id=?";

        String sql2="insert into transactions(user_from,user_to,transaction_amount) values(?,?,?)";
        try {

            PreparedStatement preparedStatement=connection.prepareStatement(sql);
            preparedStatement.setInt(1,userFrom);

            ResultSet resultSet=preparedStatement.executeQuery();
            resultSet.next();

            int balance=resultSet.getInt(1);

            if(!resultSet.isClosed()) {
                resultSet.close();
            }

            if(balance>amount) {
                cashFlow(userFrom, -amount);
                cashFlow(userTo, amount);

                PreparedStatement preparedStatement2=connection.prepareStatement(sql2);
                preparedStatement2.setInt(1,userFrom);
                preparedStatement2.setInt(2,userTo);
                preparedStatement2.setDouble(3,amount);
                preparedStatement2.execute();

            }else{
                System.out.println("Sorry, your balance isn't enough to complete transaction");
                throw new Exception("You don't have enough money on your balance");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    static List<User> listUsers() {

        String sql = "select * from users";

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
}

