package org.badfish.signin.proxy;

import cn.nukkit.item.Item;

import java.util.LinkedHashMap;

/**
 * 由于pnx与其他版本的羊毛id不同
 * @author Sobadfish
 * @date 2023/4/1
 */
public class ItemProxy {

    public static final LinkedHashMap<String,String> DICT = new LinkedHashMap<>();

    public static void init(){
        DICT.put("35:1","-557");
        DICT.put("35:14","-556");
        DICT.put("35:8","-552");
        DICT.put("35:5","-559");

    }


    public static Item getItem(String id){
        //检查核心是否为PNX
        boolean isPnx = false;
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("CODENAME");
            isPnx = true;
        } catch (ClassNotFoundException | NoSuchFieldException ignore) {
        }

        if(isPnx && DICT.containsKey(id)){
            return Item.fromString(DICT.get(id));
        }
        return Item.fromString(id);
    }
}
