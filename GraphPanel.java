import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * This is the GraphPanel class. 
 * The class provides a graphical representation of COVID-19 data over time.
 *
 * @author Aïda Tadlaoui, Chen Xin Wang, Isabella Landgrebe and Saruta Kittipattananon 
 * @version 12
 */
public class GraphPanel extends VBox {
    private static final int MAX_DATA_POINTS = 40;
    private LineChart<String, Number> lineChart;
    private ComboBox<String> boroughComboBox;
    private List<CovidData> currentData; 
    private List<CovidData> fullData;
    private LocalDate startDate, endDate;
    private ComboBox<String> graphComboBox;
    private Button showOnMapButton;
    private Button learnMoreButton;
    
    /**
     * Initialises a "GraphPanel" object.
     */
    public GraphPanel() {
        super(); // calls the constructor of the parent class(VBox)
        
        // initialises components
        initialiseGraphComboBox();
        initialiseBoroughComboBox();
        initialGraph();
        initialiseShowOnMapButton();
        initialiseLearnMoreButton();
    }
    
    /**
     * Initializes the graph combo box.
     * The combo box is populated with options for different types of graphs related to COVID-19 data.
     * The default selection is "New Cases Over Time".
     */
    private void initialiseGraphComboBox() {
        graphComboBox = new ComboBox<>();
        graphComboBox.getItems().addAll(
            "New Cases Over Time",
            "Total Deaths Over Time",
            "Retail & Recreation Mobility Change",
            "Grocery & Pharmacy Mobility Change",
            "Rate of Case Change"
        );
        graphComboBox.getSelectionModel().selectFirst(); // selects "New Cases Over Time" making it the default
        graphComboBox.setOnAction(event -> updateSelectedGraph()); // updates the graph displated in the UI
        this.getChildren().add(graphComboBox); // add to the top of the panel
    }
    
    /**
     * Initialises the borough combo box.
     * The combo box is populated with borough names and an option for "All Boroughs".
     */
    private void initialiseBoroughComboBox() {
        boroughComboBox = new ComboBox<>();
        boroughComboBox.getItems().add("All Boroughs");
        boroughComboBox.getItems().addAll(
            "Barking And Dagenham", "Barnet", "Bexley", "Brent", "Bromley",
            "Camden", "Croydon", "Ealing", "Enfield", "Greenwich", "Hackney",
            "Hammersmith and Fulham", "Haringey", "Harrow", "Havering",
            "Hillingdon", "Hounslow", "Islington", "Kensington And Chelsea",
            "Kingston upon Thames", "Lambeth", "Lewisham", "Merton", "Newham",
            "Redbridge", "Richmond Upon Thames", "Southwark", "Sutton",
            "Tower Hamlets", "Waltham Forest", "Wandsworth", "Westminster"
        );
        boroughComboBox.getSelectionModel().selectFirst();
        boroughComboBox.setOnAction(event -> updateSelectedGraph());
        boroughComboBox.setOnAction(event -> {
            updateSelectedGraph(); // updates the selected graph based on the chosen borough
            updateShowOnMapState(); // upadtes the state of the "Show on Map" button based on the selected borough 
        });
        this.getChildren().add(boroughComboBox);
    }
    
    /**
     * Initialises and sets up a LineChart to display COVID-19 cases.
     * The initial graph displayed when user comes on to Graph panel.
     */
    private void initialGraph() {
        // sets up x and y axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("New Cases");

        // LineChart setup
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("COVID-19 New Cases Over Time");
        this.getChildren().add(lineChart); // adds the line chart to the panel
    }
    
