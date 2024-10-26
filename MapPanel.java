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
import java.util.Collections;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import java.util.Map;
import java.util.List;

// @author Aïda Tadlaoui, Saruta Kittipattananon, Chen Wang and Isabella Landgrebe

public class MapPanel extends Pane {
    private ImageView mapImageView; // holds the map image
    private HashMap<String, Polygon> boroughPolygons;
    private HashMap<String, Integer> boroughDeathRates; // stores death rates for each borough
    private LocalDate fromDate; // start date to filter data
    private LocalDate toDate; // end date to filter data
    private Button keyButton; // button to show the colour key
 
    public MapPanel() {
        // initialise borough polygons and death rates
        boroughPolygons = new HashMap<>();
        boroughDeathRates = new HashMap<>();
        
        /* calculated coordinates for polygons
         * horizontal difference (e.g. between HRGY and BARN) between each coordindate is approximately 80 (altered slightly if it suits it better)
         * vertical difference between coordinate (e.g. between HRGY and WSTM) is approximately 140 (altered slightly if it suits it better)
         */
        addBorough("ENFI", new double[]{320, 86, 358, 65, 358, 22, 320, 0, 282, 22, 282, 65});
        addBorough("BARN", new double[]{199, 157, 237, 135, 237, 93, 199, 71, 161, 93, 161, 135});
        addBorough("HRGY", new double[]{280, 157, 318, 135, 318, 93, 280, 71, 242, 93, 242, 135});
        addBorough("WALT", new double[]{360, 157, 398, 135, 398, 93, 360, 71, 322, 93, 322, 135});
        addBorough("HRRW", new double[]{79, 226, 117, 205, 117, 162, 79, 140, 41, 162, 41, 205});
        addBorough("BREN", new double[]{159, 226, 197, 205, 197, 162, 159, 140, 121, 162, 121, 205});
        addBorough("CAMD", new double[]{240, 226, 277, 205, 277, 162, 240, 140, 201, 162, 201, 205});
        addBorough("ISLI", new double[]{320, 226, 358, 205, 358, 162, 320, 140, 282, 162, 282, 205});
        addBorough("HACK", new double[]{400, 226, 438, 205, 438, 162, 400, 140, 362, 162, 362, 205});
        addBorough("REDB", new double[]{481, 226, 519, 205, 519, 162, 481, 140, 443, 162, 443, 205});
        addBorough("HAVE", new double[]{561, 226, 599, 205, 599, 162, 561, 140, 523, 162, 523, 205});
        addBorough("HILL", new double[]{38, 296, 76, 274, 76, 232, 38, 211, 0, 232, 0, 274});
        addBorough("EALI", new double[]{119, 296, 157, 274, 157, 232, 119, 211, 81, 232, 81, 274});
        addBorough("KENS", new double[]{199, 296, 237, 274, 237, 232, 199, 211, 161, 232, 161, 274});
        addBorough("WSTM", new double[]{280, 296, 318, 274, 318, 232, 280, 211, 242, 232, 242, 274});
        addBorough("TOWH", new double[]{360, 296, 398, 274, 398, 232, 360, 211, 322, 232, 322, 274});
        addBorough("NEWH", new double[]{441, 296, 479, 274, 479, 232, 441, 211, 403, 232, 403, 274});
        addBorough("BARK", new double[]{521, 296, 559, 274, 559, 232, 521, 211, 483, 232, 483, 274});
        addBorough("HOUN", new double[]{79, 366, 117, 345, 117, 302, 79, 280, 41, 302, 41, 345});
        addBorough("HAMM", new double[]{159, 366, 197, 345, 197, 302, 159, 280, 121, 302, 121, 345});
        addBorough("WAND", new double[]{240, 366, 277, 345, 277, 302, 240, 280, 201, 302, 201, 345});
        addBorough("CITY", new double[]{320, 366, 358, 345, 358, 302, 320, 280, 282, 302, 282, 345});
        addBorough("GWCH", new double[]{400, 366, 438, 345, 438, 302, 400, 280, 362, 302, 362, 345});
        addBorough("BEXL", new double[]{481, 366, 519, 345, 519, 302, 481, 280, 443, 302, 443, 345});
        addBorough("RICH", new double[]{119, 435, 157, 413, 157, 371, 119, 350, 81, 371, 81, 413});
        addBorough("MERT", new double[]{199, 435, 237, 413, 237, 371, 199, 350, 161, 371, 161, 413});
        addBorough("LAMB", new double[]{280, 435, 318, 413, 318, 371, 280, 350, 242, 371, 242, 413});
        addBorough("STHW", new double[]{360, 435, 398, 413, 398, 371, 360, 350, 322, 371, 322, 413});
        addBorough("LEWS", new double[]{441, 435, 479, 413, 479, 371, 441, 350, 403, 371, 403, 413});
        addBorough("KING", new double[]{159, 506, 197, 483, 197, 441, 159, 420, 121, 441, 121, 483});
        addBorough("SUTT", new double[]{240, 506, 277, 483, 277, 441, 240, 420, 201, 441, 201, 483});
        addBorough("CROY", new double[]{320, 506, 358, 483, 358, 441, 320, 420, 282, 441, 282, 483});
        addBorough("BROM", new double[]{400, 506, 438, 483, 438, 441, 400, 420, 362, 441, 362, 483});
        
        // sets up map with the attached png
        mapImageView = new ImageView(new Image(getClass().getResourceAsStream("/boroughs.png")));
        mapImageView.setPreserveRatio(true); // maintains the png size ratio
        mapImageView.setFitWidth(600); // sets width of map image
        this.getChildren().add(mapImageView); // adds image to panel
        mapImageView.toBack(); // ensures png is behind the created polygons
        
        updateDeathRates(); // colour code boroughs
        
        // initialise button to create the key for the polygons
        keyButton = new Button("Show Key");
        keyButton.setLayoutX(600-80);
        keyButton.setLayoutY(20);
        this.getChildren().add(keyButton);
        
        // key button shows colour key
        keyButton.setOnAction(e -> showKeyBox());
    }

