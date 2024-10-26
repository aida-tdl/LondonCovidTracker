import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The StatisticsPanel class extends BorderPane to create a UI component that displays various COVID-19 related statistics.
 * It allows navigating through different statistics using navigation buttons.
 */
public class StatisticsPanel extends BorderPane {
    private Label statLabel; // Label for displaying the name of the current statistic
    private Label statValue; // Label for displaying the value of the current statistic
    private Button prevButton, nextButton;
    
    private int currentStatisticIndex = 0; // Index of the currently displayed statistic
    private int totalDeaths;
    private double averageRetailRecreationGMR;
    private double averageGroceryPharmacyGMR;
    private long averageTotalCases;
    
    private VBox statisticsContainer;
    private List<VBox> statisticViews;
    private List<String> statistics = List.of(
        "Statistic 1",
        "Statistic 2",
        "Statistic 3",
        "Statistic 4"
    );
    
    /**
     * Constructs a new StatisticsPanel and initializes its UI components and state.
     */
    public StatisticsPanel() {
        initializeUI();
        updateStatisticDisplay();
    }

    /**
     * Initializes the UI components of the panel, setting up labels, buttons, and layout.
     */
    private void initializeUI() {
        // Setup statistic label
        statLabel = new Label();
        statLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        statLabel.setAlignment(Pos.CENTER);
        BorderPane.setMargin(statLabel, new Insets(70, 0, 0, 100)); 
        
        // Setup statistic value label
        statValue = new Label(); 
        statValue.setFont(new Font("Arial", 45));
        statValue.setAlignment(Pos.CENTER);
        BorderPane.setMargin(statValue, new Insets(0, 0, 20, 0));

        // Initialize navigation buttons
        prevButton = new Button("<");
        nextButton = new Button(">");
        prevButton.setFont(new Font("Arial", 18));
        nextButton.setFont(new Font("Arial", 18));
        BorderPane.setMargin(prevButton, new Insets(0, 25, 50, 0));
        BorderPane.setMargin(nextButton, new Insets(0, 25, 50, 0));

        // VBox for left button 
        VBox leftButtonBox = new VBox(prevButton);
        leftButtonBox.setAlignment(Pos.CENTER);
        leftButtonBox.setFillWidth(true);

        // VBox for right button
        VBox rightButtonBox = new VBox(nextButton);
        rightButtonBox.setAlignment(Pos.CENTER);
        rightButtonBox.setFillWidth(true);

        // Set the buttons characteristics
        prevButton.setMaxHeight(300);
        prevButton.setMinWidth(100); 
        nextButton.setMaxHeight(300);
        nextButton.setMinWidth(100); 
        VBox.setVgrow(prevButton, Priority.ALWAYS);
        VBox.setVgrow(nextButton, Priority.ALWAYS);

        // Add components to the BorderPane layout
        this.setTop(statLabel);
        this.setCenter(statValue);
        this.setLeft(leftButtonBox);
        this.setRight(rightButtonBox);

        // Add action events for the buttons
        prevButton.setOnAction(e -> navigate(-1));
        nextButton.setOnAction(e -> navigate(1));
    }

    /**
     * Updates the display with the current statistic's name and value.
     */
    private void updateStatisticDisplay() {
        switch (currentStatisticIndex) {
            // Determine which statistic to display based on the current index
            case 0:
                statLabel.setText("Average Retail & Recreation Mobility Change");
                statValue.setText(String.format("%.2f%%", averageRetailRecreationGMR));
                break;
            case 1:
                statLabel.setText("Average Grocery & Pharmacy Mobility Change");
                statValue.setText(String.format("%.2f%%", averageGroceryPharmacyGMR));
                break;
            case 2:
                statLabel.setText("Total Deaths");
                statValue.setText(String.valueOf(totalDeaths));
                break;
            case 3:
                statLabel.setText("Average Total Cases");
                statValue.setText(String.format("%d", averageTotalCases));
                break;
        }
    }

    /**
     * Navigates through the statistics based on the specified direction.
     * @param direction The direction of navigation: -1 for previous, 1 for next.
     */
    private void navigate(int direction) {
        currentStatisticIndex += direction;
        if (currentStatisticIndex < 0) {
            currentStatisticIndex = statistics.size() - 1; // Wrap to the last statistic
        } else if (currentStatisticIndex >= statistics.size()) {
            currentStatisticIndex = 0; // Wrap to the first statistic
        }
        updateStatisticDisplay();
    }   
    
    /**
     * Updates statistics based on the filtered data provided.
     * @param filteredData The list of CovidData objects to calculate statistics from.
     */
    public void updateStatistics(List<CovidData> filteredData) {
        // Implementation of statistics calculation from filtered data
        
        // Calculate the average mobility change in retail and recreation sectors
        averageRetailRecreationGMR = filteredData.stream()
            .mapToInt(CovidData::getRetailRecreationGMR)
            .average()
            .orElse(0.0);

        // Calculate the average mobility change in grocery and pharmacy sectors
        averageGroceryPharmacyGMR = filteredData.stream()
            .mapToInt(CovidData::getGroceryPharmacyGMR)
            .average()
            .orElse(0.0);

        // Calculate the total number of deaths
        totalDeaths = filteredData.stream()
            .mapToInt(CovidData::getTotalDeaths) 
            .sum();

        // Calculate the average of total cases
        averageTotalCases = Math.round(filteredData.stream()
            .mapToInt(CovidData::getTotalCases)
            .average()
            .orElse(0.0));
            
        updateStatisticDisplay();
    }
    
    /**
     * Enables or disables navigation buttons.
     * @param enabled true to enable the buttons, false to disable.
     */
    public void enableNavigationButtons(boolean enabled) {
        prevButton.setDisable(!enabled);
        nextButton.setDisable(!enabled);
    }
    
    // Unit Tests code:
    /**
     * Retrieves the average mobility change percentage in the retail and recreation sectors.
     * @return The average retail and recreation mobility change as a double.  
     */
    public double getAverageRetailRecreationGMR() {
        return averageRetailRecreationGMR;
    }
    
    /**
     * Retrieves the average mobility change percentage in the grocery and pharmacy sectors.
     * @return The average grocery and pharmacy mobility change as a double.  
     */
    public double getAverageGroceryPharmacyGMR() {
        return averageGroceryPharmacyGMR;
    }
    
    /**
     * Retrieves the total number of deaths related to COVID-19.
     * @return The total number of COVID-19 related deaths as an integer. 
     */
    public int getTotalDeaths() {
        return totalDeaths;
    }
    
    /**
     * Retrieves the average number of total cases of COVID-19.
     * @return The average number of COVID-19 cases as a long integer. 
     */
    public long getAverageTotalCases() {
        return averageTotalCases;
    }
}