    /**
     * Updates the selected graph based on the data, start date, end date and selected borough.
     */
    public void updateSelectedGraph() {
        if (fullData == null || startDate == null || endDate == null) {
            return; // no data to update the graph with
        }
    
        System.out.println("Current data size before filtering: " + currentData.size());
    
        this.currentData = new ArrayList<>(fullData);

        String selectedBorough = boroughComboBox.getValue();
        List<CovidData> filteredAndSampledData = filterByBoroughAndSampleData(currentData, selectedBorough, startDate, endDate);

        // method now calls the graph update methods with the correctly filtered and sampled data
        switch (graphComboBox.getValue()) {
            case "New Cases Over Time":
                updateGraph(filteredAndSampledData, startDate, endDate);
                break;
            case "Total Deaths Over Time":
                updateTotalDeaths(filteredAndSampledData, startDate, endDate);
                break;
            case "Retail & Recreation Mobility Change":
                updateRRMobilityTrend(filteredAndSampledData, startDate, endDate);
                break;
            case "Grocery & Pharmacy Mobility Change":
                updateGPMobilityTrend(filteredAndSampledData, startDate, endDate);
                break;
            case "Rate of Case Change":
                updateRateOfCaseChange(filteredAndSampledData, startDate, endDate);
                break;
            default:
                break;
        }
    }
    
    /**
     * Updates the line chart with new data for the number of new COVID_19 cases over time.
     * 
     * @param allData    The list of COVID-19 data to be displayed on the chart.
     * @param startDate  The start date of the data range to be displayed.
     * @param endDate    The end date of the data range to be displayed.
     */
    private void updateGraph(List<CovidData> allData, LocalDate startDate, LocalDate endDate) {
        // initialises the graph with the appropiate title and axis labels
        initialiseGraph("COVID-19 New Cases Over Time", "New Cases");
        
        // update instance variables with provided data
        this.currentData = allData; 
        this.startDate = startDate; 
        this.endDate = endDate;
        
        // gets the selected borough from the combo box
        String selectedBorough = boroughComboBox.getValue();
        
        // filters the data based on selected borough
        List<CovidData> filteredData;
        if (!"All Boroughs".equals(selectedBorough)) {
            filteredData = allData.stream()
                .filter(data -> data.getBorough().equals(selectedBorough))
                .collect(Collectors.toList());
        } else {
            filteredData = new ArrayList<>(allData);  
        }
        
        lineChart.getData().clear(); // clears previous data from the line chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedBorough.equals("All Boroughs") ? "All Boroughs" : selectedBorough);
        
        // iterates over each date within the specified range
        LocalDate tempDate = startDate;
        while (!tempDate.isAfter(endDate)) {
            final LocalDate currentDate = tempDate;
            // calculates the sum of the new cases for the current date
            int sumNewCases = filteredData.stream()
                                       .filter(data -> LocalDate.parse(data.getDate()).equals(currentDate))
                                       .mapToInt(CovidData::getNewCases)
                                       .sum();
                                
            series.getData().add(new XYChart.Data<>(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), sumNewCases)); // adds the data points to the series  
            tempDate = tempDate.plusDays(1); // moves to the next day
        }
        
