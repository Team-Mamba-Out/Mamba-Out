package org.mamba.Utils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
public class OrderSerial {
    // 生成18位订单编号，返回 long 类型
    public static long generateOrderNumber() {
        // 获取当前日期部分（格式：yyyyMMddHHmmss），长度为14位
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datePart = sdf.format(new Date());

        // 生成随机数部分（4位）
        Random random = new Random();
        int randomPart = random.nextInt(10000); // 生成0到9999的随机数

        // 拼接成18位字符串
        String orderNumberStr = datePart + String.format("%04d", randomPart);

        // 转换为long类型
        return Long.parseLong(orderNumberStr);
    }
}
