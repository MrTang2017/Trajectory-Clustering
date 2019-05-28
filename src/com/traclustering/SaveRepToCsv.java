package com.traclustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * add the extracted cluster feature to file
 * @author ÌÀ¼ª
 *
 */
public class SaveRepToCsv {

	private static String filePath = "rep.csv";

	public static void save(Map<Integer, ClusterRepresent> crMap) throws FileNotFoundException {
		try {
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			for(Integer i:crMap.keySet()) {
				String label=Integer.toString(i);
				ClusterRepresent cRepresent=crMap.get(i);
				String x1=Double.toString(cRepresent.getStartPoint().getLng());
				String y1=Double.toString(cRepresent.getStartPoint().getLat());
				String t1=cRepresent.getStartPoint().getTime();
				String x2=Double.toString(cRepresent.getEndPoint().getLng());
				String y2=Double.toString(cRepresent.getEndPoint().getLat());
				String t2=cRepresent.getEndPoint().getTime();
				String avgVelocity=Double.toString(cRepresent.getAvgVelocity());
				String avgAngle=Double.toString(cRepresent.getAvgAngle());
				bw.write(label+","+x1+","+y1+","+t1+","+x2+","+y2+","+t2+
						","+avgVelocity+","+avgAngle);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
