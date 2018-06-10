package tool1.utils;

import java.util.Iterator;
import java.util.stream.Stream;

import tool1.exceptions.InvalidConfigException;
import tool1.mapping.DBFileds;
import tool1.mapping.SupplierStatus;

public class Mapping {

	public static String getDBFileds(String value) throws InvalidConfigException {
		try {
			return DBFileds.valueOf(value.toUpperCase()).getValue();
		} catch (IllegalArgumentException e) {
			throw new InvalidConfigException("Invalid DB_FIELDS " + value + " in config file");
		}
	}

	public static Integer getSupplierStatus(String value) throws InvalidConfigException {
		try {
			return SupplierStatus.valueOf(value.toUpperCase()).getValue();
		} catch (IllegalArgumentException e) {
			throw new InvalidConfigException("Invalid SUPPLIER_STATUS " + value + " in config file");
		}
	}

	public static String getAllDBFields(String value) throws InvalidConfigException {
		String fields = "";
		boolean errors = false;
		String eFields = "";

		for (Iterator<String> iterator = Stream.of(value.split(",")).distinct().filter(e -> e.length() > 0)
				.iterator(); iterator.hasNext();) {
			String item = iterator.next();
			try {
				fields += "," + Mapping.getDBFileds(item.trim());
			} catch (InvalidConfigException e) {
				errors = true;
				eFields += "," + item;
			}
			Configuration.setNoOfFields(Configuration.getNoOfFields() + 1);
		}

		if (errors)
			throw new InvalidConfigException("Invalid DB_FIELDS " + eFields.replaceFirst(",", "") + " in config file");

		return fields.replaceFirst(",", "");
	}

}