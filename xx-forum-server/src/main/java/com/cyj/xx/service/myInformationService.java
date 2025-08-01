package com.cyj.xx.service;

import com.cyj.xx.dto.UserChangePwd;
import com.cyj.xx.dto.UserEnrollDTO;
import com.cyj.xx.dto.UserLoginDTO;
import com.cyj.xx.dto.UserReviseDTO;
import com.cyj.xx.dvo.*;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;

import java.util.List;

public interface myInformationService {
    Result<UserInformationDVO> UserInformation();

    Result<List<MyInformationMainSecsDVO>> getSections(Long userId);

    Result<PageResult> getPosts(int page,int pageSize);

    Result<List<MyInformationFollowDVO>> getFollow(Long userId);

    Result<PageResult> getReply(Long userId,int pageNum,int pageSize);

    Result<PageResult> getPost(Long userId, int pageNum, int pageSize);

    Result<PageResult> getMyCollection(Long userId, int pageNum, int pageSize);
}
