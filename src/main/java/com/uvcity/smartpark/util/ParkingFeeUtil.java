package com.uvcity.smartpark.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 停车收费计算工具类.
 * 收费标准：
 * 1）停车小于15分钟不收费
 * 2）8时至22时，停车1小时内3元，超过1小时每延长1小时加收3元；
 * 3）11时至12时，16时30分至17时30分，停车30分钟内收1元，超过30分收3元；
 * 4）晚22时至次日8时，收10元。
 * @author chenling
 */
public class ParkingFeeUtil {
    /**
     * 一天的毫秒数
     */
    public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;
    /** 一小时的毫秒数 */
    public static final long ONE_HOUR_MILLIS = 60 * 60 * 1000;
    /** 半小时的毫秒数 */
    public static final long HALF_HOUR_MILLIS = 30 * 60 * 1000;
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    //private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final List<String> holidays = new ArrayList<String>();

    //public static String currentDay;
    /**
     * 计算停车费
     *
     * @param t1 驶入时间
     * @param t2 驶出时间
     * @return 应收停车费
     */
/*    public static double countParkingFee(Long t1, Long t2) {
        t1 *= 1000;
        t2 *= 1000;
        //区域(1- 一类区域,2- 二类区域,3- 三类区域)
        Integer z1 = 1;
        //工作日标识(1- 工作日 , 2- 休息日)
        //Integer d1 = 1;
        //除以1000是为了转换成秒
        long between = (t2 - t1) / 1000;
        long minute1 = between % 60 > 0 ? between / 60 + 1 : between / 60;
        //停车时间小于30分钟不收费
        if (minute1 <= 30) {
            return 0;
        }
        Date date1 = new Date(t1);
        Date date2 = new Date(t2);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        if(!isHolidayOrFestival(date1)){ //工作日收费时段8点到20点
            calendar.set(Calendar.HOUR_OF_DAY, 8);
        } else{ //非工作日收费时间10点到20点
            calendar.set(Calendar.HOUR_OF_DAY, 10);
        }
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //String currentDay = dateToStringFormat(date1,"yyyyMMdd");
        //currentDay = DateUtil.dateToStringFormat(date1,"yyyyMMdd");
        if (date1.before(calendar.getTime())) {// 8点或10前驶入
            return countNightParkingFee(date1, date2, z1);
        } else {// 8点或10点后驶入
            calendar.set(Calendar.HOUR_OF_DAY, 20);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (date1.before(calendar.getTime())) {// 20前驶入
                return countNormalParkingFee(date1, date2, z1);
            } else {// 20点后驶入
                return countNightParkingFee(date1, date2, z1);
            }
        }

    }*/

