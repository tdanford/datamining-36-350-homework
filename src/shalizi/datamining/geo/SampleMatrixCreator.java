package shalizi.datamining.geo;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class SampleMatrixCreator {
	
	public static void main(String[] args) { 
		File dir = new File(
				"/Users/tdanford/Documents/Data/Microarrays/Affy/" +
				"YeastS98/geo_data/samples/");
		try {
			new SampleMatrixCreator(dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class ProbeMap { 
		private Map<String,Set<String>> orfToProbes;
		
		public ProbeMap(File f) throws IOException { 
			orfToProbes = new TreeMap<String,Set<String>>();
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while((line = br.readLine()) != null) { 
				if(line.length() > 0) { 
					String[] array = line.split("\t");
					if(array.length > 1) {
						String p = array[0], orf = array[1].trim();
						if(orf.length() > 0) { 
							if(!orfToProbes.containsKey(orf)) { 
								orfToProbes.put(orf, new TreeSet<String>());
							}
							orfToProbes.get(orf).add(p);
						}
					} else { 
						System.err.println(String.format("\"%s\"", line));
					}
				}
			}
			br.close();
			System.out.println(String.format("Loaded Probe Map: %s\n\t# ORFs: %d",
					f.getAbsolutePath(), orfToProbes.size()));
		}
		
		public Double averageOrf(SampleParser parser, String orf) { 
			Set<String> probes = orfToProbes.containsKey(orf) ? 
					orfToProbes.get(orf) : new TreeSet<String>();
			int count = 0;
			double sum = 0.0;
			for(String p : probes) { 
				int idx = parser.findProbe(p);
				if(idx != -1) { 
					Double v = parser.getValue(idx);
					if(v != null) { 
						sum += v;
						count += 1;
					}
				}
			}
			return count == 0 ? null : sum / (double)count;
		}
		
		public Set<String> orfs() { return orfToProbes.keySet(); }
	}
	
	private static String sampleName(String filename) { 
		Pattern p = Pattern.compile("(.*)\\.sample\\.txt");
		Matcher m = p.matcher(filename);
		return m.matches() ? m.group(1) : filename;
	}

	public SampleMatrixCreator(File dir) throws IOException { 
		File[] samples = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".sample.txt");
			} 
		});
		File[] pmfiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".probemap.txt");
			} 
		});
		ProbeMap probeMap = new ProbeMap(pmfiles[0]);
		
		File output = new File(dir, "sample_matrix.txt");
		outputMatrix(output, probeMap, samples);
	}
	
	private void outputMatrix(File outf, ProbeMap pm, File[] samples) throws IOException {
		SampleParser[] parsers = new SampleParser[samples.length];
		for(int i = 0; i < parsers.length; i++) {
			System.out.println(String.format("Loading: %s", samples[i].getAbsolutePath()));
			parsers[i] = new SampleParser(samples[i]);
		}
		System.out.println(String.format("Outputting matrix (%d expts) : %s", 
				samples.length, outf.getAbsolutePath()));
		PrintStream ps = new PrintStream(new FileOutputStream(outf));
		
		ps.print("ORF");
		for(int i = 0; i < samples.length; i++) { 
			ps.print(String.format("\t%s", sampleName(samples[i].getName())));
		}
		ps.println();
		
		for(String orf : pm.orfs()) {
			ps.print(orf);
			for(int i = 0; i < parsers.length; i++) { 
				Double value = pm.averageOrf(parsers[i], orf);
				String val = value != null ? String.format("%.2f", value) : "NA";
				ps.print("\t" + val);
			}
			ps.println();
		}
		
		ps.close();
	}
}
