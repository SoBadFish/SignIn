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

    private Map<String, PlayerSignInData> playerSignInData;

    private PlayerSignManager(Map<String, PlayerSignInData> signInData){
        this.playerSignInData = signInData;
    }

    public static PlayerSignManager managerCreate(){
        Map<String, PlayerSignInData> dataMap = new HashMap<>();
        for (PlayerSignInData signInData : SignInMainClass.DATA_STORAGE.loadAll()) {
            if (signInData.getSignMonth() == -1) {
                signInData.setSignMonth(Tool.geMonth());
            }
            //如果是新的一个月那就重置累计进度
            if (signInData.getSignMonth() != Tool.geMonth()) {
                signInData.reset();
            }
            dataMap.put(signInData.getPlayerName(), signInData);
        }
        return new PlayerSignManager(dataMap);
    }

    public Collection<PlayerSignInData> getPlayerSignInData() {
        return playerSignInData.values();
    }

    public PlayerSignInData getPlayerData(String playerName){
        return playerSignInData.computeIfAbsent(playerName, PlayerSignInData::new);
    }

}
