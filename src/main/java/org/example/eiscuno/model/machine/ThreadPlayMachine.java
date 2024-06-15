package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

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
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Aqui iria la logica de colocar la carta
                if(machineCanPlay){
                    putCardOnTheTable();
                    hasPlayerPlayed = false;
                    System.out.println("jugo");
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
                    putCardOnTheTable();

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
        int index = (int) (Math.random() * machinePlayer.getCardsPlayer().size());
        Card card = machinePlayer.getCard(index);
        table.addCardOnTheTable(card);
        tableImageView.setImage(card.getImage());

        machinePlayer.removeCard(gameUnoController.findPosCardsMachinePlayer(card));
        gameUnoController.printCardsMachine();
        gameUnoController.setPlayHuman(true);
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
}
