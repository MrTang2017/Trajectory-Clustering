package com.traclustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveKDistanceToCsv {

	public static void save(String filename,List<Double> KD) {
		File file=new File(filename);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		BufferedWriter bw=null;
		try {
			bw=new BufferedWriter(new FileWriter(file));
			for(double dis:KD) {
				bw.write(Double.toString(dis));
				bw.newLine();			
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		String filename="./KD/1.csv";
		List<Double> list=new ArrayList<>();
		list.add(4.5);
		list.add(4.6);
		list.add(0.9);
		save(filename,list);
		
	}
}
