package com.robod.attendancesystem.utils;

import android.text.TextUtils;

import com.robod.attendancesystem.entity.Constants;
import com.robod.attendancesystem.entity.Student;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author Robod
 * @date 2020/9/29 16:33
 */
public class NetWorkUtils {

    /**
     * 开一个子线程向百度服务器发送post请求
     *
     * @param requestUrl
     * @param param
     * @return
     */
    public static String baiduPost(final String requestUrl, final String param) {
        FutureTask<String> task = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return HttpUtil.post(requestUrl, Constants.ACCESS_TOKEN,
                        "application/json", param);
            }
        });
        new Thread(task).start();
        try {
            String result = task.get();
            return !TextUtils.isEmpty(result) ? result : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 开一个子线程向百度服务器发送post请求,遍历list集合向服务器请求数据
     *
     * @param requestUrl
     * @param userIdList
     * @return
     */
    public static List<Student> baiduPost2(final String requestUrl, final List<String> userIdList) {
        FutureTask<List<Student>> task = new FutureTask<>(new Callable<List<Student>>() {
            @Override
            public List<Student> call() throws Exception {
                List<Student> students = new ArrayList<>();
                String param;
                for (int i = 0; i < userIdList.size(); i++) {
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("group_id", "students");
                    paramMap.put("user_id", userIdList.get(i));
                    param = GsonUtils.toJson(paramMap);
                    if (i != 0) {
                        Thread.sleep(1000 / 2 + 10);  //2qps，所以歇个520毫秒再访问
                    }
                    String result = HttpUtil.post(requestUrl, Constants.ACCESS_TOKEN,
                            "application/json", param);
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject = new JSONObject(result);
                        if ("SUCCESS".equals(jsonObject.getString("error_msg"))) {
                            String resultJson = jsonObject.getString("result");
                            jsonObject = new JSONObject(resultJson);
                            String userListJson = jsonObject.getString("user_list");
                            JSONArray userList = new JSONArray(userListJson);
                            if (userList.length() > 0) {
                                String userJson = userList.getString(0);
                                jsonObject = new JSONObject(userJson);
                                String userInfo = jsonObject.getString("user_info");
                                if (!TextUtils.isEmpty(userInfo)) {
                                    String[] userInfoArray = userInfo.split("_");
                                    if (userInfoArray.length == 3) {
                                        Student student = new Student();
                                        student.setNumber(userInfoArray[0]);
                                        student.setName(userInfoArray[1]);
                                        student.setClass_(userInfoArray[2]);
                                        students.add(student);
                                    } else {
                                        return null;
                                    }
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        }
                    } else {
                        return null;
                    }
                }
                return students;
            }
        });
        new Thread(task).start();
        try {
            List<Student> students = task.get();
            return students;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
