package jdbc;

import org.sql2o.Sql2o;

import java.sql.*;
import java.util.Map;

public class DataBaseConnections {
    static Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5438/postgres","postgres","postgres");
    public static void main(String[] args) throws SQLException {
        insertUserBySql2o(new User(321, "User inserted 321"));
    }
    public static void getUserByJDBC() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5438/postgres","postgres","postgres")) {
            PreparedStatement preparedStatement = connection.prepareStatement("select*from public.users where user_id = ?");
            preparedStatement.setInt(1, 7);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println("Users:");
                System.out.println(resultSet.getString("name") + " with id=" + resultSet.getString("user_id"));
            }
        }
    }

    public static void insertUserByJDBC() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5438/postgres","postgres","postgres")) {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into users(user_id, name) values (?, ?)");
            preparedStatement.setInt(1, 222);
            preparedStatement.setString(2, "User 222 inserted");
            preparedStatement.executeUpdate();
        }
    }

    public static void getUserBySql2o() throws SQLException {
        sql2o.setDefaultColumnMappings(Map.of("user_id", "userId"));
        try (org.sql2o.Connection connection = sql2o.open()) {
            System.out.println(connection.createQuery("select*from users where user_id = :id")
                    .addParameter("id", 7)
                    .executeAndFetch(User.class));
        }
    }

    public static void insertUserBySql2o(User user) throws SQLException {
        try (org.sql2o.Connection connection = sql2o.beginTransaction()) {
            connection.createQuery("insert into users(user_id, name) values (:userId, :name)")
                    .bind(user)
                    .executeUpdate();
            connection.commit();
        }
    }
}
