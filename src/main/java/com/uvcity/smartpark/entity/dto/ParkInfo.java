package com.uvcity.smartpark.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author chenling
 */
@Data
public class ParkInfo {

    private String  id;
    private String carNo;
    private Integer parkType;
    private String parkNo;
    private Integer parkSpotNo;
    private String spotSn;
    private Timestamp accessTime;
    private String imagePath;
    private String deviceNo;
    private Timestamp createDate;

}
