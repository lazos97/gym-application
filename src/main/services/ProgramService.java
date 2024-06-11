package main.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import main.database;

public class ProgramService {

    private final TableView<ObservableList<String>> table;

    public ProgramService(TableView<ObservableList<String>> table) {
        this.table = table;
    }

    public void fetchPrograms() {
        String query = "SELECT id, room_name, date, start_hour FROM fitness_programs";
        try (Connection conn = database.connectDb(); ResultSet rs = conn.createStatement().executeQuery(query)) {

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= 4; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            table.setItems(data);
        } catch (SQLException ex) {
            System.err.println("Error fetching data from database: " + ex.getMessage());
        }
    }
}
