package com.uvcity.smartpark.enums;

import lombok.Getter;

/**
 * @author chenling
 */

@Getter
public enum GateType {
    /**
     * 入口
     */
    ENTRY(1,"入口"),

    EXIT(2,"出口");


    private Integer code;
    private String msg;
    GateType(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }
}
