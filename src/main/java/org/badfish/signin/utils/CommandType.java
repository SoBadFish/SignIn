package org.badfish.signin.utils;

/**
 * @author BadFish
 */
public enum CommandType {
    /**帮助* */
    HELP("help"),
    /**重置* */
    RESET("reset"),
    /**给予补签卡* */
    GIVE("give"),
    /**重载配置* */
    RELOAD("reload");

    protected String name;
    CommandType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CommandType get(String name){
        for(CommandType type: CommandType.values()){
            if(type.getName().equalsIgnoreCase(name)){
                return type;
            }
        }
        return null;
    }
}
