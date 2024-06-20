package org.example.eiscuno.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.machine.ThreadWinGame;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.GameUnoStage;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController {

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


        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this,this.deck, this.threadSingUNOMachine); // Pasar referencia de GameUnoController

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer(), this.machinePlayer.getCardsPlayer(), this, this.threadPlayMachine);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");

        threadPlayMachine.setThreadSingUNOMachine(threadSingUNOMachine); // Establecer referencia después de la creación

        t.start();
        threadPlayMachine.start();

        threadWinGame = new ThreadWinGame(this.humanPlayer, this.machinePlayer);
        Thread w =new Thread(threadWinGame,"treadWinGame");
        w.start();


    }

    /**
     * Initializes the variables for the game.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
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
                    playHuman=false;
                    cardTable=card;
                    handleCardEffect(card);
                    System.out.println(cardTable.getColor()+" "+card.getValue());
                    threadPlayMachine.setHasPlayerPlayed(true);
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
                System.out.println(card.getValue()+" "+card.getColor()+" "+i);
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
            if(!canPutCard(cardTable)){
                takecard=true;
                playHuman=false;
                threadPlayMachine.setHasPlayerPlayed(true);
                System.out.println("entra por no poder");
                System.out.println(takecard);
            }
            humanCanSayONE=true;
            threadSingUNOMachine.setMachineCanSayOneToPlayer(true);
            takecard=false;
        }else if(machineSayOne){
            humanPlayer.addCard(deck.takeCard());
            printCardsHumanPlayer();
            playHuman=false;
            threadPlayMachine.setHasPlayerPlayed(true);
            takecard=true;
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

    public void setPlayHuman(boolean playHuman) {
        this.playHuman = playHuman;
    }
    public void setHumanCanSayONE(boolean humanCanSayONE){this.humanCanSayONE=humanCanSayONE;}
    public void setHumanCanSayONEToMachine(boolean humanCanSayONEToMachine){this.humanCanSayONEToMachine=humanCanSayONEToMachine;}

    @FXML
    void onHandleButtonExit(ActionEvent event) {
        GameUnoStage.deleteInstance();
    }

    public Card getCardTable() {
        return cardTable;
    }

    public void setCardTable(Card card){
        this.cardTable=card;
    }

    private void handleCardEffect(Card card) {
        String effect = card.getEffect();

        switch (effect) {
            case "WILD":
                chooseColor();
            case "+4":
                chooseColor();
                if (effect.equals("+4")) {
                    //opponentDrawCards(4);
                }
                break;
            case "+2":
                //opponentDrawCards(2);
                break;
            case "SKIP":
                //skipOpponentTurn();
                break;
            default:
                // No hay efecto especial
                break;
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
    private void chooseColor() {
        ColorPickerDialog colorPicker = new ColorPickerDialog();
        colorPicker.display();
        String chosenColor = colorPicker.getSelectedColor();
        if (chosenColor != null) {
            cardTable.setColor(chosenColor);
            // Actualizar la imagen de la carta en la tabla si es necesario
            // tableImageView.setImage(new Image("file:path/to/color_changed_card_image_" + chosenColor.toLowerCase() + ".png"));
        }
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
}
