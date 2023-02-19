package org.badfish.signin.items;


/**
 * @author BadFish
 */
public class CmdItem {
    private String name;

    private String cmd;

    public CmdItem(String name,String cmd){
        this.name = name;
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "name: "+name+" cmd:"+cmd;
    }

    public String getName() {
        return name;
    }

    public String getCmd() {
        return cmd;
    }
}
