package com.uvcity.smartpark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uvcity.smartpark.entity.po.ParkingRecord;

/**
 * @author chenling
 */
public interface MqService extends IService<ParkingRecord>{


    public void saveParkRecord(ParkingRecord parkingRecord);


}
