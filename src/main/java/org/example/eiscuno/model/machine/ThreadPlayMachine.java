package org.example.eiscuno.model.machine;

import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.Random;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;
    private GameUnoController gameUnoController = new GameUnoController();
    private boolean machineCanPlay=true;
    private Deck deck;
    private ThreadSingUNOMachine threadSingUNOMachine;

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUnoController gameUnoController, Deck deck, ThreadSingUNOMachine threadSingUNOMachine) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.threadSingUNOMachine=threadSingUNOMachine;
        this.gameUnoController = gameUnoController;
        this.deck=deck;
    }

    public void run() {
        while (true){

            synchronized (threadSingUNOMachine) {
                try {
                    threadSingUNOMachine.wait();  // Espera a que ThreadSingUNOMachine notifique
                    //Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*
            try {
                //threadSingUNOMachine.wait();  // Espera a que ThreadSingUNOMachine notifique
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/


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

                        if (canPlay2){
                            //gameUnoController.setPlayHuman(true);
                            hasPlayerPlayed = false;
                            putCardOnTheTable();
                            System.out.println("jugo2");
                            gameUnoController.setTextAction("Maquina Jugó 2");
                        }else{

                            gameUnoController.setPlayHuman(true);
                            hasPlayerPlayed = false;
                            System.out.println("No pudo jugar");
                            gameUnoController.setTextAction("Maquina no pudo jugar");
                        }

                    }

                }else{
                    machinePlayer.addCard(deck.takeCard());
                    //threadSingUNOMachine.setMachineCanSayOne(true);
                    gameUnoController.setMachineCanSayOne(true);
                    //********************************************
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
                    foundCard=false;



                    break;
                }
            }
        }
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    public void setMachineCanPlay(boolean machineCanPlay) {
        this.machineCanPlay = machineCanPlay;
    }


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

}
