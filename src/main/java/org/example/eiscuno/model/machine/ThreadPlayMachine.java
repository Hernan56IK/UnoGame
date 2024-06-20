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
                // Aqui iria la logica de colocar la carta


                if(machineCanPlay){
                    boolean canPlay=canPutCard(gameUnoController.getCardTable());
                    if(canPlay){
                        putCardOnTheTable();
                        hasPlayerPlayed = false;
                        System.out.println("jugo");

                        System.out.println(gameUnoController.isTakecard());
                        System.out.println(gameUnoController.isPlayHuman());

                    }else{
                        machinePlayer.addCard(deck.takeCard());
                        System.out.println("tomó1");
                        threadSingUNOMachine.setMachineCanSayOne(true);
                        gameUnoController.setHumanCanSayONEToMachine(true);
                        gameUnoController.printCardsMachine();
                        boolean canPlay2=canPutCard(gameUnoController.getCardTable());

                        if (canPlay2){
                            putCardOnTheTable();
                            hasPlayerPlayed = false;
                            System.out.println("jugo");

                            System.out.println(gameUnoController.isTakecard());
                            System.out.println(gameUnoController.isPlayHuman());
                        }else{
                            gameUnoController.setPlayHuman(true);
                            hasPlayerPlayed = false;
                            System.out.println("No pudo jugar");

                            System.out.println(gameUnoController.isTakecard());
                            System.out.println(gameUnoController.isPlayHuman());
                        }
                    }


                }else{
                    machinePlayer.addCard(deck.takeCard());
                    threadSingUNOMachine.setMachineCanSayOne(true);
                    gameUnoController.setHumanCanSayONEToMachine(true);
                    gameUnoController.printCardsMachine();
                    try{
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //putCardOnTheTable();

                    gameUnoController.setPlayHuman(true);
                    hasPlayerPlayed = false;
                    machineCanPlay=true;
                    System.out.println("tomo");
                    gameUnoController.setHumanCanSayONE(false);
                }
            }
        }
    }

    private void putCardOnTheTable(){


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
                    handleCardEffect(card, tableCard);
                    foundCard=false;
                    gameUnoController.setPlayHuman(true);
                    Card tableCard2=gameUnoController.getCardTable();
                    System.out.println(tableCard2.getColor()+" "+tableCard2.getValue());
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

    public void setThreadSingUNOMachine(ThreadSingUNOMachine threadSingUNOMachine) {
        this.threadSingUNOMachine = threadSingUNOMachine;
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

    private void handleCardEffect(Card card, Card tableCard) {
        String effect = card.getEffect();
        //String effect="+4";
        System.out.println(effect);
        Random random = new Random();
        String[] colors = {"RED", "BLUE", "YELLOW", "GREEN"};
        int randomIndex = random.nextInt(colors.length);
        String randomText = colors[randomIndex];

        switch (effect) {
            case "WILD":
                card.setColor(randomText);
                table.addCardOnTheTable(card);
                break;
            case "+4":

                card.setColor(randomText);
                table.addCardOnTheTable(card);

                if (effect.equals("+4")) {
                    System.out.println("yes");
                }
                break;
            case "+2":
                //opponentDrawCards(2);
                break;
            case "SKIP", "RESERVE":
                // Crear la pausa
                PauseTransition pause = new PauseTransition(Duration.seconds(2)); // Esperar 2 segundos
                pause.setOnFinished(event -> {
                    putCardOnTheTable();
                });
                pause.play();

                break;
            default:
                // No hay efecto especial
                break;
        }
    }
}
