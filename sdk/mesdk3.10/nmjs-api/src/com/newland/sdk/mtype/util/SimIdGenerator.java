package com.newland.sdk.mtype.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 *<p> System inner serial number generator </p>
 *<p> A method that gets no repeated serial number with 1000 in the system is as follows</p>
 *<pre>
 *	Create...
 *	Object object = new Object();
 *	SimpleSerialIdGenerator idgenerator = new SimpleSerialIdGenerator(1000);
 *	Thread 1...
 *		Long s1 = idgenerator.getId(object);
 *	Thread 2...
 *		Long s2 = idgenerator.getId(object);
 *</pre>
 *It can be expected that it will be sure to occur with increments <tt>1000</tt>,<tt>s1 != s2</tt>
 *
 *
 *
 */
public final class SimIdGenerator{

	private long maxValue;
	
	private final Map <Object,AtomicLong> valueMap = new HashMap<Object, AtomicLong>();
	
	public SimIdGenerator(long maxValue){
		this.maxValue = maxValue;
	}

	public long getMaxValue() {
		return this.maxValue;
	}

	
	private Long getSerialId(Object seed,Integer plus) {
		AtomicLong value = null;
		synchronized (valueMap) {
			value = valueMap.get(seed);
			if(value == null){
				value = new AtomicLong(1);
				valueMap.put(seed, value);
			}
		}
		synchronized (value) {
			long l = value.getAndAdd(plus);
			if(value.get() > maxValue){
				value.set(1);
			}
			return l;
		}
	}


	public void clear(Object seed) {
		synchronized (valueMap) {
			AtomicLong value = valueMap.get(seed);
			if(value == null||value.get() == 0){
				return;
			}else{
				valueMap.put(seed, new AtomicLong(0));
			}
		}
	}
	
	public Long getId(Object seed,Integer plus){
		return getSerialId(seed, plus);
	}

	public Long getId(Object seed) {
		return getSerialId(seed,1);
	}

	public void clearAll() {
		synchronized (valueMap) {
			valueMap.clear();
		}
	}
}
