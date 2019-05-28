package com.traclustering;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TimeComparator implements Comparator<String> {

	@Override
	public int compare(String t1, String t2) {
		Date Date1 = new Date(0);
		Date Date2 = new Date(0);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time1 = "2018-12-21 " + t1;
		String time2 = "2018-12-21 " + t2;
		try {
			Date1 = (Date) df.parse(time1);
			Date2 = (Date) df.parse(time2);
			if (Date1.compareTo(Date2) != 0)
				return Date1.compareTo(Date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
