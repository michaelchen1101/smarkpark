package com.uvcity.smartpark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uvcity.smartpark.entity.po.ParkingRecordTemp;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author chenling
 */
@Repository
@Mapper
public interface ParkingRecordTempMapper extends BaseMapper<ParkingRecordTemp> {

}