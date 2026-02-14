package org.badfish.signin.storage;

import com.yirankuma.yrdatabase.api.annotation.Column;
import com.yirankuma.yrdatabase.api.annotation.PrimaryKey;
import com.yirankuma.yrdatabase.api.annotation.Table;

/**
 * 签到数据库实体
 */
@Table("signin_data")
public class SignInEntity {

    @PrimaryKey("player_name")
    private String playerName;

    /** 签到日期列表，逗号分隔的 yyyy-MM-dd 字符串 */
    @Column(value = "sign_in_dates", type = "TEXT")
    private String signInDates;

    @Column("sign_month")
    private int signMonth;

    @Column("cumulative_count")
    private int cumulativeCount;

    @Column("retroactive_count")
    private int retroactiveCount;

    public SignInEntity() {
    }

    public SignInEntity(String playerName, String signInDates, int signMonth,
                        int cumulativeCount, int retroactiveCount) {
        this.playerName = playerName;
        this.signInDates = signInDates;
        this.signMonth = signMonth;
        this.cumulativeCount = cumulativeCount;
        this.retroactiveCount = retroactiveCount;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getSignInDates() {
        return signInDates;
    }

    public void setSignInDates(String signInDates) {
        this.signInDates = signInDates;
    }

    public int getSignMonth() {
        return signMonth;
    }

    public void setSignMonth(int signMonth) {
        this.signMonth = signMonth;
    }

    public int getCumulativeCount() {
        return cumulativeCount;
    }

    public void setCumulativeCount(int cumulativeCount) {
        this.cumulativeCount = cumulativeCount;
    }

    public int getRetroactiveCount() {
        return retroactiveCount;
    }

    public void setRetroactiveCount(int retroactiveCount) {
        this.retroactiveCount = retroactiveCount;
    }
}
