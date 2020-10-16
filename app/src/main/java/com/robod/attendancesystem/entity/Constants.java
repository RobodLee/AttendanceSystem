package com.robod.attendancesystem.entity;

/**
 * @author Robod
 * @date 2020/9/29 16:14
 */
public class Constants {

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
    public static final String ACCESS_TOKEN = "24.9275f6ff756cbae96aebabf4f63cda47.2592000.1603959221.282335-22773965";

}
