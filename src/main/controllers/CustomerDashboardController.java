package main.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.database;
import main.helpers.ColumnsBuilder;
import main.helpers.UserStoreHelper;
import main.services.ProgramService;

//CLASS ΓΙΑ ΤΟ DASHBOARD ΤΟΥ CUSTOMER
public class CustomerDashboardController implements Initializable {

    @FXML
    private AnchorPane Dashboard_custPage;

    @FXML
    private Button dashboard_CustSelect;

    @FXML
    private Label dashboard_NFithessPrograms;

    @FXML
    private Label dashboard_NMyReservations;

    @FXML
    private AnchorPane fithessprograms_Custpage;

    @FXML
    private Button fitnessPrograms_CustSelect;

    @FXML
    private Button logout;

    @FXML
    private AnchorPane main_form;

    @FXML
    private AnchorPane myReservations_CustPage;

    @FXML
    private Button myreservation_CustSelect;

    @FXML
    private Label user_label;

    @FXML
    private AnchorPane cust_image;

    @FXML
    private Label programLabel;

    @FXML
    private TableView<ObservableList<String>> programTableUsers;

    @FXML
    private TableView<ObservableList<String>> myReservationsTable;

    @FXML
    private Label scheduleLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private TableView<ObservableList<String>> MyReportTable;

    @FXML
    private Button MyReportsButton_CustSelect;

    @FXML
    private AnchorPane myReportsCustPage;

    private String selected_id;

    private LocalDate selected_date;

    private String selected_schedule;

    private String selected_program;

    private Connection connect;

    private PreparedStatement prepare;

