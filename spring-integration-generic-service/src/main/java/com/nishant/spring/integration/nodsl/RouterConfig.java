package com.nishant.spring.integration.nodsl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Configuration
public class RouterConfig {

	@Bean
	public IntegrationFlow received() {
		return IntegrationFlows.from(Http.inboundChannelAdapter("/receivedMsg")
				.requestMapping(arg0 -> arg0.methods(HttpMethod.POST))
				.requestPayloadType(String.class)
				.headerMapper(defaultHttpHeaderMapper())
				)
				.channel(receivedChannel())
				.get();
	}

	@Bean
	public DefaultHttpHeaderMapper defaultHttpHeaderMapper() {
		DefaultHttpHeaderMapper dhm= new DefaultHttpHeaderMapper();
		String[] str1=new String[1];
		str1[0]="*";
		dhm.setInboundHeaderNames(str1);
		return dhm;
	}

	@Bean
	public MessageChannel receivedChannel() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel="receivedChannel")
	@Bean
	public MessageHandler serviceSavingAccount() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println("savingAccountChannel msg.,...."+message.getPayload()+">>>>>>>>"+message);				
			}
		};
	}
}
