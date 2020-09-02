package com.uvcity.smartpark.enums;

import com.uvcity.smartpark.exception.ErrorType;
import lombok.Getter;

/**
 * @author chenling
 */

@Getter
public enum ExecType {
    /**
     * 无入场
     */
    NO_ENTRY(1,"无入场数据"),REPEAT_ENTRY(2,"重复入场"),NO_OBUID(3,"未查找到对应的OBUID");
   private Integer code;
   private String mesg;

    ExecType(Integer code, String mesg) {
        this.code = code;
        this.mesg = mesg;
    }


}