    private void addBorough(String name, double[] coordinates) {
        Polygon polygon = new Polygon(coordinates); // create polygon with their given coordinates
        polygon.setStroke(Color.BLACK);
        polygon.setStrokeWidth(2); 
        polygon.setFill(Color.TRANSPARENT); // initially transparent before data is loaded
        boroughPolygons.put(name, polygon);
        this.getChildren().add(polygon);
        polygon.toFront(); // ensure polygon is in front of map png
        
        // when polygon is clicked it will show detailed information about that borough:
        polygon.setOnMouseClicked(event -> {
            String fullName = BoroughMapper.getBoroughFullName(name); 
            if (fullName != null) {
                showBoroughDetails(fullName);
            }else {
                System.out.println("Mapping not found");
            }
        });
    }

    public void updateDeathRates() {
        if (boroughDeathRates == null || boroughDeathRates.isEmpty()) {
            System.out.println("No death rates in data set.");
            return;
        }
        
        // determine minimum and maximum death rates for the data range
        int minDeaths = Collections.min(boroughDeathRates.values());
        int maxDeaths = Collections.max(boroughDeathRates.values());
        
        // colours each polygon depending on the death rate
        for (Map.Entry<String, Integer> entry : boroughDeathRates.entrySet()) {
            String borough = entry.getKey();
            int deaths = entry.getValue();
            Polygon polygon = boroughPolygons.get(borough);

            if (polygon != null) {
                Color color = determineColor(deaths, minDeaths, maxDeaths);
                polygon.setFill(color);
            }
            else {
                System.out.println("No polygon found for borough: " + borough);
            }
        }
    }

    private Color determineColor(int deaths, int minDeaths, int maxDeaths) {
        
        // calculates the colour for the minimum and maximum death rates
        Color lowDeathRateColor = Color.LIGHTBLUE;
        Color highDeathRateColor = Color.DARKBLUE;
        
        // Normalise the death rate to a value between 0 and 1
        double normalised = (double) (deaths - minDeaths) / (maxDeaths - minDeaths);
        
        // Colour depend on death rate
        double red = lowDeathRateColor.getRed() + normalised * (highDeathRateColor.getRed() - lowDeathRateColor.getRed());
        double green = lowDeathRateColor.getGreen() + normalised * (highDeathRateColor.getGreen() - lowDeathRateColor.getGreen());
        double blue = lowDeathRateColor.getBlue() + normalised * (highDeathRateColor.getBlue() - lowDeathRateColor.getBlue());
        
        // Return the interpolated color
        return new Color(red, green, blue, 0.5); 
    }

