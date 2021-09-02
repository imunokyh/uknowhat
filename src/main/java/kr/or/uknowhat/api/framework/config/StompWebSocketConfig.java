package kr.or.uknowhat.api.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import kr.or.uknowhat.api.ubusiness.common.HttpHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//registry.addEndpoint("/").setAllowedOriginPatterns("*").addInterceptors(new HttpHandshakeInterceptor());
		registry.addEndpoint("/stomp").setAllowedOriginPatterns("*").addInterceptors(new HttpHandshakeInterceptor()).withSockJS();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/publish");
		registry.enableSimpleBroker("/subscribe");
	}
	
	@Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		//registration.setMessageSizeLimit(160 * 64 * 1024);    // Max incoming message size, default : 64 * 1024
		//registration.setSendTimeLimit(20 * 10000);            // default : 10 * 10000
		//registration.setSendBufferSizeLimit(10 * 512 * 1024); // Max outgoing buffer size, default : 512 * 1024
	}
}
