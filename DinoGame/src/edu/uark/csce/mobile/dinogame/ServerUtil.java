package edu.uark.csce.mobile.dinogame;

public class ServerUtil {
	//free temp domain
	public static final String SERVER_ADDR = "http://cscemobile.sytes.net/";
	//gets locations not visited by userid or all if userid is not sent
	public static final String URL_ALL_LOCATIONS = SERVER_ADDR + "get_locations.php";

	//returns item data based on location id sent
	public static final String URL_ITEMS_LOCATION = SERVER_ADDR + "get_item_by_location.php";
	//updates location as visited based on userid and location id sent
	public static final String URL_UPDATE_LOC_VIS = SERVER_ADDR + "location_visited.php";
	
	
}
