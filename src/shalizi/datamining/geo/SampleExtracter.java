package shalizi.datamining.geo;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class SampleExtracter extends SoftParser {
	
	public static void main(String[] args) { 
		File dir = new File("/Users/tdanford/Documents/Data/Microarrays/Affy/YeastS98/geo_data/");
		File[] fs = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("GSE");  
			} 
		});

		SampleExtracter parser = new SampleExtracter(dir);
		for(int i = 0; i < fs.length; i++) { 
			File f = fs[i];
			try { 
				InputStream is = new FileInputStream(f);
				if(f.getName().endsWith("gz")) { 
					is = new GZIPInputStream(is);
				}
				System.out.println(String.format("Loading: %s", fs[i].getAbsolutePath()));
				
				parser.parse(is);
				is.close();
				
			} catch(IOException ie) {
				ie.printStackTrace(System.err);
			}
		}
	}
	
	private File outputDir;
	private PrintStream output;
	
	public SampleExtracter(File outDir) { 
		outputDir = outDir;
		output = null;
	}
	
	public SampleExtracter(File input, File outDir) throws IOException {
		this(outDir);
		if(!outputDir.isDirectory()) { throw new IllegalArgumentException(); }
		if(!input.exists() || input.isDirectory()) { 
			throw new IllegalArgumentException();
		}
		parse(input);
	}

	public void handleComment(String comment) {
	}

	public void handleDataLine(String line) {
		printIfOpen(line);
	}

	public void handleEntry(String key, String value) {
	}

	public void handleSection(String type, String value) {
		if(type.equals("SAMPLE")) { 
			closeOutputIfOpen();
			File out = new File(outputDir, String.format("%s.sample.txt", value));
			try {
				output = new PrintStream(new FileOutputStream(out));
				System.out.println(String.format("Saving: %s", value));
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void printIfOpen(String line) { 
		if(output != null) { 
			output.println(line);
		}
	}
	
	private void closeOutputIfOpen() { 
		if(output != null) { 
			output.close();
			output = null;
		}		
	}
	
	public void handleSOFTEnd() {
		closeOutputIfOpen();
	}
	
	public void handleSOFTStart() { 
		
	}
}
