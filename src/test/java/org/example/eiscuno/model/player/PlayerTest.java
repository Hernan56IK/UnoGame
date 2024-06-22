package org.example.eiscuno.model.player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void ListPlayerTest() {
        Player onePlayer = new Player("player1");
        onePlayer.removeCard(40);
    }
    @Test
    void ListPlayerTest1() {
        Player onePlayer = new Player("player1");
        onePlayer.removeCard(0);
    }
}