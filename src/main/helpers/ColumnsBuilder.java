package main.helpers;

import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import main.database;

//CLASS ΓΙΑ ΝΑ ΦΤΙΑΧΝΟΥΜΕ ΤΑ COLUMNS ΣΤΑ TABLES
public class ColumnsBuilder {

    public static void createColumns(String[] rows, TableView<ObservableList<String>> table,
            boolean isProgram, boolean isUser, UIUpdater... uiUpdater) {
        ArrayList<Double> columnWidths = new ArrayList<>();
        double totalWidthPercentage = 100.0;
        double idWidthPercentage = 10.0;
        double remainingWidthPercentage = totalWidthPercentage - idWidthPercentage;
        double widthArray = remainingWidthPercentage / (rows.length - 1);

        columnWidths.add(idWidthPercentage);

        for (int i = 1; i < rows.length; i++) {
            columnWidths.add(widthArray);
        }

        for (int i = 0; i < rows.length; i++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(rows[i]);
            final int index = i;
            column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));
            column.setCellFactory(tc -> {
                TableCell<ObservableList<String>, String> cell = new TableCell<>();
                cell.setAlignment(Pos.CENTER);
                Label label = new Label();
                label.setAlignment(Pos.CENTER);
                cell.setGraphic(label);
                cell.textProperty().bind(cell.itemProperty());
                if (isProgram && uiUpdater.length > 0) {
                    cell.setOnMouseClicked(event -> {
                        ObservableList<String> rowData = cell.getTableRow().getItem();
                        if (rowData != null) {
                            uiUpdater[0].updateUI(rowData);
                        }
                    });
                }
                return cell;
            });

            column.prefWidthProperty().bind(table.widthProperty().multiply(columnWidths.get(i) / totalWidthPercentage));
            table.getColumns().add(column);
        }
    }

    public static void handleBuildAndQuery(String query, TableView<ObservableList<String>> table) {
        try (Connection conn = database.connectDb(); ResultSet rs = conn.createStatement().executeQuery(query)) {
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
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
