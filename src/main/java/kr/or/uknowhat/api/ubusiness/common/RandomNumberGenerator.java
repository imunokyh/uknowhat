package kr.or.uknowhat.api.ubusiness.common;

import java.util.Random;

public class RandomNumberGenerator {
	public String generateNumber(int length, boolean duplicate) {
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		String numStr = "";
		
		for (int i = 0; i < length; i++) {
			String ran = Integer.toString(rand.nextInt(10));
			
			if (duplicate) {
				numStr += ran;
			} else {
				if (!numStr.contains(ran)) {
					numStr += ran;
				} else {
					i -= 1;
				}
			}
		}
		
		return numStr;
	}
}
