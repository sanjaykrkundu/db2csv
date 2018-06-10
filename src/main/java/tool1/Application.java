package tool1;

import org.apache.log4j.Logger;

import tool1.constants.CommonConstants;
import tool1.utils.Configuration;

public class Application {

	static {
		System.setProperty("mylog", CommonConstants.LOG_FILE_NAME);
	}

	static Logger logger = Logger.getLogger(Application.class);

	public static void main(String[] args) {
		try {
			Configuration.loadConfiguration();
			Configuration.print();
			DBService.validateClientsFromDB();
			DBService.getData();
			System.out.println("Files are generated inside " + CommonConstants.OUTPUT_FOLDER);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			System.out.println(e.getMessage());
		}

	}

}
