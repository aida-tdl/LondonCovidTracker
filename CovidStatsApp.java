import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// @author Aïda Tadlaoui, Saruta Kittipattananon, Chen Wang and Isabella Landgrebe

// Main class to create the JavaFX GUI application
public class CovidStatsApp extends Application {

    private StackPane panelsContainer = new StackPane();
    private List<Pane> panels = List.of(new Pane(), new Pane(), new Pane()); // Placeholder for actual panels
    private int currentPanelIndex = 0;
    private Button backButton, forwardButton;
    private ComboBox<LocalDate> fromComboBox;
    private ComboBox<LocalDate> toComboBox;
    private Label dateRangeLabel;
    private MapPanel mapPanel;
    private StatisticsPanel statisticsPanel;
    private GraphPanel graphPanel;
    
    // constructor for the application:
    public CovidStatsApp() {
        // initialises welcome panel
        VBox welcomePanel = new VBox(10); // vertical box holds UI elents for message
        Label welcomeLabel = new Label("Welcome to the London COVID-19 Statistics Viewer!\nPlease select a date range to begin.");
        dateRangeLabel = new Label(); // label shows selected date range
        welcomePanel.getChildren().addAll(welcomeLabel, dateRangeLabel);
        welcomePanel.setAlignment(Pos.CENTER); // aligns welcome message
        
        // initialises the map, statistics and graph panels
        mapPanel = new MapPanel();
        mapPanel.setVisible(false); // not initially visible
        statisticsPanel = new StatisticsPanel();
        statisticsPanel.setVisible(false); 
        graphPanel = new GraphPanel(); 
        graphPanel.setVisible(false); 
        
        // list of panels to manage more easily
        panels = new ArrayList<>(List.of(welcomePanel, mapPanel, statisticsPanel, graphPanel));
    }
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane(); // main layout container
        
        setupTopMenu(borderPane, primaryStage);
        setupNavigation(borderPane);
        
        ArrayList<CovidData> allData = new CovidDataLoader().load(); // loads the COVID data
        updateDateSelectors(allData); // sets up date selection componenets
        
        panelsContainer.getChildren().addAll(panels);
        updatePanelVisibility(); // shows correct panel based on current index
        
        borderPane.setCenter(panelsContainer); // adds panels to centre
        
        Scene scene = new Scene(borderPane, 800, 600); // scene is of size 800 x 600
        primaryStage.setTitle("COVID-19 Statistics Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        HashMap<String, Integer> deathRates = calculateDeathRates(allData); // calculates death rates
        mapPanel.setDeathRates(deathRates); // updates map panel using the calculated death rates
    }
    
    // menu bar and date selectors: allows user to exit and pick date
    private void setupTopMenu(BorderPane borderPane, Stage primaryStage) {
        MenuBar menuBar = new MenuBar(); // menu bar at the top
        Menu fileMenu = new Menu("Menu"); // menu drops down
        MenuItem exitItem = new MenuItem("Exit"); 
        fileMenu.getItems().add(exitItem);
        menuBar.getMenus().add(fileMenu);
        
        fromComboBox = new ComboBox<>(); // 'from' date
        toComboBox = new ComboBox<>(); // 'to' date
        
        HBox dateSelectors = new HBox(5, new Label("From:"), fromComboBox, new Label("To:"), toComboBox);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        ToolBar toolBar = new ToolBar(spacer, dateSelectors);
        
        VBox topContainer = new VBox(menuBar, toolBar); // container for menu bar and toolbar
        borderPane.setTop(topContainer);
        
        exitItem.setOnAction(event -> primaryStage.close()); // exits the application
    }
    
    private void updateDateSelectors(ArrayList<CovidData> allData) {
        Set<LocalDate> uniqueDates = allData.stream() // gets unique dates form the data
            .map(data -> LocalDate.parse(data.getDate()))
            .collect(Collectors.toSet());
        
        // converts the dates into observable list for the combo boxes:
        ObservableList<LocalDate> sortedDates = FXCollections.observableArrayList(uniqueDates);
        FXCollections.sort(sortedDates);

        // sets the sorted dates as items so the combo boxes can handle the chosen date range
        fromComboBox.setItems(sortedDates);
        toComboBox.setItems(sortedDates);

        // events triggered from the date range update
        fromComboBox.setOnAction(event -> updateDateRange());
        toComboBox.setOnAction(event -> updateDateRange());
    }
    
    // updates the rest of the application depending on the selected date range
    public void updateDateRange() {
        LocalDate fromDate = fromComboBox.getValue(); 
        LocalDate toDate = toComboBox.getValue();
        
        // ensures the selected date range is valid (i.e. toDate does not come before fromDate)
        if (fromDate != null && toDate != null && !fromDate.isAfter(toDate)) {
            ArrayList<CovidData> filteredData = filterData(fromDate, toDate); // filters the data to only contain the data within the date range
            
            // update panels with filtered data:
            mapPanel.setDeathRates(calculateDeathRates(filteredData));
            mapPanel.setFromDate(fromDate);
            mapPanel.setToDate(toDate);
            statisticsPanel.updateStatistics(filteredData);
            graphPanel.updateDateAndGraph(fromDate, toDate, filteredData);
            
            // format and display selected date range
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            String formattedFromDate = fromDate.format(formatter);
            String formattedToDate = toDate.format(formatter);
            dateRangeLabel.setText("Current date range: " + formattedFromDate + " - " + formattedToDate);
            
            dateRangeLabel.setTextFill(javafx.scene.paint.Color.BLACK);
            enableNavigation(true); // enables navigation between panels
            
            } 
        else {
            dateRangeError(); // handles invalid date range
        }
    }
    
