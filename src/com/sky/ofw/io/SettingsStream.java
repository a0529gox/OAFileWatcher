package com.sky.ofw.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsStream {
	
	public static final String SETTINGS_FILE_NAME = "settings.ini";

	public SettingsStream() {
		// TODO Auto-generated constructor stub
	}

	public static synchronized Map<String, String> read() throws IOException {
		Map<String, String> data = new HashMap<String, String>();
		
		File file = getSettingsFile();
		if (!file.exists()) {
			return data;
		}
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				int idx = line.indexOf("=");
				if (idx > 0) {
					String key = line.substring(0, idx);
					String value = line.substring(idx + 1);
					data.put(key, value);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		
		return data;
	}
	
	public static synchronized void write(Map<String, String> data) throws IOException {
		File file = getSettingsFile();
		BufferedWriter bw = null;
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		try {
			bw = new BufferedWriter(new FileWriter(file));
			
			for (String key : data.keySet()) {
				String line = key + "=" + data.get(key);
				
				bw.write(line);
				bw.newLine();
			}
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
		
	}
	
	private static File getSettingsFile() {
		return new File(SETTINGS_FILE_NAME);
	}
}
