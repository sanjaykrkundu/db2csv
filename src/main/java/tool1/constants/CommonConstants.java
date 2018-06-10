package tool1.constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonConstants {

	private static String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());

	public static final String CONFIG_FILE = "input" + File.separator + "config.properties";
	public static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	public static final String TENANTID_PATTERN = "[0-9]{1,11}";
	public static final String SUPPLIER_TABLE_NAME = "SMDMMASTERTBL";
	public static final String CLIENT_TABLE_NAME = "IMDMCLIENTMASTERTBL";
	public static final String ALL_TENANT_ID = "ALL";
	public static final String CSV_NULL_VALUE = "NULL";

	public static final String OUTPUT_FOLDER = "output" + File.separator + timeStamp;
	public static final String LOG_FILE_NAME = OUTPUT_FOLDER + File.separator + "logs.log";

}
