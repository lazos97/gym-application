package main.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import main.database;
import main.helpers.UserStoreHelper;
import java.sql.ResultSet;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import main.helpers.ColumnsBuilder;
import main.services.ProgramService;

//CLASS ΓΙΑ ΤΟ DASHBOARD ΤΟΥ ADMIN
public class AdminDashboardController implements Initializable {

    @FXML
    private Label dashboard_NM;

    @FXML
    private Label dashboard_NR;

    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private Button dashboard_select;

    @FXML
    private DatePicker fithessPrograms_Date;

    @FXML
    private AnchorPane fitnessPrograms_form;

    @FXML
    private TextField fitnessPrograms_name;

    @FXML
    private ComboBox<String> fitnessPrograms_schedule;

    @FXML
    private Button fitnessPrograms_select;

    @FXML
    private Button logout;

    @FXML
    private AnchorPane main_form;

    @FXML
    private AnchorPane members_form;

    @FXML
    private Button members_select;

    @FXML
    private Button reservation_select;

    @FXML
    private TableView<ObservableList<String>> programs_table;

    @FXML
    private TableView<ObservableList<String>> members_table;

    @FXML
    private TableView<ObservableList<String>> reservations_table;

    @FXML
    private AnchorPane reservations_form;

    @FXML
    private PieChart pieChart;
    
    @FXML
    private TableView<ObservableList<String>> reports_table;
    
    @FXML
    private Button reports_select;

    @FXML
    private Label user_label;
    
    @FXML
    private AnchorPane Reports_form;

    private Connection connect;

    private PreparedStatement prepare;

    private String selected_id;

    private LocalDate selected_date;

    private String selected_schedule;

    private String selected_program;
    
