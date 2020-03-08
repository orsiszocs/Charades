package gui;
import java.util.Timer;
import java.util.TimerTask;

import controller.Controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

/**
 * It handles the interactions with the user.
 * First the user pushes the "Start new game" button. The user writes the number of teams and clicks "Send".
 * For each team, the team name is written in the text field intended for it, and the team members are written
 * line by line to the text area intended for team members. "Send" must be clicked after entering data on each team.
 * After all the teams are added, the information about the next round is displayed on the right side. The player
 * chooses a difficulty level and clicks "Start round". By holding the "Show word" button pressed, the player can
 * see the word that should be guessed by their team. By clicking the "Start countdown" button, a timer starts
 * counting down from 100 seconds. If the word is guessed, the "Guessed" button should be pressed, otherwise the
 * "Not guessed" button. The score on the scoreboard will be modified accordingly. "Change previous answer" should
 * be clicked when someone accidentally clicks on "Guessed" instead of "Not guessed" or vice versa. If that is not
 * the case, a new round can be started.
 */
public class GUI {
    Controller controller;
    HBox mainBox;
    Button startGameButton;
    Button showWordButton;
    HBox showWordBox;
    Button startCountdownButton;
    Button goodGuessButton;
    Button badGuessButton;
    Button changeGuessButton;
    Button startRoundButton;
    Label actionLabel;
    Label teamLabel;
    Label playerLabel;
    VBox roundBox;
    Label wordLabel;
    ToggleGroup difficultyGroup;
    Label countdownLabel;
    HBox countdownBox;
    HBox guessBox;
    Timer timer=null;
    HBox difficultyBox;
    TextField numberTeamsTextField;
    HBox numberTeamsBox;
    Button doneButton;
    TextField teamNameTextField;
    HBox teamNameBox;
    TextArea teamMembersTextArea;
    HBox teamMembersBox;
    VBox teamAdderBox;
    VBox gamePlayerBox;
    Label inputDataLabel;
    TextArea scoreTextArea;
    VBox scoreBoardBox;

    /**
     * Creates the layout of the graphical user interface.
     * @param controller handles the actions that are not directly related to the user interface
     * @param mainBox will contain all the GUI elements
     */
    public GUI(Controller controller, HBox mainBox) {
        this.controller=controller;
        this.mainBox=mainBox;
        fillTeamAdder();
        fillGamePlayer();
        fillScoreBoard();
        gamePlayerBox.setVisible(false);
        numberTeamsBox.setVisible(false);
        doneButton.setVisible(false);
        inputDataLabel.setVisible(false);
        teamNameBox.setVisible(false);
        teamMembersBox.setVisible(false);
        roundBox.setVisible(false);
        mainBox.getChildren().add(teamAdderBox);
        mainBox.getChildren().add(gamePlayerBox);
        mainBox.getChildren().add(scoreBoardBox);
    }

    /**
     * Fills the rightmost side of the window. Adds the text area containing the scoreboard and the labels containing the team, member and action of the new round.
     */
    private void fillScoreBoard() {
        scoreBoardBox=new VBox(10);
        //roundBox
        teamLabel=new Label("Team: ");
        playerLabel=new Label("Player: ");
        actionLabel=new Label("Action: ");
        roundBox=new VBox(teamLabel,playerLabel,actionLabel);
        scoreBoardBox.getChildren().add(roundBox);
        roundBox.setSpacing(2);
        //scoreTextArea
        scoreTextArea=new TextArea("");
        scoreTextArea.setEditable(false);
        scoreTextArea.setWrapText(true);
        scoreTextArea.setPrefColumnCount(10);
        scoreTextArea.setPrefRowCount(7);
        scoreBoardBox.getChildren().add(scoreTextArea);
    }

