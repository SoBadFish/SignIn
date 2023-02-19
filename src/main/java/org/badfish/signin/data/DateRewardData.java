package org.badfish.signin.data;


import java.util.ArrayList;
import java.util.Map;

/**
 * 固定日期的奖励
 * @author BadFish
 */
public class DateRewardData extends BaseRewardData{

    private String time;

    private String displayName;



    public DateRewardData() {
        super(-1, new ArrayList<>());
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static ArrayList<BaseRewardData> asList(Map map , BaseRewardData c){
        ArrayList<BaseRewardData> data = new ArrayList<>();
        for(Object o: map.keySet()){
           BaseRewardData rewardData = asBaseRewardData(map, o.toString(),c);
           Map m1 = (Map) map.get(o);
           if(rewardData instanceof DateRewardData){
               ((DateRewardData) rewardData).setTime(o.toString());
               if(m1.containsKey("display")) {
                   ((DateRewardData) rewardData).setDisplayName(m1.get("display").toString());
               }else{
                   ((DateRewardData) rewardData).setDisplayName(null);
               }
               data.add(rewardData);
           }
        }
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DateRewardData){
            return getTime().equalsIgnoreCase(((DateRewardData) obj).getTime());
        }
        return false;
    }
}
