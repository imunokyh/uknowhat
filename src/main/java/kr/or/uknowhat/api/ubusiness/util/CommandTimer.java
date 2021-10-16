package kr.or.uknowhat.api.ubusiness.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import kr.or.uknowhat.api.ubusiness.common.MessageType;
import kr.or.uknowhat.api.ubusiness.question.vo.MessageVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public  class CommandTimer extends HTimer {
	
	private SimpMessagingTemplate simpMessageTemplate;
	private String roomNumber;
	private Long count;
	
    public synchronized void commit(){
        //printLog();
    	MessageVo vo = new MessageVo();
		vo.setContent(String.valueOf(count));
		vo.setRoomNumber(roomNumber);
		
		vo.setType(MessageType.TIMECNT);
		simpMessageTemplate.convertAndSend("/subscribe/play/room/" + roomNumber, vo);
		
		if (count == 0) {
			log.info((MessageType.TIMEOUT).toString());
			vo.setType(MessageType.TIMEOUT);
		}
		
    	simpMessageTemplate.convertAndSend("/subscribe/play/room/" + roomNumber, vo);
    	count--;
    }
    
    public CommandTimer(){
    	
    }
    
    public CommandTimer(SimpMessagingTemplate simpMessagingTemplate, String roomNumber, Long count) {
    	this.simpMessageTemplate = simpMessagingTemplate;
    	this.roomNumber = roomNumber;
    	this.count = count;
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

