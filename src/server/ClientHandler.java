package src.server;



import src.server.auth.AuthService;
import src.server.auth.HandShake;
import src.server.auth.PasswordManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;


public class ClientHandler {

    private HandShake handShake;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;

    private int id;
    private String nickname;


    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }


    public ClientHandler(Server server, Socket socket) {

        try {
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                String inStr;
                while (true) {
                    try {
                        inStr = in.readUTF();
                        if (inStr.startsWith("/src/server/auth ")) {
                            String[] words = inStr.split(" ");
                            String strId = null;
                            String login = words[1];
                            String pass = PasswordManager.encodePassword(words[2]);
                            try {
                                nickname = AuthService.getFieldByLoginAndPass("nickname", login, pass);
                                strId = AuthService.getFieldByLoginAndPass("id", login, pass);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            if (strId != null) {
                                id = Integer.parseInt(strId);
                            }

                            if (nickname != null) {
                                if (server.isSubscribed(this)) {
                                    out.writeUTF("/repeatUser");
                                } else {
                                    out.writeUTF("/authok");
                                }
                            } else {
                                out.writeUTF("/no_auth");
                            }

                        } else if (inStr.startsWith("/end")) {
                            if (server.isSubscribed(this))
                                server.unsubscribe(this);
                            if (inStr.equals("end0"))
                                break;
                        } else if (inStr.startsWith("/log_in ")) {
                            String[] fields = inStr.split(" ");
                            String nick = fields[1];
                            String login = fields[2];
                            String pass = PasswordManager.encodePassword(fields[3]);
                            try {
                                if (AuthService.getIdByField("nickname", nick) != null) {
                                    out.writeUTF("/log_in_no_nick");
                                } else if (AuthService.getIdByField("login", login) != null) {
                                    out.writeUTF("/log_in_no_login");
                                } else {
                                    out.writeUTF("/log_in_ok");
                                    AuthService.addUser(nick, login, pass, "FALSE");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else if (inStr.startsWith("/black_msg")) {

                            String[] strArr = inStr.split(" ");
                            String nickToCheck = strArr[1];
                            try {
                                Integer idBlack = AuthService.getIdByField("nickname", nickToCheck);
                                if (AuthService.isBlack(idBlack, id)) {
                                    AuthService.removeBlack(id, idBlack);
                                } else {
                                    AuthService.addBlack(id, idBlack);
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else if (inStr.startsWith("/blackCheck ")) {
                            String[] strArr = inStr.split(" ");
                            String nickToCheck = strArr[1];
                            try {
                                Integer idBlack = AuthService.getIdByField("nickname", nickToCheck);
                                if (AuthService.isBlack(idBlack, id)) {
                                    out.writeUTF("/black");
                                } else {
                                    out.writeUTF("/black_no");
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        } else if (inStr.startsWith("/make_black")) {
                            String[] strArr = inStr.split(" ");
                            String nick = strArr[1];
                            Integer idBlack = null;
                            try {
                                idBlack = AuthService.getIdByField("nickName", nick);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            if (inStr.startsWith("/make_black_no ")) {
                                AuthService.removeBlack(id, idBlack);
                            } else {
                                AuthService.addBlack(id, idBlack);
                            }
                        } else if (inStr.startsWith("/msg_server ")) {
                            String msg = inStr.substring(12);
                            String result = checkParamFromMsg(msg);
                            if (result.equals("//noParam")) {
                                server.broadcastMsg(this, msg);
                            } else if (result.equals("//notFind")) {

                            } else {
                                String nick = result;
                                ClientHandler receiver = server.getClientByNick(nick);
                                server.broadcastMsg(this, receiver, msg.substring(3 + nick.length()));
                            }
                        } else if (inStr.startsWith("/change_pass")) {
                            String[] words = inStr.split(" ");
                            if (AuthService.checkMatchPass(id, PasswordManager.encodePassword(words[1]))) {
                                String newPass = PasswordManager.encodePassword(words[2]);
                                AuthService.changeUserPass(id, newPass);
                                out.writeUTF("/change_pass_ok");
                            } else {
                                out.writeUTF("/err_change_pass");
                            }
                        } else if (inStr.startsWith("/interrupt")) {
                            out.writeUTF("/interrupt");
                        } else if (inStr.startsWith("/change_nick")) {
                            String[] words = inStr.split(" ");
                            String newNickName = words[1];
                            if (AuthService.getIdByField("nickname", newNickName) == null) {
                                AuthService.changeUserNick(id, newNickName);
                                nickname = newNickName;
                                out.writeUTF("/change_nick_ok");
                            } else {
                                out.writeUTF("/err_change_nick");
                            }
                        } else if (inStr.startsWith("/createUserByAdmin")) {
                            if(AuthService.getSuper(id).equals("TRUE")){
                                String[] data = inStr.split(" ");
                                String nick = data[1];
                                String login = data[2];
                                String pass = PasswordManager.encodePassword(data[3]);
                                String superUser = data[4];
                                if (superUser.equals("user")) superUser = "FALSE";
                                if (superUser.equals("super")) superUser = "TRUE";
                                if (AuthService.getIdByField("nickname", nick) == null &&
                                        AuthService.getIdByField("login", login) == null) {
                                    AuthService.addUser(nick, login, pass, superUser);
                                    out.writeUTF("/create_user_ok");
                                } else {
                                    out.writeUTF("/err_create_user");
                                }
                            } else {
                                out.writeUTF("/err_create_user");
                            }

                        } else if (inStr.startsWith("/remove_user_by_admin")) {
                            if(AuthService.getSuper(id).equals("TRUE")){
                                String[] words = inStr.split(" ");
                                Integer userId = AuthService.getIdByField(words[1], words[2]);
                                if (userId != null) {
                                    if (userId == id) {
                                        out.writeUTF("/err_remove_self");
                                    } else {
                                        AuthService.removeUser(userId);
                                        out.writeUTF("/remove_user_ok");
                                    }
                                } else {
                                    out.writeUTF("/err_remove_user");
                                }
                            } else {
                                out.writeUTF("/err_remove_user");
                            }

                        } else if (inStr.startsWith("/edit_user_by_admin")) {
                            if (AuthService.getSuper(id).equals("TRUE")){
                                String[] data = inStr.split(" ");
                                String field = data[1];
                                String fieldValue = data[2];
                                String newLogin = data[3];
                                String newNickname = data[4];
                                String newPass = PasswordManager.encodePassword(data[5]);
                                String newPrivilegies = data[6];
                                if (newPrivilegies.equals("user")) newPrivilegies = "FALSE";
                                if (newPrivilegies.equals("super")) newPrivilegies = "TRUE";
                                Integer userId = AuthService.getIdByField(field, fieldValue);
                                if (userId != null && userId != id) {
                                    AuthService.editUser(userId, newLogin, newNickname, newPass, newPrivilegies);
                                    out.writeUTF("/edit_user_ok");
                                } else {
                                    out.writeUTF("/err_edit_user");
                                }
                            }else{
                                out.writeUTF("/err_edit_user");
                            }

                        } else if (inStr.startsWith("/user_list_online")){
                            StringBuilder s = new StringBuilder();
                            Vector<Integer> users = server.getIds();
                            for (Integer userId:users) {
                                s.append(AuthService.getInfoForUserById(userId));
                            }
                            out.writeUTF("/user_list_o " + s);
                        } else if (inStr.startsWith("/user_list_super")){
                            if(AuthService.getSuper(id).equals("TRUE")){
                                String usersInfo = AuthService.getInfoForSuperByID();
                                out.writeUTF("/user_list_s " + usersInfo);
                            }
                        } else if (inStr.startsWith("/check_me")){
                            String sup = AuthService.getSuper(id);
                            out.writeUTF("/super_is " + sup);
                        } else if (inStr.startsWith("/q_expression")){
                            handShake = AuthService.getHandShake();
                            out.writeUTF("/a_expression!" + handShake.getExpression());
                        } else if(inStr.startsWith("/q_solution")){
                            if(handShake != null){
                                if(inStr.split(" ")[1].equals(String.valueOf(handShake.getSolution()))){
                                    out.writeUTF("/a_solution_ok");
                                    server.subscribe(this);
                                } else {
                                    out.writeUTF("/err_a_solution");
                                }
                            }
                        }
                    } catch (IOException | SQLException e) {
                        try {
                            in.close();
                            out.close();
                            socket.close();
                            break;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String nickname, String msg) {
        try {
            out.writeUTF("/msg_client " + nickname + ": " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSelfMsg(String nickname, String msg) {
        try {
            out.writeUTF("/msg_client /self " + nickname + ": " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String checkParamFromMsg(String msg) {
        Integer id;
        if (msg.length() > 2) {
            if (msg.substring(0, 3).equals("/w ")) {

                String nickAndMsg = msg.substring(3);
                int endIndex = nickAndMsg.indexOf(' ');
                String nick = nickAndMsg.substring(0, endIndex);
                try {
                    id = AuthService.getIdByField("nickname", nick);
                } catch (SQLException | NullPointerException e) {
                    return "//notFind";
                }
                if (server.isSubscribed(id)) {
                    return nick;
                } else {
                    return "//notFind";
                }
            }
        }
        return "//noParam";

    }

}