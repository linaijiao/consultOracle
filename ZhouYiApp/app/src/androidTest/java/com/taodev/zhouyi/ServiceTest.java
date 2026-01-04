package com.taodev.zhouyi;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry; // 关键导入
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.taodev.zhouyi.calendar.CommonCalendarService;
import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.Pillar;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDateTime;

@RunWith(AndroidJUnit4.class)
public class ServiceTest {
    @Test
    public void testServiceWithRealContext() throws IOException{

        System.out.println("--- 开始测试 ---");
        // 1. 获取真机 Context (为了读取 assets)
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // === 侦探代码开始 ===
        Log.d("AssetCheck", "正在检查 assets 目录...");
        String[] files = appContext.getAssets().list(""); // 列出根目录所有文件
        if (files != null) {
            for (String file : files) {
                Log.d("AssetCheck", "找到文件: " + file);
            }
        } else {
            Log.e("AssetCheck", "Assets 目录是空的！");
        }
        // === 侦探代码结束 ===
        CommonCalendarService service = new CommonCalendarService(appContext);

        System.out.println("-Testing: 验证立春分界线逻辑-");
        // --- 场景 A: 2024年2月3日 (立春前) ---
        // 2024年立春是 2月4日。所以在2月3日出生的人，年柱应该是 2023年(癸卯)
        FourPillarsInput inputBefore = new FourPillarsInput();
        inputBefore.setLocalDateTime(LocalDateTime.of(2024, 2, 3, 12, 0));
        // 假设你还需要设置地点来计算 UTC，这里假设 TimeConverter 能处理默认情况或你手动设
        inputBefore.setLongitude(120.0); // 北京时间经度
        inputBefore.setTimezoneIdStr("Asia/Shanghai"); // 如果你的 Input 需要时区

        Pillar yearPillarBefore = service.getYearPillar(inputBefore);
        System.out.println("2024-02-03 (立春前) 计算结果: " + yearPillarBefore.getStem() + yearPillarBefore.getBranch());

        // 断言：应该是 癸(Gui) 卯(Mao)
        // 注意：这里要看你的 bazi_rules.json 里汉字是怎么写的，或者是拼音
        assertEquals("癸", yearPillarBefore.getStem());
        assertEquals("卯", yearPillarBefore.getBranch());


        // --- 场景 B: 2024年2月5日 (立春后) ---
        // 应该是 2024年(甲辰)
        FourPillarsInput inputAfter = new FourPillarsInput();
        inputAfter.setLocalDateTime(LocalDateTime.of(2024, 2, 5, 12, 0));
        inputAfter.setLongitude(120.0);
        inputAfter.setTimezoneIdStr("Asia/Shanghai"); // 如果你的 Input 需要时区

        Pillar yearPillarAfter = service.getYearPillar(inputAfter);
        System.out.println("2024-02-05 (立春后) 计算结果: " + yearPillarAfter.getStem() + yearPillarAfter.getBranch());

        // 断言：应该是 甲(Jia) 辰(Chen)
        assertEquals("甲", yearPillarAfter.getStem());
        assertEquals("辰", yearPillarAfter.getBranch());
        System.out.println("Service 创建成功: " + service);

        FourPillarsInput input = new FourPillarsInput();
        LocalDateTime time = LocalDateTime.of(2024, 2, 4, 17, 0);
        input.setLocalDateTime(time);
        input.setLongitude(120.0); // 北京时间经度
        input.setTimezoneIdStr("Asia/Shanghai"); // 如果你的 Input 需要时

        Pillar[] pillars = service.getFourPillars(input);
        Log.d("BaziTest", "年: " + pillars[0].getStem() + pillars[0].getBranch());
        Log.d("BaziTest", "月: " + pillars[1].getStem() + pillars[1].getBranch());
        Log.d("BaziTest", "日: " + pillars[2].getStem() + pillars[2].getBranch());
        Log.d("BaziTest", "时: " + pillars[3].getStem() + pillars[3].getBranch());

    }
}
