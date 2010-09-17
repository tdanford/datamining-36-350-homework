package shalizi.datamining.geo;

import java.io.*;
import java.util.*;

public class SampleMatrix {
	
	public static void main(String[] args) { 
		File dir = new File(
				"/Users/tdanford/Documents/Data/Microarrays/Affy/YeastS98/geo_data/samples/");
		File f = new File(dir, "sample_matrix.txt");
		try {
			SampleMatrix m = new SampleMatrix(f);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String[] expts;
	private String[] orfs;
	private Map<String,Integer> orfMap, exptMap;
	private Double[][] matrix;
	
	public Double[] getOrfValues(String orf) { 
		return orfMap.containsKey(orf) ? 
				matrix[orfMap.get(orf)] : null;
	}
	
	public Double[] getExptValues(String expt) {
		if(!exptMap.containsKey(expt)) { return null; }
		int eidx = exptMap.get(expt);
		Double[] e = new Double[orfs.length];
		for(int i = 0; i < e.length; i++) { 
			e[i] = matrix[i][eidx];
		}
		return e;
	}
	
	public Set<String> orfs() { return orfMap.keySet(); }
	public Set<String> expts() { return exptMap.keySet(); }
	
	public Double getValue(String orf, String expt) { 
		return orfMap.containsKey(orf) && exptMap.containsKey(expt) ? 
				matrix[orfMap.get(orf)][exptMap.get(expt)] : null;
	}
	
	public SampleMatrix(File input) throws IOException { 
		BufferedReader br = new BufferedReader(new FileReader(input));
		String line = br.readLine();
		
		String[] array = line.split("\t");
		expts = new String[array.length-1];
		exptMap = new TreeMap<String,Integer>();
		for(int i = 0; i < expts.length; i++) { 
			expts[i] = array[i+1]; 
			exptMap.put(expts[i], i);
		}
		
		ArrayList<String> orflist = new ArrayList<String>();
		ArrayList<Double[]> matrixlist = new ArrayList<Double[]>();
		orfMap = new TreeMap<String,Integer>();
		
		int lineNum = 0;
		while((line = br.readLine()) != null) { 
			array = line.split("\t");
			if(array.length > 1) {
				orflist.add(array[0]);
				Double[] d = new Double[array.length-1];
				for(int i = 0; i < d.length; i++) { 
					d[i] = Double.parseDouble(array[i+1]);
				}
				matrixlist.add(d);
				orfMap.put(array[0], lineNum++);
			}
		}
		br.close();

		orfs = orflist.toArray(new String[0]);
		matrix = matrixlist.toArray(new Double[0][]);
		
		System.out.println(String.format("Loaded sample matrix:\n" +
				"\t# ORFs: %d\n" +
				"\t# Expts: %d", orfs.length, expts.length));
	}

}
