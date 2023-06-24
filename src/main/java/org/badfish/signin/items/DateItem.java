package org.badfish.signin.items;


import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.DateRewardData;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.data.SignInRewardData;

import org.badfish.signin.utils.ItemType;
import org.badfish.signin.utils.Tool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author BadFish
 */
public class DateItem {

    public final static String TAG = "Date_Item_";

    private int index;

    private Date date;

    private boolean isNow;

    private int day;

    public DateItem(int day, Date date, int index, boolean isNow){
        this.day = day;
        this.index = index;
        this.date = date;
        this.isNow = isNow;
    }

    public boolean isNow() {
        return isNow;
    }

    public int getIndex() {
        return index;
    }

    public Date getDate() {
        return date;
    }

    public int getDay() {
        return day;
    }

    private void addLore(ArrayList<CmdItem> cmdItems, ArrayList<String> r){
        for (CmdItem cmdItem : cmdItems) {
            r.add(TextFormat.colorize('&',"&r - "+cmdItem.getName()));
        }
    }

    public Item asItem(PlayerSignInData data){
        Calendar cal = new GregorianCalendar();
        boolean signIn = data.isSignIn(date);
        cal.setTime(new Date());
        int nowDay = cal.get(Calendar.DAY_OF_MONTH);
        CompoundTag tag = new CompoundTag();
        ArrayList<String> lore = new ArrayList<>();
        ArrayList<String> r = new ArrayList<>();
        DateRewardData dateRewardData = SignInMainClass.ITEM_REWARD_MANAGER.getDateRewardData(Tool.geMonth()+"-"+day);
        SignInRewardData signInRewardData = SignInMainClass.ITEM_REWARD_MANAGER.getRewardData(day);
        r.add(TextFormat.colorize('&',"&r&e签到奖励\n"));
        if(signInRewardData != null){
            addLoreByReward(r, signInRewardData.getCmdItems(), signInRewardData.getRoundItems(), null);
        }
        if(dateRewardData != null) {
            addLoreByReward(r, dateRewardData.getCmdItems(), dateRewardData.getRoundItems(), dateRewardData);
        }


        Item item = Item.fromString("35:1");
        switch (Tool.getSignType(day)){
            case LAST:
                item = Item.fromString("35:14");
                if(!signIn){
                    lore.add(TextFormat.colorize('&',"&r&c未签到"));
                    lore.add("");
                    lore.addAll(r);
                    lore.add("");
                    lore.add(TextFormat.colorize('&',"&r&e&l点击补签"));
                }
                break;
            case NEXT:
                if(!signIn) {
                    item = Item.fromString("35:8");
                    lore.add(TextFormat.colorize('&', "&r&c未签到"));
                    lore.add("");
                    lore.addAll(r);
                    lore.add("");
                    lore.add(TextFormat.colorize('&', "&r&c&l未到签到时间"));
                }
                break;
            default:
                if(!signIn){
                    lore.add(TextFormat.colorize('&',"&r&c未签到"));
                    lore.add("");
                    lore.addAll(r);
                    lore.add("");
                    lore.add(TextFormat.colorize('&',"&r&e&l点击签到"));
                }
                break;
        }
        if(signIn){
            item = Item.fromString("35:5");
            tag.putBoolean(TAG+"sign",true);
            lore.add(TextFormat.colorize('&',"&r&a已签到"));
            lore.add("");
        }

        tag.putInt(TAG+"day",day);
        String name = "&r&l&e"+(cal.get(Calendar.MONTH) + 1)+"月 "+day+"日";

        item.setNamedTag(tag);
        item.setCount(day);
        if(day == nowDay){
            item.addEnchantment(Enchantment.get(0));
        }
        return Tool.toSignItem(item,name,lore, ItemType.SIGN);

    }

    private void addLoreByReward(ArrayList<String> r, ArrayList<CmdItem> cmdItems, ArrayList<RoundItem> roundItems, DateRewardData dateRewardData) {
        if(dateRewardData != null){
            r.add(TextFormat.colorize('&',"&r"+ dateRewardData.getDisplayName()));
        }
        if(cmdItems.size() > 0){
            r.add(TextFormat.colorize('&', "&r&7可获得 &r"));
            addLore(cmdItems,r);
        }

        if(roundItems.size() > 0){
            r.add(TextFormat.colorize('&', "&r&7概率获得 &r"));
            for(RoundItem roundItem: roundItems){
                addLore(roundItem.getCmdItems(),r);
            }
        }
    }


}
