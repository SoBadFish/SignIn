package org.badfish.signin.storage.provider;

import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.utils.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * YAML 文件存储实现
 */
public class YamlDataStorageProvider implements DataStorageProvider {

    @Override
    public void init() {
        File playersDir = new File(SignInMainClass.MAIN_INSTANCE.getDataFolder(), "players");
        if (!playersDir.exists()) {
            playersDir.mkdirs();
        }
    }

    @Override
    public PlayerSignInData load(String playerName) {
        File file = new File(SignInMainClass.MAIN_INSTANCE.getDataFolder() + "/players/" + playerName + ".yml");
        if (!file.exists()) {
            return null;
        }
        Config config = new Config(file.getPath(), Config.YAML);
        PlayerSignInData signInData = new PlayerSignInData(
                playerName,
                (ArrayList<String>) config.getStringList("signInDate")
        );
        signInData.setCumulativeCount(config.getInt("cumulativeCount", 0));
        signInData.setRetroactiveCount(config.getInt("retroactiveCount", 0));
        signInData.setSignMonth(config.getInt("signMonth", -1));
        return signInData;
    }

    @Override
    public Collection<PlayerSignInData> loadAll() {
        Collection<PlayerSignInData> result = new ArrayList<>();
        for (String name : Tool.getDefaultFiles("players")) {
            Config config = new Config(
                    SignInMainClass.MAIN_INSTANCE.getDataFolder() + "/players/" + name + ".yml",
                    Config.YAML
            );
            PlayerSignInData signInData = new PlayerSignInData(
                    name,
                    (ArrayList<String>) config.getStringList("signInDate")
            );
            signInData.setCumulativeCount(config.getInt("cumulativeCount", 0));
            signInData.setRetroactiveCount(config.getInt("retroactiveCount", 0));
            signInData.setSignMonth(config.getInt("signMonth", -1));
            result.add(signInData);
        }
        return result;
    }

    @Override
    public void save(PlayerSignInData data) {
        doSave(data);
    }

    @Override
    public void saveAsync(PlayerSignInData data) {
        PlayerSignInData snapshot = data.snapshot();
        Server.getInstance().getScheduler().scheduleAsyncTask(SignInMainClass.MAIN_INSTANCE, new AsyncTask() {
            @Override
            public void onRun() {
                doSave(snapshot);
            }
        });
    }

    @Override
    public void saveAll(Collection<PlayerSignInData> dataCollection) {
        for (PlayerSignInData data : dataCollection) {
            save(data);
        }
    }

    private void doSave(PlayerSignInData data) {
        // 对同一玩家文件加锁，防止并发写入导致数据覆盖
        synchronized (data.getPlayerName().intern()) {
            Config config = new Config(
                    SignInMainClass.MAIN_INSTANCE.getDataFolder() + "/players/" + data.getPlayerName() + ".yml",
                    Config.YAML
            );
            config.set("signInDate", data.getSignIn());
            config.set("signMonth", data.getSignMonth());
            config.set("cumulativeCount", data.getCumulativeCount());
            config.set("retroactiveCount", data.getRetroactiveCount());
            config.save();
        }
    }

    @Override
    public void close() {
        // YAML 不需要关闭
    }
}
