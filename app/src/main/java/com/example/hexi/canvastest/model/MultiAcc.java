package com.example.hexi.canvastest.model;

/**
 * Created by zhiguo.jiang on 16/7/22.
 */
public class MultiAcc {
    //TT的业务为1(business)
    public static final int BUSINESSTYPE_TT = 1;
    //YG的业务为3(business)
    public static final int BUSINESSTYPE_YG = 3;
    //大盘(category)
    public static final int BIG_INDEX = 1;
    //中盘(category)
    public static final int MIDDLE_INDEX = 2;
    //微盘(category)
    public static final int TINY_INDEX = 3;

    //用户类型(1开户,3激活)
    public static int OPEN_ACCOUNT_USER_TYPE = 1;
    public static int ACTIVE_ACCOUNT_USER_TYPE = 3;
    //默认状态(1默认,0,非默认)
    public static int STATUS_DEFAULT = 1;
    public static int STATUS_NOT_DEFAULT = 0;
    //绑卡失败
    public static int BIND_BANK_STATUS_FAIL = 0;
    public Long id;

    /**
     * 登陆账号
     */
    public String loginAcc;

    /**
     * 手机号
     */
    public String phone;

    /**
     * 公司id
     */
    public int serverId;

    /**
     * 用户默认选择的业务
     */
    public int defStatus;

    /**
     * 删除状态
     */
    public int delStatus;

    /**
     * 创建时间
     */
    public long createDate;

    /**
     * 更新时间
     */
    public long updateDate;

    /**
     * 交易所
     */
    public int business;

    /**
     * 1大盘2中盘3微盘
     */
    public int category;

    public String token;
    public int userType;

    /**
     * 是否绑卡
     * 0绑卡失败，1绑卡成功，2激活成功
     */
    public int bindBankStatus;

    /**
     * 获取业务类型
     */
    public LoginInfoResult.BusinessType getBusinessType() {
        if (business == BUSINESSTYPE_TT) {
            return LoginInfoResult.BusinessType.TT_B;
        } else if (business == BUSINESSTYPE_YG) {
            if (category == BIG_INDEX) {
                return LoginInfoResult.BusinessType.YG_B;
            } else if (category == MIDDLE_INDEX) {
                return LoginInfoResult.BusinessType.YG_M;
            }

        }
        return LoginInfoResult.BusinessType.TT_B;
    }

    public boolean isActiveTradeAccount() {
        return defStatus == STATUS_DEFAULT && userType == ACTIVE_ACCOUNT_USER_TYPE;
    }

    public boolean isBindCard() {
        return bindBankStatus != BIND_BANK_STATUS_FAIL;
    }
}
