package org.example.eiscuno.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.machine.ThreadWinGame;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.GameUnoStage;

import java.util.Random;

/**
 * Controller class for the Uno game.
 * @author Juan Camilo Jimenez, hernan Dario Garcia, James sanchez
 * @version 2.0
 */
public class GameUnoController {
    @FXML
    private TextField textAction;

    @FXML
    private GridPane gridPaneCardsMachine;

    @FXML
    private GridPane gridPaneCardsPlayer;

    @FXML
    private ImageView tableImageView;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;
    private boolean playMachine=true;
    private boolean playHuman=true;
    private boolean humanCanSayONEToMachine=true;
    private boolean humanCanSayONE=true;
    private ThreadWinGame threadWinGame;
    private Card cardTable;
    private Card card;
    private boolean machineSayOne=false;
    private boolean takecard=true;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();
        printCardsMachine();



        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer(), this.machinePlayer.getCardsPlayer(), this, this.threadPlayMachine);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this,this.deck, this.threadSingUNOMachine); // Pasar referencia de GameUnoController

        threadWinGame = new ThreadWinGame(this.humanPlayer, this.machinePlayer, this.deck, this);
        Thread w =new Thread(threadWinGame,"treadWinGame");




        t.start();
        threadPlayMachine.start();
        w.start();

        handleCardEffect(cardTable, cardTable, machinePlayer);
    }

    /**
     * Initializes the variables for the game.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.table = new Table();
        this.deck = new Deck(this, table);

        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;

        cardTable= deck.takeCard();
        tableImageView.setImage(cardTable.getImage());

        String cardValue=cardTable.getValue();
        String cardColor=cardTable.getColor();
        System.out.println(cardValue+" "+cardColor);



    }

    /**
     * Prints the human player's cards on the grid pane.
     */
    private void printCardsHumanPlayer() {

        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

        for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
            Card card = currentVisibleCardsHumanPlayer[i];
            ImageView cardImageView = card.getCard();

            cardImageView.setOnMouseClicked((MouseEvent event) -> {
                if((cardTable.getValue()==card.getValue() || cardTable.getColor()==card.getColor() || card.getValue()=="WILD"||card.getValue()=="+4") && playHuman){
                    // Aqui deberian verificar si pueden en la tabla jugar esa carta
                    gameUno.playCard(card);
                    tableImageView.setImage(card.getImage());
                    humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                    printCardsHumanPlayer();

                    cardTable=card;
                    handleCardEffect(card, cardTable, humanPlayer);

                    takecard=true;
                }
            });


            this.gridPaneCardsPlayer.add(cardImageView, i, 0);
        }
    }


    /**
     * Prints the machine player's cards on the grid pane.
     */
    public void printCardsMachine() {
        Platform.runLater(() -> {
            int columns;
            int cols = gridPaneCardsMachine.getColumnConstraints().size();
            this.gridPaneCardsMachine.getChildren().clear();
            Card[] currentVisibleCardsMachinePlayer = this.gameUno.getCurrentVisibleCardsMachinePlayer(0);
            int numcards = currentVisibleCardsMachinePlayer .length;

            if(numcards>cols){
                columns=cols;
            }else{
                columns=numcards;
            }

            for (int i = 0; i < currentVisibleCardsMachinePlayer.length; i++) {
                Card card = currentVisibleCardsMachinePlayer[i];
                System.out.println(card.getValue()+" "+card.getColor()+" "+i+" carta mano maquina");
            }

            for (int i = 0; i < columns; i++) {

                String imageCover = "file:src/main/resources/org/example/eiscuno/cards-uno/card_uno.png";
                Image cardImage = new Image(imageCover);
                ImageView cardImageView = new ImageView(cardImage);
                cardImageView.setY(16);
                cardImageView.setFitHeight(90);
                cardImageView.setFitWidth(70);


                this.gridPaneCardsMachine.add(cardImageView, i, 0);
            }});

    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the position of a specific card in the machine player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    public  Integer findPosCardsMachinePlayer(Card card) {
        for (int i = 0; i < this.machinePlayer.getCardsPlayer().size(); i++) {
            if (this.machinePlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Handles the "Back" button action to show the previous set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the "Next" button action to show the next set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the action of taking a card.
     *
     * @param event the action event
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        System.out.println(takecard);
        if (playHuman && takecard){
            humanPlayer.addCard(deck.takeCard());
            printCardsHumanPlayer();
            humanCanSayONE=true;
            takecard=false;
            if(!canPutCard(cardTable)){
                takecard=true;
                playHuman=false;
                threadPlayMachine.setHasPlayerPlayed(true);
            }
        }else if(machineSayOne){
            humanPlayer.addCard(deck.takeCard());
            printCardsHumanPlayer();
            playHuman=false;
            threadPlayMachine.setHasPlayerPlayed(true);
            takecard=true;
            if(cardTable.getValue()=="SKIP"||cardTable.getValue()=="+2"||cardTable.getValue()=="S+4"||cardTable.getValue()=="WILD"||cardTable.getValue()=="RESERVE"){
                playHuman=true;
                threadPlayMachine.setHasPlayerPlayed(false);
            }
        }

    }

    /**
     * Checks if the human player can put a card on the table.
     *
     * @param tableCard the card on the table
     * @return true if the player can put a card, false otherwise
     */
    private boolean canPutCard(Card tableCard){
        boolean can=false;
        for(int i=0; i<humanPlayer.getCardsPlayer().size();i++){
            Card card = humanPlayer.getCard(i);
            if(tableCard.getValue()==card.getValue() || tableCard.getColor()==card.getColor()|| card.getValue()=="WILD"||card.getValue()=="+4") {
                can = true;
            }
        }
        return can;
    }

    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        int numCardsMachinePlayer = machinePlayer.getCardsPlayer().size();
        int numCardsHumaPlayer = humanPlayer.getCardsPlayer().size();

        if(numCardsMachinePlayer==1 && humanCanSayONEToMachine){
            System.out.println("JUGADOR GRITA UNO A LA MAQUINA!");
            playHuman=false;
            threadPlayMachine.setHasPlayerPlayed(true);
            threadPlayMachine.setMachineCanPlay(false);
            threadSingUNOMachine.setMachineCanSayOne(false);

        }else if(numCardsHumaPlayer==1 && humanCanSayONE){
            System.out.println("JUGADOR GRITA UNO!");
            threadSingUNOMachine.setMachineCanSayOneToPlayer(false);

        }
    }


    /**
     * Handles the exit button action.
     *
     * @param event The action event triggered by the exit button.
     */
    @FXML
    void onHandleButtonExit(ActionEvent event) {
        GameUnoStage.deleteInstance();
    }



    /**
     * Handles the effects of a card when played.
     *
     * @param card The card that is played.
     * @param tableCard The card currently on the table.
     * @param player The player who played the card.
     */
    public void handleCardEffect(Card card, Card tableCard, Player player) {
        String effect=card.getEffect();
        switch (effect) {
            case "WILD":
                chooseColor(card,tableCard,player);
                if(player==humanPlayer){
                    playHuman=false;
                    System.out.println(cardTable.getColor()+" "+card.getValue());
                    threadPlayMachine.setHasPlayerPlayed(true);
                }
                break;
            case "+4":
                if(player==humanPlayer){
                    setTextAction("jugador pierde turno y toma 4");
                    playHuman=true;
                    threadPlayMachine.setHasPlayerPlayed(false);
                    takecard=true;
                    chooseColor(card,tableCard,player);
                    table.addCardOnTheTable(card);
                    drawCards(machinePlayer,4);
                    System.out.println(card.getColor()+" "+card.getValue());
                    break;
                }else{
                    takecard=false;
                    setTextAction("Maquina pierde turno y toma 4");
                    setPlayHuman(false);
                    chooseColor(card,tableCard,player);
                    table.addCardOnTheTable(card);
                    drawCards(humanPlayer,4);
                    System.out.println(card.getColor()+" "+card.getValue());
                    threadPlayMachine.setHasPlayerPlayed(true);

                    break;
                }
            case "+2":
                if(player==humanPlayer){
                    setTextAction("Maquina pierde turno y toma 2");
                    drawCards(machinePlayer,2);
                    takecard=true;
                    break;
                }else{
                    takecard=false;
                    setTextAction("jugador pierde turno y toma 2");
                    setPlayHuman(false);
                    drawCards(humanPlayer,2);
                    threadPlayMachine.setHasPlayerPlayed(true);
                    break;
                }
            case "SKIP", "RESERVE":
                if (player==humanPlayer){
                    setTextAction("Maquina pierde turno");
                    playHuman=true;
                    threadPlayMachine.setHasPlayerPlayed(false);
                    takecard=true;
                    break;
                }else{
                    takecard=false;
                    setTextAction("Jugador pierde turno");
                    setPlayHuman(false);
                    threadPlayMachine.setHasPlayerPlayed(true);
                    break;
                }
            default:
                if (player==humanPlayer){
                    playHuman=false;
                    System.out.println(cardTable.getColor()+" "+card.getValue());
                    threadPlayMachine.setHasPlayerPlayed(true);
                    break;
                }else{
                    setPlayHuman(true);
                    Card tableCard2=getCardTable();
                    threadPlayMachine.setHasPlayerPlayed(false);
                    System.out.println(tableCard2.getColor()+" "+tableCard2.getValue());
                    System.out.println("si esta llegando aqui");
                }

        }
    }

    /**
     * Inner class for displaying a color picker dialog.
     */
    public class ColorPickerDialog {
        private String selectedColor;

        /**
         * Gets the selected color from the dialog.
         *
         * @return The selected color.
         */
        public String getSelectedColor() {
            return selectedColor;
        }

        /**
         * Displays the color picker dialog.
         */
        public void display() {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Choose a Color");

            Button redButton = createColorButton("RED");
            Button greenButton = createColorButton("GREEN");
            Button blueButton = createColorButton("BLUE");
            Button yellowButton = createColorButton("YELLOW");

            HBox hbox = new HBox(10, redButton, greenButton, blueButton, yellowButton);
            hbox.setPadding(new Insets(15));
            Scene scene = new Scene(hbox);

            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        }

        /**
         * Creates a button for selecting a color.
         *
         * @param color The color represented by the button.
         * @return The button for selecting the color.
         */
        private Button createColorButton(String color) {
            Button button = new Button(color);
            button.setStyle("-fx-background-color: " + color.toLowerCase() + "; -fx-text-fill: black;");
            button.setOnAction(event -> {
                selectedColor = color;
                ((Stage) button.getScene().getWindow()).close();
            });
            return button;
        }
    }

    /**
     * Allows the player to choose a color.
     *
     * @param card The card for which the color is chosen.
     * @param tableCard The card currently on the table.
     * @param player The player choosing the color.
     */
    public void chooseColor(Card card, Card tableCard, Player player) {
        if (player.equals(humanPlayer)) {
            ColorPickerDialog colorPicker = new ColorPickerDialog();
            colorPicker.display();
            String chosenColor = colorPicker.getSelectedColor();
            if (chosenColor != null) {
                cardTable.setColor(chosenColor);
            }
        }else{
            Random random = new Random();
            String[] colors = {"RED", "BLUE", "YELLOW", "GREEN"};
            int randomIndex = random.nextInt(colors.length);
            String randomText = colors[randomIndex];
            card.setColor(randomText);
            table.addCardOnTheTable(card);
            setTextAction("Maquina elije color: "+randomText);
            setPlayHuman(true);
            threadPlayMachine.setHasPlayerPlayed(false);
        }

    }

    /**
     * Draws a specified number of cards for a player.
     *
     * @param player The player drawing cards.
     * @param numCards The number of cards to draw.
     */
    public void drawCards(Player player, int numCards) {
        Platform.runLater(() -> {
            for (int i = 0; i < numCards; i++) {
                player.addCard(deck.takeCard());
            }
            if (player.equals(humanPlayer)) {
                printCardsHumanPlayer();
                textAction.setText("Jugador toma " + numCards + " cartas.");
            } else {
                printCardsMachine();
                textAction.setText("Maquina toma " + numCards + " cartas.");
            }
        });
    }


    /**
     * Sets whether the machine said "UNO".
     *
     * @param say Whether the machine said "UNO".
     */
    public void setMachineSayOne(boolean say){
        this.machineSayOne=say;
    }

    /**
     * Checks if it is the human player's turn.
     *
     * @return True if it is the human player's turn, false otherwise.
     */
    public boolean isPlayHuman() {
        return playHuman;
    }

    /**
     * Checks if the player can take a card.
     *
     * @return True if the player can take a card, false otherwise.
     */
    public boolean isTakecard() {
        return takecard;
    }

    /**
     * Gets the human player.
     *
     * @return The human player.
     */
    public Player getHumanPlayer() {
        return humanPlayer;
    }

    /**
     * Sets the card on the table.
     *
     * @param card the card to set on the table
     */
    public void setCardTable(Card card){
        this.cardTable=card;
    }

    /**
     * Gets the card on the table.
     *
     * @return the card on the table
     */
    public Card getCardTable() {
        return cardTable;
    }

    /**
     * Sets whether it is the human player's turn.
     *
     * @param playHuman true if it is the human player's turn, false otherwise
     */
    public void setPlayHuman(boolean playHuman) {
        this.playHuman = playHuman;
    }

    /**
     * Sets whether the human player can say "UNO".
     *
     * @param humanCanSayONE true if the human player can say "UNO", false otherwise
     */
    public void setHumanCanSayONE(boolean humanCanSayONE){this.humanCanSayONE=humanCanSayONE;}

    /**
     * Sets whether the human player can say "UNO" to the machine.
     *
     * @param humanCanSayONEToMachine true if the human player can say "UNO" to the machine, false otherwise
     */
    public void setHumanCanSayONEToMachine(boolean humanCanSayONEToMachine){this.humanCanSayONEToMachine=humanCanSayONEToMachine;}

    /**
     * Sets whether the player has played.
     *
     * @param bool true if the player has played, false otherwise
     */
    public void setHasPlayerPlayed(boolean bool){
        threadPlayMachine.setHasPlayerPlayed(bool);
    }

    /**
     * Sets whether the machine can say "UNO".
     *
     * @param bool true if the machine can say "UNO", false otherwise
     */
    public void setMachineCanSayOne(boolean bool){
        threadSingUNOMachine.setMachineCanSayOne(bool);
    }

    /**
     * Sets the text action.
     *
     * @param text the text to set
     */
    public void setTextAction(String text){
        textAction.setText(text);
    }

    /**
     * Sets whether the "UNO" thread is running.
     *
     * @param bool true if the "UNO" thread is running, false otherwise
     */
    public void setRunningOneThread(boolean bool){
        threadSingUNOMachine.setRunning(bool);
    }

    /**
     * Sets whether the play machine thread is running.
     *
     * @param bool true if the play machine thread is running, false otherwise
     */
    public void setRunningPlayMachineThread(boolean bool){
        threadPlayMachine.setRunning(bool);
    }

    /**
     * Sets whether a card should be taken.
     *
     * @param takecard true if a card should be taken, false otherwise
     */
    public void setTakecard(boolean takecard) {
        this.takecard = takecard;
    }
}
