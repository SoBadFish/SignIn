package org.badfish.signin.data;


import java.util.ArrayList;


/**
 * @author BadFish
 */
public class SignInRewardData extends BaseRewardData{


    public SignInRewardData(){
        super(-1,new ArrayList<>());
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SignInRewardData){
            return this.getDay() == ((SignInRewardData) obj).getDay();
        }
        return false;
    }
}
