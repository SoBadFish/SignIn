package org.badfish.signin.panel;

import cn.nukkit.Player;

import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.items.CumulativeItem;
import org.badfish.signin.items.DateItem;
import org.badfish.signin.utils.Tool;


import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author BadFish
 */
public class DisplayPanel implements InventoryHolder {


    private static final int ITEM_INDEX = 36;

    private static final int LINE_SIZE = 9;



    /**
     * 画主页面
     * @param player 玩家
     * */
    public static Map<Integer,Item> getPanel(Player player){
        //根据玩家设备适配不同的GUI
        Map<Integer,Item> panel = new LinkedHashMap<>();
        int line = 1;
        if (player.getLoginChainData().getDeviceOS() == 7) {
            //Win10 感谢Lt_Name大佬的提供
            for(int i = 0; i < InventoryType.DOUBLE_CHEST.getDefaultSize(); i++){
                if(i == 0){
                    panel.put(i, Tool.getBorderItem(player));
                    continue;
                }
                if(i % 9 == 0){
                    panel.put(i, Tool.getBorderItem(player,Item.get(321)));
                }
                if(i % (9 * line - 1) == 0){
                    line++;
                    panel.put(i, Tool.getBorderItem(player,Item.get(321)));
                }
            }
        }else{
            panel.put(53, Tool.getBorderItem(player));
        }


        return panel;
    }

    /**
     * 获取签到页面
     * @param player 玩家
     * */
    public static Map<Integer,Item> getDatePanel(Player player){
        Map<Integer,Item> panel = getPanel(player);
        //获取日期
        for(DateItem dateItem: Tool.getIndexByDay(new Date(),player.getLoginChainData().getDeviceOS() == 7)){
            panel.put(dateItem.getIndex(),dateItem.asItem(SignInMainClass.PLAYER_SIGN_IN_MANAGER.getPlayerData(player.getName())));
        }
        //连续签到奖励实现
        //TODO 累计签到奖励
        if(player.getLoginChainData().getDeviceOS() == 7) {
            panel.put(35, CumulativeItem.toItem(player));
        }else{
            panel.put(52, CumulativeItem.toItem(player));
        }
        return panel;
    }



    public void sendMothPanel(Player player){
        Map<Integer,Item> panel = getDatePanel(player);
        int mon = Tool.geMonth();
        displayPlayer(player,panel, TextFormat.colorize('&',"&b&l"+mon+"月签到"));
    }






    public void displayPlayer(Player player,Map<Integer, Item> itemMap,String name){
        ChestInventoryPanel panel = new ChestInventoryPanel(this,name);
        panel.setContents(itemMap);
        panel.id = Entity.entityCount++;
        player.addWindow(panel);
    }


    @Override
    public Inventory getInventory() {
        return null;
    }
}
