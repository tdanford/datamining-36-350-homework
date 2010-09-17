package shalizi.datamining.geo;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class SampleDescriptionExtracter extends SoftParser {
	
	public static void main(String[] args) { 
		File dir = new File("/Users/tdanford/Documents/Data/Microarrays/" +
				"Affy/YeastS98/geo_data/");
		
		File[] fs = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("GSE");  
			} 
		});

		try { 
			SampleDescriptionExtracter parser = new SampleDescriptionExtracter(dir);
			for(int i = 0; i < fs.length; i++) { 
				File f = fs[i];
				InputStream is = new FileInputStream(f);
				if(f.getName().endsWith("gz")) { 
					is = new GZIPInputStream(is);
				}
				System.out.println(String.format("Loading: %s", fs[i].getAbsolutePath()));

				parser.parse(is);
				is.close();

			}
			parser.close();
		} catch(IOException ie) {
			ie.printStackTrace(System.err);
		}
	}
	
	private File outputDir;
	private PrintStream output;
	
	private String sampleKey;
	private String sampleTitle;
	private String sampleValue;
	
	public SampleDescriptionExtracter(File outDir) throws IOException { 
		outputDir = outDir;
		output = new PrintStream(new FileOutputStream(
				new File(outputDir, "sample-descriptions.txt")));
		sampleKey = null;
		sampleTitle = sampleValue = null;
	}
	
	public SampleDescriptionExtracter(File input, File outDir) throws IOException {
		this(outDir);
		if(!outputDir.isDirectory()) { throw new IllegalArgumentException(); }
		if(!input.exists() || input.isDirectory()) { 
			throw new IllegalArgumentException();
		}
		parse(input);
	}

	public void handleComment(String comment) {
		if(comment.startsWith("VALUE")) { 
			String[] array = comment.split("=");
			sampleValue = array[1].trim();
		}
	}

	public void handleDataLine(String line) {
	}

	public void handleEntry(String key, String value) {
		if(key.equals("SAMPLE_TITLE")) { 
			sampleTitle = value;
		}
	}

	public void handleSection(String type, String value) {
		if(type.equals("SAMPLE")) {
			printIfOpen();
			sampleKey = value;
		}
	}
	
	private void printIfOpen() { 
		if(output != null && sampleKey != null) {  
			output.println(String.format("%s\t%s\t%s", 
					sampleKey, sampleTitle, sampleValue));
			sampleKey = sampleTitle = sampleValue = null;
		}
	}
	
	public void close() { 
		if(output != null) { 
			printIfOpen();
			output.close();
			output = null;
		}		
	}
	
	public void handleSOFTEnd() {
		printIfOpen();
	}
	
	public void handleSOFTStart() { 
		
	}
}