    public void setDeathRates(HashMap<String, Integer> deathRates) {
        this.boroughDeathRates = deathRates;
        updateDeathRates();
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }
    
     public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
    
    public void showBoroughDetails(String boroughIdentifier){
        Stage detailStage = new Stage(); //create a new window
        detailStage.setTitle("COVID Details for " + boroughIdentifier); // set title based on the borough name 
        
        VBox vbox = new VBox(10); 
        vbox.setPadding(new javafx.geometry.Insets(15, 12, 15, 12)); 
        

    
        ComboBox<String> sortOptions = new ComboBox<>(); // create a new dropdown menu for soriting options
        sortOptions.getItems().addAll("Date", "New COVID Cases", "Total COVID Cases", "New COVID Deaths", 
        "Retail and Recreation GMR", "Grocery and Pharmacy GMR", "Parks GMR", "Transit Stations GMR", 
        "Workplaces GMR", "Residential GMR"); // add all the sorting options
        sortOptions.setPromptText("Sort by..."); //set the default name of the drop down menu
        
        TableView<CovidData> tableView = new TableView<>(); // create the table by using TabelView
        setupTableView(tableView); 
        
        List<CovidData> boroughData = fetchDataForBorough(boroughIdentifier, fromDate, toDate); // retrieve a list of CovidData for a specific borough
        ObservableList<CovidData> dataObservableList = FXCollections.observableArrayList(boroughData); // covenrt list to an ObservableList that can be used in TableView
        
        
        
        
        VBox.setVgrow(tableView, Priority.ALWAYS); // make TableView expand to fill the Vbox vertically
        
        // lamda expression to sets the action when an options is selected from the comboBox
        sortOptions.setOnAction(event -> {
            String selectedOption = sortOptions.getValue();
            Comparator<CovidData> comparator = getComparator(selectedOption);// gets the appropriate comparator based on the selected sort option
            if (comparator != null) {
                FXCollections.sort(dataObservableList, comparator);
            }
        });
        
        // Set the TableView to sort the first column in descending order by default
        tableView.setItems(dataObservableList);
        tableView.getColumns().get(0).setSortType(TableColumn.SortType.DESCENDING); 
        tableView.getSortOrder().add(tableView.getColumns().get(0));
        
        // Add the sort ComboBox and the TableView to the VBox layout
        vbox.getChildren().addAll(sortOptions, tableView);
    
        // Create the Scene and show the Stage
        Scene scene = new Scene(vbox, 600, 400);
        detailStage.setScene(scene);
        detailStage.show();
    }
    
    private List<CovidData> fetchDataForBorough(String boroughIdentifier, LocalDate fromDate, LocalDate toDate) {
        CovidDataLoader dataLoader = new CovidDataLoader();
        List<CovidData> allData = dataLoader.load(); // load all data
        
                
        // Filter the data based on the borough identifier and the date range
        List<CovidData> filteredData = allData.stream()
            .filter(data -> data.getBorough().equals(boroughIdentifier)) // match borough
            .filter(data -> {
                try {
                    LocalDate dataDate = LocalDate.parse(data.getDate());
                    // handle null dates
                    boolean isAfterStart = (fromDate == null) || !dataDate.isBefore(fromDate);
                    boolean isBeforeEnd = (toDate == null) || !dataDate.isAfter(toDate);
                    return isAfterStart && isBeforeEnd;
                } catch (DateTimeParseException e) {
                    // if there is an error parsing the date, print the error
                    System.err.println("Error parsing date: " + data.getDate());
                    return false; // exclude this record if the date is invalid
                }
            })
            .collect(Collectors.toList()); // collect the results from the stream back into a list
        
        return filteredData;
    }
    
    /**
     * This method returns a comparator for CovidData based on specific sorting option
     */
    private Comparator<CovidData> getComparator(String option) {
        switch (option) {
            case "Date":
                return Comparator.comparing(CovidData::getDate);
            case "New COVID Cases":
                return Comparator.comparing(CovidData::getNewCases);
            case "Total COVID Cases":
                return Comparator.comparing(CovidData::getTotalCases);
            case "New COVID Deaths":
                return Comparator.comparing(CovidData::getNewDeaths);
            case "Retail and Recreation GMR":
                return Comparator.comparing(CovidData::getRetailRecreationGMR);
            case "Grocery and Pharmacy GMR":
                return Comparator.comparing(CovidData::getGroceryPharmacyGMR);
            case "Parks GMR":
                return Comparator.comparing(CovidData::getParksGMR);
            case "Transit Stations GMR":
                return Comparator.comparing(CovidData::getTransitGMR); 
            case "Workplaces GMR":
                return Comparator.comparing(CovidData::getWorkplacesGMR);
            case "Residential GMR":
                return Comparator.comparing(CovidData::getResidentialGMR);    
            default:
                return null;
        }
    }
    
