package com.uvcity.smartpark.entity.po;



import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * etc_channel
 * @author 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EtcChannel extends Model<EtcChannel> {
    private String cardSn;

    private String obuId;

    private String obuTransId;

    private String carNo;

    private Integer carColor;

    private Integer carType;

    private static final long serialVersionUID = 1L;
}