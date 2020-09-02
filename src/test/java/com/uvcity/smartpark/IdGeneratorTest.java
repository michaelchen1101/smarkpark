package com.uvcity.smartpark;

import com.uvcity.smartpark.entity.po.ParkingOrder;
import com.uvcity.smartpark.mapper.ParkingOrderMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class IdGeneratorTest {

    @Resource
    private ParkingOrderMapper parkingOrderMapper;

    @Test
    public void test() {
        ParkingOrder parkingOrder = new ParkingOrder();
        parkingOrder.setCarNo("粤B88888");

        parkingOrderMapper.insert(parkingOrder);
        Assert.assertEquals(Long.valueOf(1L), parkingOrder.getOrderNo());
    }
}
