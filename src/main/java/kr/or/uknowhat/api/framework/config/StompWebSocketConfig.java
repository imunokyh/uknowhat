package kr.or.uknowhat.api.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import kr.or.uknowhat.api.ubusiness.common.HttpHandshakeInterceptor;
import kr.or.uknowhat.api.ubusiness.common.StompAuthChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/** STOMP heart-beat 주기 (ms). 응답 없는 클라이언트를 빠르게 끊어 오프라인 처리하기 위함 */
	private static final long HEARTBEAT_MS = 10000;

	@Autowired
	private StompAuthChannelInterceptor stompAuthChannelInterceptor;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//registry.addEndpoint("/").setAllowedOriginPatterns("*").addInterceptors(new HttpHandshakeInterceptor());
		registry.addEndpoint("/stomp").setAllowedOriginPatterns("*").addInterceptors(new HttpHandshakeInterceptor()).withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		ThreadPoolTaskScheduler heartbeatScheduler = new ThreadPoolTaskScheduler();
		heartbeatScheduler.setPoolSize(1);
		heartbeatScheduler.setThreadNamePrefix("stomp-heartbeat-");
		heartbeatScheduler.initialize();

		registry.setApplicationDestinationPrefixes("/publish");
		// /queue : 개인(세션/사용자) 전용 메시지 (오류 알림, 출제자 전용 데이터)
		registry.enableSimpleBroker("/subscribe", "/queue")
				.setHeartbeatValue(new long[] { HEARTBEAT_MS, HEARTBEAT_MS })
				.setTaskScheduler(heartbeatScheduler);
		registry.setUserDestinationPrefix("/user");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompAuthChannelInterceptor);
	}

	@Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		//registration.setMessageSizeLimit(160 * 64 * 1024);    // Max incoming message size, default : 64 * 1024
		//registration.setSendTimeLimit(20 * 10000);            // default : 10 * 10000
		//registration.setSendBufferSizeLimit(10 * 512 * 1024); // Max outgoing buffer size, default : 512 * 1024
	}
}
