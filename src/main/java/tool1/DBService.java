package tool1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import tool1.constants.CommonConstants;
import tool1.exceptions.DBException;
import tool1.exceptions.InvalidConfigException;
import tool1.mapping.MandatoryProperties;
import tool1.utils.CSVFileWriter;
import tool1.utils.Configuration;
import tool1.utils.DBConnection;

public class DBService {

	private static Logger logger = Logger.getLogger(DBService.class);

	private static List<Long> clientIds = new ArrayList<>();

	public static void validateClientsFromDB() throws InvalidConfigException, DBException {

		String clients = (String) Configuration.get(MandatoryProperties.CLIENT_ID.name());
		Connection connection = DBConnection.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			if (clients.equalsIgnoreCase(CommonConstants.ALL_TENANT_ID)) {
				String getAllClientsQuery = "SELECT DISTINCT(CLIENTID) FROM " + CommonConstants.SUPPLIER_TABLE_NAME
						+ " WHERE SUPPLIERSTATUS = ?";

				statement = connection.prepareStatement(getAllClientsQuery);
				statement.setInt(1, (int) Configuration.get(MandatoryProperties.SUPPLIER_STATUS.name()));

				resultSet = statement.executeQuery();
				while (resultSet.next()) {
					clientIds.add(resultSet.getLong(1));
				}

			} else {
				HashMap<Long, Boolean> clientsIds = new HashMap<>();
				Stream.of(clients.split(",")).forEach(e -> {
					Long clientId = Long.parseLong(e);
					DBService.clientIds.add(clientId);
					clientsIds.put(clientId, false);
				});

				String validateClientQuery = "SELECT CLIENTID FROM " + CommonConstants.CLIENT_TABLE_NAME
						+ " WHERE CLIENTID IN (" + clients.replaceAll(CommonConstants.TENANTID_PATTERN, "?") + ")";

				statement = connection.prepareStatement(validateClientQuery);
				{
					int clientCounter = 1;
					for (Long clientid : clientsIds.keySet()) {
						statement.setLong(clientCounter++, clientid);
					}
				}

				resultSet = statement.executeQuery();
				while (resultSet.next()) {
					Long cid = resultSet.getLong(1);
					clientsIds.put(cid, true);
				}
				String listOfInvalidClients = clientsIds.entrySet().parallelStream().filter(e -> e.getValue() == false)
						.map(e -> e.getKey().toString()).collect(Collectors.joining(","));

				if (!listOfInvalidClients.equals("")) {
					DBConnection.closeConnection();
					throw new InvalidConfigException(listOfInvalidClients + " tenant(s) are not present in DB");
				}
			}
		} catch (SQLException e) {
			throw new DBException("Error while fetching data");
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					DBConnection.closeConnection();
			} catch (SQLException e) {
				throw new DBException("Error while closing Statement and ResultSet");
			}
		}

	}

	public static void getData() throws SQLException, DBException, IOException {
		System.out.println("Connected to DB " + Configuration.get(MandatoryProperties.DB_NAME.name()));
		int supplierStatus = (int) Configuration.get(MandatoryProperties.SUPPLIER_STATUS.name());
		String fieldsToBeSearchedFor = (String) Configuration.get(MandatoryProperties.DB_FIELDS.name());

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		System.out.println("Preparing Query...");
		String query = "SELECT " + fieldsToBeSearchedFor + " FROM " + CommonConstants.SUPPLIER_TABLE_NAME
				+ " WHERE SUPPLIERSTATUS = ? AND CLIENTID = ?";

		try {
			connection = DBConnection.getConnection();
			System.out.println("Fetching Data...");
			for (Long clientId : clientIds) {
				statement = connection.prepareStatement(query);
				statement.setInt(1, supplierStatus);
				statement.setLong(2, clientId);

				logger.info("Creating csv file for client " + clientId + " for supplier status "
						+ Configuration.getSupplierStatus());

				CSVFileWriter writer = new CSVFileWriter(CommonConstants.OUTPUT_FOLDER,
						clientId + "_" + Configuration.getSupplierStatus() + ".csv");

				writer.write(Configuration.getUsersDbFields());
				resultSet = statement.executeQuery();

				ResultSetMetaData metaData = resultSet.getMetaData();

				while (resultSet.next()) {
					String row = "";
					for (int i = 1; i <= Configuration.getNoOfFields(); i++) {
						String data;

						if (metaData.getColumnType(i) == Types.TIMESTAMP) {
							Timestamp timestamp = resultSet.getTimestamp(i);
							if (timestamp != null)
								data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
										.format(new Date(timestamp.getTime()));
							else
								data = null;
						} else {
							data = resultSet.getString(i);
						}

						row += "," + CSVFileWriter.toCSVString(data);

					}
					writer.write(row.replaceFirst(",", ""));
				}

				writer.close();

			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new DBException("Error fetching data");
		} catch (IOException e) {

		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					DBConnection.closeConnection();
				System.out.println("Fetching Data completed successfully!!!");
			} catch (SQLException e) {
				throw new DBException("Error while closing Statement and ResultSet");
			}
		}
	}
}
