package com.traclustering;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * calculate the distance between segments
 * 
 * @author Tang
 *
 */
public class SegDistance {
	private Segment segment1;
	private Segment segment2;

	public SegDistance(Segment s1, Segment s2) {
		this.segment1 = s1;
		this.segment2 = s2;
	}

	/**
	 * calculate perpendicular distance between segments
	 * 
	 * @return
	 */
	public double perpendDist() {
		Point a = this.segment1.sp;
		Point b = this.segment1.ep;
		Point c = this.segment2.sp;
		Point d = this.segment2.ep;
		double abDist = this.segment1.distMeter();
		double cdDist = this.segment2.distMeter();
		double acDist = a.distMeter(c);
		double adDist = a.distMeter(d);
		double bcDist = b.distMeter(c);
		double bdDist = b.distMeter(d);
		if (abDist <= cdDist) {
			double p1 = (acDist + adDist + cdDist) / 2;
			double p2 = (bcDist + bdDist + cdDist) / 2;
			double s1 = Math.sqrt(p1 * (p1 - acDist) * (p1 - adDist) * (p1 - cdDist));
			double s2 = Math.sqrt(p2 * (p2 - bcDist) * (p2 - bdDist) * (p2 - cdDist));
			double l1 = (2 * s1) / cdDist;
			double l2 = (2 * s2) / cdDist;
			if ((l1 + l2) == 0) {
				return 0;
			} else {
				double pDis = (l1 * l1 + l2 * l2) / (l1 + l2);
				return 1 / (1 + Math.exp(-pDis + 5));
			}
		} else {
			double p1 = (acDist + bcDist + abDist) / 2;
			double p2 = (adDist + bdDist + abDist) / 2;
			double s1 = Math.sqrt(p1 * (p1 - acDist) * (p1 - bcDist) * (p1 - abDist));
			double s2 = Math.sqrt(p2 * (p2 - adDist) * (p2 - bdDist) * (p2 - abDist));
			double l1 = (2 * s1) / abDist;
			double l2 = (2 * s2) / abDist;
			if ((l1 + l2) == 0) {
				return 0;
			} else {
				double pDis = (l1 * l1 + l2 * l2) / (l1 + l2);
				return 1 / (1 + Math.exp(-pDis + 5));
			}
		}
	}

	/**
	 * calculate horizontal distance between segments
	 * 
	 * @return
	 */

	public double horizonDist() {
		Point a = this.segment1.sp;
		Point b = this.segment1.ep;
		Point c = this.segment2.sp;
		Point d = this.segment2.ep;
		double abDist = this.segment1.distKmeter();
		double cdDist = this.segment2.distKmeter();
		if (abDist <= cdDist) {
			double caX = a.getLng() - c.getLng(), caY = a.getLat() - c.getLat();
			double cdX = d.getLng() - c.getLng(), cdY = d.getLat() - c.getLat();
			double acDist = a.distKmeter(c);
			double h1;
			if (acDist == 0) {
				h1 = 0;
			} else {
				h1 = acDist * 1000 * Math.abs((caX * cdX + caY * cdY) / (acDist * cdDist));
			}
			double dbX = b.getLng() - d.getLng(), dbY = b.getLat() - d.getLat();
			double dcX = c.getLng() - d.getLng(), dcY = c.getLat() - d.getLat();
			double bdDist = b.distKmeter(d);
			double h2;
			if (bdDist == 0) {
				h2 = 0;
			} else {
				h2 = bdDist * 1000 * Math.abs((dbX * dcX + dbY * dcY) / (bdDist * cdDist));
			}

			double hDis = 1 / (1 + Math.exp(-1.0/2* Math.max(h1, h2) + 5));
			return hDis;
		} else {
			double acX = c.getLng() - a.getLng(), acY = c.getLat() - a.getLat();
			double abX = b.getLng() - a.getLng(), abY = b.getLat() - a.getLat();
			double acDist = a.distKmeter(c);
			double h1;
			if (acDist == 0) {
				h1 = 0;
			} else {
				h1 = acDist * 1000 * Math.abs((acX * abX + acY * abY) / (acDist * abDist));
			}
			double bdX = d.getLng() - b.getLng(), bdY = d.getLat() - b.getLat();
			double baX = b.getLng() - a.getLng(), baY = b.getLat() - a.getLat();
			double bdDist = b.distKmeter(d);
			double h2;
			if (bdDist == 0) {
				h2 = 0;
			} else {
				h2 = bdDist * 1000 * Math.abs((bdX * baX + bdY * baY) / (bdDist * abDist));
			}
			double hDis = 1 / (1 + Math.exp(-1.0/2* Math.max(h1, h2) + 5));
			return hDis;
		}
	}

