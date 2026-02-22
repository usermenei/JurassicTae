package gamemode.lobby.logic;

import gamemode.lobby.LivingThing.Player;

public class GameLogic {
    private static GameLogic instance;
    private Player player = new Player(400,711);
    private GameState gameState;

    private GameLogic() {
        player = new Player(400,711);
    }

    public static GameLogic getInstance() {
        if (instance == null) {
            instance = new GameLogic();
        }
        return instance;
    }

    public Player getPlayer() {
        return player;
    }

    public void update(){
        if(gameState == GameState.SPAWN){
            
        }
    }

}
