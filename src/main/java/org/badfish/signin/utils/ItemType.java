package org.badfish.signin.utils;

/**
 * @author BadFish
 */
public enum ItemType {
    /**无* */
    NOT("not"),
    /**奖励* */
    REWARD("reward"),
    /**签到* */
    SIGN("sign");

    protected String name;
    ItemType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ItemType get(String name){
        for(ItemType type: ItemType.values()){
            if(type.getName().equalsIgnoreCase(name)){
                return type;
            }
        }
        return null;
    }
}
