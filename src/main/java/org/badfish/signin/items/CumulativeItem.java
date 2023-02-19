package org.badfish.signin.items;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.BaseRewardData;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.utils.ItemType;
import org.badfish.signin.utils.Tool;

import java.util.ArrayList;


/**
 * @author BadFish
 */
public class CumulativeItem {

    public static Item toItem(Player player){
        PlayerSignInData playerSignInData = SignInMainClass.PLAYER_SIGN_IN_MANAGER.getPlayerData(player.getName());
        ArrayList<String> lore = new ArrayList<>();
        for(BaseRewardData cumurData: SignInMainClass.ITEM_REWARD_MANAGER.getPlayerAllCumulativeData(playerSignInData)){
            lore.add(TextFormat.colorize('&',"&r&2累计签到 &7"+cumurData.getDay()+" &2天"));
            for(CmdItem cmdItem: cumurData.getCmdItems()){
                lore.add(TextFormat.colorize('&',"&r - "+cmdItem.getName()));
            }
        }
        if(lore.isEmpty()){
            lore.add(TextFormat.colorize('&',"&r&c暂无奖励"));
        }
        return Tool.toSignItem(Item.get(54),"&r&l&e领取累计签到奖励",lore, ItemType.REWARD);
    }
}
