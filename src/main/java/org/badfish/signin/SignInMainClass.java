package org.badfish.signin;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.manager.ItemRewardManager;
import org.badfish.signin.manager.PlayerSignManager;
import org.badfish.signin.panel.DisplayPanel;
import org.badfish.signin.panel.lib.AbstractFakeInventory;

import org.badfish.signin.utils.CommandType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author BadFish
 */
public class SignInMainClass extends PluginBase {


    public static SignInMainClass MAIN_INSTANCE;

    public static ItemRewardManager ITEM_REWARD_MANAGER;

    public static PlayerSignManager PLAYER_SIGN_IN_MANAGER;




    @Override
    public void onEnable() {
        MAIN_INSTANCE = this;
        this.getLogger().info("正在加载签到插件");
        //检查核心兼容
        checkServer();
        loadConfig();

        this.getLogger().info("签到插件加载完成 by 某吃瓜咸鱼");
        this.getLogger().info("本插件完全免费 如果你是购买的，那你就被坑了");
        this.getServer().getPluginManager().registerEvents(new SignInListener(),this);
    }

    private void checkServer(){
        boolean ver = false;
        //双核心兼容
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT_PM1E");
            ver = true;

        } catch (ClassNotFoundException | NoSuchFieldException ignore) { }
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            "Nukkit PetteriM1 Edition".equalsIgnoreCase(c.getField("NUKKIT").get(c).toString());
            ver = true;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignore) {
        }

        AbstractFakeInventory.IS_PM1E = ver;
        if(ver){
            this.getLogger().info("当前插件运行在: Nukkit PM1E 核心上");
        }else{
            this.getLogger().info("当前插件运行在: Nukkit 核心上");
        }
    }


    public ArrayList<String> getBoderMessage(){
        return (ArrayList<String>) getConfig().getStringList("border-lore");
    }

    private void loadConfig(){
        this.saveDefaultConfig();
        this.reloadConfig();
        ITEM_REWARD_MANAGER = ItemRewardManager.managerCreate(getConfig());
        PLAYER_SIGN_IN_MANAGER = PlayerSignManager.managerCreate();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(args.length > 0){
                if(sender.isOp()) {
                    CommandType type = CommandType.get(args[0].toLowerCase());
                    if (type != null) {
                        switch (type) {
                            case RELOAD:
                                for (PlayerSignInData signInData : PLAYER_SIGN_IN_MANAGER.getPlayerSignInData()) {
                                    signInData.save();
                                }
                                loadConfig();
                                sender.sendMessage("配置重启完成");
                                break;
                            case GIVE:
                                if(args.length > 2){
                                    PlayerSignInData signInData;
                                    String playerName = args[1];
                                    String send = playerName;

                                    String c = args[2];
                                    int count = 1;
                                    try {
                                        count = Integer.parseInt(c);
                                    }catch (Exception ignore){}
                                    LinkedList<Player> players = new LinkedList<>();
                                    if (playerName.startsWith("@p")){
                                        send = "自己";
                                        //直接就是自己呗
                                        players.add((Player) sender);
                                    }else if(playerName.startsWith("@r")){

                                        ArrayList<Player> rp = new ArrayList<>(Server.getInstance().getOnlinePlayers().values());
                                        Player r = rp.get(new Random().nextInt(rp.size()));
                                        send = "随机一名玩家("+r.getName()+")";
                                        players.add(r);

                                    }else if(playerName.startsWith("@a")){
                                        send = "全体玩家";
                                        players.addAll(Server.getInstance().getOnlinePlayers().values());
                                    }else{
                                        Player p = Server.getInstance().getPlayer(playerName);
                                        if(p != null){
                                            players.add(p);
                                        }

                                    }
                                    for(Player player: players){
                                        if(player != null){
                                            playerName = player.getName();
                                            player.sendMessage(TextFormat.colorize('&',"&7你获得了 &2"+count+" &7张补签卡"));
                                        }
                                        signInData = PLAYER_SIGN_IN_MANAGER.getPlayerData(playerName);
                                        signInData.addRetroactiveCount(count);

                                    }
                                    sender.sendMessage(TextFormat.colorize('&',"&7成功给予玩家 &e"+send+" &2"+count+" &7张补签卡"));




                                }else{
                                    sender.sendMessage("请执行/qd help 查看帮助");
                                }
                                break;
                            case HELP:
                                sender.sendMessage("/qd help 帮助指令");
                                sender.sendMessage("/qd give <player> <count> 给予玩家补签卡");
                                sender.sendMessage("/qd reset 重置所有玩家签到数据");
                                sender.sendMessage("/qd reload 重载配置文件");
                                break;
                            case RESET:
                                for (PlayerSignInData signInData : PLAYER_SIGN_IN_MANAGER.getPlayerSignInData()) {
                                    signInData.reset();
                                }
                                sender.sendMessage("重置完成");
                                break;
                            default:
                                break;
                        }
                        return true;
                    } else {
                        return true;
                    }
                }
            }
            DisplayPanel p = new DisplayPanel();
            p.sendMothPanel((Player) sender);
        }
        return true;
    }




}
