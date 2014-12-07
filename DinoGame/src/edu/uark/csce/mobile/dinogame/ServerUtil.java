package edu.uark.csce.mobile.dinogame;

public class ServerUtil {

	public static final String SERVER_ADDR = "http://cscemobile.sytes.net/";
	
	public static final String URL_ALL_LOCATIONS = SERVER_ADDR + "get_locations.php";
	
	public static final String URL_LOCATION_DETAILS = SERVER_ADDR + "get_product_details.php";

	//PROBLY dont need bottom three
	public static final String URL_ITEMS_LOCATION = SERVER_ADDR + "get_item_by_location.php";
	
	public static final String URL_UPDATE_LOC_VIS = SERVER_ADDR + "location_visited.php";
	
	public static final String URL_CREATE_PRODUCT = SERVER_ADDR + "create_product.php";
	
}
