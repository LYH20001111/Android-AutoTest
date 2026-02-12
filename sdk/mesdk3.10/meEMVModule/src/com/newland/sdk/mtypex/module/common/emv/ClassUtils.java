package com.newland.sdk.mtypex.module.common.emv;

import java.util.HashSet;
import java.util.Set;

public class ClassUtils {
	
	private static final Set<Class<?>> wrappers = new HashSet<Class<?>>();
	
	static{
		wrappers.add(Boolean.class);
		wrappers.add(Character.class);
		wrappers.add(Byte.class);
		wrappers.add(Short.class);
		wrappers.add(Integer.class);
		wrappers.add(Long.class);
		wrappers.add(Float.class);
		wrappers.add(Double.class);
		wrappers.add(Void.class);
	}
	private static void assignNull(Object tgt){
		if(tgt == null)
			throw new IllegalArgumentException("input should not be null!");
		
		return;
	}
	
	public static boolean isByteArrays(Class<?> tgtClz){
		assignNull(tgtClz);
		
		return tgtClz.getName().equals("[B") || tgtClz.getName().equals("[Ljava.lang.Byte;");
	}
	public static boolean isIntegerArrays(Class<?> tgtClz){
		assignNull(tgtClz);
		
		return tgtClz.getName().equals("[I") || tgtClz.getName().equals("[Ljava.lang.Integer;");
	}
	
	public static boolean isArray(Object tgt){
		assignNull(tgt);
		
		return tgt.getClass().getName().charAt(0) == '[';
	}
	
	public static boolean isWrapperTypesOrPrimitive(Object tgt){
        return wrappers.contains(tgt.getClass());
    }
	

}
