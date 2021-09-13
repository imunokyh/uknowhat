package kr.or.uknowhat.api.ubusiness.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public  class CommandTimer extends HTimer {
    public synchronized void commit(){
            printLog();
    }
    
    public void printLog() {
    	//오늘 날짜로 초기화
		Calendar nowTime = Calendar.getInstance();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
		String strNowTime = sd.format(nowTime.getTime());
		
		//String strNowTime = String.format("%1$tF %1$tT",nowTime); //2014-09-14 03:16:22
		//String strNowTime = String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS",nowTime); //2014-09-14 03:16:22
		//System.out.println(strNowTime);

		System.out.println("[ " + strNowTime + " ] : 로그 처리");
	}
}

