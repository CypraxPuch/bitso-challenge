package com.ledze.bitcoin.bitsochallenge;

import com.ledze.bitcoin.bitsochallenge.client.OrderBookClient;
import com.ledze.bitcoin.bitsochallenge.operation.OrderBookOp;
import com.ledze.bitcoin.bitsochallenge.websocket.DiffOrdersChannel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BitsoChallengeApplication  extends Application {

	private static final Logger log = LoggerFactory.getLogger(BitsoChallengeApplication.class);
	private ConfigurableApplicationContext applicationContext;

	private static final String MAIN_FXML_FILE = "/fxml/hello.fxml";
	private static final String APPLICATION_STYLE_SHEET = "/styles/styles.css";

	@Autowired private OrderBookOp op;

	public static void main(String[] args) {
		//SpringApplication.run(BitsoChallengeApplication.class, args);
		launch(args);
	}

	@Override
	public void init() throws Exception {
		SpringApplication app = new SpringApplication(BitsoChallengeApplication.class);
		app.setWebEnvironment(false);
		applicationContext = app.run();
		applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
	}

	@Override
	public void start(Stage stage) throws Exception {
		op.init();
		//stage.setScene(new Scene(mainLayout));
		stage.show();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		applicationContext.close();
	}
}