    // alters whether navigation is available depending on if a valid date has been selected
    private void enableNavigation(boolean enable) {
        backButton.setDisable(!enable); // enables/disables back button
        forwardButton.setDisable(!enable); // enables/disables forward button
        statisticsPanel.enableNavigationButtons(enable); // enables/disables navigation in statistics panel
    }
    
    // filters dataset based on date range
    private ArrayList<CovidData> filterData(LocalDate fromDate, LocalDate toDate) {
        ArrayList<CovidData> allData = new CovidDataLoader().load(); // loads data
        ArrayList<CovidData> filteredData = new ArrayList<>(); // preapres the filtered (depending on the dates) data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // mathces the date format used in the dataset
        
        // filters through the data in the selected date range
        for (CovidData record : allData) {
            LocalDate recordDate;
            try {
                recordDate = LocalDate.parse(record.getDate(), formatter); // Parse with the formatter
            } 
            catch (DateTimeParseException e) {
                // handles situation when date cannot be parsed
                e.printStackTrace(); // logs the error
                continue; // skips to next iteration if error occurs
            }
            if (!recordDate.isBefore(fromDate) && !recordDate.isAfter(toDate)) {
                filteredData.add(record); // adds to filtered list if within the range
            }
        }
        return filteredData; // returns this filtered data
    }
    
    // calculate death rate for each borough:
    private HashMap<String, Integer> calculateDeathRates(ArrayList<CovidData> dataList) {
        HashMap<String, Integer> deathRates = new HashMap<>();
    
        for (CovidData data : dataList) {
            String boroughName = data.getBorough();
            String polygonKey = BoroughMapper.getPolygonKey(boroughName); // maps each borough to a unique key as shown on the Map Panel
    
            // if a mapping is found, it counts the death rates for the borough for all the selected dates:
            if (polygonKey != null) {
                int deaths = data.getNewDeaths(); 
                deathRates.merge(polygonKey, deaths, Integer::sum);
            } else {
                System.out.println("No mapping found for borough: " + boroughName);
            }
        }
    
        return deathRates;
    }
    
    // creates error message if invalid date range is used
    private void dateRangeError() {
        Alert alert = new Alert(AlertType.WARNING); // warning for user if date range is invalid
        alert.setTitle("Invalid Date Range");
        alert.setHeaderText("The selected date range is invalid.");
        alert.setContentText("Please select a valid start and end date where the start date is before the end date.");
        
        // checks if current panel is not the first panel so that it shows a different error message (since there is already a message on the first panel)
        if (currentPanelIndex != 0) { // if not on first panel
            alert.showAndWait(); // waits for user to close the warning box
        } 
        else {
            dateRangeLabel.setText("This date range is invalid, please try again."); // if on first panel, it just updates the label to add an error message
            dateRangeLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
        
        enableNavigation(false); // disables navigation buttons until valid date range has been selected
    }

    // sets navigation buttons on border pane
    private void setupNavigation(BorderPane borderPane) {
        backButton = new Button("< Back");
        forwardButton = new Button("Forward >");
        backButton.setDisable(true); // initially disabled
        forwardButton.setDisable(true); // initially disabled
        
        backButton.setOnAction(event -> navigate(-1)); // back button goes back a panel
        forwardButton.setOnAction(event -> navigate(1)); // forward button goes forward a panel
        
        HBox backContainer = new HBox(backButton); // wraps back button in HBox
        backContainer.setAlignment(Pos.BOTTOM_LEFT);
        backContainer.setPadding(new Insets(10, 0, 10, 20)); 
        
        HBox forwardContainer = new HBox(forwardButton); // wraps forward button in HBox
        forwardContainer.setAlignment(Pos.BOTTOM_RIGHT);
        forwardContainer.setPadding(new Insets(10, 20, 10, 0));
        
        borderPane.setLeft(backContainer);
        borderPane.setRight(forwardContainer);
    }
    
    // allows for navigation between panels
    private void navigate(int direction) {
        //currentPanelIndex += direction;
        if (currentPanelIndex == 0 && direction > 0 && fromComboBox.getValue() != null && toComboBox.getValue() != null && !fromComboBox.getValue().isAfter(toComboBox.getValue())) {
            currentPanelIndex += direction; // moves to next panel if on first panel
        } else if (direction != 0) {
            currentPanelIndex += direction; // adjust index to change as the panel it is on changes
        }
        
        if (currentPanelIndex < 0) {
            currentPanelIndex = panels.size() -1; // goes to map panel (last panel) if on welcome panel (first panel)
        } else if (currentPanelIndex >= panels.size()){
            currentPanelIndex = 0;
        }
        
        updatePanelVisibility(); // updates which panel is visible depending on current panel index
    }
    
    // updates visibility based on current panel index
    private void updatePanelVisibility() {
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).setVisible(i == currentPanelIndex); // only current panel is visible
        }
    }

    // main method to launch application:
    public static void main(String[] args) {
        launch(args);
    }
}
