package com.mantis.java8;

import java.util.Arrays;

public class CollectionUtils {
	public static void runThreadUseLambda() {
	    new Thread(() -> {
	        System.out.println("Run!");
	    }).start();
	}
    
	
	
    public static void main(String[] args) {
    	runThreadUseLambda();
    	
    	String[] strings = {"32","24","21","567"};
    	Arrays.sort(strings, (s1 , s2) -> Integer.compare(Integer.parseInt(s1),Integer.parseInt(s2)));
    	
    	for (String str : strings) {
			System.out.println(str);
		}
    	
    }
}