	/**
	 * measure the similarity of velocity
	 * 
	 * @return
	 */
	public double velocity() {
		double v1 = this.segment1.velocity();
		double v2 = this.segment2.velocity();
		return Math.abs(v1 - v2) / Math.max(v1, v2);
	}

	/**
	 * measure the similarity of direction
	 */
	public double dirDist() {
		Point a = this.segment1.sp;
		Point b = this.segment1.ep;
		Point c = this.segment2.sp;
		Point d = this.segment2.ep;
		double abX = b.getLng() - a.getLng(), abY = b.getLat() - a.getLat();
		double cdX = d.getLng() - c.getLng(), cdY = d.getLat() - c.getLat();
		double dirDist = (abX * cdX + abY * cdY) / (this.segment1.distKmeter() * this.segment2.distKmeter());
		return dirDist <= 0 ? 1 : 1 - dirDist;
	}

	/**
	 * measure the approximation of time interval
	 * 
	 * @return
	 */
	public double durDist() {
		double period1 = this.segment1.duration();
		double period2 = this.segment2.duration();
		return Math.abs(period1 - period2) / Math.max(period1, period2);
	}

	/**
	 * measure the approximation of time period
	 * 
	 * @return
	 */
	public double timeDist() {
		double hour1 = this.interval(this.segment1.sp.getTime(), this.segment2.sp.getTime());
		double hour2 = this.interval(this.segment1.ep.getTime(), this.segment2.ep.getTime());
		double dt = hour1 + hour2;
		return 1 / (1 + Math.exp(-10 * dt + 5));
	}

	/**
	 * calculate the time interval
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	private double interval(String t1, String t2) {
		Date date1 = new Date(0);
		Date date2 = new Date(0);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = "2018-12-21 " + t1;
		String str2 = "2018-12-21 " + t2;
		double interval = 0;
		try {
			date1 = df.parse(str1);
			date2 = df.parse(str2);
			double start = date1.getTime();
			double end = date2.getTime();
			interval = start - end;
		} catch (ParseException e) {
			System.err.println("unparseable using" + df);
		}
		return Math.abs((interval / 1000) / 3600);
	}

	/**
	 * the total distance between segments
	 * 
	 * @return
	 */
	public double totalDist() {
		double totalDistance = 1.0 / 6 * this.perpendDist() + 1.0 / 6 * this.horizonDist() + 1.0 / 6 * this.velocity()
				+ 1.0 / 6 * this.dirDist() + 1.0 / 6 * this.durDist() + 1.0 / 6 * this.timeDist();
		return totalDistance;
	}

	/**
	 * test the distance between segments
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Point pa = new Point(4.25, 2.53, "12:00:15");
		Point pb = new Point(5.65, 10.24, "12:30:45");
		Segment segment1 = new Segment(0, pa, pb);

		Point pc = new Point(4.26, 2.535, "11:50:25");
		Point pd = new Point(5.64, 10.25, "12:45:10");
		Segment segment2 = new Segment(1, pc, pd);

		SegDistance sd = new SegDistance(segment1, segment2);
		System.out.println(sd.totalDist());
	}
}
