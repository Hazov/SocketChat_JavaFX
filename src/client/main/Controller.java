package src.client.main;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;


public class Controller implements Initializable {


    public ToolBar toolBar2;
    public Button sendBtn;
    public ToolBar toolBar;
    public Label nameApp;
    public TextArea userField;
    public Button authBtn;
    public TextField loginField;
    public PasswordField passField;
    public Label authErrLabel;
    public VBox authBox;
    public VBox chatBox;
    public ScrollPane scrollMsg;
    public StackPane stackPane;
    public Button authLogInBtn;
    public TextField logInNickField;
    public TextField logInloginField;
    public TextField logInPassField;
    public TextField loginRePassField;
    public Button endLogInBtn;
    public Label logInErrLabel;
    public VBox logInBox;
    public Hyperlink toAuthHyper;
    public Pane successfulBox;
    public VBox settingsBox;
    public VBox changePassBox;
    public TextField oldPassField;
    public TextField newPassField;
    public TextField repeatNewPassField;
    public Label errChangePass;
    public Button toAuthBtn;
    public Label dateTimeLabel;
    public Button changeNickBtn;
    public Button acceptChangeNick;
    public TextField newNickField;
    public Label errChangeNickLabel;
    public VBox changeNickBox;
    public StackPane stackSettings;
    public TextField nickToCreateField;
    public TextField loginToCreateField;
    public PasswordField passToCreateField;
    public VBox createUserBox;
    public Button acceptCreateUserBtn;
    public Button addUserBySuperBtn;
    public Button removeUserBySuperBtn;
    public Button editUserBySuperBtn;
    public Button infoUserBySuperBtn;
    public ToggleGroup toogleUser;
    public RadioButton rUser;
    public RadioButton rSuper;
    public Label errCreateUserLabel;
    public VBox removeUserBox;
    public ToggleGroup toogleDeleteUser;
    public TextField loginToRemoveField;
    public TextField nickToRemoveField;
    public HBox removeByLoginBox;
    public HBox removeByNickBox;
    public RadioButton rbByLogin;
    public RadioButton rbByNick;
    public Label errRemoveUserLabel;
    public RadioButton rbByLoginEditUser;
    public TextField byLoginEditUserField;
    public RadioButton rbByNickNameEditUser;
    public TextField byNickNameEditUserField;
    public TextField newLoginEditUserField;
    public TextField newNickEditUserField;
    public TextField newPassEditUserField;
    public Button acceptEditUserByAdmin;
    public Label errEditUserLabel;
    public VBox setEditBox;
    public HBox byLoginEditBox;
    public HBox byNickEditBox;
    public RadioButton rbSuperEdit;
    public RadioButton rbUserEdit;
    public VBox userListToUser;
    public VBox infoUserBox;
    public VBox infoUserBySuperBox;
    public VBox userListToSuper;
    public VBox userMenuSetBox;
    public VBox superMenuSetBox;
    public HBox expressionLine;
    public TextField solutionField;
    public Label authExpressionLbl;


    @FXML
    VBox mainPane;
    @FXML
    Button btnClose;
    @FXML
    VBox msgs;

    private final String ADDRESS = "localhost";
    private final int PORT = 7777;
    private Socket socket = null;
    private DataInputStream in;
    private DataOutputStream out;

    private Pane currentBox = null;
    private Pane currentSetBox = null;
    boolean checkRadio = false;

