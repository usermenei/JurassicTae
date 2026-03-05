package gamemode.gym;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gamemode.lobby.Player.Player;
import gamemode.lobby.logic.GameController;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class testGym {
    public Player player = GameController.getInstance().getPlayer();

    @BeforeAll
    static void initJFX() {
        Platform.startup(() -> {});
    }

    @Test
    void testGymFeeDeduction() {
        player.setMoney(1000);
        boolean canEnter = GameController.getInstance().enoughMoney(player);
        assertEquals(canEnter,true);
        assertEquals(player.getMoney(),500);
    }

    @Test
    void notEnoughMoney() {
        player.setMoney(200);
        boolean canEnter = GameController.getInstance().enoughMoney(player);
        assertEquals(canEnter,false);
        assertEquals(player.getMoney(),200);
    }


    @Test
    void testGymRewardCalculation() {
        player.setStrength(10);
        player.setExp(0);
        player.setMaxHp(100);

        int score = 600;

        GameController.getInstance().setReward(score);

        assertEquals(12, player.getStrength()); // 600/300 = 2
        assertEquals(6, player.getExp());       // 600/100 = 6
        assertEquals(101, player.getMaxHp());   // 600/500 = 1
    }
    @Test
    void testLowScoreReward() {
        player.setStrength(10);

        int score = 100;

        GameController.getInstance().setReward(score);

        assertEquals(10, player.getStrength()); // 100/300 = 0
    }

}
