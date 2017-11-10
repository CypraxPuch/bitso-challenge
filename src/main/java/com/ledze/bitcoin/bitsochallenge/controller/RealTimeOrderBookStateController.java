package com.ledze.bitcoin.bitsochallenge.controller;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class RealTimeOrderBookStateController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeOrderBookStateController.class);

    @FXML
    private TextField bestBidsAsks;

    @FXML
    private Label messageLabel;

    @FXML
    private TextArea consoleLogscreen;

    private Service<String> backgroundThread;

    @FXML
    private void showBestBidsAndAsks() {
        LOGGER.info("action showBestBidsAndAsks");
        messageLabel.setText("");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
