package com.uvcity.smartpark.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.uvcity.smartpark.entity.po.*;
import com.uvcity.smartpark.enums.ExecType;
import com.uvcity.smartpark.enums.GateType;
import com.uvcity.smartpark.gateway.MqttGateway;
import com.uvcity.smartpark.mapper.*;
import com.uvcity.smartpark.service.MqService;
import com.uvcity.smartpark.util.ParkingFeeUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.soap.Addressing;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenling
 */

@Service
@Slf4j
public class MqServiceImpl extends ServiceImpl<ParkingRecordMapper,ParkingRecord> implements MqService {


    //@Autowired
    //private ParkingRecordMapper parkingRecordMapper;
    @Autowired
    private EtcChannelMapper etcChannelMapper;
    @Autowired
    private ParkingRecordTempMapper parkingRecordTempMapper;
    //@Autowired
    //private ParkingOrderMapper parkingOrderMapper;
    @Autowired
    private ParkingExecMapper parkingExecMapper;
    @Autowired
    private MqttGateway mqttGateway;
    @Override
    public void saveParkRecord(ParkingRecord parkingRecord) {
           //先保存停车记录流水
        Boolean b = this.save(parkingRecord);

        //出入场处理  1:入  2：出
        if(parkingRecord.getParkType().equals(GateType.ENTRY.getCode())){
            //TODO  1.规则引擎  消息路由  获取摄像头和RSU对应关系  摄像头触发RSU天线开机读卡指令 暂时由EMQX规则引擎实现 异步返回读卡信息
            //入场车辆插入停车临时表
            ParkingRecordTemp parkingRecordTemp = new ParkingRecordTemp();
            BeanUtils.copyProperties(parkingRecord,parkingRecordTemp);
            parkingRecordTempMapper.insert(parkingRecordTemp);
        }else if(parkingRecord.getParkType().equals(GateType.EXIT.getCode())){
            //查询
            //List<ParkingRecord>  list = parkingRecordMapper.queryByLatestRecord(parkingRecord.getCarNo());
            //查询此车是否存在停车临时表有入场数据
            List<ParkingRecordTemp>  list = parkingRecordTempMapper.selectList(new QueryWrapper<ParkingRecordTemp>().eq("car_no",parkingRecord.getCarNo()));
            if(list.isEmpty()){
                //TODO 有出场没有进场的记录，记录到异常表，后续人工处理
                ParkingExec parkingExec = new ParkingExec();
                parkingExec.setCarNo(parkingRecord.getCarNo());
                parkingExec.setCreateTime(new Timestamp(System.currentTimeMillis()));
                parkingExec.setExecType(ExecType.NO_ENTRY.getCode());
                parkingExecMapper.insert(parkingExec);
            }else if(list.size()>1){
                //TODO 未产生订单的多条入场记录，记录到异常表
                ParkingExec parkingExec = new ParkingExec();
                parkingExec.setCarNo(parkingRecord.getCarNo());
                parkingExec.setCreateTime(new Timestamp(System.currentTimeMillis()));
                parkingExec.setExecType(ExecType.REPEAT_ENTRY.getCode());
                parkingExecMapper.insert(parkingExec);
            }else {
                //TODO 查询车牌对应的OBUID,生成订单
               //ParkingRecord parkingRecord1 = list.get(0);
                EtcChannel etcChannel = etcChannelMapper.selectOne(new QueryWrapper<EtcChannel>().eq("car_no",parkingRecord.getCarNo()));
               //ParkingRecordTemp parkingRecordTemp = parkingRecordTempMapper.selectOne(new QueryWrapper<ParkingRecordTemp>().eq("car_no",parkingRecord.getCarNo()));
                ParkingRecordTemp parkingRecordTemp = list.get(0);
                if(etcChannel !=null&& etcChannel.getObuId()!=null){

                    ParkingOrder parkingOrder = new ParkingOrder();
                    parkingOrder.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    parkingOrder.setEnterSn(parkingRecordTemp.getSn());
                    parkingOrder.setEnterTime(parkingRecordTemp.getAccessTime());
                    parkingOrder.setExitSn(parkingRecord.getSn());
                    parkingOrder.setExitTime(parkingRecord.getAccessTime());
                    double fee = ParkingFeeUtil.countParkingFee(parkingRecordTemp.getAccessTime(),parkingRecord.getAccessTime());
                    parkingOrder.setFee(fee);
                    parkingOrder.setAmount(new Double(fee*100).longValue());
                    parkingOrder.setParkCode(parkingRecord.getParkCode());
                    parkingOrder.setParkSpotNo(parkingRecord.getParkSpotNo());
                    parkingOrder.setSpotSn(parkingRecord.getSpotSn());
                    parkingOrder.setCarNo(parkingRecord.getCarNo());
                    parkingOrder.setObuId(etcChannel.getObuId());
                    parkingOrder.insert();


                    //删除临时该车在临时停车表的入场数据
                    //Map<String,Object> columnMap = new HashMap<String,Object>();
                    //columnMap.put("car_no",parkingRecord.getCarNo());
                    //parkingRecordTempMapper.deleteByMap(columnMap);
                    parkingRecordTempMapper.delete(new QueryWrapper<ParkingRecordTemp>()
                            .lambda().eq(ParkingRecordTemp::getCarNo, parkingRecord.getCarNo()));
                }else {
                    //TODO 出场未找到OBUID，并记录到异常表
                    ParkingExec parkingExec = new ParkingExec();
                    parkingExec.setCarNo(parkingRecord.getCarNo());
                    parkingExec.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    parkingExec.setExecType(ExecType.NO_OBUID.getCode());
                    parkingExecMapper.insert(parkingExec);
                }

            }
        }

    }
}
