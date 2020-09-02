package com.uvcity.smartpark.entity.po;

import java.sql.Timestamp;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * parking_exec
 * @author 
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("parking_exec")
public class ParkingExec extends Model<ParkingExec> {
    private Long id;

    private Integer execType;

    private String carNo;

    private Timestamp createTime;

    private String execDetail;

}