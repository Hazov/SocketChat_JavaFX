package src.server.auth;

import java.sql.*;


public class AuthService {
    static Connection connection;
    static Statement statement;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:1.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getFieldByLoginAndPass(String field, String login, String pass) throws SQLException {
        String qry = String.format("SELECT %s FROM users WHERE login = '%s' AND pass = '%s'", field, login, pass);
        ResultSet rs;
        rs = statement.executeQuery(qry);
        if(rs.next()){
            return rs.getString(field);
        }
        return null;
    }

    public static Integer getIdByField(String field, String value) throws SQLException {
        String qry = String.format("SELECT id FROM users WHERE %s = '%s'", field, value);
        ResultSet rs;
        rs = statement.executeQuery(qry);
        if(rs.next()){
            return Integer.parseInt(rs.getString("id"));
        }
        return null;
    }

    public static void addUser(String nickname, String login, String pass, String superUser) {
        String qry = String.format("INSERT INTO users (login, pass, nickname, super)\n" +
                "VALUES ('%s', '%s', '%s', '%s')", login, pass, nickname, superUser);
        try {
            statement.execute(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isBlack(Integer user_id_black, Integer user_id) throws SQLException {
        String qry = String.format("SELECT user_id_black FROM blacklist WHERE user_id = '%d' AND user_id_black = '%d'", user_id, user_id_black);
        ResultSet rs;
        rs = statement.executeQuery(qry);
        String result = null;
        if(rs.next()){
            result = rs.getString("user_id_black");
        }
        return result != null;
    }

    public static void addBlack(Integer user_id, Integer user_id_black){
        if(user_id != null || user_id_black != null) {
            String qry = String.format("INSERT INTO blacklist (user_id, user_id_black)\n" +
                    "VALUES ('%d', '%d')", user_id, user_id_black);
            try {
                statement.execute(qry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeBlack(Integer user_id, Integer user_id_black) {
        if(user_id != null || user_id_black != null) {
            String qry = String.format("DELETE FROM blacklist \n" +
                    "WHERE user_id = '%d' AND user_id_black = '%d'", user_id, user_id_black);
            try {
                statement.execute(qry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void ShowAllUsersInHTML(){
        String qry = "SELECT * FROM users";
        ResultSet rs;
    }
    public static boolean checkMatchPass(int id, String oldp) throws SQLException {
        String qry = String.format("SELECT pass FROM users WHERE id = %s", id);
        ResultSet rs;
        rs = statement.executeQuery(qry);
        if(rs.next()){
            String oldpass = rs.getString("pass");
            if(oldpass.equals(oldp))
                return true;
        }
        return false;
    }

    public static void changeUserPass(int id, String newPass) {
        String qry = String.format("UPDATE users \n" +
                "SET pass = %s\n" +
                "WHERE id = %s", newPass, id);
        try {
            statement.execute(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void changeUserNick(int id, String nickname) {
        String qry = String.format("UPDATE users \n" +
                "SET nickname = %s\n" +
                "WHERE id = %s", nickname, id);
    }


    public static void removeUser(Integer userId) {
        String qry = String.format("DELETE FROM users WHERE id = %s", userId);
        try {
            statement.execute(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void editUser(Integer userId, String newLogin, String newNickname, String newPass, String newPrivilegies) {
        String qry = "UPDATE users SET ";
        if(!newLogin.equals("nothing")) qry += String.format("login = '%s', ", newLogin);
        if(!newNickname.equals("nothing")) qry += String.format("nickname = '%s', ", newNickname);
        if(!newPass.equals("nothing")) qry += String.format("pass = '%s', ", newPass);
        qry += String.format("super = '%s' WHERE id = %s", newPrivilegies, userId);
        try {
            statement.execute(qry);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String getInfoForUserById(Integer id) {
        String qry = String.format("SELECT login, nickname, super FROM users WHERE id = %s ", id);
        StringBuilder s = new StringBuilder();
        try {
            ResultSet rs = statement.executeQuery(qry);
            while (rs.next()){
                s.append(rs.getString("login")).append(" ");
                s.append(rs.getString("nickname")).append(" ");
                if(rs.getString("super").equals("TRUE")) s.append("super" + " ");
                else s.append("user" + " ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s.toString();

    }

    public static String getInfoForSuperByID() {
        String qry = "SELECT login, nickname, pass, super FROM users";
        StringBuilder s = new StringBuilder();
        try {
            ResultSet rs = statement.executeQuery(qry);
            while (rs.next()) {
                s.append(rs.getString("login")).append(" ");
                s.append(rs.getString("nickname")).append(" ");
                if (rs.getString("super").equals("TRUE")) s.append("super" + " ");
                else s.append("user" + " ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s.toString();
    }

    public static String getSuper(int id) {
        String qry = String.format("SELECT super FROM users WHERE id = %s", id);
        String sup = "";
        try {
            ResultSet rs = statement.executeQuery(qry);
            if(rs.next()){
                sup = rs.getString("super");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sup;
    }
    public static HandShake getHandShake(){
        return new HandShake();
    }
}
