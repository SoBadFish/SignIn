package org.badfish.signin.manager;

import cn.nukkit.utils.Config;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.utils.Tool;

import java.util.ArrayList;
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
        Config config;
        Map<String, PlayerSignInData> dataMap = new HashMap<>();
        PlayerSignInData signInData;
        for(String name: Tool.getDefaultFiles("players")){
            config = new Config(SignInMainClass.MAIN_INSTANCE.getDataFolder()+"/players/"+name+".yml",Config.YAML);
            signInData = new PlayerSignInData(name, (ArrayList<String>) config.getStringList("signInDate"));
            signInData.setCumulativeCount(config.getInt("cumulativeCount",0));
            signInData.setRetroactiveCount(config.getInt("retroactiveCount",0));
            signInData.setSignMonth(config.getInt("signMonth",-1));
            if(signInData.getSignMonth() == -1){
                signInData.setSignMonth(Tool.geMonth());
            }
            //如果是新的一个月那就重置累计进度
            if(signInData.getSignMonth() != Tool.geMonth()){
                signInData.reset();
            }
            dataMap.put(name, signInData);
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
