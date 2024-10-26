import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

// unit test to test the statistics panel
class StatisticsPanelTest {

    private StatisticsPanel panel; // holds the statistics panel to be tested

    // sets up testing environment before the test
    @BeforeEach
    public void setUp() {
        panel = new StatisticsPanel(); // initialises the StatisticsPanel instance
    }
    
    // creates test to ensure that the statistics are calculated correctly in the StatisticsPanel class
    @Test
    public void updateStatisticsCorrectly() {
        // mock data in format: CovidData(date, borough, retailAndRecreation, groceryAndPharmacy, parks, transitStations, workplaces, residential, newCases, totalCases, newDeaths, totalDeaths)
        // randomly selected from the CovidData list (null values represented as 0)
        List<CovidData> mockData = List.of(
            new CovidData("2022-10-15", "Kingston Upon Thames", -20, -3, 69, -5, -1, 1, 11, 64560, 1, 426),
            new CovidData("2022-05-02", "Hammersmith And Fulham", -14, -19, 45, -55, -78, 14, 28, 63203, 0, 317),
            new CovidData("2022-01-16", "City Of London", -43, -65, -39, -38, 0, 0, 300, 70659, 0, 532),
            new CovidData("2021-11-04", "Westminster", -34, -13, -31, -34, -37, 10, 75, 26427, 0, 335),
            new CovidData("2021-02-26", "Bromley", -50, 2, 72, -58, -59, 24, 12, 24215, 2, 430),
            new CovidData("2020-07-30", "Kensington And Chelsea", -56, -15, 37, -64, -62, 17, 0, 588, 0, 93),
            new CovidData("2022-10-15", "Southwark", -3, 3, -1, -5, -3, 0, 2, 10, 0, 0),
            new CovidData("2022-10-21", "Greenwich", 0, 0, 0, 0, 0, 0, 21, 96904, 1, 620)
        );
        
        panel.updateStatistics(mockData); // method to be tested with mock data
        
        // hand-calculated average based on the mock data:
        double expectedAverageRetailRecreation = -27.5;
        double expectedAverageGroceryPharmacy = -13.75;
        int expectedTotalDeaths = 2753;
        long expectedAverageTotalCases = 43321;
        
        // asserts that the values calculated in the StatisticsPanel match the expected results
        assertEquals(expectedAverageRetailRecreation, panel.getAverageRetailRecreationGMR(), 0.01, "Average Retail & Recreation GMR is incorrect");
        assertEquals(expectedAverageGroceryPharmacy, panel.getAverageGroceryPharmacyGMR(), 0.01, "Average Grocery & Pharmacy GMR is incorrect");
        assertEquals(expectedTotalDeaths, panel.getTotalDeaths(), "Total deaths are incorrect");
        assertEquals(expectedAverageTotalCases, panel.getAverageTotalCases(), "Average total cases are incorrect");
    }
}
