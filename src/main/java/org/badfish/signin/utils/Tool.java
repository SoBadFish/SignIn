package org.badfish.signin.utils;



import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.items.DateItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author BadFish
 */
public class Tool {



    public static ArrayList<DateItem> getIndexByDay(Date date,boolean win10){
        int index = 1;
        int line = 1;
        ArrayList<DateItem> dateItems = new ArrayList<>();
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        int nowDate = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH,1);
        int dayOfWeek =cal.get(Calendar.DAY_OF_WEEK);
        for(int i =1;i<dayOfWeek;i++) {
            index++;
        }
        int maxDay=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for(int i=1;i <= maxDay;i++) {
            if(win10){
                if(index % ((9 * line) - 1) == 0) {
                    line++;
                    index++;
                }
                if(index % 9 == 0){
                    index ++;
                }

                dateItems.add(new DateItem(i,cal.getTime(),index,i == nowDate));
                cal.add(Calendar.DAY_OF_MONTH, 1);
                index++;
            }else{
                dateItems.add(new DateItem(i,cal.getTime(),index,i == nowDate));
                cal.add(Calendar.DAY_OF_MONTH, 1);
                index++;
            }
        }
        return dateItems;

    }

    /**
     * 获取当前月份与日期
     */
    public static String geMonthAndDay(){
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        return (cal.get(Calendar.MONTH) + 1)+"-"+(cal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 获取当前月份
     */
    public static int geMonth(){
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        return cal.get(Calendar.MONTH) + 1;
    }
    /**
     * 获取月份
     */
    public static int geMonthByString(String date){
        return Integer.parseInt(date.split("-")[1]);

    }

    /**
     * 根据日期获取Date
     * */
    public static Date getDateByDay(int day){
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH,day);
        return cal.getTime();
    }

    public static SignType getSignType(int day){
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        if(day > cal.get(Calendar.DAY_OF_MONTH)){
            return SignType.NEXT;
        }else if(day < cal.get(Calendar.DAY_OF_MONTH)){
            return SignType.LAST;
        }
        return SignType.THIS;
    }

    public static String getDateToString(Date date){
        SimpleDateFormat lsdStrFormat = new SimpleDateFormat("yyyy-MM-dd");
        return lsdStrFormat.format(date);
    }
    public static Item getBorderItem(Player player,Item item){
        Calendar cal = new GregorianCalendar();
        PlayerSignInData playerSignInData = SignInMainClass.PLAYER_SIGN_IN_MANAGER.getPlayerData(player.getName());
        cal.setTime(new Date());
        int mon = cal.get(Calendar.MONTH) + 1;
        ArrayList<String> lore = new ArrayList<>();
        for(String m:SignInMainClass.MAIN_INSTANCE.getBoderMessage()){
            lore.add(TextFormat.colorize('&',m
                    .replace("{mon}",mon+"")
                    .replace("{count}",playerSignInData.getCount()+"")
                    .replace("{r}",playerSignInData.getRetroactiveCount()+"")));
        }

        return toSignItem(item,"&r签到统计",lore,ItemType.NOT);
    }

    public static Item getBorderItem(Player player){
        return getBorderItem(player,Item.get(426));
    }

    public static Item toSignItem(Item item, String name, ArrayList<String> lore, ItemType type){
        if(name != null) {
            item.setCustomName(TextFormat.colorize('&', name));
        }
        CompoundTag tag = item.getNamedTag();
        if(tag == null){
            tag = new CompoundTag();
        }
        tag.putString(DateItem.TAG + "tag", "DateItem");
        tag.putString(DateItem.TAG + "type", type.getName());
        item.setNamedTag(tag);
        item.setLore(lore.toArray(new String[0]));
        return item;
    }

    public static ItemType getItemType(Item item){
        if(item.hasCompoundTag() && item.getNamedTag().contains(DateItem.TAG + "tag")){
            String type = item.getNamedTag().getString(DateItem.TAG+"type");
            return ItemType.get(type);
        }
        return null;
    }

    public static String[] getDefaultFiles(String fileName) {
        List<String> names = new ArrayList<>();
        File files = new File(SignInMainClass.MAIN_INSTANCE.getDataFolder()+ "/"+fileName);
        if(files.isDirectory()){
            File[] filesArray = files.listFiles();
            if(filesArray != null){
                if(filesArray.length > 0){
                    for(File file : filesArray){
                        String name = file.getName();
                        if(name.split("\\.").length > 1) {
                            names.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                        }
                    }
                }
            }
        }
        return names.toArray(new String[0]);
    }
}
