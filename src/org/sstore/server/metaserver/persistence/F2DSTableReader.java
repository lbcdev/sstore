package org.sstore.server.metaserver.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.sstore.utils.Constants;

public class F2DSTableReader {

	private static Logger log = Logger.getLogger(F2DSTableReader.class);

	public static Map<String, String> loadFromDisk() {
		Map<String, String> f2dsTable = new HashMap<String, String>();
		String filepath = Constants.METAROOTDIR + "f2dsTable";
		File f = new File(filepath);
		if (f.exists()) {
			try (Stream<String> stream = Files.lines(Paths.get(filepath))) {
				stream.forEach((str) -> {
					String[] pair = str.split("@");
					if (pair.length == 2) {
						f2dsTable.put(pair[0], pair[1]);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return f2dsTable;
	}

}
