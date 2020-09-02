package com.uvcity.smartpark.entity.po;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * parking_record_temp
 * @author 
 */
@Data
public class ParkingRecordTemp extends Model<ParkingRecordTemp> {
    private Long id;

    private String sn;

    private String parkCode;

    private Integer parkSpotNo;

    private String spotSn;

    private Integer parkType;

    private String carNo;

    private Timestamp accessTime;

    private Timestamp createTime;

}