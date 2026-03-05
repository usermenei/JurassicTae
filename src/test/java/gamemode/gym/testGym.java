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
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX already started
        }
    }

    @Test
    void testGymFeeDeduction() {
        player.setMoney(1000);
        boolean canEnter = GameController.getInstance().enoughMoney(player);
        assertEquals(canEnter,true);
        assertEquals(player.getMoney(),500);
    }

    @Test
    void testExactMoneyForGym() {
        player.setMoney(500);

        boolean canEnter = GameController.getInstance().enoughMoney(player);

        assertEquals(true, canEnter);
        assertEquals(0, player.getMoney());
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
    @Test
    void testZeroScoreReward() {
        player.setStrength(10);
        player.setExp(5);
        player.setMaxHp(100);

        GameController.getInstance().setReward(0);

        assertEquals(10, player.getStrength());
        assertEquals(5, player.getExp());
        assertEquals(100, player.getMaxHp());
    }

    @Test
    void testHighScoreReward() {
        player.setStrength(10);
        player.setExp(0);
        player.setMaxHp(100);

        int score = 1500;

        GameController.getInstance().setReward(score);

        assertEquals(15, player.getStrength()); // 1500/300 = 5
        assertEquals(15, player.getExp());      // 1500/100 = 15
        assertEquals(103, player.getMaxHp());   // 1500/500 = 3
    }

    @Test
    void testNegativeScore() {
        player.setStrength(10);

        GameController.getInstance().setReward(-100);

        assertEquals(10, player.getStrength());
    }

    @Test
    void testMultipleRewards() {
        player.setStrength(10);

        GameController.getInstance().setReward(300);
        GameController.getInstance().setReward(300);

        assertEquals(12, player.getStrength());
    }

    @Test
    void fullHpAfterGym() {
        player.setMaxHp(100);

        GameController.getInstance().setReward(3000);

        assertEquals(106, player.getMaxHp());
        assertEquals(106, player.getHp());
    }

}
