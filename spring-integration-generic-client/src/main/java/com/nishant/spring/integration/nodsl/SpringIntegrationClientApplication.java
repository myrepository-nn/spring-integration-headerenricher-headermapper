package com.nishant.spring.integration.nodsl;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.StaticHeaderValueMessageProcessor;

import com.fasterxml.jackson.core.JsonProcessingException;
@SpringBootApplication
@IntegrationComponentScan
public class SpringIntegrationClientApplication {

	public static void main(String[] args) throws JsonProcessingException {
		ConfigurableApplicationContext context=SpringApplication.run(SpringIntegrationClientApplication.class, args);
		Gateway gate=context.getBean(Gateway.class);
		Scanner scanner=new Scanner(System.in);
		while(scanner.hasNext()) {
			String line=scanner.nextLine();
			gate.exchange(line);
		}
		scanner.close();

	}

	@MessagingGateway(defaultRequestChannel="enrichHeadersChannel")
	public interface Gateway{
		public void exchange(String out);
	}

	@Bean
	public DefaultHttpHeaderMapper defaultHttpHeaderMapper() {
		DefaultHttpHeaderMapper dhm= new DefaultHttpHeaderMapper();
		String[] str1=new String[2];
		str1[0]="HTTP_REQUEST_HEADERS";
		str1[1]="key1";
		dhm.setOutboundHeaderNames(str1);
		dhm.setUserDefinedHeaderPrefix("");
		return dhm;
	}

	@Bean
	@Transformer(inputChannel = "enrichHeadersChannel", outputChannel = "httpOut.input")
	public HeaderEnricher enrichHeaders() {
		Map<String, HeaderValueMessageProcessor<?>> headersToAdd = new HashMap<>();
		headersToAdd.put("key1",new StaticHeaderValueMessageProcessor<String>("forsavingchannel"));
		headersToAdd.put("Content-type",new StaticHeaderValueMessageProcessor<String>("application/json"));
		HeaderEnricher enricher = new HeaderEnricher(headersToAdd);
		return enricher;
	}

	@Bean
	public IntegrationFlow httpOut() {
		return f -> f.handle(Http.outboundChannelAdapter("http://localhost:8082/receivedMsg").headerMapper(defaultHttpHeaderMapper()));
	}
}
