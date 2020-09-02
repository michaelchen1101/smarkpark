package com.uvcity.smartpark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uvcity.smartpark.entity.po.ParkingExec;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author chenling
 */
@Repository
@Mapper
public interface ParkingExecMapper extends BaseMapper<ParkingExec> {

}