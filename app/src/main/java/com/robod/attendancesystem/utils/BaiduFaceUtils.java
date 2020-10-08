package com.robod.attendancesystem.utils;

import android.text.TextUtils;

import com.robod.attendancesystem.entity.Constants;
import com.robod.attendancesystem.entity.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Robod
 * @date 2020/9/29 16:30
 * 调用百度人脸识别API的工具类
 */
public class BaiduFaceUtils {

    private static final String TAG = "BaiduFaceUtils";

    /**
     * 注册人脸
     *
     * @return
     */
    public static boolean register(Map<String, String> paramMap) {
        paramMap.put("image_type", "BASE64");    //图片格式，这里指定用图片的base64编码
        paramMap.put("group_id", "students");    //用户组id，统一存放在students组内
        paramMap.put("liveness_control", "NORMAL");  //活体检测，LOW:较低的活体要求(高通过率 低攻击拒绝率)；NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率)；HIGH: 较高的活体要求(高攻击拒绝率 低通过率)

        final String param = GsonUtils.toJson(paramMap);
        String result = NetWorkUtils.baiduPost(Constants.FACE_REGISTER_URL, param);
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String errorMsg = jsonObject.getString("error_msg");
                if ("SUCCESS".equals(errorMsg)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    public static List<Student> getStudents() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("group_id", "students");

        final String param = GsonUtils.toJson(paramMap);
        String result = NetWorkUtils.baiduPost(Constants.STUDENTS_LIST_URL, param);
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if ("SUCCESS".equals(jsonObject.getString("error_msg"))) {
                    String resultJson = jsonObject.getString("result");
                    JSONObject resultObject = new JSONObject(resultJson);
                    String userIdListString = resultObject.getString("user_id_list");
                    JSONArray userIdListArray = new JSONArray(userIdListString);
                    if (userIdListArray.length() > 0) {
                        List<String> userIdList = new ArrayList<>();
                        for (int i = 0; i < userIdListArray.length(); i++) {
                            userIdList.add(userIdListArray.getString(i));
                        }
                        List<Student> students = NetWorkUtils.baiduPost2(Constants.GET_USER_URL, userIdList);
                        if (students.size() != userIdList.size()) {
                            throw new RuntimeException("数据不一致-----------");
                        }
                        for (int i = 0; i < students.size(); i++) {
                            //遍历查询到的数据，添加或修改至本地数据库
                            List<Student> studentsFromDB = LitePal.where("number = ?", students.get(i).getNumber())
                                    .find(Student.class);
                            if (studentsFromDB != null && studentsFromDB.size() > 0) {
                                Student studentFromDB = studentsFromDB.get(0);
                                studentFromDB.setName(students.get(i).getName());
                                studentFromDB.setClass_(students.get(i).getClass_());
                                studentFromDB.save();
                            } else {
                                students.get(i).save();
                            }
                        }
                        return students;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 在人脸库中搜索人脸数据
     *
     * @param paramMap
     * @return user_info (学号_姓名_班级)
     */
    public static String search(Map<String, String> paramMap) {
        paramMap.put("image_type", "BASE64");
        paramMap.put("group_id_list", "students");
        paramMap.put("liveness_control", "NORMAL"); //活体检测
        String param = GsonUtils.toJson(paramMap);

        String result = NetWorkUtils.baiduPost(Constants.FACE_SEARCH_URL, param);
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if ("SUCCESS".equals(jsonObject.getString("error_msg"))) {
                    String resultJson = jsonObject.getString("result");
                    jsonObject = new JSONObject(resultJson);
                    String userListJson = jsonObject.getString("user_list");
                    JSONArray userList = new JSONArray(userListJson);
                    if (userList.length()>0) {
                        jsonObject = userList.getJSONObject(0);
                        if (Double.doubleToLongBits(jsonObject.getDouble("score")) >=
                                Double.doubleToLongBits(80.0)) {
                            return jsonObject.getString("user_info");
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
