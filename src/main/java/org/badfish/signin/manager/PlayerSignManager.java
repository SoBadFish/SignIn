package org.badfish.signin.manager;

import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.utils.Tool;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author BadFish
 */
public class PlayerSignManager {

    private Map<String, PlayerSignInData> playerSignInDataCache;

    /** 标记从其他子服切换过来、数据尚未经过 DB 验证的玩家 */
    private final Set<String> needsVerification = new HashSet<>();

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

    /**
     * 异步加载完成后写入缓存，并标记需要在首次写操作前验证
     */
    public void putPlayerData(String playerName, PlayerSignInData data) {
        playerSignInDataCache.put(playerName, data);
        needsVerification.add(playerName);
    }

    /**
     * 验证完成后用 DB 最新数据刷新缓存，不重置验证标记
     */
    public void refreshCache(String playerName, PlayerSignInData data) {
        playerSignInDataCache.put(playerName, data);
    }

    /** 判断该玩家是否需要在写操作前进行 DB 验证 */
    public boolean needsVerification(String playerName) {
        return needsVerification.contains(playerName);
    }

    /** 标记验证已完成 */
    public void markVerified(String playerName) {
        needsVerification.remove(playerName);
    }

    public void removePlayerData(String playerName) {
        playerSignInDataCache.remove(playerName);
        needsVerification.remove(playerName);
    }

    public boolean containsPlayer(String playerName) {
        return playerSignInDataCache.containsKey(playerName);
    }
}
