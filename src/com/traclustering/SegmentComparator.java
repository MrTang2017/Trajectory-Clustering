package com.traclustering;
/**
 *compare two segments according to time
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class SegmentComparator implements Comparator<Segment> {

	@Override
	public int compare(Segment s1, Segment s2) {
		// start time of the segment s1 
		String st1 = s1.sp.getTime();
		// end time of the segment s1
		String et1 = s1.ep.getTime();
		// start time of the segment s2
		String st2 = s2.sp.getTime();
		// end time of the segment s2
		String et2 = s2.ep.getTime();

		Date sDate1 = new Date(0);
		Date eDate1 = new Date(0);
		Date sDate2 = new Date(0);
		Date eDate2 = new Date(0);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = "2018-12-21 " + st1;
		String etr1 = "2018-12-21 " + et1;
		String str2 = "2018-12-21 " + st2;
		String etr2 = "2018-12-21 " + et2;
		try {
			sDate1 = (Date) df.parse(str1);
			sDate2 = (Date) df.parse(str2);
			if (sDate1.compareTo(sDate2) != 0)
				return sDate1.compareTo(sDate2);
			else {
				try {
					eDate1 = (Date) df.parse(etr1);
					eDate2 = (Date) df.parse(etr2);
					if (eDate1.compareTo(eDate2) != 0)
						return eDate1.compareTo(eDate2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
