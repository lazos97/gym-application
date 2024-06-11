package main.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.regex.Matcher;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.helpers.FXMLHelper;
import main.database;
import main.helpers.UserStoreHelper;

public class AuthController implements Initializable {

    @FXML
    private Label edit_label;

    @FXML
    private AnchorPane login_form;

    @FXML
    private PasswordField si_password;

    @FXML
    private TextField si_username;

    @FXML
    private TextField su_email;

    @FXML
    private TextField su_address;

    @FXML
    private PasswordField su_password;

    @FXML
    private TextField su_username;

    @FXML
    private AnchorPane sub_form;

    @FXML
    private Button sub_loginBtn;

    @FXML
    private Button sub_signupBtn;

    //DATABASE ΕΡΓΑΛΕΙΑ. ΕΓΙΝΑΝ ΚΑΙ IMPORT ΞΕΧΩΡΙΣΤΑ.
    private Connection connect;
    private PreparedStatement prepare;

    public void login() {

        String sql = "SELECT * FROM users WHERE username = ? and password = ?";

        connect = database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, si_username.getText());
            prepare.setString(2, si_password.getText());

            //ΕΛΕΓΧΟΣ ΓΙΑ ΤΟ LOGIN. ΕΑΝ ΥΠΑΡΧΟΥΝ EMPTY ΠΕΔΙΑ ΚΑΙ ΛΑΘΟΣ ΕΓΓΡΑΦΕΣ ΘΑ ΕΜΦΑΝΙΖΕΙ ERROR.
            Alert alert;

