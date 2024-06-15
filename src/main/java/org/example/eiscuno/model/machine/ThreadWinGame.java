package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.view.GameUnoStage;

public class ThreadWinGame implements Runnable{
    private GameUnoController gameUnoController;
    private Player machinePlayer;
    private Player humanPlayer;
    private boolean running=true;



    public ThreadWinGame(Player humanPlayer, Player machinePlayer){
        this.humanPlayer=humanPlayer;
        this.machinePlayer=machinePlayer;
    }

    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep((long) (Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            verifiedGame();
        }


    }
    private void verifiedGame(){
        int numsCardsMachine=machinePlayer.getCardsPlayer().size();
        int numsCardsHuman=humanPlayer.getCardsPlayer().size();
        //System.out.println(numsCardsHuman+numsCardsMachine);
        if(numsCardsHuman==0){
            System.out.println("HAS GANADO ESTA PARTIDA");
            Platform.runLater(() -> GameUnoStage.deleteInstance());
            running=false;
        }else if(numsCardsMachine==0){
            System.out.println("HAS PERDIDO... HASTA LA PROXIMA");
            Platform.runLater(() -> GameUnoStage.deleteInstance());
            running=false;
        }
    }
}
