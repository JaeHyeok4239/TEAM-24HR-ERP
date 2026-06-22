package com.hr24.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.hr24.auth.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker // STOMP 기반 WebSocket 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 프론트에서 접속할 엔드포인트
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS(); // SockJS 폴백 (브라우저 호환성)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // 서버→클라이언트 prefix
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트→서버 prefix
        registry.setUserDestinationPrefix("/user"); // 개인 알림용 (/user/queue/notifications)
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // STOMP CONNECT 시 JWT 토큰으로 사용자 인증 (개인 알림 라우팅에 필요)
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String bearer = accessor.getFirstNativeHeader("Authorization");
                    if (bearer != null && bearer.startsWith("Bearer ")) {
                        String token = bearer.substring(7);
                        if (jwtProvider.validateToken(token)) {
                            String loginId = jwtProvider.getLoginId(token);
                            // SecurityContext Principal 설정 → /user/{loginId}/queue/... 라우팅 동작
                            accessor.setUser(new UsernamePasswordAuthenticationToken(loginId, null, List.of()));
                        }
                    }
                }
                return message;
            }
        });
    }
}
