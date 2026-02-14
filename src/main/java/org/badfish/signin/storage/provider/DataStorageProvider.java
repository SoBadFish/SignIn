package org.badfish.signin.storage.provider;

import org.badfish.signin.data.PlayerSignInData;

import java.util.Collection;

/**
 * 玩家签到数据存储接口
 */
public interface DataStorageProvider {

    void init();

    Collection<PlayerSignInData> loadAll();

    void save(PlayerSignInData data);

    void saveAll(Collection<PlayerSignInData> dataCollection);

    void close();
}
