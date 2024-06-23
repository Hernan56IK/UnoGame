package org.example.eiscuno.model.machine;

import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.view.GameUnoStage;

import java.util.ArrayList;

/**
 * The ThreadSingUNOMachine class represents the thread that handles the logic for the machine to call UNO in the Uno game.
 */
public class ThreadSingUNOMachine implements Runnable{
    private ArrayList<Card> cardsPlayer;
    private ArrayList<Card> machineCardsPlayer;
    private boolean machineCanSayOne=true;
    private boolean machineCanSayOneToPlayer=true;
    private GameUnoController gameUnoController;
    private ThreadPlayMachine threadPlayMachine;
    private boolean running=true;


    /**
     * Constructor for the ThreadSingUNOMachine class.
     *
     * @param cardsPlayer        the player's cards
     * @param machineCardsPlayer the machine's cards
     * @param gameUnoController  the Uno game controller
     * @param threadPlayMachine  the thread that handles the machine's play
     */
    public ThreadSingUNOMachine(ArrayList<Card> cardsPlayer, ArrayList<Card> machineCardsPlayer, GameUnoController gameUnoController, ThreadPlayMachine threadPlayMachine){
        this.cardsPlayer = cardsPlayer;
        this.machineCardsPlayer=machineCardsPlayer;
        this.gameUnoController=gameUnoController;
        this.threadPlayMachine=threadPlayMachine;
    }

    /**
     * Method that runs when the thread starts.
     */
    @Override
    public void run(){
        while (running){
            try {
                Thread.sleep((long) (Math.random() * 5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheHumanPlayer();

            synchronized (this) {
                notify();  // Notifica a ThreadPlayMachine
            }
        }
    }

    /**
     * Checks if the human player or the machine has one card and updates the game state accordingly.
     */
    private void hasOneCardTheHumanPlayer(){
        if(cardsPlayer.size() == 1 && machineCanSayOneToPlayer){
            //System.out.println("UNO AL JUGADOR");
            gameUnoController.setTextAction("UNO AL JUGADOR");
            gameUnoController.setHasPlayerPlayed(false);
            gameUnoController.setHumanCanSayONE(false);
            gameUnoController.setPlayHuman(false);
            gameUnoController.setMachineSayOne(true);


        }else if(machineCardsPlayer.size()==1 && machineCanSayOne){
            //System.out.println("UNO PARA DEFENSA");
            gameUnoController.setTextAction("UNO PARA DEFENSA");
            gameUnoController.setHumanCanSayONEToMachine(false);
        }
    }

    /**
     * Sets whether the machine can call UNO.
     *
     * @param machineCanSayOne true if the machine can call UNO, false otherwise
     */
    public void setMachineCanSayOne(boolean machineCanSayOne){this.machineCanSayOne=machineCanSayOne;}

    /**
     * Sets whether the machine can call UNO to the player.
     *
     * @param machineCanSayOneToPlayer true if the machine can call UNO to the player, false otherwise
     */
    public void setMachineCanSayOneToPlayer(boolean machineCanSayOneToPlayer){this.machineCanSayOneToPlayer=machineCanSayOneToPlayer;}

    /**
     * Sets whether the thread is running.
     *
     * @param running true if the thread is running, false otherwise
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}