    /**
     * 计算停车费
     *
     * @param time1
     *            驶入时间
     * @param time2
     *            驶出时间
     * @return 应收停车费
     */
    public static double countParkingFee(Date time1, Date time2) {
        //区域(1- 一类区域,2- 二类区域,3- 三类区域)
        Integer z1 = 1;
        //工作日标识(1- 工作日 , 2- 休息日)
        //Integer d1 = 1;
        long between = (time2.getTime() - time1.getTime()) / 1000;
        long minute1 = between % 60 > 0 ? between / 60 + 1 : between / 60;
        //停车时间小于30分钟不收费
        if (minute1 <= 30) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time1);
        //工作日收费时段8点到20点
        if(!isHolidayOrFestival(time1)){
            calendar.set(Calendar.HOUR_OF_DAY, 8);
        } else{ //非工作日收费时间10点到20点
            calendar.set(Calendar.HOUR_OF_DAY, 10);
        }
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 8点或10点前驶入
        if (time1.before(calendar.getTime())) {
            return countNightParkingFee(time1, time2, z1);
        } else {
            // 8点后驶入
            calendar.set(Calendar.HOUR_OF_DAY, 20);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // 20点前驶入
            if (time1.before(calendar.getTime())) {
                return countNormalParkingFee(time1, time2, z1);
            } else {// 20点后驶入
                return countNightParkingFee(time1, time2, z1);
            }
        }

    }

    /**
     * 计算正常停车费<br/>
     *  工作日:收费时段是从早上08:00到晚上20:00
     *  非工作日：收费时段是从早上10:00到晚上20:00
     * 1一类区域
     *  1)工作日：首半小时免费；0.5小时到3小时，5元每小时；3小时之后，10元每小时
     *  2) 非工作日：首半小时免费；0.5小时到3小时，2.5元每小时；3小时之后，5元每小时
     * 2二类区域
     *  1) 工作日：首半小时免费；0.5小时到3小时，3元每小时；3小时之后，6元每小时
     *  2) 非工作日：首半小时免费；0.5小时到3小时，1.5元每小时；3小时之后，3元每小时
     * 3三类区域
     *  1)工作日：首半小时免费；0.5小时到3小时，2元每小时；3小时之后，4元每小时
     *  2)非工作日：首半小时免费；0.5小时到3小时，1元每小时；3小时之后，4元每小时
     * @param time1
     *            驶入时间
     * @param time2
     *            驶出时间
     * @return 应收停车费
     */
    private static double countNormalParkingFee(Date time1, Date time2, Integer z1) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time1);
        boolean isHoliday = isHolidayOrFestival(time1);
        //工作日收费时段8点到20点
        if(!isHoliday){
            calendar.set(Calendar.HOUR_OF_DAY, 8);
        } else{ //非工作日收费时间10点到20点
            calendar.set(Calendar.HOUR_OF_DAY, 10);
        }
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 在8点或10点前驶入
        if (time1.before(calendar.getTime())) {
            return countNightParkingFee(time1, time2, z1);
        } else {// 进入时间在8点或10点以后
            calendar.set(Calendar.HOUR_OF_DAY, 20);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // 20点前驶出
            if (time2.before(calendar.getTime())) {
                long interval = (time2.getTime() - time1.getTime())
                        / HALF_HOUR_MILLIS;
                long mod = (time2.getTime() - time1.getTime())
                        % HALF_HOUR_MILLIS;
                if (mod > 0) {
                    interval = interval + 1;
                }// 大于半小时小于三小时,这里的停车总时长肯定是大于半小时的
                if (interval <= 6) {
                    //一类区域
                    if (z1 == 1) {
                        //工作日
                        if (!isHoliday) {
                            return interval * 5;
                        } else {
                            return interval * 2.5;
                        }
                        //二类区域
                    } else if (z1 == 2) {
                        if (!isHoliday) {
                            return interval * 3;
                        } else {
                            return interval * 1.5;
                        }
                        //三类区域
                    } else if (z1 == 3) {
                        if (!isHoliday) {
                            return interval * 2;
                        } else {
                            return interval;
                        }
                    }
                } else {// 大于小时内
                    if (z1 == 1) {//一类区域
                        if (!isHoliday) {//工作日
                            return 5*6 + (interval - 6)* 10;
                        } else {
                            return 2.5*6 + (interval -6)* 5;
                        }
                    } else if (z1 == 2) {//二类区域
                        if (!isHoliday) {
                            return 3*6 + (interval - 6) * 6;
                        } else {
                            return 1.5*6 + (interval - 6) * 3;
                        }
                    } else if (z1 == 3) {//三类区域
                        if (!isHoliday) {
                            return 2*6 + (interval - 6)* 4;
                        } else {
                            return 6+ (interval -6)* 2;
                        }
                    }
                }
            } else {// 超过20点
                long interval = (calendar.getTimeInMillis()  - time1.getTime())
                        / HALF_HOUR_MILLIS;
                long mod = (calendar.getTimeInMillis()  - time1.getTime())
                        % HALF_HOUR_MILLIS;
                if (mod > 0) {
                    interval = interval + 1;
                }
                if (interval <= 6) {// 大于半小时小于三小时
                    if (z1 == 1) {//一类区域
                        if (!isHoliday) {//工作日
                            return interval * 5 + countNightParkingFee(calendar.getTime(), time2, z1);
                        } else {
                            return interval * 2.5 + countNightParkingFee(calendar.getTime(), time2, z1);
                        }
                    } else if (z1 == 2) {//二类区域
                        if (!isHoliday) {
                            return interval * 3 + countNightParkingFee(calendar.getTime(), time2, z1);
                        } else {
                            return interval * 1.5 + countNightParkingFee(calendar.getTime(), time2, z1);
                        }
                    } else {//三类区域
                        if (!isHoliday) {
                            return interval * 2 + countNightParkingFee(calendar.getTime(), time2, z1);
                        } else {
                            return interval + countNightParkingFee(calendar.getTime(), time2, z1);
                        }
                    }
                } else {
                    if (z1 == 1) {//一类区域
                        if (!isHoliday) {//工作日
                            return 5*6 + (interval - 6)* 10 + countNightParkingFee(calendar.getTime(), time2, z1);
                        } else {
                            return 2.5*6 + (interval -6)* 5 + countNightParkingFee(calendar.getTime(), time2, z1);
                        }
                    } else if (z1 == 2) {//二类区域
                        if (!isHoliday) {
                            return 3*6 + (interval - 6) * 6 + countNightParkingFee(calendar.getTime(), time2, z1);
                        } else {
                            return 1.5*6 + (interval - 6) * 3 + countNightParkingFee(calendar.getTime(), time2, z1);
                        }
                    } else {//三类区域
                        if (!isHoliday) {
                            return 2*6 + (interval - 6)* 4 + countNightParkingFee(calendar.getTime(), time2, z1);
                        } else {
                            return 6+ (interval -6)* 2 + countNightParkingFee(calendar.getTime(), time2, z1);
                        }
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 计算深夜停车费<br/>
     * <br/>
     *
     * @param time1
     *            驶入时间
     * @param time2
     *            驶出时间
     * @return 应收停车费
     */
    private static double countNightParkingFee(Date time1, Date time2, Integer z1) {


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time1);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 在20点前驶入
        if (time1.before(calendar.getTime())) {
            //在8点或10点后驶入
           //calendar.set(Calendar.HOUR_OF_DAY, 0);
            /*if(!isHolidayOrFestival(time1)){
                calendar.set(Calendar.HOUR_OF_DAY, 8);
            }else {
                calendar.set(Calendar.HOUR_OF_DAY, 10);
            }
            if (time1.after(calendar.getTime())){
                return countNormalParkingFee(calendar.getTime(), time2, z1);
            }else{*/
                if(!isHolidayOrFestival(time1)){
                    calendar.set(Calendar.HOUR_OF_DAY, 8);
                }else {
                    calendar.set(Calendar.HOUR_OF_DAY, 10);
                }
                //8点或10点以后驶出
                if(time2.after(calendar.getTime())){
                    return countNormalParkingFee(calendar.getTime(), time2, z1);
                }else {
                    return 0;
                }
            //}

        } else {// 在20点后驶入
            /*Boolean isHoliday = false;
            isHoliday = isHolidayOrFestival(time1);*/
            calendar.add(Calendar.DATE, 1);
            //工作日收费时段8点到20点
            if(!nextDayIsHolidayOrFestival(time1)){
                calendar.set(Calendar.HOUR_OF_DAY, 8);
            } else{//非工作日收费时间10点到20点
                calendar.set(Calendar.HOUR_OF_DAY, 10);
            }
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // 在次天8点或10点前离开
            if (time2.before(calendar.getTime())) {
                return 0;
            } else {
                /*long interval = (time2.getTime() - time1.getTime())
                        / HALF_HOUR_MILLIS;
                long mod = (time2.getTime() - time1.getTime())
                        % HALF_HOUR_MILLIS;
                if (mod > 0) {
                    interval = interval + 1;
                }*/
                return countNormalParkingFee(calendar.getTime(), time2 ,z1 );
            }
        }
    }

    /**
     * 判断当天是否是节假日 节日只包含1.1；5.1；10.1
     *
     * @param date 时间
     * @return 非工作时间：true;工作时间：false
     */
    public static boolean isHolidayOrFestival(Date date) {
        boolean result = false;
        boolean isHolidayTmp = isHoliday(date);
        if (isHolidayTmp) {
            result = true;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            //周末直接为非工作时间
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                result = true;
            } /*else {//周内9点到17:30为工作时间
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                if (hour < 9 || (hour == 17 && minute > 30) || hour >= 18) {
                    result = true;
                }
            }*/
        }
        return result;
    }

    /**
     * 判断次日是否是节假日 节日只包含1.1；5.1；10.1
     *
     * @param date 时间
     * @return 非工作时间：true;工作时间：false
     */
    public static boolean nextDayIsHolidayOrFestival(Date date) {
        boolean result = false;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int day1 = c1.get(Calendar.DATE);
        c1.set(Calendar.DATE, day1 + 1);
        boolean isHolidayTmp = isHoliday(c1.getTime());
        if (isHolidayTmp) {
            result = true;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(c1.getTime());
            //周末直接为非工作时间
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                result = true;
            } /*else {//周内9点到17:30为工作时间
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                if (hour < 9 || (hour == 17 && minute > 30) || hour >= 18) {
                    result = true;
                }
            }*/
        }
        return result;
    }
    /**
     * 根据判断当前时间是否是节日
     *
     * @param date
     *            时间
     * @return
     */
    private static boolean isHoliday(Date date) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        String dateStr = sdf.format(date);
        if (holidays.size() > 0) {
            for (String holiday : holidays) {
                if (holiday.equals(dateStr)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 日期转字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToStringFormat(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time1;
        Date time2;
        Long t1;
        Long t2;
        //1
        time1 = format.parse("2020-03-06 04:00:00");
        time2 = format.parse("2020-03-06 10:32:00");
        System.out.println(countParkingFee(time1, time2));
        System.out.println(System.currentTimeMillis());
        t1 = time1.getTime();
        t2 = time2.getTime();
        //除以1000是为了转换成秒
        long between = (t2 - t1) / 1000;
        long minute1 = between % 60 > 0 ? between / 60 + 1 : between / 60;
        System.out.println("秒：" + between + " 分：" + minute1);

       /* //18
        time1 = format.parse("2015-10-01 08:00:00");
        time2 = format.parse("2015-10-01 13:36:30");
        System.out.println(countParkingFee(time1, time2));

        //42
        time1 = format.parse("2015-10-01 08:00:00");
        time2 = format.parse("2015-10-01 21:59:59");
        System.out.println(countParkingFee(time1, time2));

        //52
        time1 = format.parse("2015-10-01 08:00:00");
        time2 = format.parse("2015-10-01 22:00:00");
        System.out.println(countParkingFee(time1, time2));

        //16
        time1 = format.parse("2015-10-01 21:36:23");
        time2 = format.parse("2015-10-02 08:36:30");
        System.out.println(countParkingFee(time1, time2));

        //10
        time1 = format.parse("2015-10-01 22:36:23");
        time2 = format.parse("2015-10-02 06:36:30");
        System.out.println(countParkingFee(time1, time2));

        //12
        time1 = format.parse("2015-10-01 10:36:23");
        time2 = format.parse("2015-10-01 13:36:30");
        System.out.println(countParkingFee(time1, time2));

        //6
        time1 = format.parse("2015-10-01 10:36:23");
        time2 = format.parse("2015-10-01 11:36:30");
        System.out.println(countParkingFee(time1, time2));

        //3
        time1 = format.parse("2015-10-01 10:36:23");
        time2 = format.parse("2015-10-01 11:26:30");
        System.out.println(countParkingFee(time1, time2));

        //6
        time1 = format.parse("2015-10-01 11:16:23");
        time2 = format.parse("2015-10-01 12:26:30");
        System.out.println(countParkingFee(time1, time2));

        //3
        time1 = format.parse("2015-10-01 11:00:00");
        time2 = format.parse("2015-10-01 12:00:00");
        System.out.println(countParkingFee(time1, time2));

        //0
        time1 = format.parse("2015-10-01 08:00:00");
        time2 = format.parse("2015-10-01 08:00:00");
        System.out.println(countParkingFee(time1, time2));

        //52
        time1 = format.parse("2015-10-01 08:00:00");
        time2 = format.parse("2015-10-02 08:00:00");
        System.out.println(countParkingFee(time1, time2));

        //58
        time1 = format.parse("2015-10-01 08:00:00");
        time2 = format.parse("2015-10-02 10:00:00");
        System.out.println(countParkingFee(time1, time2));

        time1 = format.parse("2015-10-01 08:00:00");
        time2 = format.parse("2016-10-01 08:00:00");
        System.out.println(countParkingFee(time1, time2));*/
    }
}
