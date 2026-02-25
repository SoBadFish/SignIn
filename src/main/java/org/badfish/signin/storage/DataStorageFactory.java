package org.badfish.signin.storage;

import cn.nukkit.Server;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.storage.provider.DataStorageProvider;
import org.badfish.signin.storage.provider.DatabaseDataStorageProvider;
import org.badfish.signin.storage.provider.YamlDataStorageProvider;

/**
 * 数据存储工厂，根据配置创建对应的 DataStorage 实现
 */
public class DataStorageFactory {

    public static DataStorageProvider create(String storageType) {
        if ("database".equalsIgnoreCase(storageType)) {
            if (Server.getInstance().getPluginManager().getPlugin("YRDatabase") == null) {
                SignInMainClass.MAIN_INSTANCE.getLogger().warning(
                        "配置了 database 存储但未安装 YRDatabase 插件，回退到 yaml 存储"
                );
                return createYaml();
            }
            try {
                DataStorageProvider storage = new DatabaseDataStorageProvider();
                storage.init();
                SignInMainClass.MAIN_INSTANCE.getLogger().info("使用 YRDatabase 数据库存储");
                return storage;
            } catch (Exception e) {
                SignInMainClass.MAIN_INSTANCE.getLogger().warning(
                        "YRDatabase 初始化失败，回退到 yaml 存储: " + e.getMessage()
                );
                return createYaml();
            }
        }
        return createYaml();
    }

    private static DataStorageProvider createYaml() {
        DataStorageProvider storage = new YamlDataStorageProvider();
        storage.init();
        SignInMainClass.MAIN_INSTANCE.getLogger().info("使用 YAML 文件存储");
        return storage;
    }
}
