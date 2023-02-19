package org.badfish.signin.items;


import java.util.ArrayList;


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

    /**计算小数位*/
    private int mathRoundLine(double round){
        int n = 0;
        int i = (int)round;
        while((round - i)>0) {
            round = round*10;
            i = (int)round;
            n++;
        }
        return n;
    }
    public boolean hasPull(){
        int r = (int) (1 + Math.random() * Math.pow(10, mathRoundLine(round)));
        int r2 = (int) (round * Math.pow(10, mathRoundLine(round)));
        return r <= r2;
    }

}
