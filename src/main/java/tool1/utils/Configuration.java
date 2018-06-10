package tool1.utils;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.Logger;

import tool1.constants.CommonConstants;
import tool1.exceptions.InvalidConfigException;
import tool1.mapping.MandatoryProperties;

public class Configuration {

	static Logger logger = Logger.getLogger(Configuration.class);

	private static Map<String, Object> config = new HashMap<String, Object>();
	private static String username;
	private static String password;
	private static String usersDbFields;
	private static String supplierStatus;
	private static int noOfFields;

	public static void set(String key, String value) {
		config.put(key, value);
	}

	public static Object get(String key) {
		return config.get(key);
	}

	public static void print() {
		logger.info("=========================");
		for (Entry<String, Object> e : config.entrySet()) {
			if (!e.getKey().equals(MandatoryProperties.DB_FIELDS.name())
					&& !e.getKey().equals(MandatoryProperties.SUPPLIER_STATUS.name()))
				logger.info(e.getKey() + " = " + e.getValue());
		}
		logger.info("USERNAME = " + username);
		logger.info("UI fields provided by USER = " + usersDbFields);
		logger.info("SUPPLIER STATUS = " + supplierStatus);
		logger.info("=========================");
	}

	private static void validateConfigurations() throws InvalidConfigException {
		supplierStatus = (String) Configuration.get(MandatoryProperties.SUPPLIER_STATUS.name());
		config.put(MandatoryProperties.SUPPLIER_STATUS.name(), Mapping.getSupplierStatus(supplierStatus));
		usersDbFields = (String) Configuration.get(MandatoryProperties.DB_FIELDS.name());
		config.put(MandatoryProperties.DB_FIELDS.name(), Mapping.getAllDBFields(usersDbFields));
		validateIP((String) config.get(MandatoryProperties.DB_SERVER.name()));
		validatePort((String) config.get(MandatoryProperties.DB_PORT.name()));
		config.put(MandatoryProperties.CLIENT_ID.name(),
				validateTenantId((String) config.get(MandatoryProperties.CLIENT_ID.name())));
	}

	public static String getSupplierStatus() {
		return supplierStatus;
	}

	public static String getUsersDbFields() {
		return usersDbFields;
	}

	private static void validateIP(String ip) throws InvalidConfigException {
		if (!ip.equalsIgnoreCase("localhost"))
			if (!ip.matches(CommonConstants.IPADDRESS_PATTERN)) {
				throw new InvalidConfigException("Invalid IP in config file");
			}
	}

	private static String validateTenantId(String value) throws InvalidConfigException {

		boolean errors = false;
		String eTenant = "";
		String tenant = "";
		value = value.replace(" ", "");

		if (!value.equalsIgnoreCase(CommonConstants.ALL_TENANT_ID)) {
			for (String item : value.split(",")) {
				if (item.length() == 0)
					continue;
				if (!item.matches(CommonConstants.TENANTID_PATTERN)) {
					errors = true;
					eTenant += "," + item;
				} else {
					tenant += "," + item;
				}
			}

			if (errors) {
				throw new InvalidConfigException(
						"Invalid TENANT_ID(s) " + eTenant.replaceFirst(",", "") + " in config file.");
			}
			return tenant.replaceFirst(",", "");
		} else {
			return CommonConstants.ALL_TENANT_ID;
		}

	}

	private static void validatePort(String portValue) throws InvalidConfigException {
		int port;
		try {
			port = Integer.parseInt(portValue);
			if (port > 65535 || port < 1024)
				throw new InvalidConfigException("Invalid DB_PORT in config file, should be between [1024-65535]");
		} catch (NumberFormatException e) {
			throw new InvalidConfigException("Invalid DB_PORT in config file, should be numeric");
		}

	}

	private static void readCredentials() throws Exception {
		Console console = null;
		try {
			console = System.console();
			if (console != null) {
				username = console.readLine("Username: ");
				char[] pwd = console.readPassword("Password: ");
				password = new String(pwd);
			} else {
				try (Scanner scanner = new Scanner(System.in)) {
					System.out.print("Username : ");
					username = scanner.nextLine();
					System.out.print("Password : ");
					password = scanner.nextLine();
				} catch (Exception e) {
					throw e;
				}
			}

		} catch (Exception e) {
			throw e;
		}

	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static void loadConfiguration() throws Exception {
		logger.info("reading config.properties file.");
		new PropertiesFile().loadConfiguration();
		logger.info("validating configurations.");
		Configuration.validateConfigurations();
		logger.info("reading creadentials");
		Configuration.readCredentials();
	}

	public static int getNoOfFields() {
		return noOfFields;
	}

	public static void setNoOfFields(int noOfFields) {
		Configuration.noOfFields = noOfFields;
	}
}
