package com.taodev.zhouyi.calendar;

import org.junit.Test;
import android.content.Context;
import com.taodev.zhouyi.calendar.CommonCalendarService;
import org.mockito.Mockito;

public class CalendarTest {

    @Test
    public void testMyService() {
        Context mockContext = Mockito.mock(Context.class);
        Mockito.when(mockContext.getApplicationContext()).thenReturn(mockContext);
        System.out.println("---------- 开始测试 ----------");

        try {
            // 1. 创建你的 Service 对象
            CommonCalendarService service = new CommonCalendarService(mockContext);
            System.out.println("Service 初始化成功！");

            // 2. 在这里调用你想测的方法，比如：
            // 假设你的方法叫 run() 或者 getResult()
            // String result = service.someMethod();

            // 3. 打印结果看看
            System.out.println("Service 对象创建成功: " + service);
            // System.out.println("运行结果: " + result);
        } catch (Exception e) {
            System.out.println("初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("---------- 测试结束 ----------");
    }
}