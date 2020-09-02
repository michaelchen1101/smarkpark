package com.uvcity.smartpark.entity.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * parking_order
 * @author 
 */
@Data
public class ParkingOrder extends Model<ParkingOrder> {

    @TableId(type = IdType.ASSIGN_ID)
    private String orderNo;

    private String carNo;

    private Date enterTime;

    private Date exitTime;

    private String parkCode;

    private Integer parkSpotNo;

    private String spotSn;

    private Double fee;

    private Long amount;

    private Integer payChannel;

    private String obuId;

    private Integer orderStatus;

    private String enterSn;

    private String exitSn;

    private String transSn;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}