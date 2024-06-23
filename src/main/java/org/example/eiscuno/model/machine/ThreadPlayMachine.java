package org.example.eiscuno.model.machine;

import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.machine.observers.observableClass;
import org.example.eiscuno.model.machine.observers.observerExample;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

/**
 * The ThreadPlayMachine class represents the execution thread for the machine player in the Uno game.
 */
public class ThreadPlayMachine extends Thread{
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;
    private GameUnoController gameUnoController = new GameUnoController();
    private boolean machineCanPlay=true;
    private Deck deck;
    private ThreadSingUNOMachine threadSingUNOMachine;
    private boolean running=true;

    observableClass observable;


    /**
     * Constructor for the ThreadPlayMachine class.
     *
     * @param table               the game table
     * @param machinePlayer       the machine player
     * @param tableImageView      the table image view
     * @param gameUnoController   the Uno game controller
     * @param deck                the card deck
     * @param threadSingUNOMachine the thread for singing UNO
     */

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUnoController gameUnoController, Deck deck, ThreadSingUNOMachine threadSingUNOMachine) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.threadSingUNOMachine=threadSingUNOMachine;
        this.gameUnoController = gameUnoController;
        this.deck=deck;
        observable = new observableClass();
    }

    /**
     * Method that runs when the thread starts.
     */
    public void run() {
        while (running){

            synchronized (threadSingUNOMachine) {
                try {
                    threadSingUNOMachine.wait();  // Espera a que ThreadSingUNOMachine notifique
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(hasPlayerPlayed){
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if(machineCanPlay){
                    boolean canPlay=canPutCard(gameUnoController.getCardTable());
                    if(canPlay){
                        //gameUnoController.setPlayHuman(true);
                        hasPlayerPlayed = false;
                        putCardOnTheTable();
                        observerExample obs = new observerExample();
                        observable.addObserver(obs);
                        observable.notification();
                        observable.deleteObserver(obs);
                        System.out.println("jugo1");
                        gameUnoController.setTextAction("Maquina Jugó 1");

                    }else{

                        machinePlayer.addCard(deck.takeCard());
                        System.out.println("tomó1");
                        gameUnoController.setTextAction("Maquina tomó 1 carta-1");

                        gameUnoController.setMachineCanSayOne(true);

                        gameUnoController.setHumanCanSayONEToMachine(true);
                        gameUnoController.printCardsMachine();
                        boolean canPlay2=canPutCard(gameUnoController.getCardTable());
                        try{
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (canPlay2){
                            //gameUnoController.setPlayHuman(true);
                            hasPlayerPlayed = false;
                            putCardOnTheTable();
                            observerExample obs = new observerExample();
                            observable.addObserver(obs);
                            observable.notification();
                            observable.deleteObserver(obs);
                            System.out.println("jugo2");
                            gameUnoController.setTextAction("Maquina Jugó 2");
                        }else{

                            gameUnoController.setPlayHuman(true);
                            hasPlayerPlayed = false;
                            System.out.println("No pudo jugar");
                            gameUnoController.setTextAction("Maquina no pudo jugar");
                            gameUnoController.setTakecard(true);
                        }

                    }

                }else{
                    machinePlayer.addCard(deck.takeCard());
                    gameUnoController.setMachineCanSayOne(true);
                    gameUnoController.setHumanCanSayONEToMachine(true);
                    gameUnoController.printCardsMachine();
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gameUnoController.setPlayHuman(true);
                    hasPlayerPlayed = false;
                    machineCanPlay=true;
                    System.out.println("tomó");
                    gameUnoController.setTextAction("Maquina tomó 1 carta");
                    gameUnoController.setHumanCanSayONE(false);

                }
            }
        }
    }

    /**
     * Puts a card on the table if the conditions are met.
     */
    public void putCardOnTheTable(){


        Card tableCard=gameUnoController.getCardTable();
        boolean foundCard=true;

        while(foundCard){
            for(int i=0; i<machinePlayer.getCardsPlayer().size();i++){
                Card card = machinePlayer.getCard(i);
                if(tableCard.getValue()==card.getValue() || tableCard.getColor()==card.getColor()|| card.getValue()=="WILD"||card.getValue()=="+4") {

                    table.addCardOnTheTable(card);
                    tableImageView.setImage(card.getImage());
                    machinePlayer.removeCard(gameUnoController.findPosCardsMachinePlayer(card));
                    gameUnoController.printCardsMachine();
                    gameUnoController.setCardTable(card);
                    System.out.println(card.getValue()+" "+card.getColor());

                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(e -> {
                        gameUnoController.handleCardEffect(card, tableCard,machinePlayer);

                    });
                    pause.play();
                    gameUnoController.setTakecard(true);
                    foundCard=false;



                    break;
                }
            }
        }
    }

    /**
     * Sets whether the player has played.
     *
     * @param hasPlayerPlayed true if the player has played, false otherwise
     */

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    /**
     * Sets whether the machine can play.
     *
     * @param machineCanPlay true if the machine can play, false otherwise
     */
    public void setMachineCanPlay(boolean machineCanPlay) {
        this.machineCanPlay = machineCanPlay;
    }


    /**
     * Checks if the machine can put a card on the table.
     *
     * @param tableCard the card on the table
     * @return true if the machine can put a card, false otherwise
     */
    private boolean canPutCard(Card tableCard){
        boolean can=false;
        for(int i=0; i<machinePlayer.getCardsPlayer().size();i++){
            Card card = machinePlayer.getCard(i);
            if(tableCard.getValue()==card.getValue() || tableCard.getColor()==card.getColor()|| card.getValue()=="WILD"||card.getValue()=="+4") {
                can = true;
            }
        }
        return can;
    }

    /**
     * Sets whether the thread is running.
     *
     * @param running true if the thread is running, false otherwise
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

}