    /**
     * Fills the leftmost side of the window. It adds the "Start new game" and "Send" buttons, number of teams and team name text fields, team members text area.
     */
    private void fillTeamAdder() {
        teamAdderBox=new VBox(10);
        //startGameButton
        HBox startDoneBox=new HBox(10);
        startGameButton=new Button("Start new game");
        EventHandler<ActionEvent> startGameEvent = e -> {
            gamePlayerBox.setVisible(false);
            controller.startNewGame();
            if(timer!=null) {
                timer.cancel();
            }
            doneButton.setVisible(true);
            numberTeamsBox.setVisible(true);
            inputDataLabel.setVisible(false);
            teamNameBox.setVisible(false);
            teamMembersBox.setVisible(false);
            scoreTextArea.setText("");
            roundBox.setVisible(false);
            teamMembersTextArea.setText("");
            teamNameTextField.setText("");
            numberTeamsTextField.setText("");
        };
        startGameButton.setOnAction(startGameEvent);
        //doneButton
        doneButton=new Button("Send");
        EventHandler<ActionEvent> doneEvent=e-> {
            if(inputDataLabel.isVisible()) { //read team name and members
                try {
                    controller.addTeam(teamNameTextField.getText(),teamMembersTextArea.getText());
                    if(controller.gameCanStart()) { //start game
                        gamePlayerBox.setVisible(true);
                        showWordBox.setVisible(false);
                        countdownBox.setVisible(false);
                        guessBox.setVisible(false);
                        roundBox.setVisible(true);
                        changeGuessButton.setVisible(false);
                        numberTeamsBox.setVisible(false);
                        doneButton.setVisible(false);
                        inputDataLabel.setVisible(false);
                        teamNameBox.setVisible(false);
                        teamMembersBox.setVisible(false);
                        startRoundButton.setVisible(true);
                        difficultyBox.setVisible(true);
                        teamLabel.setText("Team: "+controller.getNextTeam());
                        playerLabel.setText("Player: "+controller.getNextPlayer());
                        actionLabel.setText("Action: "+controller.getNextAction());
                    }
                    else { //input next team
                        inputDataLabel.setText("Provide information about team number "+controller.getTeamOrder()+".");
                    }
                    scoreTextArea.setText(controller.getScoreBoard());
                } catch (CharadesException ex) {
                    Alert alert=new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("An error has occurred");
                    alert.setHeaderText(ex.toString());
                    alert.showAndWait();
                }
                teamMembersTextArea.setText("");
                teamNameTextField.setText("");
            }
            else { //read number of teams
                String input=numberTeamsTextField.getText();
                try {
                    controller.setNumberTeams(input);
                    numberTeamsBox.setVisible(false);
                    teamNameBox.setVisible(true);
                    teamMembersBox.setVisible(true);
                    inputDataLabel.setVisible(true);
                    inputDataLabel.setText("Provide information about team number 1.");
                } catch (CharadesException ex) {
                    Alert alert=new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("An error has occurred");
                    alert.setHeaderText(ex.toString());
                    alert.showAndWait();
                }
                numberTeamsTextField.setText("");
            }
        };
        doneButton.setOnAction(doneEvent);
        startDoneBox.getChildren().add(startGameButton);
        startDoneBox.getChildren().add(doneButton);
        teamAdderBox.getChildren().add(startDoneBox);
        //numberTeamsBox
        numberTeamsTextField=new TextField();
        Label numberTeamsLabel=new Label("Number of teams: ");
        numberTeamsBox=new HBox(numberTeamsLabel,numberTeamsTextField);
        teamAdderBox.getChildren().add(numberTeamsBox);
        //inputDataLabel
        inputDataLabel=new Label("");
        teamAdderBox.getChildren().add(inputDataLabel);
        //teamNameBox
        teamNameTextField=new TextField();
        Label teamNameLabel=new Label("Team name: ");
        teamNameBox=new HBox(teamNameLabel,teamNameTextField);
        teamAdderBox.getChildren().add(teamNameBox);
        teamNameBox.setSpacing(5);
        //teamMembersBox
        Label teamMembersLabel=new Label("Team members: ");
        teamMembersTextArea=new TextArea();
        teamMembersTextArea.setWrapText(true);
        teamMembersTextArea.setPrefRowCount(4);
        teamMembersTextArea.setPrefColumnCount(9);
        teamMembersBox=new HBox(teamMembersLabel,teamMembersTextArea);
        teamAdderBox.getChildren().add(teamMembersBox);
        teamAdderBox.setSpacing(5);
    }

