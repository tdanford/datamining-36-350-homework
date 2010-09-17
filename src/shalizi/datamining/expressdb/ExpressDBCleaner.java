package shalizi.datamining.expressdb;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class ExpressDBCleaner {
	
	public static void main(String[] args) {
		File dir = new File("/Users/tdanford/Documents/Projects/shalizi-dm-homework/ExpressDB");
		File input = new File(dir, "expressdb_normalized.txt");
		File output = new File(dir, "expressdb_cleaned.txt");
		
		try {
			new ExpressDBCleaner(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ExpressDBCleaner(File input, File output) throws IOException { 
		BufferedReader br = new BufferedReader(new FileReader(input));
		PrintStream ps = new PrintStream(new FileOutputStream(output));
		String line = null;
		Set<Integer> ignore = new TreeSet<Integer>();
		ignore.add(1);
		ignore.add(2);
		ignore.add(3);
		
		int count = -1;
		int linenum = 0;
		
		while((line = br.readLine()) != null) {  
			String[] array = splitter(line, "\t");
			String[] subset = subset(array, ignore);
			if(count == -1) { 
				count = subset.length - 1; 
				for(int i = 1; i < subset.length; i++) { 
					if(i > 1) { ps.print("\t"); }
					ps.print(subset[i]);					
				}
			} else { 
				if(subset.length != count + 1) { 
					System.err.println(String.format(
							"\tLine %d has length %d != %d", linenum, array.length, count+1));
				}
				for(int i = 0; i < subset.length; i++) { 
					if(i > 0) { ps.print("\t"); }
					ps.print(subset[i]);
				}
			}
			ps.println();
			linenum += 1;
		}
		br.close();
		ps.close();
		
		System.out.println(String.format("%s -> %s", input.getAbsolutePath(), 
				output.getAbsolutePath()));
	}
	
	private String[] splitter(String line, String find) { 
		int idx = -1, last = -1;
		ArrayList<String> splits = new ArrayList<String>();
		while((idx = line.indexOf(find, idx+1)) != -1) { 
			String col = line.substring(last+1, idx);
			splits.add(col);
			last = idx;
		}

		if(last != -1) { 
			splits.add(line.substring(last+1, line.length()));
		} else { 
			splits.add(line);
		}
		
		return splits.toArray(new String[0]);
	}
	
	private int count(String line, String find) { 
		int idx = -1;
		int count = 0;
		while((idx = line.indexOf(find, idx+1)) != -1) { 
			count += 1;
		}
		return count;
	}
	
	private String[] subset(String[] base, Set<Integer> ignore) { 
		ArrayList<String> subset = new ArrayList<String>();
		for(int i = 0; i < base.length; i++) { 
			if(!ignore.contains(i)) { 
				subset.add(base[i]);
			}
		}
		return subset.toArray(new String[0]);
	}
}
