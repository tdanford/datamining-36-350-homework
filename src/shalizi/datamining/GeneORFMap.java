package shalizi.datamining;

import java.util.*;
import java.io.*;

public class GeneORFMap {

	private File inputFile;
	private Map<String,String> orf2Gene, gene2Orf;
	
	public String orf(String gene) {
		return gene2Orf.get(gene); 
	}
	
	public String gene(String orf) {
		if(!orf2Gene.containsKey(orf)) { 
			return orf;
		}
		return orf2Gene.get(orf); 
	}
	
	public Collection<String> orfs() { return orf2Gene.keySet(); }
	public Collection<String> genes() { return gene2Orf.keySet(); }
	
	public GeneORFMap(File dir) throws IOException {
		inputFile = new File(dir, "orf_gene_names.txt");
		if(!inputFile.exists()) { 
			throw new IllegalArgumentException(inputFile.getAbsolutePath()); 
		}
		orf2Gene = new TreeMap<String,String>();
		gene2Orf = new TreeMap<String,String>();
		parse();
	}
	
	private void parse() throws IOException { 
		String line = null;
		String[] array = null;
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		while((line = br.readLine()) != null) { 
			array = line.split("\t");
			String orf = array[0], gene = array[1];
			orf2Gene.put(orf, gene);
			gene2Orf.put(gene, orf);
		}
		br.close();
	}
}
