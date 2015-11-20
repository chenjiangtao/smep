package com.aesirteam.smep.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class FluxMonitor {
	
	protected class FluxItem {
		protected long total = 0;
		protected LinkedBlockingQueue<Float> cycleQueue = new LinkedBlockingQueue<Float>();
	}
	
	protected static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
	protected static Map<String, FluxItem> ds= new HashMap<String, FluxItem>();
	protected String id;
	protected long startTimeMillis = 0, unit, cycle;
	protected boolean print = false;
	
	public FluxMonitor(String id) {
		this(id, 2000L, 3L, true);
	}
	
	public FluxMonitor(String id, long unit, long cycle, boolean print) {
		this.id = id;
		this.unit = unit;
		this.cycle = cycle;
		this.print = print;
		
		if (!ds.containsKey(id)) {
			FluxItem fi = new FluxItem();
			ds.put(id, fi);	
		}
	}
	
	public long incr(int count) {
		long result = 0;
		for(int i = 0; i < count; i++) {
			result = incr();
		}
		return result;
	}
	
	public long incr() {		 
		if (!print) return -1;
		
		if (0 == startTimeMillis)
			startTimeMillis = System.currentTimeMillis();
		
		FluxItem fi = ds.get(id);
		if (++fi.total % unit == 0 ) {
			float elapsedTime  = (System.currentTimeMillis() - startTimeMillis) / 1000;
			try { fi.cycleQueue.put(elapsedTime); } catch (InterruptedException e) {}
			
			startTimeMillis = System.currentTimeMillis();
			
			//Print
			if (fi.cycleQueue.size() >= cycle) {
				float avg = calculateAvg(fi);
				System.out.println(String.format("%s FLUX  %s(BV: %s): TPS: %s", sdf.format(System.currentTimeMillis()), id, unit, avg > 0 ? (int)(unit / avg) : "unlimited"));
			}
		}
		return fi.total;
	}

	private float calculateAvg(FluxItem fi) {
		float elapsedTimeSum = 0;
		Collection<Float> elapsedTimeList = new ArrayList<Float>();
		fi.cycleQueue.drainTo(elapsedTimeList, (int)cycle);
		for (Float elapsedTime : elapsedTimeList) {
			elapsedTimeSum += elapsedTime;
		}
		return elapsedTimeSum / cycle;
	}
}