    //ΠΑΙΡΝΟΥΜΕ ΤΑ USER REPORTS ΚΑΙ ΤΑ ΕΜΦΑΝΙΖΟΥΜΕ ΣΤΟΝ ADMIN
    public void getReports(){
        String query = "SELECT r.id, u.username, r.content "
                + "FROM reports r JOIN users u ON r.user_id = u.id";          

        try (Connection conn = database.connectDb(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= 3; i++) {
                        row.add(rs.getString(i));
                    }
                    data.add(row);
                }
                reports_table.setItems(data);
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching data from database: " + ex.getMessage());
        }
    }

    //METHOD ΓΙΑ ΝΑ ΦΤΙΑΞΟΥΜΕ ΤΟ PIECHART ΣΤΟΝ ADMIN.
    private void populatePieChart() {
        String query = "SELECT p.room_name, COUNT(r.program_id) AS reservations_count "
                + "FROM reservations r "
                + "JOIN fitness_programs p ON r.program_id = p.id "
                + "GROUP BY r.program_id";

        try (Connection conn = database.connectDb(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                // Clear existing data in PieChart
                pieChart.getData().clear();

                // Populate PieChart with data
                while (rs.next()) {
                    String programName = rs.getString("room_name");
                    int reservationsCount = rs.getInt("reservations_count");
                    PieChart.Data data = new PieChart.Data(programName, reservationsCount);
                    pieChart.getData().add(data);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching data from database: " + ex.getMessage());
        }

    }

    private void showNumberOnHover() {
        for (PieChart.Data data : pieChart.getData()) {
            Tooltip tooltip = new Tooltip();
            int reservationsCount = (int) Math.round(data.getPieValue());
            tooltip.setText(String.valueOf(reservationsCount));
            Tooltip.install(data.getNode(), tooltip);
        }
    }

    private void showNumberAlways() {
        for (PieChart.Data data : pieChart.getData()) {
            int reservationsCount = (int) Math.round(data.getPieValue());
            data.nameProperty().setValue(data.getName() + ": " + reservationsCount);
        }
    }

    public void getReservations() {
        String query = "SELECT r.id, u.username, u.email, p.room_name, p.date, p.start_hour "
                + "FROM reservations r JOIN fitness_programs p ON r.program_id = p.id "
                + "JOIN users u ON r.user_id = u.id";

        try (Connection conn = database.connectDb(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= 6; i++) {
                        row.add(rs.getString(i));
                    }
                    data.add(row);
                }
                reservations_table.setItems(data);
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching data from database: " + ex.getMessage());
        }
    }

    public void delete() {
        if (selected_id == null || selected_id.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the selected fitness program?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            connect = database.connectDb();
            try {
                connect = database.connectDb();
                String sql = "DELETE FROM fitness_programs WHERE id = ?";

                prepare = connect.prepareStatement(sql);
                prepare.setInt(1, Integer.parseInt(selected_id));

                int rowsDeleted = prepare.executeUpdate();

                if (rowsDeleted > 0) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Deletion Successful");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Fitness program deleted successfully.");
                    successAlert.showAndWait();

                    countData();
                    reset();
                    loadFitnessPrograms();
                    populatePieChart();
                    showNumberOnHover();
                    showNumberAlways();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to delete fitness program. Please try again.");
                    errorAlert.showAndWait();
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
    }

    public void reset() {
        fitnessPrograms_schedule.getSelectionModel().clearSelection();
        fitnessPrograms_name.setText("");
        fithessPrograms_Date.setValue(null);
        selected_id = null;
    }

    public void update() {
        boolean hasAllValues = (selected_program != null && !selected_program.isEmpty())
                && (selected_schedule != null && !selected_schedule.isEmpty())
                && (selected_date != null);

        String time = fitnessPrograms_schedule.getSelectionModel().getSelectedItem();
        String program = fitnessPrograms_name.getText();
        LocalDate date = fithessPrograms_Date.getValue();

        boolean hasAllNewValues = (date != null)
                && (time != null && !time.isEmpty())
                && (program != null && !program.isEmpty());

        if (!hasAllValues || !hasAllNewValues) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }

        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        boolean hasSameValues = time.equals(selected_schedule) && selected_program.equals(program) && date.equals(selected_date);

        if (hasSameValues) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please change at least 1 field for update");
            alert.showAndWait();
            return;
        }

        connect = database.connectDb();
        try {
            connect = database.connectDb();
            String sql = "UPDATE fitness_programs SET room_name = ?, date = ?, start_hour = ? WHERE id = ?";

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, program);
            prepare.setString(2, formattedDate);
            prepare.setString(3, time);
            prepare.setInt(4, Integer.parseInt(selected_id));

            int rowsUpdated = prepare.executeUpdate();
            if (rowsUpdated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully updated the fitness program!");
                alert.showAndWait();

                reset();
                loadFitnessPrograms();
                populatePieChart();
                showNumberOnHover();
                showNumberAlways();
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
        }

    }

    public void add() {
        String time = fitnessPrograms_schedule.getSelectionModel().getSelectedItem();
        String program = fitnessPrograms_name.getText();
        LocalDate date = fithessPrograms_Date.getValue();

        boolean hasAllValues = (time != null && !time.isEmpty())
                && (program != null && !program.isEmpty())
                && (date != null);

        if (!hasAllValues) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
            return;
        }
        //Format το date απο yyyy-mm-dd σε dd-mm-yyyy
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        connect = database.connectDb();
        try {
            String sql = "INSERT INTO fitness_programs (room_name, date, start_hour) VALUES(?,?,?)";

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, program);
            prepare.setString(2, formattedDate);
            prepare.setString(3, time);

            prepare.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Message");
            alert.setHeaderText(null);
            alert.setContentText("Successfuly add a fitness program!");
            alert.showAndWait();

            reset();
            loadFitnessPrograms();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to add fitness program due to a database error: " + e.getMessage());
            alert.showAndWait();
        }

    }

    //ΑΛΛΑΓΗ ΦΟΡΜΑΣ ΑΝΑΛΟΓΑ ΜΕ ΤΟ BUTTON
    public void switchForm(ActionEvent event) {
        if (event.getSource() == dashboard_select) {
            dashboard_form.setVisible(true);
            fitnessPrograms_form.setVisible(false);
            members_form.setVisible(false);
            reservations_form.setVisible(false);
            Reports_form.setVisible(false);
        } else if (event.getSource() == fitnessPrograms_select) {
            dashboard_form.setVisible(false);
            fitnessPrograms_form.setVisible(true);
            members_form.setVisible(false);
            reservations_form.setVisible(false);
            Reports_form.setVisible(false);
        } else if (event.getSource() == members_select) {
            dashboard_form.setVisible(false);
            fitnessPrograms_form.setVisible(false);
            members_form.setVisible(true);
            reservations_form.setVisible(false);
            Reports_form.setVisible(false);
        } else if (event.getSource() == reservation_select) {
            dashboard_form.setVisible(false);
            fitnessPrograms_form.setVisible(false);
            members_form.setVisible(false);
            reservations_form.setVisible(true);
            Reports_form.setVisible(false);
        }
        else if (event.getSource() == reports_select) {
            dashboard_form.setVisible(false);
            fitnessPrograms_form.setVisible(false);
            members_form.setVisible(false);
            reservations_form.setVisible(false);
            Reports_form.setVisible(true);
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

    //LOOP ΓΙΑ ΤΙΣ ΩΡΕΣ ΣΤΟ SCHEDULE
    private void populateHours() {
        for (int hour = 9; hour <= 10; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d %s", hour, minute, (hour < 12) ? "AM" : "PM");
                fitnessPrograms_schedule.getItems().add(time);
            }
        }
    }

    private void handleProgramSelection(ObservableList<String> rowData) {
        selected_id = rowData.get(0);
        selected_program = rowData.get(1);
        selected_date = LocalDate.parse(rowData.get(2), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        selected_schedule = rowData.get(3);

        fitnessPrograms_name.setText(selected_program);
        fithessPrograms_Date.setValue(selected_date);
        fitnessPrograms_schedule.getSelectionModel().select(selected_schedule);
    }

    // Method to load data from the database and populate the table
    private void loadFitnessPrograms() {
        ProgramService programServ = new ProgramService(programs_table);
        programServ.fetchPrograms();
    }

    //METHOD ΓΙΑ ΝΑ ΕΜΦΑΝΙΖΕΙ ΤΑ RESERVATIONS ΚΑΙ ΤΑ MEMBERS
    private void loadReservationsAndMembers() {
        String query = "SELECT id, email, username, address FROM users";
        ColumnsBuilder.handleBuildAndQuery(query, members_table);
    }

    // ΜΕΤΡΑΕΙ ΤΑ ΝΟΥΜΕΡΑ ΣΤΟ DASHBOARD ΤΟΥ ADMIN.
    private void countData() {
        String query = "SELECT COUNT(*) AS user_count FROM users";
        handleCounts(query, dashboard_NM, "user_count");

        String queryFitness = "SELECT COUNT(*) AS fitness_programs_count FROM fitness_programs";
        handleCounts(queryFitness, dashboard_NR, "fitness_programs_count");
    }

    private void handleCounts(String query, Label label, String field) {
        try (Connection conn = database.connectDb(); ResultSet rs = conn.createStatement().executeQuery(query)) {
            if (rs.next()) {
                label.setText(String.valueOf(rs.getInt(field)));
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching data from database: " + ex.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populateHours();
        updateStatusLabel();

        String[] rows = {"ID", "Program", "Date", "Schedule"};
        ColumnsBuilder.createColumns(rows, programs_table, true, false, this::handleProgramSelection);

        String[] rowsMembers = {"ID", "Ε-mail", "Username", "Address"};
        ColumnsBuilder.createColumns(rowsMembers, members_table, false, false);

        String[] rowsReservations = {"ID", "Member Name", "E-mail", "Fitness Programs", "Date", "Schedule"};
        ColumnsBuilder.createColumns(rowsReservations, reservations_table, false, false);
        
        String[] rowsReports = {"ID", "Member Name", "Content"};
        ColumnsBuilder.createColumns(rowsReports, reports_table, false, false);

        loadFitnessPrograms();
        loadReservationsAndMembers();
        countData();
        getReservations();
        populatePieChart();
        showNumberOnHover();
        showNumberAlways();
        getReports();

        fitnessPrograms_schedule.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        });
    }

}
