package org.mamba.controller;

import okhttp3.RequestBody;
import org.mamba.config.MicrosoftOAuthConfig;
import org.mamba.entity.User;
import org.mamba.mapper.AdminMapper;
import org.mamba.mapper.LecturerMapper;
import org.mamba.mapper.StudentMapper;
import org.mamba.mapper.UserMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private LecturerMapper lecturerMapper;

    @Autowired
    private AdminMapper adminMapper;

    private final MicrosoftOAuthConfig microsoftOAuthConfig;
    private final OkHttpClient httpClient = new OkHttpClient();

    public AuthController(UserMapper userMapper, MicrosoftOAuthConfig microsoftOAuthConfig) {
        this.userMapper = userMapper;
        this.microsoftOAuthConfig = microsoftOAuthConfig;
    }

    // **1. 引导用户跳转到 Microsoft 登录页面**
    @GetMapping("/login")
    public String login() {
        String url = "https://login.microsoftonline.com/" + microsoftOAuthConfig.getTenantId() + "/oauth2/v2.0/authorize" +
                "?client_id=" + microsoftOAuthConfig.getClientId() +
                "&response_type=code" +
                "&redirect_uri=" + microsoftOAuthConfig.getRedirectUri() +
                "&scope=openid email profile" +
                "&response_mode=query" +
                "&state=random_string";
        return "PLEASE VISIT: <a href='" + url + "'>" + url + "</a>";
    }

    // **2. 处理 Microsoft OAuth 回调**
    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) {
        try {
            // **2.1 交换授权码获取 Access Token**
            String tokenUrl = "https://login.microsoftonline.com/" + microsoftOAuthConfig.getTenantId() + "/oauth2/v2.0/token";
            RequestBody body = new FormBody.Builder()
                    .add("client_id", microsoftOAuthConfig.getClientId())
                    .add("client_secret", microsoftOAuthConfig.getClientSecret())
                    .add("code", code)
                    .add("redirect_uri", microsoftOAuthConfig.getRedirectUri())
                    .add("grant_type", "authorization_code")
                    .build();
            Request request = new Request.Builder().url(tokenUrl).post(body).build();
            Response response = httpClient.newCall(request).execute();
            String responseBody = response.body().string();
            Map<String, Object> tokenResponse = new com.fasterxml.jackson.databind.ObjectMapper().readValue(responseBody, Map.class);
            String accessToken = (String) tokenResponse.get("access_token");

            // **2.2 使用 Access Token 获取用户信息**
            Request userRequest = new Request.Builder()
                    .url("https://graph.microsoft.com/v1.0/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
            Response userResponse = httpClient.newCall(userRequest).execute();
            String userBody = userResponse.body().string();
            Map<String, Object> userInfo = new com.fasterxml.jackson.databind.ObjectMapper().readValue(userBody, Map.class);

            // **2.3 解析用户信息**
            String microsoftId = (String) userInfo.get("id");
            String email = (String) userInfo.get("mail");
            String name = (String) userInfo.get("displayName");

            // **2.4 只允许 dundee.ac.uk 邮箱登录**
            if (!email.endsWith("@dundee.ac.uk")) {
                return "Sorry, but we only allow UoD students and staffs!";
            }

            // **2.5 存入数据库**
            User existingUser = userMapper.getUserByMicrosoftId(microsoftId);
            if (existingUser == null) {
                User newUser = new User();
                newUser.setMicrosoftId(microsoftId);
                newUser.setEmail(email);
                newUser.setName(name);
                // TODO Only student logic so far
                // By default it's student, maybe it has to be changed later
                userMapper.createUser(microsoftId, email, name, "Student");
                int uid = userMapper.getUserByMicrosoftId(microsoftId).getUid();
                studentMapper.createStudent(email, uid, name, null, 0);

                return "New user registered (student): " + email;
            }
            return "User already exists: " + existingUser.getEmail();

        } catch (IOException e) {
            e.printStackTrace();
            return "LOGIN FAILURE!";
        }
    }
}
