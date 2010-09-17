package shalizi.datamining.geo;

import java.io.*;

public class SampleMatrixCounter {
	
	public static void main(String[] args) { 
		File dir = new File(
				"/Users/tdanford/Documents/Data/Microarrays/Affy/YeastS98/geo_data/samples/");
		File f = new File(dir, "sample_matrix.txt");
		try {
			new SampleMatrixCounter(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String[] expts;
	private int[] counts;
	
	public SampleMatrixCounter(File input) throws IOException { 
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = br.readLine();
		expts = line.split("\t");
		counts = new int[expts.length];
		for(int i = 0; i < counts.length; i++) { counts[i] = 0; }
		while((line = br.readLine()) != null) { 
			String[] array = line.split("\t");
			if(array.length > 1) { 
				counts[0] += 1;
				for(int i = 1; i < array.length; i++) { 
					if(array[i].equals("NA")) { 
						counts[i] += 1;
					}
				}
			}
		}
		br.close();
		
		for(int i = 0; i < counts.length; i++) { 
			if(counts[i] > 0) { 
				System.out.println(String.format("%s \t%d", expts[i], counts[i]));
			}
		}
	}

}
