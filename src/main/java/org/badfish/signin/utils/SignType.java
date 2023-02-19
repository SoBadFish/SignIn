package org.badfish.signin.utils;

/**
 * @author BadFish
 */
public enum  SignType {
    /**上一天* */
    LAST("last"),
    /**当天* */
    THIS("this"),
    /**下一天* */
    NEXT("next");

    protected String name;
    SignType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SignType get(String name){
        for(SignType type: SignType.values()){
            if(type.getName().equalsIgnoreCase(name)){
                return type;
            }
        }
        return null;
    }
}
