package com.ledze.bitcoin.bitsochallenge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealTimeOrderBookStateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeOrderBookStateController.class);

    @FXML
    private TextField bestBidsAsks;

    @FXML
    private Label messageLabel;

    @FXML
    private void showBestBidsAndAsks(){
        LOGGER.info("action showBestBidsAndAsks");
        messageLabel.setText("");

    }

}
