package org.badfish.signin.storage.provider;

import cn.nukkit.utils.Config;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.utils.Tool;

import java.util.ArrayList;
import java.util.Collection;

/**
 * YAML 文件存储实现
 */
public class YamlDataStorageProvider implements DataStorageProvider {

    @Override
    public void init() {
        // YAML 不需要额外初始化
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

    @Override
    public void saveAll(Collection<PlayerSignInData> dataCollection) {
        for (PlayerSignInData data : dataCollection) {
            save(data);
        }
    }

    @Override
    public void close() {
        // YAML 不需要关闭
    }
}
