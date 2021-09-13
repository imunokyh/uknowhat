package kr.or.uknowhat.api.ubusiness.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


// ref: https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=javaking75&logNo=220121303842
public class HTimer extends TimerTask {
	private boolean state = true;
    private long howSeconds = 0;// 몇 milliseconds동안 실행될 것인가?
    private int count = 0;
    private int from = 0;// 현재부터 몇 milliseconds후 시작할 것인가?
    private int interval = 0;// 몇 milliseconds간격으로 반복할 것인가?
	private Timer timer;

	public HTimer() {
		init();
	}

	public void init() {
		timer = new Timer();
	}
	
	@Override
	public void run() {
		if (!state) {
			commit();
		} else {
			if (howSeconds / interval <= count) {
				commit();
				end();
			} else {
				commit();
			}
		}
		count++; // 1000초 동안 100초 간격이면 10번 반복
	}

	public void start(long howSeconds, int from, int interval) {
		System.out.println("Program Start......");
		this.interval = interval;
		this.from = from;
		this.howSeconds = howSeconds;
		state = true;
		timer.schedule(this, from, interval);// 스케줄
	}

    public void start(int interval) {// 오버로딩
        start(0, 0, interval);// 0초동안 0초후 각격으로->1번
        state = false;
    }
	
	public void start() {// 오버로딩
		start(0, 0, 1000);
		state = false;
	}

	public void end() {
		this.cancel();
		System.out.println("Program End......");
	}

	public synchronized void commit() {
		System.out.println((count) + " " + new Date());
	}
}
