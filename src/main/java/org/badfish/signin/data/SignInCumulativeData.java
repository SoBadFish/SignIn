package org.badfish.signin.data;


import java.util.ArrayList;

/**
 * 签到奖励类
 * @author BadFish
 */
public class SignInCumulativeData extends BaseRewardData{


    public SignInCumulativeData(){
        super(-1,new ArrayList<>());
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SignInCumulativeData){
            return getDay() == ((SignInCumulativeData) obj).getDay();
        }
        return false;
    }

}
