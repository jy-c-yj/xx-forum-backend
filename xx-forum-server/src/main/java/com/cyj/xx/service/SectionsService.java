package com.cyj.xx.service;

import com.cyj.xx.dto.CreateSectionDTO;
import com.cyj.xx.dvo.HotSectionsDVO;
import com.cyj.xx.dvo.MySectionDVO;
import com.cyj.xx.entity.Section;
import com.cyj.xx.entity.admin;
import com.cyj.xx.result.PageResult;
import com.cyj.xx.result.Result;

import java.util.List;

public interface SectionsService {


    Result<List<HotSectionsDVO>> HotSection(Integer limit);

    Result<List<MySectionDVO>> mySection(Long id);

    Result<Section> sectionDetail(Long id);

    Result<MySectionDVO> followSec(Long sectionId);

    Result<Boolean> isFollowSec(Long sectionId);

    Result<Boolean> unfollowSec(Long sectionId);

    Result<PageResult> searchSection(String keyword, int page, int size);

    Result<Boolean> createSection(Long userId, CreateSectionDTO createSectionDTO);
}
