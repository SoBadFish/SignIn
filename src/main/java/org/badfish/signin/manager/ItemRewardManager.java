package org.badfish.signin.manager;

import cn.nukkit.utils.Config;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.data.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author BadFish
 */
public class ItemRewardManager {

    /**
     * 固定日期奖励
     * */
    private ArrayList<BaseRewardData> dateData;


    private ArrayList<BaseRewardData> cumulativeData;

    private ArrayList<BaseRewardData> signInRewardData;

    private ItemRewardManager(ArrayList<BaseRewardData> data,ArrayList<BaseRewardData> rewardData,ArrayList<BaseRewardData> dateData){
        this.cumulativeData = data;
        this.signInRewardData = rewardData;
        this.dateData = dateData;
    }

    public static ItemRewardManager managerCreate(Config config){
        ArrayList<BaseRewardData> cumulativeData = BaseRewardData.asList((Map) config.get("sign-reward-day"),new SignInCumulativeData());
        ArrayList<BaseRewardData> signInRewardData = BaseRewardData.asList((Map) config.get("sign-reward"),new SignInRewardData());
        ArrayList<BaseRewardData> dateData = new ArrayList<>();
        if(config.exists("date-reward-sign")) {
            dateData = DateRewardData.asList((Map) config.get("date-reward-sign"), new DateRewardData());
        }

        return new ItemRewardManager(cumulativeData,signInRewardData,dateData);
    }
    /**
     * 获取玩家可领取的所有累计奖励
     *
     * 计算累计签到
     * */
    public ArrayList<BaseRewardData> getPlayerAllCumulativeData(PlayerSignInData signInData){
        ArrayList<BaseRewardData> rewardData = new ArrayList<>();
        for(BaseRewardData cumurData: SignInMainClass.ITEM_REWARD_MANAGER.getCumulativeData()){
            if(cumurData.getDay() <= signInData.getCount()
                    && cumurData.getDay() > signInData.getCumulativeCount()
                    && signInData.getCount() > signInData.getCumulativeCount()){
                rewardData.add(cumurData);
            }
        }
        return rewardData;
    }

    public SignInCumulativeData getCumulativeData(int day){
        SignInCumulativeData cumulativeData = new SignInCumulativeData();
        cumulativeData.setDay(day);
        if(this.cumulativeData.contains(cumulativeData)){
            return (SignInCumulativeData) this.cumulativeData.get(this.cumulativeData.indexOf(cumulativeData));
        }
        return null;
    }


    public ArrayList<BaseRewardData> getCumulativeData() {
        return cumulativeData;
    }

    public ArrayList<BaseRewardData> getSignInRewardData() {
        return signInRewardData;
    }

    public SignInRewardData getRewardData(int day){
        SignInRewardData cumulativeData = new SignInRewardData();
        cumulativeData.setDay(day);
        if(this.signInRewardData.contains(cumulativeData)){
            return (SignInRewardData) this.signInRewardData.get(this.signInRewardData.indexOf(cumulativeData));
        }
        cumulativeData.setDay(-1);
        if(this.signInRewardData.contains(cumulativeData)) {
            return (SignInRewardData) this.signInRewardData.get(this.signInRewardData.indexOf(cumulativeData));
        }


        return null;
    }
    public DateRewardData getDateRewardData(String time){
        DateRewardData cumulativeData = new DateRewardData();
        cumulativeData.setTime(time);
        if(this.dateData.contains(cumulativeData)){
            return (DateRewardData) this.dateData.get(this.dateData.indexOf(cumulativeData));
        }
        return null;
    }

    @Override
    public String toString() {
        return "date: "+dateData.toString()+"\ncumulativeData"
                +cumulativeData.toString()
                +"\nsignInRewardData: "+signInRewardData.toString();
    }
}
