package org.badfish.signin.data;

import org.badfish.signin.items.CmdItem;
import org.badfish.signin.items.RoundItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author BadFish
 */
public abstract class BaseRewardData implements Cloneable{

    private static final String REWARD_VALUE = "rewards";

    private static final String REWARD_OTHER_VALUE = "other-rewards";

    private int day;



    private ArrayList<RoundItem> roundItems;

    private ArrayList<CmdItem> cmdItems;



    public BaseRewardData(int day,ArrayList<CmdItem> cmdItems){
        this.day = day;
        this.cmdItems = cmdItems;
    }

    public int getDay() {
        return day;
    }

    public void setRoundItems(ArrayList<RoundItem> roundItems) {
        this.roundItems = roundItems;
    }

    public ArrayList<RoundItem> getRoundItems() {
        return roundItems;
    }

    public ArrayList<CmdItem> getCmdItems() {
        return cmdItems;
    }

    static BaseRewardData asBaseRewardData(Map map, String id, BaseRewardData c){

        CmdItem item;
        RoundItem roundItem;
        ArrayList<RoundItem> roundItems;
        ArrayList<CmdItem> cmdItems;
        BaseRewardData c1;
        CmdItem cache;
        ArrayList<CmdItem> cmdItemsCache;
        c1 = c.clone();
        cmdItems = new ArrayList<>();
        roundItems = new ArrayList<>();
        //日期

        Map map1 = (Map) map.get(id);
        if(map1.containsKey(REWARD_VALUE)) {
            for (Object o1 : (List) map1.get(REWARD_VALUE)) {
                try {
                    item = new CmdItem(o1.toString().split("&")[1],
                            o1.toString().split("&")[0]);
                } catch (Exception e) {
                    continue;
                }
                cmdItems.add(item);
            }
        }
        if(map1.containsKey(REWARD_OTHER_VALUE)) {
            Map map2 = (Map) map1.get(REWARD_OTHER_VALUE);

            for (Object oround : map2.keySet()) {
                cmdItemsCache = new ArrayList<>();
                for (Object o1 : (List) map2.get(oround)) {
                    try {
                        cache = new CmdItem(o1.toString().split("&")[1],
                                o1.toString().split("&")[0]);
                    } catch (Exception e) {
                        continue;
                    }
                    cmdItemsCache.add(cache);
                }
                try {
                    roundItem = new RoundItem(Double.parseDouble(oround.toString()), cmdItemsCache);
                } catch (Exception ignore) {
                    continue;
                }
                roundItems.add(roundItem);
            }
        }
//
        int d = -1;
        try{
            d = Integer.parseInt(id);
        }catch (Exception ignore){}
        c1.setRoundItems(roundItems);
        c1.setDay(d);
        c1.setCmdItems(cmdItems);
        return c1;
    }

    public static ArrayList<BaseRewardData> asList(Map map , BaseRewardData c){
        ArrayList<BaseRewardData> data = new ArrayList<>();
        for(Object o: map.keySet()){
            data.add(asBaseRewardData(map, o.toString(),c));
        }
        return data;
    }



    @Override
    public String toString() {
        return "day: "+day+" cmd:"+cmdItems;
    }

    @Override
    public BaseRewardData clone()  {
        try {
            return (BaseRewardData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setCmdItems(ArrayList<CmdItem> cmdItems) {
        this.cmdItems = cmdItems;
    }
}
