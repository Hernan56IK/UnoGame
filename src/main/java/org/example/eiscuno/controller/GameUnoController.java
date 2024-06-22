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



    @FXML
    void onHandleButtonExit(ActionEvent event) {
        GameUnoStage.deleteInstance();
    }



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

    public class ColorPickerDialog {
        private String selectedColor;

        public String getSelectedColor() {
            return selectedColor;
        }

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


    public void setMachineSayOne(boolean say){
        this.machineSayOne=say;
    }

    public boolean isPlayHuman() {
        return playHuman;
    }

    public boolean isTakecard() {
        return takecard;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public void setCardTable(Card card){
        this.cardTable=card;
    }

    public Card getCardTable() {
        return cardTable;
    }

    public void setPlayHuman(boolean playHuman) {
        this.playHuman = playHuman;
    }
    public void setHumanCanSayONE(boolean humanCanSayONE){this.humanCanSayONE=humanCanSayONE;}
    public void setHumanCanSayONEToMachine(boolean humanCanSayONEToMachine){this.humanCanSayONEToMachine=humanCanSayONEToMachine;}

    public void setHasPlayerPlayed(boolean bool){
        threadPlayMachine.setHasPlayerPlayed(bool);
    }
    public void setMachineCanSayOne(boolean bool){
        threadSingUNOMachine.setMachineCanSayOne(bool);
    }
    public void setTextAction(String text){
        textAction.setText(text);
    }

    public void setRunningOneThread(boolean bool){
        threadSingUNOMachine.setRunning(bool);
    }
    public void setRunningPlayMachineThread(boolean bool){
        threadPlayMachine.setRunning(bool);
    }

    public void setTakecard(boolean takecard) {
        this.takecard = takecard;
    }
}