        lineChart.getData().add(series); // adds the series to the line chart 
        applyStyleToSeries(series); // applies styling to the series
    }
    
    /**
     * Updates the line chart with new data for total deaths over time.
     * 
     * @param allData    The list of COVID-19 data to be displayed on the chart.
     * @param startDate  The start date of the data range to be displayed.
     * @param endDate    The end date of the data range to be displayed.
     */
    private void updateTotalDeaths(List<CovidData> allData, LocalDate startDate, LocalDate endDate) {
        initialiseGraph("COVID-19 Total Deaths Over Time", "Total Deaths");
        this.currentData = allData; 
        this.startDate = startDate; 
        this.endDate = endDate;
        String selectedBorough = boroughComboBox.getValue();
        List<CovidData> filteredData = filterByBorough(allData, selectedBorough);
        
        lineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedBorough.equals("All Boroughs") ? "All Boroughs" : selectedBorough + " - Total Deaths");

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            int totalDeaths = filteredData.stream()
                                          .filter(data -> LocalDate.parse(data.getDate()).equals(currentDate))
                                          .mapToInt(CovidData::getTotalDeaths)
                                          .sum();
            series.getData().add(new XYChart.Data<>(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), totalDeaths));
        }

        lineChart.getData().add(series);
        applyStyleToSeries(series);
    }
    
    /**
     * Updates the line chart with new data for the Retail & Recreation Mobility Change.       
     * 
     * @param allData    The list of COVID-19 data to be displayed on the chart.
     * @param startDate  The start date of the data range to be displayed.
     * @param endDate    The end date of the data range to be displayed.
     */
    private void updateRRMobilityTrend(List<CovidData> allData, LocalDate startDate, LocalDate endDate) {
        this.currentData = allData;
        this.startDate = startDate;
        this.endDate = endDate;
        String selectedBorough = boroughComboBox.getValue();
        List<CovidData> filteredData = filterByBorough(allData, selectedBorough);
        initialiseGraph("Retail & Recreation Mobility Change", "Mobility Change (%)");
    
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedBorough.equals("All Boroughs") ? "All Boroughs - Retail & Recreation" : selectedBorough + " - Retail & Recreation");
    
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            double averageRetailRecreation = filteredData.stream()
                                                         .filter(data -> LocalDate.parse(data.getDate()).equals(currentDate))
                                                         .mapToInt(CovidData::getRetailRecreationGMR)
                                                         .average()
                                                         .orElse(0); // assumes 0 if no data available
    
            series.getData().add(new XYChart.Data<>(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), averageRetailRecreation));
        }
    
        lineChart.getData().add(series);
        applyStyleToSeries(series);
        // adjusts the yAxis label for mobility trends
        ((NumberAxis) lineChart.getYAxis()).setLabel("Retail & Recreation Mobility Change (%)");
    }

    /**
     * Updates the line chart with new data for the Grocery & Pharmacy Mobility Change.
     * 
     * @param allData    The list of COVID-19 data to be displayed on the chart.
     * @param startDate  The start date of the data range to be displayed.
     * @param endDate    The end date of the data range to be displayed.
     */
    private void updateGPMobilityTrend(List<CovidData> allData, LocalDate startDate, LocalDate endDate) {
        this.currentData = allData;
        this.startDate = startDate;
        this.endDate = endDate;
        String selectedBorough = boroughComboBox.getValue();
        List<CovidData> filteredData = filterByBorough(allData, selectedBorough);
    
        initialiseGraph("Grocery & Pharmacy Mobility Change", "Mobility Change (%)");
    
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedBorough.equals("All Boroughs") ? "All Boroughs - Grocery & Pharmacy" : selectedBorough + " - Grocery & Pharmacy");
    
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            double averageGPMobility = filteredData.stream()
                                                   .filter(data -> LocalDate.parse(data.getDate()).equals(currentDate))
                                                   .mapToInt(CovidData::getGroceryPharmacyGMR)
                                                   .average()
                                                   .orElse(0);
    
            series.getData().add(new XYChart.Data<>(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), averageGPMobility));
        }
    
        lineChart.getData().add(series);
        applyStyleToSeries(series);
    }
    
    /**
     * Updates the line chart with new data for the rate of case change.
     * 
     * @param allData    The list of COVID-19 data to be displayed on the chart.
     * @param startDate  The start date of the data range to be displayed.
     * @param endDate    The end date of the data range to be displayed.
     */
    private void updateRateOfCaseChange(List<CovidData> allData, LocalDate startDate, LocalDate endDate) {
        this.currentData = allData;
        this.startDate = startDate;
        this.endDate = endDate;
        String selectedBorough = boroughComboBox.getValue();
        List<CovidData> filteredData = filterByBorough(allData, selectedBorough);
    
        initialiseGraph("COVID-19 Rate of Case Change", "Rate of Change");
    
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedBorough.equals("All Boroughs") ? "Rate of Change - All Boroughs" : "Rate of Change - " + selectedBorough);
    
        Integer previousDayCases = null;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            int currentDayCases = filteredData.stream()
                                              .filter(data -> LocalDate.parse(data.getDate()).equals(currentDate))
                                              .mapToInt(CovidData::getNewCases)
                                              .sum();
            if (previousDayCases != null) {
                int rateOfChange = currentDayCases - previousDayCases;
                series.getData().add(new XYChart.Data<>(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), rateOfChange));
            }
            previousDayCases = currentDayCases;
        }
    
        lineChart.getData().add(series);
        applyStyleToSeries(series);
    }
    
    /**
     * Filters the COVID-19 data by the selectded borough.
     * 
     * @param allData          The list of COVID-19 data to filter.
     * @param selectedBorough  The selected borough.
     * @return                 The filtered list of COVID-19 data.
     */
    private List<CovidData> filterByBorough(List<CovidData> allData, String selectedBorough) {
        if (!"All Boroughs".equals(selectedBorough)) {
            // filters the data to include only the selected borough
            return allData.stream()
                          .filter(data -> data.getBorough().equals(selectedBorough))
                          .collect(Collectors.toList());
        } else {
            // returns the entire data if "All Boroughs" is selected
            return new ArrayList<>(allData);  
        }
    }
    
    /**
     * Filters the COVID-19 data by the selected borough and samples the data within the specified date range.
     * Sampling ensures that the number of data points does not exceed the maximum data points limit.
     * 
     * @param allData          The list of COVID-19 data to filter and sample.
     * @param selectedBorough  The selected borough.
     * @param start            The start date of the sampling period.
     * @param end              The end date of the sampling period.
     * @return                 The filtered and sampled list of COVID-19 data.
     */
    private List<CovidData> filterByBoroughAndSampleData(List<CovidData> allData, String selectedBorough, LocalDate start, LocalDate end) {
        List<CovidData> filteredData = filterByBorough(allData, selectedBorough); // filters the covid-19 data by the selected borough 
        long daysBetween = ChronoUnit.DAYS.between(start, end); // calculates the number of days between the start and end dates
        int sampleInterval = (int) Math.ceil((double) daysBetween / MAX_DATA_POINTS); // calculates the sample interval to ensure the number of data points does not exceed the maximum limit
        List<CovidData> sampledData = new ArrayList<>(); // creayes a list to store the sampled data
        
        // iterates over the filtered data with the specified sample interval
        for (int i = 0; i < filteredData.size(); i += sampleInterval) {
            LocalDate dataDate = LocalDate.parse(filteredData.get(i).getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // checks if the data date falls within specified date range
            if (!dataDate.isBefore(start) && !dataDate.isAfter(end)) {
                sampledData.add(filteredData.get(i)); // adds the data to the sampled data list
            }
        }
        return sampledData;
    }

    /**
     * Sets the choices for the borough selection ComboBox based on the provided list of boroughs.
     * 
     * @param boroughs  The list of borough names to populate the ComboBox.
     */
    public void setBoroughChoices(List<String> boroughs) {
        boroughComboBox.getItems().clear(); // clears existing items in the ComboBox
        boroughComboBox.getItems().add("All Boroughs"); // adds "All Boroughs"
        boroughComboBox.getItems().addAll(boroughs); // adds all the boroughs from the provided list
        boroughComboBox.getSelectionModel().selectFirst(); // selects first item to be the deault (" All Boroughs")
    }
    
    /**
     * Styles the series in the LineChart.
     * 
     * @param series The series to which the style will be applied.
     */
    private void applyStyleToSeries(XYChart.Series<String, Number> series) {
        // runs the style applciation on the JavaFX Application Threa
        Platform.runLater(() -> {
            // loops through each data point in the series
            for (XYChart.Data<String, Number> data : series.getData()) {
                // looks up the chart-line-symbol node for each data point
                Node symbol = data.getNode().lookup(".chart-line-symbol");
                if (symbol != null) {
                    // if the symbol node exists, set its background color to transparent
                    symbol.setStyle("-fx-background-color: transparent, transparent;"); 
                }
            }
        });
    }
    
    /**
     * Initialises a new LineChart with the specified title and y-axis label, replacing any existing LineChart in the GraphPanel.
     * 
     * @param title      The title of the LineChart.
     * @param yAxisLabel The label for the y-axis of the LineChart.
     */
    private void initialiseGraph(String title, String yAxisLabel) {
        this.getChildren().remove(lineChart); // removes any existing LineChart from the GraphPanel
    
        // creates new axes for the LineChart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel(yAxisLabel);
    
        // creates a new LineChart with the specified axes and title
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);
        
        // adds the new LineChart to the GraphPanel
        this.getChildren().add(lineChart);
    }
    
    /**
     * Initialises the "Show on Map" button, sets its action to open the selected borough in Google Maps, and adds it to the GraphPanel. By default, the button is disabled until a borough is selected.
     */
    private void initialiseShowOnMapButton() {
        showOnMapButton = new Button("Show on Map"); // creates the "Show on Map" button with the specified text
        showOnMapButton.setOnAction(event -> openBoroughInGoogleMaps()); // sets the action of the button to open the selected borough in Google Maps
        showOnMapButton.setDisable(true); // initially disable the button until a borough is selected
        this.getChildren().add(showOnMapButton); // adds the button to the GraphPanel
    }

    /**
     * Attempts to open the selected borough in Google Maps.
     * If a borough is selected and it's not "All Boroughs", it constructs a URI string to represent the location on Google Maps and opens it in the default web browser.
     * If any exception occurs during this process, it prints the stack trace.
     */
    private void openBoroughInGoogleMaps() {
        try {
            // gets the currently selected borough from the combo box
            String selectedBorough = boroughComboBox.getValue();
            // checks if a borough is selected and it's not "All Boroughs"
            if (selectedBorough != null && !selectedBorough.equals("All Boroughs")) {
                // constructs the URI string for the selected borough on Google Maps
                String uriString = "https://www.google.com/maps/place/" + (selectedBorough + " London").replace(" ", "+");
                // opens the URI in the default web browser
                Desktop.getDesktop().browse(new URI(uriString));
            }
        } catch (Exception e) {
            // prints the stack trace if any exception occurs
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the state of the "Show on Map" button based on the selected borough.
     * If the selected borough is "All Boroughs", the button is disabled.
     * Otherwise, the button is enabled.
     */
    private void updateShowOnMapState() {
        // gets the currently selected borough from the combo box
        String selectedBorough = boroughComboBox.getValue();
        // disables the button if the selected borough is "All Boroughs", otherwise enable it
        showOnMapButton.setDisable("All Boroughs".equals(selectedBorough));
    }
    
    /**
     * Updates the start date, end date, current data, and full data with the provided values, then triggers an update of the selected graph.
     * 
     * @param startDate     The new start date for the graph data.
     * @param endDate       The new end date for the graph data.
     * @param filteredData  The filtered data to be displayed on the graph.
     */
    public void updateDateAndGraph(LocalDate startDate, LocalDate endDate, List<CovidData> filteredData) {
        // updates start date and end date
        this.startDate = startDate;
        this.endDate = endDate;
        
        // updates current data and full data with a copy of the filtered data
        this.currentData = new ArrayList<>(filteredData); 
        this.fullData = new ArrayList<>(filteredData); 
        
        // triggers an update of the selected graph
        updateSelectedGraph(); 
    }
    
    /**
     * Initialises the "Learn More" button, which provides information about COVID-19 prevention
     * when clicked. The button is added to the bottom of the panel and occupies vertical space.
     */
    private void initialiseLearnMoreButton() {
        learnMoreButton = new Button("Click me to learn more about COVID-19 prevention."); // creates the "Learn More" button with a specific text
        learnMoreButton.setOnAction(event -> showPreventionPopup()); // sets an action event to show the prevention popup when the button is clicked
        VBox.setVgrow(learnMoreButton, Priority.ALWAYS); // ensures the button occupies vertical space in the layout
        this.getChildren().add(learnMoreButton); // adds the button to the panel
    }
    
    /**
     * Displays an informational popup providing ways to prevent COVID-19 transmission and ensure safety. 
     * The popup includes a title, header text, and a list of prevention measures.
     */
    private void showPreventionPopup() {
        Alert alert = new Alert(AlertType.INFORMATION); // creates an Alert dialog of type INFORMATION
        alert.setTitle("COVID-19 Prevention"); // sets the title of the alert
        alert.setHeaderText("Ways to prevent getting COVID-19 and ensure safety:"); // sets the header of the alert
        alert.setContentText("1. Wash your hands frequently with soap and water.\n"
                           + "2. Wear a mask in public places.\n"
                           + "3. Maintain social distancing.\n"
                           + "4. Avoid large gatherings.\n"
                           + "5. Get vaccinated if eligible.\n"
                           + "6. Stay informed through trusted sources."); // sets the content text with a list of prevention measures
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // sets the minimum height of the dialog pane to use the preferred size
        alert.showAndWait(); // shows the alert dialog and wait for user interaction
    }
}
