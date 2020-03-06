package com.yudachi.admin.controller.v1;

import com.yudachi.admin.apis.LoginControllerApi;
import com.yudachi.admin.service.UserLoginService;
import com.yudachi.model.admin.pojos.AdUser;
import com.yudachi.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController implements LoginControllerApi {

    @Autowired
    private UserLoginService userLoginService ;

    @RequestMapping("/in")
    public ResponseResult login(@RequestBody AdUser user){
        return userLoginService.login(user);
    }

}
