package org.badfish.signin;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLocallyInitializedEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;
import org.badfish.signin.data.BaseRewardData;
import org.badfish.signin.data.DateRewardData;
import org.badfish.signin.data.PlayerSignInData;
import org.badfish.signin.data.SignInRewardData;
import org.badfish.signin.items.CmdItem;
import org.badfish.signin.items.RoundItem;
import org.badfish.signin.panel.ChestInventoryPanel;
import org.badfish.signin.panel.DisplayPanel;
import com.nukkitx.fakeinventories.inventory.FakeInventory;
import org.badfish.signin.items.DateItem;
import org.badfish.signin.utils.ItemType;
import org.badfish.signin.utils.Tool;
import cn.nukkit.scheduler.AsyncTask;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author BadFish
 */
public class SignInListener implements Listener {

    private final ArrayList<Player> isLogin = new ArrayList<>();

    @EventHandler
    public void onJoinCheck(PlayerLocallyInitializedEvent event){
        //玩家真正的进服
        if(isLogin.contains(event.getPlayer())){
            isLogin.remove(event.getPlayer());
            Player player = event.getPlayer();
            // 异步加载数据，避免阻塞主线程；加载完成后通过 putPlayerData 标记待验证，首次签到前会重新从 DB 拉取最新数据
            Server.getInstance().getScheduler().scheduleAsyncTask(SignInMainClass.MAIN_INSTANCE, new AsyncTask() {
                PlayerSignInData data;
                @Override
                public void onRun() {
                    data = SignInMainClass.DATA_STORAGE.load(player.getName());
                    if (data == null) data = new PlayerSignInData(player.getName());
                    if (data.getSignMonth() == -1) data.setSignMonth(Tool.geMonth());
                    if (data.getSignMonth() != Tool.geMonth()) data.reset();
                }
                @Override
                public void onCompletion(Server server) {
                    if (!player.isOnline()) return;
                    SignInMainClass.PLAYER_SIGN_IN_MANAGER.putPlayerData(player.getName(), data);
                    if (!data.isSignIn(new Date())) {
                        new DisplayPanel().sendMothPanel(player);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(SignInMainClass.MAIN_INSTANCE.getConfig().getBoolean("join-display",true)) {
            isLogin.add(event.getPlayer());
        }
    }

    @EventHandler
    public void onItemChange(InventoryTransactionEvent event) {
        InventoryTransaction transaction = event.getTransaction();
        for (InventoryAction action : transaction.getActions()) {
            for (Inventory inventory : transaction.getInventories()) {
                if (inventory instanceof ChestInventoryPanel || inventory instanceof FakeInventory) {
                    event.setCancelled();
                    for (Player player : inventory.getViewers()) {
                        Item item = action.getSourceItem();
                        if (item.hasCompoundTag() && item.getNamedTag().contains(DateItem.TAG + "tag")) {
                            ItemType type = Tool.getItemType(item);
                            if(type != null){
                                PlayerSignInData signInData = SignInMainClass.PLAYER_SIGN_IN_MANAGER.getPlayerData(player.getName());
                                switch (type){
                                    case SIGN:
                                        int day = item.getNamedTag().getInt(DateItem.TAG+"day");
                                        switch (Tool.getSignType(day)){
                                            case THIS:
                                                if(item.getNamedTag().getBoolean(DateItem.TAG+"sign")){
                                                    player.sendMessage(TextFormat.colorize('&',"&2您已经签到过了"));
                                                } else if (SignInMainClass.PLAYER_SIGN_IN_MANAGER.needsVerification(player.getName())) {
                                                    // 从其他子服切换过来的首次签到，异步从 DB 拉取最新数据验证，防止重复签到
                                                    SignInMainClass.PLAYER_SIGN_IN_MANAGER.markVerified(player.getName());
                                                    final int verifyDay = day;
                                                    final Inventory verifyInventory = inventory;
                                                    Server.getInstance().getScheduler().scheduleAsyncTask(SignInMainClass.MAIN_INSTANCE, new AsyncTask() {
                                                        PlayerSignInData fresh;
                                                        @Override
                                                        public void onRun() {
                                                            fresh = SignInMainClass.DATA_STORAGE.load(player.getName());
                                                        }
                                                        @Override
                                                        public void onCompletion(Server server) {
                                                            if (!player.isOnline()) return;
                                                            PlayerSignInData current = SignInMainClass.PLAYER_SIGN_IN_MANAGER.getPlayerData(player.getName());
                                                            // 若本地缓存已签到（如快速重复点击），只刷新面板
                                                            if (current.isSignIn(Tool.getDateByDay(verifyDay))) {
                                                                verifyInventory.setContents(DisplayPanel.getDatePanel(player));
                                                                return;
                                                            }
                                                            // 用 DB 最新数据覆盖缓存
                                                            if (fresh != null) {
                                                                SignInMainClass.PLAYER_SIGN_IN_MANAGER.refreshCache(player.getName(), fresh);
                                                                current = fresh;
                                                            }
                                                            if (current.isSignIn(Tool.getDateByDay(verifyDay))) {
                                                                // 其他子服已签到
                                                                player.sendMessage(TextFormat.colorize('&', "&2您已经签到过了"));
                                                                verifyInventory.setContents(DisplayPanel.getDatePanel(player));
                                                            } else {
                                                                signIn(player, current, verifyInventory, verifyDay);
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    signIn(player,signInData,inventory,day);
                                                }
                                                break;
                                            case NEXT:
                                                player.sendMessage(TextFormat.colorize('&',"&c还未到签到时间 别急"));
                                                break;
                                            case LAST:
                                                if(item.getNamedTag().getBoolean(DateItem.TAG+"sign")){
                                                    player.sendMessage(TextFormat.colorize('&',"&2您已经签到过了"));
                                                }else {
                                                    if (signInData.getRetroactiveCount() > 0) {
                                                        player.sendMessage(TextFormat.colorize('&', "补签成功 你消耗了 &21&r 张补签卡"));
                                                        signInData.setRetroactiveCount(signInData.getRetroactiveCount() - 1);
                                                        signIn(player, signInData, inventory, day);
                                                    } else {
                                                        player.sendMessage(TextFormat.colorize('&', "&c你没有补签卡"));
                                                    }
                                                }
                                                break;
                                                default:break;
                                        }

                                        break;
                                    case REWARD:
                                        ArrayList<BaseRewardData> arrayList = SignInMainClass.ITEM_REWARD_MANAGER.getPlayerAllCumulativeData(signInData);
                                        int maxCount = 0;
                                        if(!arrayList.isEmpty()){
                                            for(BaseRewardData data: arrayList){
                                                if(data.getDay() > maxCount){
                                                    maxCount = data.getDay();
                                                }
                                                putPlayer(data,player);
                                            }
                                        }
                                        if(maxCount > 0 && maxCount > signInData.getCumulativeCount()){
                                            signInData.setCumulativeCount(maxCount);
                                            signInData.saveAsync();
                                        }
                                        //刷新布局
                                        inventory.setContents(DisplayPanel.getDatePanel(player));
                                        break;
                                        default:break;
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private void signIn(Player player, PlayerSignInData signInData, Inventory inventory, int day){
        player.getLevel().addSound(player.getPosition(), Sound.RANDOM_ORB,1,1,player);
        signInData.addSign(Tool.getDateByDay(day));
        inventory.setContents(DisplayPanel.getDatePanel(player));
        player.sendMessage(TextFormat.colorize('&',"&a恭喜您 签到成功!"));
        SignInRewardData signInRewardData = SignInMainClass.ITEM_REWARD_MANAGER.getRewardData(day);
        if(signInRewardData != null){
            putPlayer(signInRewardData,player);
        }
        DateRewardData dateRewardData = SignInMainClass.ITEM_REWARD_MANAGER.getDateRewardData(Tool.geMonth()+"-"+day);
        if(dateRewardData != null){
            player.sendMessage(TextFormat.colorize('&',"&r"+dateRewardData.getDisplayName()+" &r&e奖励"));
            putPlayer(dateRewardData,player);
        }
        signInData.saveAsync();
    }

    /**
     * 给予玩家奖励
     * */
    private void putPlayer(BaseRewardData signInRewardData, Player player){

        for(CmdItem cmdItem: signInRewardData.getCmdItems()){
            player.sendMessage(TextFormat.colorize('&',"&e获得: &r"+cmdItem.getName()));
            Server.getInstance().getCommandMap().dispatch(new ConsoleCommandSender(),cmdItem.getCmd().replace("@p",player.getName()));
        }
        for(RoundItem roundItem: signInRewardData.getRoundItems()){
            if(roundItem.hasPull()){
                for(CmdItem cmdItem: roundItem.getCmdItems()){
                    player.sendMessage(TextFormat.colorize('&',"&d额外获得: &r"+cmdItem.getName()));
                    Server.getInstance().getCommandMap().dispatch(new ConsoleCommandSender(),cmdItem.getCmd().replace("@p",player.getName()));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        isLogin.remove(e.getPlayer());
        String playerName = e.getPlayer().getName();
        PlayerSignInData signInData = SignInMainClass.PLAYER_SIGN_IN_MANAGER.getPlayerData(playerName);
        signInData.saveAsync();
        SignInMainClass.PLAYER_SIGN_IN_MANAGER.removePlayerData(playerName);
    }
}
