package org.badfish.signin.manager;

import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.utils.Tool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BadFish
 */
public class PlayerSignManager {

    private Map<String, PlayerSignInData> playerSignInDataCache;

    private PlayerSignManager(Map<String, PlayerSignInData> signInData){
        this.playerSignInDataCache = signInData;
    }

    public static PlayerSignManager managerCreate(){
        return new PlayerSignManager(new HashMap<>());
    }

    public Collection<PlayerSignInData> getPlayerSignInData() {
        return playerSignInDataCache.values();
    }

    public PlayerSignInData getPlayerData(String playerName) {
        PlayerSignInData data = playerSignInDataCache.get(playerName);
        if (data == null) {
            data = SignInMainClass.DATA_STORAGE.load(playerName);
            if (data == null) {
                data = new PlayerSignInData(playerName);
            }
            // 月份重置检查
            if (data.getSignMonth() == -1) {
                data.setSignMonth(Tool.geMonth());
            }
            if (data.getSignMonth() != Tool.geMonth()) {
                data.reset();
            }
            playerSignInDataCache.put(playerName, data);
        }
        return data;
    }

    public void removePlayerData(String playerName) {
        playerSignInDataCache.remove(playerName);
    }

    public boolean containsPlayer(String playerName) {
        return playerSignInDataCache.containsKey(playerName);
    }
}