            if (si_username.getText().isEmpty() || si_password.getText().isEmpty()) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                ResultSet resultQuerry = prepare.executeQuery();
                if (resultQuerry.next()) {
                    alert = new Alert(AlertType.INFORMATION);
                    int id = resultQuerry.getInt("id");
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Succesfully Login!");
                    alert.showAndWait();
                    handleStoredUser(resultQuerry.getString("username"), id);
                    loadDashboard(resultQuerry.getString("role"));
                } else {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong Username/Password");
                    alert.showAndWait();
                }
            }

        } catch (SQLException e) {
        }
    }

    //METHOD ΓΙΑ ΝΑ ΑΝΑΓΝΩΡΙΖΕΙ Η ΕΦΑΡΜΟΓΗ ΤΟΥΣ USERS ΜΕ ΒΑΣΗ ΤΟ USERNAME KAI TO ID
    public void handleStoredUser(String username, int id) {
        UserStoreHelper helper = new UserStoreHelper().getInstance();
        helper.setUsername(username);
        helper.setId(id);
    }

    //ΕΓΓΡΑΦΗ ΧΡΗΣΤΗ ΣΤΟ ΣΥΣΤΗΜΑ
    public void signup() {
        String email = su_email.getText();
        Alert alert;

        if (!email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Νot valid e-mail.");
            alert.showAndWait();
            return;
        }

        String sql = "INSERT INTO users (email, username, password, address, role) VALUES(?,?,?,?,?)";

        connect = database.connectDb();
        try {

            //ΕΛΕΓΧΟΣ ΓΙΑ ΤΑ ΚΕΝΑ ΠΕΔΙΑ.
            boolean hasAllCreds = su_email.getText().isEmpty() || su_username.getText().isEmpty()
                    || su_password.getText().isEmpty() || su_address.getText().isEmpty();
            if (hasAllCreds) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                String username = su_username.getText();
                String query = "SELECT COUNT(*) as count FROM users WHERE email = ? OR username = ? ";

                prepare = connect.prepareStatement(query);
                prepare.setString(1, email);
                prepare.setString(2, username);
                ResultSet resultDublicate = prepare.executeQuery();

                if (resultDublicate.next() && resultDublicate.getInt("count") > 0) {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("User with the same email or username all ready exists!");
                    alert.showAndWait();
                } else {

                    //ΕΑΝ Ο ΚΩΔΙΚΟΣ ΠΟΥ ΒΑΖΕΙ Ο ΧΡΗΣΤΗΣ ΕΙΝΑΙ ΚΑΤΩ ΑΠΟ 5 ΧΑΡΑΚΤΗΡΕΣ ΘΑ ΕΜΦΑΝΙΖΕΙ ERROR
                    if (su_password.getText().length() < 5) {
                        alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Invalid password. Please use more than 5 characters.");
                        alert.showAndWait();
                    } else {
                        //ΔΙΝΟΥΜΕ ΡΟΛΟ admin ΣΤΟΝ ADMIN ΚΑΙ MEMBER ΣΤΑ ΜΕΛΟΙ
                        String countQuery = "select count(*) as rowcount from users";
                        Statement statement = connect.createStatement();
                        ResultSet rs = statement.executeQuery(countQuery);
                        rs.next();
                        String role = rs.getInt("rowcount") > 0 ? "member" : "admin";

                        prepare = connect.prepareStatement(sql);
                        prepare.setString(1, email);
                        prepare.setString(2, username);
                        prepare.setString(3, su_password.getText());
                        prepare.setString(4, su_address.getText());
                        prepare.setString(5, role);

                        prepare.executeUpdate();
                        alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Information Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Successfuly crate new account!");
                        alert.showAndWait();

                        su_email.setText("");
                        su_username.setText("");
                        su_password.setText("");
                        su_address.setText("");

                        int id = rs.getInt("id");

                        handleStoredUser(username, id);
                        loadDashboard(role);
                    }
                }
            }
        } catch (SQLException e) {
        }
    }

    //ΒΑΖΩ Sliders ΓΙΑ ΝΑ ΕΜΦΑΝΙΖΕΙ ΤΟ REGISTER ΟΤΑΝ Ο ΧΡΗΣΤΗΣ ΠΑΤΑΕΙ SignUp.
    public void signupSlider() {
        TranslateTransition slider1 = new TranslateTransition();
        slider1.setNode(sub_form);
        slider1.setToX(300);
        slider1.setDuration(Duration.seconds(.1));
        slider1.play();

        slider1.setOnFinished((ActionEvent event) -> {
            edit_label.setText("Login Account");

            sub_signupBtn.setVisible(false);
            sub_loginBtn.setVisible(true);
            login_form.setVisible(false);

        });

    }

    public void loginSlider() {
        TranslateTransition slider1 = new TranslateTransition();
        slider1.setNode(sub_form);
        slider1.setToX(0);
        slider1.setDuration(Duration.seconds(.1));
        slider1.play();

        slider1.setOnFinished((ActionEvent event) -> {
            edit_label.setText("Create Account");

            sub_signupBtn.setVisible(true);
            sub_loginBtn.setVisible(false);
            login_form.setVisible(true);

        });
    }

    public boolean logout() {
        try {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(ButtonType.OK)) {

                Parent root = FXMLLoader.load(getClass().getResource("../../main/resources/views/Main.fxml"));

                Stage stage = new Stage();
                Scene scene = new Scene(root);

                FXMLHelper controller = new FXMLHelper(stage);
                controller.setupMouseEvents(root);

                UserStoreHelper helper = new UserStoreHelper().getInstance();
                helper.setUsername(null);

                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }

    //ΕΛΕΓΧΟΣ ΓΙΑ ΤΟ ΕΑΝ ΚΑΝΕΙ LOGIN Ο ADMIN ΝΑ ΤΟΥ ΕΜΦΑΝΙΣΕΙ ΤΟ ADMIN DASHBOARD ΚΑΙ Ο CUSTOMER ΣΤΟ ΔΙΚΟ ΤΟΥ.
    public void loadDashboard(String role) {
        try {
            String pageToRender = role.equals("admin") ? "../../main/resources/views/AdminDashboard.fxml" : "../../main/resources/views/CustomersDashboard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pageToRender));
            Parent dashboardRoot = loader.load();

            // Set the scene
            Scene dashboardScene = new Scene(dashboardRoot);
            Stage stage = (Stage) su_email.getScene().getWindow(); // Get the current stage

            FXMLHelper helper = new FXMLHelper(stage);
            helper.setupMouseEvents(dashboardRoot);

            stage.setScene(dashboardScene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        javafx.application.Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
