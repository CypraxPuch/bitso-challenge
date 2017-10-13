package com.ledze.bitcoin.bitsochallenge;

import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BitsoChallengeApplication implements CommandLineRunner{

	@Autowired private OrderBookClient client;

	private static final Logger LOGGER = LoggerFactory.getLogger(BitsoChallengeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BitsoChallengeApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		LOGGER.info("calling order book rest service");
		LOGGER.info("\nAvailableBook::btc_mxn:\n"+client.getAvailableBook("btc_mxn").toString());
		LOGGER.info("\nOrderBook:\n"+client.getOrderBookList("btc_mxn", "true"));
		//LOGGER.info("\nAvailableBooks:\n"+client.getAvailableBooks());
	}
}
