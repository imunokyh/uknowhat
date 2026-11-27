package kr.or.uknowhat.api.ubusiness.common;

import java.security.SecureRandom;
import java.util.Random;

public class RandomNumberGenerator {
	// 호출마다 현재 시각으로 시드를 주면 같은 ms 안의 재시도에서 동일한 번호가 반복 생성되므로 공유 인스턴스를 사용한다.
	private static final Random rand = new SecureRandom();
	
	public String generateNumber(int length, boolean duplicate) {
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
