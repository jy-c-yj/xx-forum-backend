package com.cyj.xx.service.Impl;

import com.cyj.xx.entity.admin;
import com.cyj.xx.service.adminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class adminServiceImpl implements adminService {



    @Override
    public admin login(admin admin) {
        if (admin.getId().equals("admin") && admin.getPassword().equals("123456")) {
            return admin;
        }

            return admin;

    }
}
