package com.cyj.xx.controller.admin;

import com.cyj.xx.entity.admin;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.Impl.adminServiceImpl;
import com.cyj.xx.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/admin")
public class adminController {

    @Autowired
    adminServiceImpl adminService;




    @PostMapping("/login")
    public Result login(@RequestBody admin admin) {

        System.out.println(admin);

        admin as = adminService.login(admin);
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", as.getId());
        String jwt = JwtUtil.createJwt("jycyj",12345,map);

        return Result.success();
    }

    @PostMapping("/revise")
    public Result revise(@RequestBody admin admin) {
        return Result.success();
    }

}