    /**
     * This method creates and setup column for each attribute to display in the table
     */
    private void setupTableView(TableView<CovidData> tableView) {
        // Create and set up the columns
        TableColumn<CovidData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<CovidData, Number> newCasesColumn = new TableColumn<>("New COVID Cases");
        newCasesColumn.setCellValueFactory(new PropertyValueFactory<>("newCases"));
    
        TableColumn<CovidData, Number> totalCasesColumn = new TableColumn<>("Total COVID Cases");
        totalCasesColumn.setCellValueFactory(new PropertyValueFactory<>("totalCases"));
        
        TableColumn<CovidData, Number> newDeathsColumn = new TableColumn<>("New COVID Deaths");
        newDeathsColumn.setCellValueFactory(new PropertyValueFactory<>("newDeaths"));
        
        TableColumn<CovidData, Number> retailRecretionColumn = new TableColumn<>("Retail and Recreation GMR");
        retailRecretionColumn.setCellValueFactory(new PropertyValueFactory<>("retailRecreationGMR"));
        
        TableColumn<CovidData, Number> groceryPharmarcyColumn = new TableColumn<>("Grocery and Pharmacy GMR");
        groceryPharmarcyColumn.setCellValueFactory(new PropertyValueFactory<>("groceryPharmacyGMR"));        
        
        TableColumn<CovidData, Number> parksColumn = new TableColumn<>("Parks GMR");
        parksColumn.setCellValueFactory(new PropertyValueFactory<>("parksGMR"));
        
        TableColumn<CovidData, Number> transitStationsColumn = new TableColumn<>("Transit Stations GMR");
        transitStationsColumn.setCellValueFactory(new PropertyValueFactory<>("transitGMR"));
        
        TableColumn<CovidData, Number> workplacesColumn = new TableColumn<>("Workplaces GMR");
        workplacesColumn.setCellValueFactory(new PropertyValueFactory<>("workplacesGMR"));
        
        TableColumn<CovidData, Number> residentialColumn = new TableColumn<>("Residential GMR");
        residentialColumn.setCellValueFactory(new PropertyValueFactory<>("residentialGMR"));
        
        // Add the columns to the table
        tableView.getColumns().addAll(dateColumn, newCasesColumn, totalCasesColumn, newDeathsColumn, 
        retailRecretionColumn, groceryPharmarcyColumn, parksColumn, transitStationsColumn,
        workplacesColumn, residentialColumn);
        
        
        dateColumn.setMinWidth(100);
    }
    
    // create key for the polygon colour-coding
    public void showKeyBox() {
        Alert keyAlert = new Alert(Alert.AlertType.INFORMATION); // create new information alert
        keyAlert.setTitle("Key"); 
        keyAlert.setHeaderText("Colour Key");
        
        VBox content = new VBox(5);
        content.getChildren().add(createKeyItem(Color.GREY, "No data for these dates")); // boroughs with no data
        content.getChildren().add(createKeyItem(Color.LIGHTBLUE, "Low death rate")); // boroughs with low death rate
        content.getChildren().add(createKeyItem(Color.DARKBLUE, "High death rate")); // boroughs with high death rate
        
        keyAlert.getDialogPane().setContent(content);
        keyAlert.showAndWait(); // wait for user to close the key
    }
    
    // creates each key item (a coloured rectangle and a label to explain it)
    private HBox createKeyItem(Color color, String text) {
        Rectangle colorIndicator = new Rectangle(10, 10, color); // creates rectangle for key
        Label label = new Label(" " + text);
        label.setGraphic(new Rectangle(10, 10, color));
        label.setContentDisplay(ContentDisplay.LEFT);
        HBox keyItem = new HBox(colorIndicator, label);
        keyItem.setAlignment(Pos.CENTER_LEFT);
        return keyItem;
    }
}
