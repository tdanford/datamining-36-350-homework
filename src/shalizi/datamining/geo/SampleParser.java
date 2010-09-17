package shalizi.datamining.geo;

import java.util.*;
import java.io.*;

public class SampleParser {
	
	private String[] header;
	private int probeIdx, valueIdx;
	private String[] ids;
	private Map<String,Integer> probeMap;
	private Double[] values;
	
	public String getProbe(int i) { return ids[i]; }
	public Double getValue(int i) { return values[i]; }
	public int size() { return ids.length; }
	
	public int findProbe(String p) { 
		return probeMap.containsKey(p) ? probeMap.get(p) : -1;
	}

	public SampleParser(File f) throws IOException { 
		BufferedReader br = new BufferedReader(new FileReader(f));
		probeMap =new TreeMap<String,Integer>();
		String line = br.readLine();
		String[] array = line.split("\\s+");
		header = array;
		probeIdx = valueIdx = -1;
		for(int i = 0; i < header.length; i++) { 
			if(header[i].equals("ID_REF")) { 
				probeIdx = i;
			} else if (header[i].equals("VALUE")) { 
				if(valueIdx != -1) { 
					throw new IllegalArgumentException(f.getName());
				} else { 
					valueIdx = i;
				}
			}
		}
		
		ArrayList<String> probelist = new ArrayList<String>();
		ArrayList<Double> valuelist = new ArrayList<Double>();
		
		while((line = br.readLine()) != null) { 
			array = line.trim().split("\\s+");
			String id = array[probeIdx];
			Double value = null;
			try { 
				value = Double.parseDouble(array[valueIdx]);
			} catch(NumberFormatException nfe) { 
				value = null;
			}
			probelist.add(id);
			valuelist.add(value);
		}
		
		ids = probelist.toArray(new String[0]);
		values = valuelist.toArray(new Double[0]);
		
		for(int i = 0; i < ids.length; i++) { 
			probeMap.put(ids[i], i);
		}
		
		br.close();
	}
}
