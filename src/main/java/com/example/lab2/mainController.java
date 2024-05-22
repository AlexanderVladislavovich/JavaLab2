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
    TextField showLeaderBoard;
    @FXML
    Text winnerText;
    @FXML
    TextField enterUsernameTextField;

//    public void mouseEvent(MouseEvent evn) {
//        //Circle circ = new Circle(evn.getX(), evn.getY(), 10, Color.RED);
//        //viewGame.getChildren().add(circ);
//        //client.updateModel(new Point((int)evn.getX(), (int)evn.getY()));
//        client.TEST_addPoint(new Point((int)evn.getX(), (int)evn.getY()));
//        //viewGameModel();
//    }

    @FXML
    public void mouseEvent(MouseEvent evn) {
        System.out.println("-----add bullet");
        boolean flag = client.addBullet(new Point((int)evn.getX(),(int)evn.getY()));
        if (flag) client.numOfShots++;
        System.out.println("------add bullet");
    }

    //@FXML
    public void Pause(){
//        Platform.runLater(()->{
//        System.out.println("-----------delete");
//        ObservableList<Node> list = viewGame.getChildren();
//        list.clear();
//        for (Node n:list) {
//            //System.out.print(n);
//            viewGame.getChildren().remove(n);
//        }
        //viewGame.getChildren().removeAll();
        //viewGame.getChildren().add(new Circle(56, 89, 40, Color.BLACK));
        //});

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
        System.out.println("main leaderboard");
        client.leaderBoardRequest();
//        //while (client.leaderboard == null ) {}
//        List<leaderBoard> l = client.getLeaderboard();
//        System.out.println("____________");
//        l.get(0).print();
//        showLeaderBoard = new TextField();
//        String text = "";
//        for (leaderBoard i:l) {
//            text += i.print2() + "\n";
//        }
//        showLeaderBoard.setText(text);
//        showLeaderBoard.setLayoutX(200);
//        showLeaderBoard.setLayoutY(200);

    }
    public void initialize() {
        continueButton.setDisable(true);
        client.addObserver(this);
        enterUsernameTextField.setVisible(false);
    }
    //@FXML
    public void TEST_viewGameModel() {
        System.out.println("viewgamemodel");
        Platform.runLater(()->{
            //viewGame.getChildren().removeAll();
            ObservableList<Node> list = viewGame.getChildren();
            list.clear();
//            for (Node n:list) {
//                viewGame.getChildren().remove(n);
//            }
            for (Point p:client.TEST_getData()) {
                Circle c = new Circle(p.x, p.y, 10, Color.RED);
                viewGame.getChildren().add(c);
            }
            Circle cbig = new Circle(client.getBigTarget().x, client.getBigTarget().y, 20, Color.BLUE);
            viewGame.getChildren().add(cbig);
            Circle csmall = new Circle(client.getSmallTarget().x, client.getSmallTarget().y, 10, Color.BLUE);
            viewGame.getChildren().add(csmall);
            System.out.println("main controller view model()");
            for (Point p :client.getBullets()) {
                if (p != null) {
                    Circle c = new Circle(p.x, p.y, 2, Color.GREEN);
                    viewGame.getChildren().add(c);
                    System.out.println("----bullets :" + p.PointToString());
                }
            }
            numOfShotsLabel.setText("Num of shots: " + client.numOfShots);
            scoreLabel.setText("score: " + client.score);
            //viewGame.getChildren().removeAll();
        });
    }
    public void viewWinner() {
        System.out.println("---view winner");
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
//    public void viewGameModel() {
//        System.out.println("viewgamemodel");
//        viewGame.getChildren().removeAll();
//        Circle cbig = new Circle(client.getBigTarget().x, client.getBigTarget().y, 20, Color.RED);
//        viewGame.getChildren().add(cbig);
//        Circle csmall = new Circle(client.getSmallTarget().x, client.getSmallTarget().y, 10, Color.RED);
//        viewGame.getChildren().add(csmall);
//    }
    @Override
    public void event1() {
        System.out.println("event1");
        TEST_viewGameModel();
    }

    @Override
    public void event2() {
        System.out.println("event2");
        viewWinner();
    }

    @FXML
    public void connect() {
        client.connect();
        connectButton.setDisable(true);
        enterUsernameTextField.setVisible(true);
    }
    public void sendMsg() {
        //delete_();
        //client.sendMsg();
    }
}
