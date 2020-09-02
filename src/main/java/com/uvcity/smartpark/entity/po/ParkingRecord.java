package com.uvcity.smartpark.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author chenling
 */
@Data
@TableName("parking_record")
public class ParkingRecord extends Model<ParkingRecord> {



    private String  sn;
    private String carNo;
    private Integer parkType;
    private String parkCode;
    private Integer parkSpotNo;
    private String spotSn;
    private Timestamp accessTime;
    private String imagePath;
    private String deviceNo;
    private Timestamp createDate;
}
