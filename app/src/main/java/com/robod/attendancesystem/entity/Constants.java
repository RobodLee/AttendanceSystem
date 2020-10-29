package com.robod.attendancesystem.entity;

/**
 * @author Robod
 * @date 2020/9/29 16:14
 */
public class Constants {

    /**
     * 签到成功
     */
    public static final String SIGN_IN_SUCCESS = "*1\r\n";

    /**
     * 签到失败
     */
    public static final String SIGN_IN_FAIL = "*2\r\n";

    /**
     * 签退成功
     */
    public static final String SIGN_OUT_SUCCESS = "*3\r\n";

    /**
     * 签退失败
     */
    public static final String SIGN_OUT_FAIL = "*4\r\n";

    /**
     * 开始签到
     */
    public static final String START_SIGN_IN = "#1\r\n";

    /**
     * 开始签退
     */
    public static final String START_SIGN_OUT = "#2\r\n";

    /**
     * 关闭语音
     */
    public static final String CLOSE_VOICE = "@1\r\n";

    /**
     * 发送给SignInOutFragment
     */
    public static final int MESSAGE_TO_FRAGMENT = 1;

    /**
     * 发送给MyService
     */
    public static final int MESSAGE_TO_SERVICE = 2;

    /**
     * SharedPreferences中签到时间下标对应的键名称
     */
    public static final String SIGN_IN_TIME_KEY = "SING_IN_TIME_KEY";

    /**
     * SharedPreferences中签退时间下标对应的键名称
     */
    public static final String SIGN_OUT_TIME_KEY = "SING_OUT_TIME_KEY";

    /**
     * SharedPreferences中管理员密码对应的键名称
     */
    public static final String ADMIN_PASSWORD_KEY = "ADMIN_PASSWORD_KEY";

    /**
     * 用于保存设置信息的SharedPreferences文件的名称
     */
    public static final String SP_NAME = "SP_NAME";

    /**
     * 人脸注册
     */
    public static final String FACE_REGISTER_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";

    /**
     * 人脸搜索
     */
    public static final String FACE_SEARCH_URL = "https://aip.baidubce.com/rest/2.0/face/v3/search";

    /**
     * 人脸删除
     */
    public static final String FACE_DELETE_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/delete";

    /**
     * 用户列表
     */
    public static final String STUDENTS_LIST_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/group/getusers";

    /**
     * 获取用户信息
     */
    public static final String GET_USER_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/get";

    /**
     * accessToken
     */
    public static final String ACCESS_TOKEN = "24.06334a5de6bc7e389ae1635ab436ed08.2592000.1606553588.282335-22773965";

    /**
     * 蓝牙的地址
     */
    public static final String BLE_ADDRESS = "90:9A:77:2C:B1:FA";
}
