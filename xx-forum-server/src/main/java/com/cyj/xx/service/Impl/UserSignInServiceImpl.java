package com.cyj.xx.service.Impl;


import com.cyj.xx.Aop.annotation.AddExp;
import com.cyj.xx.dvo.UserSignInDVO;
import com.cyj.xx.entity.UserSignIn;
import com.cyj.xx.mapper.UserSignInMapper;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.UserSignInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserSignInServiceImpl implements UserSignInService {

    @Autowired
    UserSignInMapper userSignInMapper;


    /**
     * 获取签到信息
     * @param sectionId
     * @param userId
     * @return
     */
    @Override
    public Result<UserSignInDVO> getUserSignIn(Long sectionId, Long userId) {
        UserSignInDVO userSignInDVO = new UserSignInDVO();
        try {
            UserSignIn userSignIn = userSignInMapper.getUserSignIn(sectionId,userId);
            userSignInDVO.setId(userSignIn.getId());
            userSignInDVO.setLastSignDate(userSignIn.getLastSignDate());
            userSignInDVO.setStatus(userSignIn.getStatus());
            userSignInDVO.setContinuousDays(userSignIn.getContinuousDays());
            return Result.success(userSignInDVO);

        }catch (Exception e) {

            return Result.error("查询数据库失败");
        }
    }


    /**
     * 签到
     * @param userSignInId
     * @return
     */
    @Override
    @AddExp
    public Result doUserSignIn(Long userSignInId) {
        try{
            UserSignIn userSignIn = userSignInMapper.selectUserSignIn(userSignInId);
            userSignIn.setLastSignDate(LocalDateTime.now());
            userSignIn.setUpdatedAt(LocalDateTime.now());
            userSignIn.setStatus(true);
            userSignIn.setContinuousDays(userSignIn.getContinuousDays()+1);
            userSignInMapper.UpdateUserSignIn(userSignIn);
            return Result.success();

        }catch (Exception e) {
            return Result.error("操作数据库出现异常");
        }
    }

    @Override
    public void BatchSignInStatus() {
        try {
            userSignInMapper.BatchSignInStatus();

        }catch (Exception e) {
            log.error("批量更新签到状态失败");
        }
    }
}