    public void getUsersReports() {
        String query = "SELECT id, content FROM reports WHERE user_id = ?";

        UserStoreHelper helper = new UserStoreHelper().getInstance();
        int userId = helper.getId();

        try (Connection conn = database.connectDb(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= 2; i++) {
                        row.add(rs.getString(i));
                    }
                    data.add(row);
                }
                MyReportTable.setItems(data);
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching data from database: " + ex.getMessage());
        }
    }

    //METHOD ΓΙΑ ΤΑ REPORTS ΤΟΥ ΧΡΗΣΤΗ
    public void makeReport() {
        UserStoreHelper helper = new UserStoreHelper().getInstance();
        int userId = helper.getId();
        String content = reportTextArea.getText();

        boolean isNotValid = content.isEmpty() || content.length() > 100;

        if (isNotValid) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please add content with max length of 100 characters");
            alert.showAndWait();
            return;
        }
        String query = "INSERT INTO reports (content, user_id) VALUES (?, ?)";

        connect = database.connectDb();
        try {
            connect = database.connectDb();

            prepare = connect.prepareStatement(query);
            prepare.setString(2, Integer.toString(userId));
            prepare.setString(1, content);

            int rowsUpdated = prepare.executeUpdate();
            if (rowsUpdated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("You made a report!");
                alert.showAndWait();

                reset();
                getUsersReports();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Failed to create a report.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create a report due to a database error: " + e.getMessage());
            alert.showAndWait();
            System.out.println(e.getMessage());
        }
    }

    //METHOD ΓΙΑ ΝΑ ΓΙΝΕΤΑΙ Η ΚΡΑΤΗΣΗ ΤΟΥ USER ΣΤΟ MyReservations Table me JOIN.
    private void getReservationsByUserId() {
        String query = "SELECT p.id, p.room_name, p.date, p.start_hour "
                + "FROM reservations r JOIN fitness_programs p ON r.program_id = p.id "
                + "WHERE r.user_id = ?";

        UserStoreHelper helper = new UserStoreHelper().getInstance();
        int userId = helper.getId();

        try (Connection conn = database.connectDb(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= 4; i++) {
                        row.add(rs.getString(i));
                    }
                    data.add(row);
                }
                myReservationsTable.setItems(data);
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching data from database: " + ex.getMessage());
        }
    }

    //KANEI RESET ΤΑ LABELS ΜΕΤΑ ΤΗΝ ΚΡΑΤΗΣΗ
    private void reset() {
        programLabel.setText("Choose...");
        dateLabel.setText("Choose...");
        scheduleLabel.setText("Choose...");
        reportTextArea.setText("");
    }

    //ΑΛΛΑΓΗ ΦΟΡΜΑΣ ΑΝΑΛΟΓΑ ΜΕ ΤΟ BUTTON
    public void switchForm(ActionEvent event) {
        if (event.getSource() == dashboard_CustSelect) {

            Dashboard_custPage.setVisible(true);
            fithessprograms_Custpage.setVisible(false);
            myReservations_CustPage.setVisible(false);
            cust_image.setVisible(true);
            myReportsCustPage.setVisible(false);

        } else if (event.getSource() == fitnessPrograms_CustSelect) {

            Dashboard_custPage.setVisible(false);
            fithessprograms_Custpage.setVisible(true);
            myReservations_CustPage.setVisible(false);
            cust_image.setVisible(false);
            myReportsCustPage.setVisible(false);

        } else if (event.getSource() == myreservation_CustSelect) {

            Dashboard_custPage.setVisible(false);
            fithessprograms_Custpage.setVisible(false);
            myReservations_CustPage.setVisible(true);
            cust_image.setVisible(false);
            myReportsCustPage.setVisible(false);

        } else if (event.getSource() == MyReportsButton_CustSelect) {

            Dashboard_custPage.setVisible(false);
            fithessprograms_Custpage.setVisible(false);
            myReservations_CustPage.setVisible(false);
            cust_image.setVisible(false);
            myReportsCustPage.setVisible(true);
        }

    }

    public void logout() {
        AuthController controller = new AuthController();
        Boolean isLoggedOut = controller.logout();
        if (isLoggedOut) {
            logout.getScene().getWindow().hide();
        }
    }

    //ΠΑΙΡΝΕΙ ΤΟ ΟΝΟΜΑ ΤΟΥ USER ΚΑΙ ΤΟ ΕΜΦΑΝΙΖΕΙ ΣΤΟ DASHBOARD ΜΕΤΑ ΤΟ WELCOME
    public void updateStatusLabel() {
        UserStoreHelper helper = new UserStoreHelper().getInstance();
        String username = helper.getUsername();
        user_label.setText(username);
    }

    public void minimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    public void close() {
        UserStoreHelper helper = new UserStoreHelper().getInstance();
        helper.setUsername(null);

        javafx.application.Platform.exit();
    }

    public void loadPrograms() {
        ProgramService programServ = new ProgramService(programTableUsers);
        programServ.fetchPrograms();
    }

    private void handleProgramSelection(ObservableList<String> rowData) {
        selected_id = rowData.get(0);
        selected_program = rowData.get(1);
        selected_date = LocalDate.parse(rowData.get(2), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        selected_schedule = rowData.get(3);

        programLabel.setText(selected_program);
        dateLabel.setText(selected_date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        scheduleLabel.setText(selected_schedule);
    }

    private int countReservation(int userId) {
        String query = "SELECT COUNT(*) FROM reservations WHERE user_id = ? AND program_id = ?;";
        int count = 0;

        try {
            connect = database.connectDb();

            prepare = connect.prepareStatement(query);
            prepare.setString(1, Integer.toString(userId));
            prepare.setString(2, selected_id);

            try (var rs = prepare.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);

                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update fitness program due to a database error: " + e.getMessage());
            alert.showAndWait();
        }

        return count;
    }

    private int countFitnessPrograms() {
        String query = "SELECT COUNT(*) FROM fitness_programs;";
        int count = 0;

        try {
            connect = database.connectDb();

            prepare = connect.prepareStatement(query);

            try (var rs = prepare.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);

                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update fitness program due to a database error: " + e.getMessage());
            alert.showAndWait();
        }

        return count;
    }

    private int countReservationForDashboard(int userId) {
        String query = "SELECT COUNT(*) FROM reservations WHERE user_id = ?;";
        int count = 0;

        try {
            connect = database.connectDb();

            prepare = connect.prepareStatement(query);
            prepare.setString(1, Integer.toString(userId));

            try (var rs = prepare.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);

                }
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update fitness program due to a database error: " + e.getMessage());
            alert.showAndWait();
        }

        return count;
    }

    private void handleCounts() {
        UserStoreHelper helper = new UserStoreHelper().getInstance();
        int userId = helper.getId();

        dashboard_NMyReservations.setText(Integer.toString(countReservationForDashboard(userId)));
        dashboard_NFithessPrograms.setText(Integer.toString(countFitnessPrograms()));
    }

    //METHOD ΓΙΑ ΤΟ ΚΟΥΜΠΙ ΠΟΥ ΚΛΕΙΝΕΙ Ο CUSTOMER RESERVATIONS
    public void makeRev() {
        UserStoreHelper helper = new UserStoreHelper().getInstance();
        int userId = helper.getId();

        boolean hasNotProgram = (selected_id == null || selected_id.isEmpty());

        if (hasNotProgram) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No program selected!");
            alert.setHeaderText(null);
            alert.setContentText("Select a program in order to make a reservation!");
            alert.showAndWait();
            return;
        }

        if (countReservation(userId) > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Reservation error!");
            alert.setHeaderText(null);
            alert.setContentText("You are already have reservation on that program!");
            alert.showAndWait();
            return;
        }

        String query = "INSERT INTO reservations (user_id, program_id) VALUES (?, ?)";

        connect = database.connectDb();
        try {
            connect = database.connectDb();

            prepare = connect.prepareStatement(query);
            prepare.setString(1, Integer.toString(userId));
            prepare.setString(2, selected_id);

            int rowsUpdated = prepare.executeUpdate();
            if (rowsUpdated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("You made a reservation!");
                alert.showAndWait();

                reset();
                loadPrograms();
                handleCounts();
                getReservationsByUserId();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update fitness program. No rows were affected.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to update fitness program due to a database error: " + e.getMessage());
            alert.showAndWait();
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String[] rows = {"ID", "Program", "Date", "Schedule"};
        ColumnsBuilder.createColumns(rows, programTableUsers, true, false, this::handleProgramSelection);
        ColumnsBuilder.createColumns(rows, myReservationsTable, false, false);

        String[] reports = {"ID", "Content",};
        ColumnsBuilder.createColumns(reports, MyReportTable, false, false);

        loadPrograms();
        updateStatusLabel();
        handleCounts();
        getReservationsByUserId();
        getUsersReports();
    }

}
