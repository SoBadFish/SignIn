package org.badfish.signin.storage.provider;

import org.badfish.signin.data.PlayerSignInData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * 玩家签到数据存储接口
 */
public interface DataStorageProvider {

    void init();

    /**
     * 加载单个玩家数据，不存在返回 null
     */
    @Nullable
    PlayerSignInData load(String playerName);

    Collection<PlayerSignInData> loadAll();

    void save(PlayerSignInData data);

    void saveAsync(PlayerSignInData data);

    void saveAll(Collection<PlayerSignInData> dataCollection);

    void close();
}