    /**
     * Fills the central part of the window. Adds the radio buttons corresponding to the difficulty levels,
     * the "Start round", "Show word", "Start countdown", "Guessed","Not guessed", "Change previous answer" buttons.
     */
    private void fillGamePlayer() {
        gamePlayerBox=new VBox(10);
        //difficultyBox
        difficultyGroup=new ToggleGroup();
        RadioButton easyButton = new RadioButton("Easy");
        easyButton.setUserData("easy");
        easyButton.setToggleGroup(difficultyGroup);
        easyButton.setSelected(true);
        RadioButton mediumButton = new RadioButton("Medium");
        mediumButton.setUserData("medium");
        mediumButton.setToggleGroup(difficultyGroup);
        RadioButton hardButton = new RadioButton("Hard");
        hardButton.setUserData("hard");
        hardButton.setToggleGroup(difficultyGroup);
        difficultyBox=new HBox(20,easyButton,mediumButton,hardButton);
        gamePlayerBox.getChildren().add(difficultyBox);
        difficultyBox.setSpacing(5);
        //startRoundButton
        startRoundButton=new Button("Start round");
        gamePlayerBox.getChildren().add(startRoundButton);
        EventHandler<ActionEvent> startRoundEvent=e-> {
            controller.startRound(difficultyGroup.getSelectedToggle().getUserData().toString());
            startRoundButton.setVisible(false);
            difficultyBox.setVisible(false);
            showWordBox.setVisible(true);
            countdownBox.setVisible(true);
            startCountdownButton.setVisible(true);
            changeGuessButton.setVisible(false);
        };
        startRoundButton.setOnAction(startRoundEvent);
        //showWordBox
        showWordButton=new Button("Show word");
        wordLabel=new Label("");
        showWordBox=new HBox(showWordButton,wordLabel);
        gamePlayerBox.getChildren().add(showWordBox);
        startRoundButton.setOnAction(startRoundEvent);
        EventHandler<MouseEvent> showWordPressedEvent= e-> {
            wordLabel.setText(controller.currentWord);
        };
        showWordButton.setOnMousePressed(showWordPressedEvent);
        EventHandler<MouseEvent> showWordReleasedEvent= e-> {
            wordLabel.setText("");
        };
        showWordButton.setOnMouseReleased(showWordReleasedEvent);
        showWordBox.setSpacing(8);
        //countdownBox
        startCountdownButton=new Button("Start countdown");
        countdownLabel=new Label("");
        countdownBox=new HBox(startCountdownButton,countdownLabel);
        EventHandler<ActionEvent> startCountdownEvent= e-> {
            timer=new Timer();
            countdownLabel.setVisible(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                int counter=100;
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public synchronized void run() {
                            countdownLabel.setText(String.valueOf(counter));
                            counter--;
                            if (counter < 0) {
                                timer.cancel();
                            }
                        }
                    });
                }
            },0,1000);
            guessBox.setVisible(true);
            startCountdownButton.setVisible(false);
        };
        startCountdownButton.setOnAction(startCountdownEvent);
        gamePlayerBox.getChildren().add(countdownBox);
        countdownBox.setSpacing(8);
        //guessBox
        goodGuessButton=new Button("Guessed");
        badGuessButton=new Button("Not guessed");
        guessBox=new HBox(goodGuessButton,badGuessButton);
        gamePlayerBox.getChildren().add(guessBox);
        EventHandler<ActionEvent> goodGuessEvent= e -> {
            timer.cancel();
            guessBox.setVisible(false);
            changeGuessButton.setVisible(true);
            startRoundButton.setVisible(true);
            difficultyBox.setVisible(true);
            countdownLabel.setVisible(false);
            controller.goodGuess();
            scoreTextArea.setText(controller.getScoreBoard());
            teamLabel.setText("Team: "+controller.getNextTeam());
            playerLabel.setText("Player: "+controller.getNextPlayer());
            actionLabel.setText("Action: "+controller.getNextAction());
        };
        goodGuessButton.setOnAction(goodGuessEvent);
        EventHandler<ActionEvent> badGuessEvent= e -> {
            timer.cancel();
            guessBox.setVisible(false);
            changeGuessButton.setVisible(true);
            startRoundButton.setVisible(true);
            difficultyBox.setVisible(true);
            countdownLabel.setVisible(false);
            controller.badGuess();
            scoreTextArea.setText(controller.getScoreBoard());
            teamLabel.setText("Team: "+controller.getNextTeam());
            playerLabel.setText("Player: "+controller.getNextPlayer());
            actionLabel.setText("Action: "+controller.getNextAction());
        };
        badGuessButton.setOnAction(badGuessEvent);
        guessBox.setSpacing(5);
        //changeGuessButton
        changeGuessButton=new Button("Change previous answer");
        gamePlayerBox.getChildren().add(changeGuessButton);
        EventHandler<ActionEvent> changeGuessEvent= e -> {
            controller.changeGuess();
            scoreTextArea.setText(controller.getScoreBoard());
        };
        changeGuessButton.setOnAction(changeGuessEvent);
    }
}
