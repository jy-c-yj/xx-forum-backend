package com.cyj.xx.mapper;


import com.cyj.xx.dvo.OperationLogDVO;
import com.cyj.xx.entity.ModeratorOperationLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ModeratorOperationLogMapper {
    void insert(ModeratorOperationLog moderatorOperationLog);


    List<OperationLogDVO> SelectBySectionId(Long sectionId);
}
