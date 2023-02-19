package org.badfish.signin.data;


import cn.nukkit.utils.Config;
import org.badfish.signin.SignInMainClass;
import org.badfish.signin.utils.Tool;

import java.util.ArrayList;
import java.util.Date;

/**
 * 本类用于记录玩家签到信息
 * @author BadFish
 */
public class PlayerSignInData {

    private int signMonth = -1;

    private String playerName;

    /**签到时间*/
    private ArrayList<String> signIn = new ArrayList<>();

    /**累计签到*/
    private int cumulativeCount = 0;

    /**补签卡*/
    private int retroactiveCount = 0;



    public PlayerSignInData(String playerName,ArrayList<String> signIn){
        this.playerName = playerName;
        this.signIn = signIn;

    }

    public void setSignIn(ArrayList<String> signIn) {
        this.signIn = signIn;
    }


    public void addRetroactiveCount(int count){
        retroactiveCount += count;
    }

    public int getCount(){
        return getThisMonthSignInData().size();
    }

    public PlayerSignInData(String playerName){
        this.playerName = playerName;
    }

    public boolean isSignIn(Date date){
        return signIn.contains(Tool.getDateToString(date));
    }

    public int getSignMonth() {
        return signMonth;
    }

    public void setSignMonth(int signMonth) {
        this.signMonth = signMonth;
    }

    public void addSign(Date date){
        if(signMonth == -1){
            signMonth = Tool.geMonth();
        }
        if(signMonth != Tool.geMonth()){
            reset();
        }
        signIn.add(Tool.getDateToString(date));
    }

    public void setRetroactiveCount(int retroactiveCount) {
        this.retroactiveCount = retroactiveCount;
    }

    /**
     * 获取补签卡数量
     * */
    public int getRetroactiveCount() {
        return retroactiveCount;
    }

    public void reset(){
        setCumulativeCount(0);
        setSignMonth(Tool.geMonth());
        save();
    }

    private ArrayList<String> getThisMonthSignInData(){
        if(getSignMonth() != Tool.geMonth()){
            setSignMonth(Tool.geMonth());
        }
        ArrayList<String> strings = new ArrayList<>();
        for(String s: signIn){
            if(Tool.geMonthByString(s) == getSignMonth()){
                strings.add(s);
            }
        }
        return strings;
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlayerSignInData){
            return ((PlayerSignInData) obj).playerName.equals(playerName);
        }
        return false;
    }

    /**
     * 累计签到次数
     * */
    public int getCumulativeCount() {
        return cumulativeCount;
    }

    public void setCumulativeCount(int cumulativeCount) {
        this.cumulativeCount = cumulativeCount;
    }

    public void save(){
        Config config = new Config(SignInMainClass.MAIN_INSTANCE.getDataFolder()+"/players/"+playerName+".yml",Config.YAML);
        config.set("signInDate",signIn);
        config.set("signMonth",signMonth);
        config.set("cumulativeCount",cumulativeCount);
        config.set("retroactiveCount",retroactiveCount);
        config.save();
    }
}
