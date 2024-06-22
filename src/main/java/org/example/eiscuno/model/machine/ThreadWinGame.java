package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.view.Alert.AlertBox;
import org.example.eiscuno.view.GameUnoStage;
import java.util.ArrayList;

public class ThreadWinGame implements Runnable{
    private GameUnoController gameUnoController;
    private Player machinePlayer;
    private Player humanPlayer;
    private boolean running=true;
    private Deck DeckOfCards;



    public ThreadWinGame(Player humanPlayer, Player machinePlayer,Deck DeckOfCards, GameUnoController gameUnoController){
        this.humanPlayer=humanPlayer;
        this.machinePlayer=machinePlayer;
        this.DeckOfCards=DeckOfCards;
        this.gameUnoController=gameUnoController;
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
        if(numsCardsHuman==0){
            System.out.println("HAS GANADO ESTA PARTIDA");
            Platform.runLater(() -> {
                GameUnoStage.deleteInstance();
                new AlertBox().showConfirm("FELICIDADES GANASTE!", "Tiene 0 cartas en mano ","Jugador gana por quedarse sin cartas");
            });
            gameUnoController.setRunningOneThread(false);
            gameUnoController.setRunningPlayMachineThread(false);
            stopThread();
        }else if(numsCardsMachine==0){
            System.out.println("HAS PERDIDO... HASTA LA PROXIMA");

            Platform.runLater(() -> {
                GameUnoStage.deleteInstance();
                new AlertBox().showConfirm("MAQUINA GANA ESTA PARTIDA", "MAQUINA tiene 0 cartas en mano ","Maquina gana por quedarse sin cartas");
            });
            stopThread();
            gameUnoController.setRunningOneThread(false);
            gameUnoController.setRunningPlayMachineThread(false);

        } else if (DeckOfCards.isEmpty()) {
            System.out.println("Deck Vacio");
            int pointsMachine=printCardValuesWhenDeckEmpty(machinePlayer.getCardsPlayer(), machinePlayer);
            int pointsHuman=printCardValuesWhenDeckEmpty(humanPlayer.getCardsPlayer(), humanPlayer);
            System.out.println("Maquina: "+pointsMachine+" Puntos");
            System.out.println("Jugador: "+pointsHuman+" Puntos");
            stopThread();
            Platform.runLater(() -> {
                if(pointsMachine<pointsHuman){
                    Platform.runLater(() -> GameUnoStage.deleteInstance());
                    gameUnoController.setTextAction("La Maquina Gana");
                    gameUnoController.setRunningOneThread(false);
                    gameUnoController.setRunningPlayMachineThread(false);
                    new AlertBox().showConfirm("LA MAQUINA GANA", "PUNTAJE DEL JUGADOR: "+pointsHuman+"\nPUNTAJE DE LA MAQUINA: "+pointsMachine,"La maquina gana por puntaje");

                }else{
                    Platform.runLater(() -> GameUnoStage.deleteInstance());
                    gameUnoController.setRunningOneThread(false);
                    gameUnoController.setRunningPlayMachineThread(false);
                    gameUnoController.setTextAction("Jugador Gana");
                    new AlertBox().showConfirm("FELICIDADES GANASTE!", "PUNTAJE DEL JUGADOR: "+pointsHuman+"\nPUNTAJE DE LA MAQUINA: "+pointsMachine,"Jugador gana por puntaje");
                }
            });

        }
    }



    private int printCardValuesWhenDeckEmpty(ArrayList<Card> cards, Player player) {
        int total=0;
        for (Card card : cards) {
            int value = convertCardValueToInt(card.getValue());
            total=total+value;
        }
        return total;
    }


    private int convertCardValueToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // Handle non-integer values, if necessary
            return 10; // Default value if conversion fails
        }
    }

    public void stopThread() {
        running = false;
    }
}
