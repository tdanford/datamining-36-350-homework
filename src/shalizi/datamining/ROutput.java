package shalizi.datamining;

import java.util.*;
import java.io.*;

import shalizi.datamining.harbison.*;
import shalizi.datamining.iyer.*;

public class ROutput {

	/**
	 * You're probably going to want to up the default heap size on the Java VM before 
	 * running this. 
	 * 
	 * java -Xms150m -Xmx300m shalizi.datamining.Test 
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) { 
		File dir = new File("/Users/tdanford/Documents/shalizi-dm-homework");
		File harbison = new File(dir, "harbison");
		File iyer = new File(dir, "iyer");
		
		try {
			HarbisonTable ht = new HarbisonTable(harbison, "ypd");
			IyerTable it = new IyerTable(iyer);
			GeneORFMap genes = new GeneORFMap(dir);
			
			TreeSet<String> hprobes = new TreeSet<String>(ht.probes());
			TreeSet<String> iprobes = new TreeSet<String>(it.probes());
			TreeSet<String> common = new TreeSet<String>();
			
			for(String hprobe : hprobes) { 
				if(iprobes.contains(hprobe)) { 
					common.add(hprobe);
				}
			}
			
			System.out.println(String.format("Common Probes: %d", common.size()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
