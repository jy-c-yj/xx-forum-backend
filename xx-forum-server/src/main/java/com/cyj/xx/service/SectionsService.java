package com.cyj.xx.service;

import com.cyj.xx.dto.ConfirmBanUserDTO;
import com.cyj.xx.dto.CreateSectionDTO;
import com.cyj.xx.dto.UpdataSectionDTO;
import com.cyj.xx.dvo.BanUserLogDVO;
import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.ManageShowSectionDVO;
import com.cyj.xx.dvo.MyFollowSectionDVO;
import com.cyj.xx.entity.Section;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;

import java.util.List;

public interface SectionsService {


    Result<List<HotSectionsDVO>> HotSection(Integer limit);

    Result<List<MyFollowSectionDVO>> mySection(Long id);

    Result<Section> sectionDetail(Long id);

    Result<MyFollowSectionDVO> followSec(Long sectionId);

    Result<Boolean> isFollowSec(Long sectionId);

    Result<Boolean> unfollowSec(Long sectionId);

    Result<PageResult> searchSection(String keyword, int page, int size);

    Result<Boolean> createSection(Long userId, CreateSectionDTO createSectionDTO);

    ManageShowSectionDVO getSection(Long sectionId, Long userId);

    Result<Boolean> UpdataSection(UpdataSectionDTO updataSectionDTO);

    Result<Boolean> isModerator(Long sectionId, Long userId);

    Result<Boolean> confirmBanUser(ConfirmBanUserDTO confirmBanUserDTO);

    Result<List<BanUserLogDVO>> GetBanUserLog(Long sectionId);

    Result<Boolean> unBanUser(Long id);
}
