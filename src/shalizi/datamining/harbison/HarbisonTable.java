package shalizi.datamining.harbison;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class HarbisonTable {
	
	/**
	 * Returns the name of the experiment with index i.
	 * 
	 * @param i
	 * @return
	 */
	public String experiment(int i) { return experiments[i]; }

	/**
	 * Returns an integer corresponding to the given experiment name. 
	 * If the argument matches an experiment name exactly, then that index is returned.
	 * Otherwise, the argument is treated as a regexp, and the index of some experiment 
	 * that matches it is returned.  
	 * @param key The name, or regexp for the name, of the experiment to find.
	 * @return The array index of the experiment, or -1 if not found.
	 */
	public int findExperiment(String key) { 
		if(exptIndices.containsKey(key)) { return exptIndices.get(key); }
		Pattern p = Pattern.compile(key);
		for(String k : exptIndices.keySet()) { 
			Matcher m = p.matcher(k);
			if(m.matches()) { 
				return exptIndices.get(k);
			}
		}
		return -1;
	}
	
	/**
	 * The complete set of probes for which values were taken in this table. 
	 * 
	 * @return
	 */
	public Collection<String> probes() { return table.keySet(); }
	
	public int quality(int i) { return qualityScores[i]; }
	
	public File inputFile() { return inputFile; }
	
	/**
	 * An array of measurements for a given probe -- 
	 * <tt>probeValues(p)[findExperiment(foo)]</tt> 
	 * gives the value of probe p in experiment foo.  If the probe has a missing value in the 
	 * experiment, then the array contains a null in that position.  
	 * 
	 * @param probe The name of the probe to retrieve.  (should be a member of probes()'s return value)
	 * @return
	 */
	public Float[] probeValues(String probe) { return table.get(probe); }
	
	/**
	 * Returns a map that associates probe names to floating point measurement values, 
	 * all from a single experiment.  Any probes with missing values in this experiment 
	 * are omitted from the map.
	 * 
	 * @param i the index of the experiment to retrieve 
	 * @return
	 */
	public Map<String,Float> exptValues(int i) { 
		TreeMap<String,Float> map = new TreeMap<String,Float>();
		for(String k : table.keySet()) { 
			Float v = table.get(k)[i];
			if(v != null) { 
				map.put(k, v);
			}
		}
		return map;
	}

	private File inputFile;
	private Integer[] qualityScores;
	private String[] experiments;
	private Map<String,Integer> exptIndices;
	private Map<String,Float[]> table;
	
	public HarbisonTable() throws IOException { 
		this("ypd");
	}
	
	public HarbisonTable(String key) throws IOException { 
		this(new File("/Users/tdanford/Documents/shalizi-dm-homework/harbison"), key);
	}
	
	public HarbisonTable(File dir, String key) throws IOException { 
		inputFile = new File(dir, String.format("ratiobygene_forpaper_%s_abbr.txt", key));
		if(!inputFile.exists()) { 
			throw new IllegalArgumentException(String.format("Unknown Harbison Key: %s (%s)", 
					key, inputFile.getAbsolutePath()));
		}
		
		exptIndices = new TreeMap<String,Integer>();
		table = new HashMap<String,Float[]>();
		
		int count = parse();
		System.out.println(String.format("Harbison: %d expts, %d probes, %d values", 
				experiments.length, table.size(), count));
	}
	
	private static int offset = 3;
	
	private int parse() throws IOException { 
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		int count = 0;
		
		line = reader.readLine(); // Quality Scores;
		String[] array = line.split("\t");
		qualityScores = new Integer[array.length-offset];
		for(int i = offset; i < array.length; i++) { 
			qualityScores[i-offset] = Integer.parseInt(array[i]);
		}
		
		line = reader.readLine(); // Experiment Names
		array = line.split("\t");
		experiments = new String[array.length-offset];
		for(int i = offset; i < array.length; i++) { 
			experiments[i-offset] = array[i];
			exptIndices.put(array[i], i-offset);
		}
		
		// Reads the rest of the table.  
		while((line = reader.readLine()) != null) { 
			array = line.split("\t");
			String id = array[0];  // first column is the ID
			if(array.length != experiments.length+offset) { 
				System.err.println(String.format("Line %s has length %d != %d",
						id, array.length, experiments.length+offset));
			} else { 
				// we're going to skip the second column (an index value), and 
				// put the rest of the values in the array.  Any parsing exceptions 
				// indicate missing (NULL) values.  
				Float[] values = new Float[experiments.length];
				for(int i = 0; i < experiments.length; i++) {
					try { 
						values[i] = Float.parseFloat(array[i+offset]);
						count += 1;
					} catch(NumberFormatException nfe) { 
						values[i] = null;
					}
				}
				table.put(id, values);
			}
		}
		
		reader.close();
		return count;
	}
}
