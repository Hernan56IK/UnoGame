package org.example.eiscuno.model.machine.observers;

import org.example.eiscuno.model.machine.observers.observer;

public class observerExample implements observer {
    /**
     * a specific example where an action is triggered when the observer is invoked
     */
    @Override
    public void update() {
        System.out.println("la maquina ha puesto una carta en el tablero");
    }
}
