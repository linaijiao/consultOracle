package com.taodev.zhouyi.fourpillars.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FourPillarsViewModel extends AppCompatActivity {

    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourpillars_result);

        textViewResult = findViewById(R.id.textViewResult);

        // 获取传递过来的数据
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name");
            String gender = extras.getString("gender");
            String trueTime = extras.getString("trueTime");
            String province = extras.getString("province");
            String city = extras.getString("city");
            String calendarType = extras.getString("calendarType");
            String year = extras.getString("year");
            String month = extras.getString("month");
            String day = extras.getString("day");
            String hour = extras.getString("hour");
            String minute = extras.getString("minute");

            // 在这里使用获取到的数据进行八字排盘计算
            //  （请替换成您自己的八字排盘逻辑）

            String result = "姓名：" + name + "\n" +
                    "性别：" + gender + "\n" +
                    "真太阳时：" + trueTime + "\n" +
                    "出生地：" + province + " " + city + "\n" +
                    "日期类型：" + calendarType + "\n" +
                    "出生日期：" + year + "年" + month + "月" + day + "日" + "\n" +
                    "出生时辰：" + hour + "时" + minute + "分\n\n" +
                    "八字排盘结果：\n" +
                    "这里显示您的八字排盘结果...";  //  替换成真实的排盘结果

            textViewResult.setText(result);

        }
    }
}