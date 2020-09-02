package com.uvcity.smartpark.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uvcity.smartpark.entity.po.ParkingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chenling
 */
@Repository
@Mapper
public interface ParkingRecordMapper extends BaseMapper<ParkingRecord> {

    @Select("select * from parking_record t1,(SELECT car_no ,max(access_time) max_time from parking_record )t3 \n" +
            "where \n" +
            "t1.car_no = #{carNo} and \n" +
            "t1.car_no = t3.car_no  and  \n" +
            "t1.access_time = t3.max_time  and  \n" +
            "not EXISTS ( select * from parking_order t2 where t1.sn = t2.enter_sn)")
    List<ParkingRecord> queryByLatestRecord(@Param("carNo") String carNo);
}
