package org.badfish.signin.items;


import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * 本类用于概率奖励
 * @author BadFish
 */
public class RoundItem {

    private double round;

    private ArrayList<CmdItem> cmdItems;

    public RoundItem(double round,ArrayList<CmdItem> cmdItems){
        this.round = round;
        this.cmdItems = cmdItems;
    }



    public ArrayList<CmdItem> getCmdItems() {
        return cmdItems;
    }

    public boolean hasPull(){
        return ThreadLocalRandom.current().nextDouble() < round;
    }

}
