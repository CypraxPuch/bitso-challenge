package com.ledze.bitcoin.bitsochallenge;

import com.ledze.bitcoin.bitsochallenge.operation.OrderBookOperation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;

@SpringBootApplication
@EnableJms
public class BitsoChallengeApplication  extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(BitsoChallengeApplication.class);
	private ConfigurableApplicationContext applicationContext;

	private static final String MAIN_FXML_FILE = "/fxml/real-time-order-book-state.fxml";
	private static final String APPLICATION_STYLE_SHEET = "/styles/styles.css";

	@Autowired private OrderBookOperation op;

	public static void main(String[] args) {
		//SpringApplication.run(BitsoChallengeApplication.class, args);
		launch(args);
	}

	@Bean
	public Queue queue(){
		return new ActiveMQQueue("difforders.queue");
	}

	@Bean
	public Queue queueBestOps(){
		return new ActiveMQQueue("bestops.queue");
	}

	@Override
	public void init() throws Exception {
		SpringApplication app = new SpringApplication(BitsoChallengeApplication.class);
		app.setWebEnvironment(false);
		applicationContext = app.run();
		applicationContext.getAutowireCapableBeanFactory().autowireBean(this);

		//starts the subscription to diffOrders channel and get the orderBook info.
		//op.init();
	}

	@Override
	public void start(Stage stage) throws Exception {

		LOGGER.info("Loading FXML for main view from: {}", MAIN_FXML_FILE);
		FXMLLoader loader = new FXMLLoader();
		Parent rootNode = loader.load(
				getClass().getResourceAsStream(
						MAIN_FXML_FILE
				)
		);

		LOGGER.info("Showing JavaFX scene");
		Scene scene = new Scene(rootNode, 400, 200);
		scene.getStylesheets().add(APPLICATION_STYLE_SHEET);

		stage.setTitle("Hello JavaFX WebSockets");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		applicationContext.close();
	}
}
