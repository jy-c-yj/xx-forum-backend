package com.cyj.xx.mapper;


import com.cyj.xx.entity.ModeratorOperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModeratorOperationLogMapper {
    void insert(ModeratorOperationLog moderatorOperationLog);
}
