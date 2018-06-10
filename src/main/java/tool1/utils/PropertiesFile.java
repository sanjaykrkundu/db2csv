package tool1.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import tool1.constants.CommonConstants;
import tool1.exceptions.InvalidConfigException;
import tool1.mapping.MandatoryProperties;

public class PropertiesFile {

	public void loadConfiguration() throws InvalidConfigException, IOException {
		Properties properties = new Properties();
		File configFile = new File(CommonConstants.CONFIG_FILE);

		try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
			properties.load(fileInputStream);

			for (MandatoryProperties property : MandatoryProperties.values()) {
				String value = properties.getProperty(property.name());
				if (value == null) {
					throw new InvalidConfigException(property + " missing in config file");
				} else if (value.equals("")) {
					throw new InvalidConfigException(property + " value missing in config file");
				} else {
					Configuration.set(property.name(), value.trim());
				}
			}

		} catch (IOException | InvalidConfigException e) {
			throw e;
		}

	}

}
