package shalizi.datamining.geo;

import java.util.*;
import java.io.*;
import java.util.regex.*;

public abstract class SoftParser {
	
	public static void main(String[] args) {
		File f = new File("/Users/tdanford/Documents/Data/Microarrays/Affy/YeastS98/geo_data/GSE10066_family.soft");
		try {
			new PrintingParser().parse(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class PrintingParser extends SoftParser {

		public void handleComment(String comment) {
		}

		public void handleDataLine(String line) {
		}

		public void handleEntry(String key, String value) {
			System.out.println(String.format("ENTRY(%s)=%s", key, value));
		}

		public void handleSection(String type, String value) {
			System.out.println(String.format("\nSECTION(%s)=%s", type, value));
		}

		public void handleSOFTEnd() {
		}

		public void handleSOFTStart() {
		} 
		
	}

	public SoftParser() { 
	}
	
	public void parse(File f) throws IOException { 
		FileInputStream fis = new FileInputStream(f);
		parse(fis);
		fis.close();		
	}
	
	private static Pattern commentPattern = Pattern.compile("^#(.*)$");
	private static Pattern sectionPattern = Pattern.compile("^\\^([^=]+)\\s*=\\s*(.*)$");
	private static Pattern entryPattern = Pattern.compile("^!.+$");
	
	public void parse(InputStream is) throws IOException { 
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		handleSOFTStart();
		
		while((line = br.readLine()) != null) { 
			Matcher m;
			m = sectionPattern.matcher(line);
			if((m = sectionPattern.matcher(line)).matches()) { 
				String type = m.group(1).trim().toUpperCase(), value = m.group(2);
				handleSection(type, value);
			} else if ((m = commentPattern.matcher(line)).matches()) {
				String comment = m.group(1);
				handleComment(comment);
			} else if ((m = entryPattern.matcher(line)).matches()) {
				int idx = line.indexOf("=");
				String key = line.substring(1, line.length()).trim();
				String value = null;
				if(idx != -1) { 
					key = line.substring(1, idx).trim();
					value = line.substring(idx+1, line.length()).trim();
				}
				handleEntry(key.toUpperCase(), value);
			} else { 
				handleDataLine(line);
			}
		}
		
		handleSOFTEnd();
	}
	
	public abstract void handleSOFTEnd();
	public abstract void handleSOFTStart();

	public abstract void handleComment(String comment);
	public abstract void handleSection(String type, String value);
	public abstract void handleEntry(String key, String value);
	public abstract void handleDataLine(String line);
}