    private String command = "";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Date date = new Date();
        try {
            socket = new Socket(ADDRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            currentBox = authBox;
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(()->{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy           HH:mm:ss");
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1), event -> dateTimeLabel.setText(LocalDateTime.now().format(dtf)))
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }).start();
    }

    public void connect() {
        Platform.runLater(() -> {
            if (socket.isClosed()) {
                try {
                    socket = new Socket(ADDRESS, PORT);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    currentBox = authBox;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (loginField.getText().equals("")) {
                authErrLabel.setText("Введите логин");
                return;
            }
            if (passField.getText().equals("")) {
                authErrLabel.setText("Введите пароль");
                return;
            }
            try {
                out.writeUTF("/src/server/auth " + loginField.getText() + " " + passField.getText().hashCode());
                String s = in.readUTF();
                if (s.equals("/authok")) {
                    out.writeUTF("/q_expression");
                    String newExpression = in.readUTF().split("!")[1];
                    authExpressionLbl.setText(newExpression);
                    expressionLine.setDisable(false);
                    expressionLine.setVisible(true);
                    solutionField.requestFocus();

                } else if (s.equals("/no_auth")) {
                    authErrLabel.setText("Неверный логин/пароль");
                    expressionLine.setDisable(true);
                    expressionLine.setVisible(false);
                } else if (s.equals("/repeatUser")) {
                    authErrLabel.setText("Этот пользователь уже в сети");
                    expressionLine.setDisable(true);
                    expressionLine.setVisible(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void authorized() {
        receiveMessages();
        enterToChat().play();
    }


    @FXML
    public void close(ActionEvent actionEvent) {
        try {
            out.writeUTF("/end0");
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.exit();
    }

    @FXML
    public void sysMini(MouseEvent dragEvent) {
        btnClose.setStyle("-fx-background-size: 43 43;");
    }

    @FXML
    public void sysSt(MouseEvent mouseDragEvent) {
        btnClose.setStyle("-fx-background-size: 45 45;");
    }


    //send
    @FXML
    public void sendMsgOnBtn(ActionEvent actionEvent) {
        sendMsg();
    }

    @FXML
    public void sendMsgOnKeys(KeyEvent keyEvent) {
        KeyCodeCombination kComb = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        if (kComb.match(keyEvent)) {
            sendMsg();
        }
    }

    private void sendMsg() {
        String msg = userField.getText();

        try {
            if (msg.startsWith("/black ")) {
                out.writeUTF("/black_msg " + msg.substring(7));
            } else if (msg.startsWith("/end ")) {
                out.writeUTF("/end ");
                socket.close();
                in.close();
                out.close();
                endSession();
            } else {
                out.writeUTF("/msg_server " + msg);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        userField.clear();
        userField.requestFocus();
    }

    @FXML
    private Animation enterToChat() {
        Timeline timeline = new Timeline();
        chatBox.setVisible(true);
        chatBox.setDisable(false);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(chatBox.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(800), new KeyValue(chatBox.opacityProperty(), 1)),
                new KeyFrame(Duration.ZERO, new KeyValue(currentBox.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(800), new KeyValue(currentBox.opacityProperty(), 0))
        );
        authBox.setVisible(false);
        authBox.setDisable(true);
        currentBox = chatBox;
        userField.requestFocus();
        return timeline;

    }

    public void endSession(){
        try {
            out.writeUTF("/interrupt");
            msgs.getChildren().remove(0, msgs.getChildren().size());
            out.writeUTF("/end");
        } catch (IOException e) {
            e.printStackTrace();
        }
        enterToAuth(false).play();
    }

    public void backToAuth() {
        enterToAuth(false).play();
    }

    @FXML
    private Animation enterToAuth(boolean successful) {

        Timeline timeline = new Timeline();
        authBox.setVisible(true);
        authBox.setDisable(false);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(authBox.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(800), new KeyValue(authBox.opacityProperty(), 1)),
                new KeyFrame(Duration.ZERO, new KeyValue(currentBox.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(800), new KeyValue(currentBox.opacityProperty(), 0))
        );
        if (successful) {
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(200), new KeyValue(successfulBox.opacityProperty(), 0)),
                    new KeyFrame(Duration.millis(400), new KeyValue(successfulBox.opacityProperty(), 1)),
                    new KeyFrame(Duration.millis(1000), new KeyValue(successfulBox.opacityProperty(), 0))
            );
        }
        currentBox.setVisible(false);
        currentBox.setDisable(true);
        authErrLabel.setText("");
        loginField.requestFocus();
        loginField.clear();
        passField.clear();
        userField.clear();
        return timeline;
    }

    public void loginView(ActionEvent actionEvent) {
        enterToLogIn().play();
    }

    private Animation enterToLogIn() {
        Timeline timeline = new Timeline();
        logInBox.setVisible(true);
        logInBox.setDisable(false);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(logInBox.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(800), new KeyValue(logInBox.opacityProperty(), 1)),
                new KeyFrame(Duration.ZERO, new KeyValue(authBox.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(800), new KeyValue(authBox.opacityProperty(), 0))
        );
        authBox.setVisible(false);
        authBox.setDisable(true);
        userField.requestFocus();
        this.currentBox = logInBox;
        return timeline;
    }


    private void receiveMessages() {

        new Thread(() -> {
            String msg;
            while (true) {
                try {
                    msg = in.readUTF();
                    if (msg.startsWith("/interrupt")) {
                        break;
                    }
                    if (msg.startsWith("/black")) {
                        command = msg;
                    }

                    if (msg.startsWith("/msg_client ")) {
                        msg = msg.substring(12);
                        addMsgToView(msg);
                    }

                } catch (IOException e) {
                    break;
                }
            }
        }).start();
    }


    private void addMsgToView(String fullMsg) {
        String backgroundCSS;
        Pos msgPos;
        boolean haveBlack;
        if (fullMsg.startsWith("/self ")) {
            fullMsg = fullMsg.substring(5);
            msgPos = Pos.CENTER_LEFT;
            backgroundCSS = "-fx-background-color: #19448410;";
            haveBlack = false;
        } else {
            msgPos = Pos.CENTER_RIGHT;
            backgroundCSS = "-fx-background-color: #9c3f1e10;";
            haveBlack = true;
        }
        String nickAndMsg = fullMsg;
        int separateIndex = fullMsg.indexOf(':');
        String nick = nickAndMsg.substring(0, separateIndex);

        Platform.runLater(() -> {
            Hyperlink nickLink = new Hyperlink(nick);
            MenuItem blackItem = new MenuItem();
            ContextMenu contextMenu = new ContextMenu();
            if (haveBlack) {
                contextMenu.getItems().add(blackItem);
            }
            nickLink.setOnContextMenuRequested(event -> {

                String nickToCheck = nickLink.getText();
                try {
                    out.writeUTF("/blackCheck " + nickToCheck);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String answer = command;
                if (answer.equals("/black_no")) {
                    blackItem.setText("Добавить в черный список");
                } else if (answer.equals("/black")) {
                    blackItem.setText("Убрать из черного списка");
                }

                contextMenu.show(nickLink, event.getScreenX(), event.getScreenY());
            });
            blackItem.setOnAction(event -> {
                String nickToMake = nickLink.getText();
                if (blackItem.getText().equals("Добавить в черный список")) {
                    try {
                        out.writeUTF("/make_black " + nickToMake);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        out.writeUTF("/make_black_no " + nickToMake);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Label text = new Label(nickAndMsg.substring(separateIndex));
            HBox msgLine = new HBox(nickLink, text);
            msgLine.setAlignment(msgPos);
            msgs.getChildren().add(msgLine);
            msgLine.setStyle(backgroundCSS);
            msgLine.setPadding(new Insets(5, 0, 5, 0));
            scrollMsg.vvalueProperty().bind(msgs.heightProperty());
        });
    }


    public void tryToLogin(ActionEvent actionEvent) {

        String nickName = logInNickField.getText();
        String login = logInloginField.getText();
        Integer password = logInPassField.getText().hashCode();
        Integer rePassword = loginRePassField.getText().hashCode();

        if (!password.equals(rePassword)) {
            logInErrLabel.setText("Пароли не совпадают");
            return;
        }

        try {
            out.writeUTF("/log_in " + nickName + " " + login + " " + password);
            String answer = in.readUTF();
            if (answer.startsWith("/log_in_no_nick")) {
                logInErrLabel.setText("Такой ник уже занят");
            } else if (answer.startsWith("/log_in_no_login")) {
                logInErrLabel.setText("Такой логин уже используется");
            } else if (answer.startsWith("/log_in_ok")) {
                logIn();
                enterToAuth(true).play();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logIn() {
        logInloginField.clear();
        logInPassField.clear();
        loginRePassField.clear();
        logInNickField.clear();


    }

    public void toSettings(ActionEvent actionEvent) {
        try {
            out.writeUTF("/interrupt");
            enterToSettings().play();
            out.writeUTF("/check_me");
            String s = in.readUTF();
            if(s.startsWith("/super_is")){
                String[] words = s.split(" ");
                String bool = words[1];
                if(bool.equals("TRUE")){
                    superMenuSetBox.setVisible(true);
                    superMenuSetBox.setDisable(false);
                }
                else {
                    superMenuSetBox.setVisible(false);
                    superMenuSetBox.setDisable(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Animation enterToSettings() {
        Timeline timeline = new Timeline();
        currentBox.setVisible(false);
        currentBox.setDisable(true);
        settingsBox.setVisible(true);
        settingsBox.setDisable(false);
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(currentBox.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(800), new KeyValue(currentBox.opacityProperty(), 0)),
                new KeyFrame(Duration.ZERO, new KeyValue(settingsBox.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(800), new KeyValue(settingsBox.opacityProperty(), 1))
        );
        currentBox = settingsBox;
        return timeline;
    }

    public void showChangePassBox(ActionEvent actionEvent) {
        showSetBox(changePassBox);
    }

    private void showSetBox(Pane pane) {
        stackSettings.getChildren().forEach(node -> {if(node!=pane)node.setVisible(false);});
        stackSettings.getChildren().forEach(node -> {if(node!=pane)node.setDisable(true);});

        pane.setDisable(!pane.isDisabled());
        pane.setVisible(!pane.isVisible());
        currentSetBox = pane;

    }

    public void tryToChangePass(ActionEvent actionEvent) throws IOException {
        Integer oldPass = oldPassField.getText().hashCode();
        Integer newPass = newPassField.getText().hashCode();
        Integer repeatNewPass = repeatNewPassField.getText().hashCode();


        if (newPass.equals(repeatNewPass)) {
            if (!oldPass.equals(newPass)) {
                out.writeUTF("/change_pass " + oldPass + " " + newPass);
                String answer = in.readUTF();
                if (answer.startsWith("/change_pass_ok")) {
                    clearFieldsToChangePass(true);
                    hideCurrentSetBox();
                    errChangePass.setVisible(false);
                } else if(answer.equals("/err_change_pass")){
                    errChangePass.setVisible(true);
                }
            } else {
                errChangePass.setVisible(true);
                clearFieldsToChangePass(false);
                return;
            }
        } else {
            errChangePass.setVisible(true);
            clearFieldsToChangePass(false);
            return;
        }
    }

    public void clearFieldsToChangePass(boolean clearErr) {
        oldPassField.clear();
        newPassField.clear();
        repeatNewPassField.clear();
        if (clearErr) {
            errChangePass.setVisible(false);
        }
    }

    public void changeNick(ActionEvent actionEvent) {
        String newNick = newNickField.getText();
        if(!newNick.equals("")){
            try {
                out.writeUTF("/change_nick " + newNick);
                String answer = in.readUTF();
                if(answer.equals("/change_nick_ok")){
                    newNickField.clear();
                    hideCurrentSetBox();
                    errChangeNickLabel.setText("");
                    showSuccessful();
                } else if (answer.equals("/err_change_nick")){
                    errChangeNickLabel.setText("Такой ник уже существует!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showChangeNickBox(ActionEvent actionEvent) {
        showSetBox(changeNickBox);
    }
    public void hideCurrentSetBox(){
        currentSetBox.setVisible(false);
        currentSetBox.setDisable(true);
        currentSetBox = null;
    }

    public void showCreateUserBox(ActionEvent actionEvent) {
        showSetBox(createUserBox);
    }

    public void createUser(ActionEvent actionEvent) {
        String nickName = nickToCreateField.getText();
        String login = loginToCreateField.getText();
        String pass = passToCreateField.getText();
        RadioButton rb = (RadioButton) toogleUser.getSelectedToggle();
        String toogle = "";
        if(rb == rSuper) toogle = "super";
        if(rb == rUser) toogle = "user";

        try {
            out.writeUTF("/createUserByAdmin " + nickName + " " + login + " " + pass + " " + toogle);
            String answer = in.readUTF();
            if(answer.equals("/create_user_ok")){
                hideCurrentSetBox();
                errCreateUserLabel.setText("");
                showSuccessful();
            } else if (answer.equals("/err_create_user")){
                errCreateUserLabel.setText("Пользователь с таким именем или логином уже занят!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void showRemoveUserBox(ActionEvent actionEvent) {
        showSetBox(removeUserBox);
        checkRadio = true;
        new Thread(() -> {
            while(checkRadio){
                if(rbByLogin.isSelected()){
                    removeByLoginBox.setDisable(false);
                    removeByNickBox.setDisable(true);
                } else {
                    removeByLoginBox.setDisable(true);
                    removeByNickBox.setDisable(false);
                }
            }
        }).start();

    }

    public void removeUserByAdmin(ActionEvent actionEvent) {
        String s = "";
        if(rbByLogin.isSelected()) s = "login " + loginToRemoveField.getText();
        if(rbByNick.isSelected()) s = "nickname " + nickToRemoveField.getText();
        try {
            out.writeUTF("/remove_user_by_admin " + s);
            String answer = in.readUTF();
            if(answer.equals("/remove_user_ok")){
                errRemoveUserLabel.setText("");
                hideCurrentSetBox();
                checkRadio = false;
                showSuccessful();
            } else if (answer.equals("/err_remove_user")){
                errRemoveUserLabel.setText("Такого пользователя не существует!");
            } else if(answer.equals("/err_remove_self")){
                errRemoveUserLabel.setText("Вы не можете удалить самого себя!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSetEditBox(ActionEvent actionEvent) {
        showSetBox(setEditBox);
        new Thread(() -> {
            checkRadio = true;
            while(checkRadio){
                if(rbByLoginEditUser.isSelected()){
                    byLoginEditBox.setDisable(false);
                    byNickEditBox.setDisable(true);
                } else {
                    byLoginEditBox.setDisable(true);
                    byNickEditBox.setDisable(false);
                }
            }
        }).start();
    }

    public void acceptEditUserByAdmin(ActionEvent actionEvent) {
        String field = "";
        String login = newLoginEditUserField.getText();
        String nickname = newNickEditUserField.getText();
        String pass = newPassEditUserField.getText();
        String privilegies = "nothing";

        if(rbSuperEdit.isSelected()) privilegies = "super";
        if(rbUserEdit.isSelected()) privilegies = "user";
        if(rbByLoginEditUser.isSelected()) field = "login " + byLoginEditUserField.getText();
        if(rbByNickNameEditUser.isSelected()) field = "nickname " + byNickNameEditUserField.getText();

        if(login.equals("")) login = "nothing";
        if(nickname.equals("")) nickname = "nothing";
        if(pass.equals("")) pass = "nothing"; else pass = String.valueOf(pass.hashCode());
        String s = "/edit_user_by_admin " + field + " " + login + " " + nickname + " " + pass + " " + privilegies;


        try {
            out.writeUTF(s);
            String answer = in.readUTF();
            if(answer.equals("/edit_user_ok")){
                clearFieldToEditUser();
                hideCurrentSetBox();
                checkRadio = false;
                errEditUserLabel.setText("");
                showSuccessful();
            }else if(answer.equals("/err_edit_user")){
                errEditUserLabel.setText("Такого пользователя не существует!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showSuccessful() {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.millis(200), new KeyValue(successfulBox.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(400), new KeyValue(successfulBox.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(1000), new KeyValue(successfulBox.opacityProperty(), 0))
        );
        timeline.play();
    }

    private void clearFieldToEditUser() {
        newLoginEditUserField.clear();
        newNickEditUserField.clear();
        newPassEditUserField.clear();
        byLoginEditUserField.clear();
        byNickNameEditUserField.clear();
    }


    public void showInfoUserBox(ActionEvent actionEvent) {
        showSetBox(infoUserBox);
    }

    public void refreshUserListForUser(ActionEvent actionEvent) {
        userListToUser.getChildren().removeAll(userListToUser.getChildren());
        String s;
        try {
            out.writeUTF("/user_list_online");
            s = in.readUTF();
            if(s.startsWith("/user_list_o")) {
                HBox hbox = new HBox();
                hbox.getChildren().addAll(new Label("ЛОГИН"), new Label("НИКНЕЙМ"), new Label("ПРИВИЛЕГИИ"));
                hbox.setSpacing(100);
                hbox.getChildren().forEach(node -> {node.minWidth(50);});
                userListToUser.getChildren().add(hbox);
                String[] data = s.split(" ");
                int count = 1;
                while(count < data.length){
                    HBox h = new HBox();
                    h.getChildren().addAll(new Label(data[count]), new Label(data[count+1]), new Label(data[count+2]));
                    for (Node node : h.getChildren()) {
                        Label label = (Label) node;
                        label.setPrefWidth(80);
                    }
                    h.setSpacing(100);
                    userListToUser.getChildren().add(h);
                    count+=3;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showInfoUserBySuperBox(ActionEvent actionEvent) {
        showSetBox(infoUserBySuperBox);
    }

    public void refreshUserListForSuper(ActionEvent actionEvent) {
        userListToSuper.getChildren().removeAll(userListToSuper.getChildren());
        String s;
        try {
            out.writeUTF("/user_list_super");
            s = in.readUTF();
            if(s.startsWith("/user_list_s")) {
                HBox hbox = new HBox();
                hbox.getChildren().addAll(new Label("ЛОГИН"), new Label("НИКНЕЙМ"), new Label("ПРИВИЛЕГИИ"));
                hbox.setSpacing(100);
                hbox.getChildren().forEach(node -> {node.minWidth(50);});
                userListToSuper.getChildren().add(hbox);
                String[] data = s.split(" ");
                int count = 1;
                while(count < data.length){
                    HBox h = new HBox();
                    h.getChildren().addAll(new Label(data[count]), new Label(data[count+1]), new Label(data[count+2]));
                    for (Node node : h.getChildren()) {
                        Label label = (Label) node;
                        label.setPrefWidth(80);
                    }
                    h.setSpacing(100);
                    userListToSuper.getChildren().add(h);
                    count+=3;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSolution(ActionEvent actionEvent) throws IOException {
        out.writeUTF("/q_solution " + solutionField.getText());
        String answer = in.readUTF();
        if(answer.equals("/a_solution_ok")){
            authorized();
        } else if (answer.equals("/err_a_solution")){
            authErrLabel.setText("Допущена ошибка");
            loginField.requestFocus();
        }
        expressionLine.setVisible(false);
        expressionLine.setDisable(true);
        solutionField.clear();
        loginField.clear();
        passField.clear();
        authExpressionLbl.setText("");
    }
}


