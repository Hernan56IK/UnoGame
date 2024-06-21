package org.example.eiscuno.model.machine;

import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.view.GameUnoStage;

import java.util.ArrayList;

public class ThreadSingUNOMachine implements Runnable{
    private ArrayList<Card> cardsPlayer;
    private ArrayList<Card> machineCardsPlayer;
    private boolean machineCanSayOne=true;
    private boolean machineCanSayOneToPlayer=true;
    private GameUnoController gameUnoController;
    private ThreadPlayMachine threadPlayMachine;

    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer, ArrayList<Card> machineCardsPlayer, GameUnoController gameUnoController, ThreadPlayMachine threadPlayMachine){
        this.cardsPlayer = cardsPlayer;
        this.machineCardsPlayer=machineCardsPlayer;
        this.gameUnoController=gameUnoController;
        this.threadPlayMachine=threadPlayMachine;
    }

    @Override
    public void run(){
        /*
        synchronized (this) {
            notify();  // Notifica a ThreadPlayMachine
        }*/
        while (true){
            try {
                Thread.sleep((long) (Math.random() * 3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheHumanPlayer();

            synchronized (this) {
                notify();  // Notifica a ThreadPlayMachine
            }
        }
    }

    private void hasOneCardTheHumanPlayer(){
        if(cardsPlayer.size() == 1 && machineCanSayOneToPlayer){
            System.out.println("UNO AL JUGADOR");
            //threadPlayMachine.setHasPlayerPlayed(false);
            gameUnoController.setHasPlayerPlayed(false);
            //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
            gameUnoController.setHumanCanSayONE(false);
            gameUnoController.setPlayHuman(false);
            gameUnoController.setMachineSayOne(true);
            /*
            synchronized (this) {
                notify();  // Notifica a ThreadPlayMachine
            }*/

        }else if(machineCardsPlayer.size()==1 && machineCanSayOne){
            System.out.println("UNO PARA DEFENSA");
            gameUnoController.setHumanCanSayONEToMachine(false);
            /*
            synchronized (this) {
                notify();  // Notifica a ThreadPlayMachine
            }*/
        }
    }

    public void setMachineCanSayOne(boolean machineCanSayOne){this.machineCanSayOne=machineCanSayOne;}
    public void setMachineCanSayOneToPlayer(boolean machineCanSayOneToPlayer){this.machineCanSayOneToPlayer=machineCanSayOneToPlayer;}


}
