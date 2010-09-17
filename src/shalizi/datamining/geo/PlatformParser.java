package shalizi.datamining.geo;

import java.io.*;
import java.util.*;

public class PlatformParser extends SoftParser {
	
	public static void main(String[] args) { 
		File dir = new File("/Users/tdanford/Documents/Data/Microarrays/Affy/YeastS98/");
		File in = new File(dir, "GPL90.annot");
		File out = new File(dir, "GPL90.probemap.txt");
		try {
			new PlatformParser(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int idIdx, orfIdx;
	private PrintStream ps;
	
	public PlatformParser(File f, File out) throws IOException { 
		idIdx = orfIdx = -1;
		ps = new PrintStream(new FileOutputStream(out));
		parse(f);
	}

	public void handleComment(String comment) {
	}

	public void handleDataLine(String line) {
		String[] array = line.split("\t");
		if(idIdx == -1) { 
			for(int i = 0; i < array.length; i++) { 
				if(array[i].equals("ID")) { 
					idIdx = i;
				} else if (array[i].equals("Platform_ORF")) { 
					orfIdx = i;
				}
			}
		} else {
			if(idIdx < array.length && orfIdx < array.length) { 
				String probe = array[idIdx];
				String orf = array[orfIdx];
				ps.println(String.format("%s\t%s", probe, orf)); 
			} else { 
				System.err.println(line);
			}
		}
	}

	public void handleEntry(String key, String value) {
	}

	public void handleSOFTEnd() {
		ps.close();
	}

	public void handleSOFTStart() {
	}

	public void handleSection(String type, String value) {
	}
}
