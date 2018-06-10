package tool1.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import tool1.constants.CommonConstants;

public class CSVFileWriter {

	private File file;
	private File dir;
	private BufferedWriter writer = null;

	public CSVFileWriter(String dirName, String fileName) throws IOException {
		dir = new File(dirName);

		if (!dir.exists())
			dir.mkdirs();

		file = new File(dir.getPath() + File.separator + fileName);

		writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);

	}

	public void write(String line) throws IOException {
		writer.write(line);
		writer.newLine();
		writer.flush();
	}

	public void close() throws IOException {
		if (writer != null) {
			writer.close();
		}
	}

	public static String toCSVString(String data) {
		String csvData = data;
		if (data == null)
			csvData = CommonConstants.CSV_NULL_VALUE;
		else if (data.contains(",") || data.contains("\"")) {
			csvData = "\"" + data.replace("\"", "\"\"") + "\"";
		}
		return csvData;
	}

}
