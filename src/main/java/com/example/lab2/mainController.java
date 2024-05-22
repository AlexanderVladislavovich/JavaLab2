package com.example.lab2;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;

public class mainController implements Observer{



    ClientClass client = new ClientClass();

    @FXML
    Pane viewGame;

    @FXML
    Label userNameLabel;
    @FXML
    Label scoreLabel;
    @FXML
    Label numOfShotsLabel;
    @FXML
    Label gamesWonLabel;

    @FXML
    Button connectButton;
    @FXML
    Button pauseButton;
    @FXML
    Button continueButton;
    @FXML
    Button readyButton;
    @FXML
    Button leaderBoardButton;

    @FXML
    Text winnerText;
    @FXML
    TextField enterUsernameTextField;

    @FXML
    public void mouseEvent(MouseEvent evn) {
        boolean flag = client.addBullet(new Point((int)evn.getX(),(int)evn.getY()));
        if (flag) client.numOfShots++;
    }

    //@FXML
    public void Pause(){
        client.pauseRequest();
        pauseButton.setDisable(true);
        continueButton.setDisable(false);
    }
    public void Continue() {
        client.continueRequest();
        pauseButton.setDisable(false);
        continueButton.setDisable(true);
    }
    public void Ready() {
        String str = enterUsernameTextField.getText();
        if(!str.equals("")) {
            client.isReady = true;
            client.readyRequest(str);
            readyButton.setDisable(true);
            userNameLabel.setText(str);

        }
    }
    public void leaderBoard() {
        client.leaderBoardRequest();
    }
    public void initialize() {
        continueButton.setDisable(true);
        client.addObserver(this);
        enterUsernameTextField.setVisible(false);
    }
    //@FXML
    public void viewGameModel() {
        Platform.runLater(()->{
            ObservableList<Node> list = viewGame.getChildren();
            list.clear();
            Circle cbig = new Circle(client.getBigTarget().x, client.getBigTarget().y, 20, Color.BLUE);
            viewGame.getChildren().add(cbig);
            Circle csmall = new Circle(client.getSmallTarget().x, client.getSmallTarget().y, 10, Color.BLUE);
            viewGame.getChildren().add(csmall);
            for (Point p :client.getBullets()) {
                if (p != null) {
                    Circle c = new Circle(p.x, p.y, 2, Color.GREEN);
                    viewGame.getChildren().add(c);
                }
            }
            numOfShotsLabel.setText("Num of shots: " + client.numOfShots);
            scoreLabel.setText("score: " + client.score);
        });
    }
    public void viewWinner() {
        Platform.runLater(() -> {
            ObservableList<Node> list = viewGame.getChildren();
            list.clear();
            winnerText = new Text();
            String txt = "";
            for (int i = 0; i < 4; i++) {
                if (client.model.stats.names[i] != null) {
                    txt += "Player: ";
                    txt += client.model.stats.names[i] + "   score: ";
                    txt += client.model.stats.score[i] + "\n";
                }
            }
            winnerText.setText(txt);
            winnerText.setX(150);
            winnerText.setY(200);
            viewGame.getChildren().add(winnerText);
        });
        readyButton.setDisable(false);
    }
    @Override
    public void event1() {
        viewGameModel();
    }

    @Override
    public void event2() {
        viewWinner();
    }

    @FXML
    public void connect() {
        client.connect();
        connectButton.setDisable(true);
        enterUsernameTextField.setVisible(true);
    }
}
