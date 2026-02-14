package org.badfish.signin.storage.provider;

import com.yirankuma.yrdatabase.api.DatabaseManager;
import com.yirankuma.yrdatabase.api.Repository;
import com.yirankuma.yrdatabase.api.CacheStrategy;
import com.yirankuma.yrdatabase.nukkit.YRDatabaseNukkit;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.storage.SignInEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * YRDatabase 数据库存储实现
 */
public class DatabaseDataStorageProvider implements DataStorageProvider {

    private Repository<SignInEntity> repository;

    @Override
    public void init() {
        DatabaseManager db = YRDatabaseNukkit.getDatabaseManager();
        this.repository = db.getRepository(SignInEntity.class);
    }

    @Override
    public Collection<PlayerSignInData> loadAll() {
        List<SignInEntity> entities = repository.findAll().join();
        Collection<PlayerSignInData> result = new ArrayList<>();
        for (SignInEntity entity : entities) {
            result.add(toPlayerData(entity));
        }
        return result;
    }

    @Override
    public void save(PlayerSignInData data) {
        repository.save(toEntity(data), CacheStrategy.WRITE_THROUGH).join();
    }

    @Override
    public void saveAll(Collection<PlayerSignInData> dataCollection) {
        List<SignInEntity> entities = dataCollection.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        repository.saveAll(entities, CacheStrategy.WRITE_THROUGH).join();
    }

    @Override
    public void close() {
        // DatabaseManager 由 YRDatabase 插件管理生命周期，无需手动关闭
    }

    private SignInEntity toEntity(PlayerSignInData data) {
        String dates = String.join(",", data.getSignIn());
        return new SignInEntity(
                data.getPlayerName(),
                dates,
                data.getSignMonth(),
                data.getCumulativeCount(),
                data.getRetroactiveCount()
        );
    }

    private PlayerSignInData toPlayerData(SignInEntity entity) {
        ArrayList<String> signIn = new ArrayList<>();
        if (entity.getSignInDates() != null && !entity.getSignInDates().isEmpty()) {
            signIn.addAll(Arrays.asList(entity.getSignInDates().split(",")));
        }
        PlayerSignInData data = new PlayerSignInData(entity.getPlayerName(), signIn);
        data.setSignMonth(entity.getSignMonth());
        data.setCumulativeCount(entity.getCumulativeCount());
        data.setRetroactiveCount(entity.getRetroactiveCount());
        return data;
    }
}
