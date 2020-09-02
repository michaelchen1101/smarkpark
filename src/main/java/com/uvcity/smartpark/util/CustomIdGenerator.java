package com.uvcity.smartpark.util;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.uvcity.smartpark.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chenling
 */
@Slf4j
@Component
public class CustomIdGenerator implements IdentifierGenerator {
    private final AtomicLong al = new AtomicLong(1);
    @Override
    public Long nextId(Object entity) {
        //可以将当前传入的class全类名来作为bizKey,或者提取参数来生成bizKey进行分布式Id调用生成.
       /* String bizKey = entity.getClass().getName();
        log.info("bizKey:{}", bizKey);
        MetaObject metaObject = SystemMetaObject.forObject(entity);
        String name = (String) metaObject.getValue("name");
        final long id = al.addAndGet(1);
        log.info("为{}生成主键值->:{}", name, id);*/
        return Long.valueOf(nextBillNumber());
    }


    /**
     * 生成单号。 <br>
     * 生成规格： 5位停车场编码 + 17位日期(yyyyMMddhhmmssSS) + 6位随机数。 其中，流水号每日重复计算。 <br>
     * 示例: 00001 161206 00001
     *
     *          车场编码，一般取实体短名称，禁止为null。
     * @return 唯一的新单号，但不保证连续性。
     * @throws ServiceException
     */
    public static String nextBillNumber()
            throws ServiceException {

        return getTime() + getRandomNum();
    }


    /**
     * 获取YYYY-MM-DD HH:mm:ss格式
     * @return
     */

    public static String getTime() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yy-MM-dd HH:mm:ss:SS");
        //System.out.println("时间戳："+sdfTime.format(new Date()).replaceAll("[[\\s-:punct:]]", ""));
        return sdfTime.format(new Date()).replaceAll("[[\\s-:punct:]]", "");
    }

    /**
     * 随机生成六位数验证码
     * @return
     */
    public static int getRandomNum(){
        Random r = new Random();
        //(int)(Math.random()*999999)
        return r.nextInt(900)+100;
    }


    public static void main(String[] args){

        log.info(nextBillNumber().length()+"------"+Long.valueOf(nextBillNumber()));
    }
}
