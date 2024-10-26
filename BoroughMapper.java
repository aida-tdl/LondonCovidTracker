import java.util.HashMap;
import java.util.Map;

// @author Aïda Tadlaoui, Saruta Kittipattananon, Chen Wang and Isabella Landgrebe

public class BoroughMapper {
    public static final HashMap<String, String> boroughToPolygon = new HashMap<>(); // maps borough full names to their polygon keys
    private static final HashMap<String, String> polygonToBorough = new HashMap<>(); // maps polygon keys back to borough full names
    
    static {
        // matches the full borough name to the polygon key name
        boroughToPolygon.put("Kingston Upon Thames", "KING");
        boroughToPolygon.put("Hammersmith And Fulham", "HAMM");
        boroughToPolygon.put("Hackney", "HACK");
        boroughToPolygon.put("Barking And Dagenham", "BARK");
        boroughToPolygon.put("Tower Hamlets", "TOWH");
        boroughToPolygon.put("Lewisham", "LEWS");
        boroughToPolygon.put("Islington", "ISLI");
        boroughToPolygon.put("Bexley", "BEXL");
        boroughToPolygon.put("Wandsworth", "WAND");
        boroughToPolygon.put("Haringey", "HRGY");
        boroughToPolygon.put("Enfield", "ENFI");
        boroughToPolygon.put("Sutton", "SUTT");
        boroughToPolygon.put("Merton", "MERT");
        boroughToPolygon.put("Havering", "HAVE");
        boroughToPolygon.put("Brent", "BREN");
        boroughToPolygon.put("Hounslow", "HOUN");
        boroughToPolygon.put("Camden", "CAMD");
        boroughToPolygon.put("Greenwich", "GWCH");
        boroughToPolygon.put("Harrow", "HRRW");
        boroughToPolygon.put("Ealing", "EALI");
        boroughToPolygon.put("Southwark", "STHW");
        boroughToPolygon.put("Newham", "NEWH");
        boroughToPolygon.put("Kensington And Chelsea", "KENS");
        boroughToPolygon.put("City Of London", "CITY");
        boroughToPolygon.put("Bromley", "BROM");
        boroughToPolygon.put("Hillingdon", "HILL");
        boroughToPolygon.put("Redbridge", "REDB");
        boroughToPolygon.put("Croydon", "CROY");
        boroughToPolygon.put("Richmond Upon Thames", "RICH");
        boroughToPolygon.put("Barnet", "BARN");
        boroughToPolygon.put("Westminster", "WSTM");
        boroughToPolygon.put("Waltham Forest", "WALT");
        boroughToPolygon.put("Ealing", "EALI");
        boroughToPolygon.put("Lambeth", "LAMB");
        
        // iterates over the boroughToPolygon map to add to map
        for (Map.Entry<String, String> entry : boroughToPolygon.entrySet()) {
        polygonToBorough.put(entry.getValue(), entry.getKey());
        }
    }
    
    /**
     * get polygon key for the specific borough name
     * 
     * @param boroughName full name of the borough.
     * @return corresponding polygon key if the borough name is mapped (otherwise null)
     */
    
    public static String getPolygonKey(String boroughName) {
        return boroughToPolygon.getOrDefault(boroughName, null); // Returns null if the borough name is not mapped
    }
    
    /**
     * get full borough name for specific polygon key
     * 
     * @param polygonKey is the key representing the borough polygon
     * @return corresponding full borough name (otherwise null)
     */
    public static String getBoroughFullName(String polygonKey) {
        return polygonToBorough.getOrDefault(polygonKey, "Unknown Borough");
    }
}
