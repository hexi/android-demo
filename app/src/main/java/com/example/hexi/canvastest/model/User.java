package com.example.hexi.canvastest.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by burizado on 14-12-12.
 */
public class User {

    public boolean canBind = false;
    public boolean canChange = false;
    public String cusUniqueId = "";
    public boolean hasBindCurrRoom = false;
    public boolean hasPhone = false;
    public String imgUrl = "";
    public boolean isBozhu = false;
    public boolean isExpert = false;
    public String loginPlatform = "";
    public int needCnt = -1;
    public boolean nextCanChange = false;
    public String nickname = "";
    public boolean online = false;
    public int scoreCnt = -1;
    public int serverId = 1;
    public int uid = -1;
    public int userStatus = -1;
    public int userType = 0;
    public String username = "";
    public String tradeAccount = "";

    private long cusId;

    @SerializedName("YG_PMEC_ownerCsrId")
    private long csrIdOfYG = -1;
    @SerializedName("YG_PMEC_ownerCsrAvatar")
    private String csrAvatarOfYG;
    @SerializedName("YG_PMEC_ownerCsrNickname")
    private String csrNicknameOfYG;
    @SerializedName("TT_TPME_ownerCsrId")
    private long csrIdOfTT = -1;
    @SerializedName("TT_TPME_ownerCsrAvatar")
    private String csrAvatarOfTT;
    @SerializedName("TT_TPME_ownerCsrNickname")
    private String csrNicknameOfTT;

    public HXCsr getCsr() {
        if (serverId == Server.TT.serverId) {
            return new HXCsr(csrIdOfTT, csrAvatarOfTT, csrNicknameOfTT);
        } else {
            return new HXCsr(csrIdOfYG, csrAvatarOfYG, csrNicknameOfYG);
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean canBind() {
        return canBind;
    }

    public boolean canChange() {
        return canChange;
    }

    public String getCusUniqueId() {
        return cusUniqueId;
    }

    public void setCusUniqueId(String cusUniqueId) {
        this.cusUniqueId = cusUniqueId;
    }

    public boolean hasBindCurrRoom() {
        return hasBindCurrRoom;
    }

    public boolean hasPhone() {
        return hasPhone;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isBozhu() {
        return isBozhu;
    }

    public boolean isExpert() {
        return isExpert;
    }

    public void setExpert(boolean isExpert) {
        this.isExpert = isExpert;
    }

    public String getLoginPlatform() {
        return loginPlatform;
    }

    public void setLoginPlatform(String loginPlatform) {
        this.loginPlatform = loginPlatform;
    }

    public int getNeedCnt() {
        return needCnt;
    }

    public void setNeedCnt(int needCnt) {
        this.needCnt = needCnt;
    }

    public boolean nextCanChange() {
        return nextCanChange;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean online() {
        return online;
    }

    public int getScoreCnt() {
        return scoreCnt;
    }

    public void setScoreCnt(int scoreCnt) {
        this.scoreCnt = scoreCnt;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTradeAccount() {
        return tradeAccount;
    }

    public void setTradeAccount(String tradeAccount) {
        this.tradeAccount = tradeAccount;
    }

    public String getHXID() {
        String serverName;
        if (serverId == 3) {
            serverName = "yg";
        } else {
            serverName = "tt";
        }

        return serverName + username;
    }

    public long getCusId() {
        return cusId;
    }

    public int getPermission() {
        if (userType == 0) {
            return 1;
        } else if (userType < 3) {
            return 6;
        } else {
            return 56;
        }
    }
}
