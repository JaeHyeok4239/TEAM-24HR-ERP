package com.hr24.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
public class FirewallConfig {
	
	// 쿠키에 한글 등 non-ASCII 문자가 포함될 경우 StrictHttpFirewall이 요청을 차단하므로 허용 처리
    @Bean
    public StrictHttpFirewall httpFirewall() {

        StrictHttpFirewall firewall =
                new StrictHttpFirewall();

        firewall.setAllowedHeaderValues(
                value -> true
        );

        return firewall;
    }
}