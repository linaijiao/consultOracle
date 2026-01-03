package com.taodev.zhouyi.fourpillars.ui;
import com.taodev.zhouyi.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FourPillarsActivity extends AppCompatActivity {

    private EditText editTextName;
    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupTrueTime;
    private Spinner spinnerProvince;
    private Spinner spinnerCity;
    private RadioGroup radioGroupCalendarType;
    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private Spinner spinnerDay;
    private Spinner spinnerHour;
    private Spinner spinnerMinute;
    private Button buttonSubmit;

    // 省份数组
    private String[] provinces;

    // 城市二维数组，与省份一一对应
    private String[][] cities;

    // 当前选中的省份和城市
    private String selectedProvince = "";
    private String selectedCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourpillars_input);

        editTextName = findViewById(R.id.editTextName);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioGroupTrueTime = findViewById(R.id.radioGroupTrueTime);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerCity = findViewById(R.id.spinnerCity);
        radioGroupCalendarType = findViewById(R.id.radioGroupCalendarType);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerHour = findViewById(R.id.spinnerHour);
        spinnerMinute = findViewById(R.id.spinnerMinute);
        buttonSubmit = findViewById(R.id.buttonSubmit);



        // 初始化 Year Spinner
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 100; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // 初始化 Month Spinner
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // 初始化 Day Spinner
        List<String> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            days.add(String.valueOf(i));
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // 初始化 Hour Spinner
        List<String> hours = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            hours.add(String.valueOf(i));
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHour.setAdapter(hourAdapter);

        // 初始化 Minute Spinner
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            minutes.add(String.valueOf(i));
        }
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minutes);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMinute.setAdapter(minuteAdapter);

        //初始化城市数据
        initCiryData();
        //设置城市数据
        setupSpinners();
        //城市下拉监听事件
        setupListeners();


        //开始排八字
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入数据
                String name = editTextName.getText().toString();
                String gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
                String trueTime = ((RadioButton) findViewById(radioGroupTrueTime.getCheckedRadioButtonId())).getText().toString();
                String province = spinnerProvince.getSelectedItem().toString();
                String city = spinnerCity.getSelectedItem().toString();
                String calendarType = ((RadioButton) findViewById(radioGroupCalendarType.getCheckedRadioButtonId())).getText().toString();
                String year = spinnerYear.getSelectedItem().toString();
                String month = spinnerMonth.getSelectedItem().toString();
                String day = spinnerDay.getSelectedItem().toString();
                String hour = spinnerHour.getSelectedItem().toString();
                String minute = spinnerMinute.getSelectedItem().toString();

                // 验证数据
                if (name.isEmpty()) {
                    Toast.makeText(FourPillarsActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 创建 Intent 并传递数据
                Intent intent = new Intent(FourPillarsActivity.this, FourPillarsViewModel.class);
                intent.putExtra("name", name);
                intent.putExtra("gender", gender);
                intent.putExtra("trueTime", trueTime);
                intent.putExtra("province", province);
                intent.putExtra("city", city);
                intent.putExtra("calendarType", calendarType);
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);
                intent.putExtra("hour", hour);
                intent.putExtra("minute", minute);

                startActivity(intent);
            }
        });
    }

    //初始化城市数据
    private void initCiryData() {
        // 从arrays.xml中获取省份数据
        provinces = getResources().getStringArray(R.array.province_array);

        // 初始化城市二维数组
        cities = new String[provinces.length][];

        // 为每个省份加载对应的城市数组
        for (int i = 0; i < provinces.length; i++) {
            String provinceName = provinces[i];
            String arrayName = getCityArrayName(provinceName);
            int arrayId = getResources().getIdentifier(arrayName, "array", getPackageName());

            if (arrayId != 0) {
                cities[i] = getResources().getStringArray(arrayId);
            } else {
                // 如果没有找到对应的城市数组，使用空数组
                cities[i] = new String[]{"请选择城市"};
            }
        }
    }

    /**
     * 根据省份名称获取对应的城市数组名称
     */
    private String getCityArrayName(String provinceName) {
        switch (provinceName) {
            case "北京市":
                return "cities_beijing";
            case "上海市":
                return "cities_shanghai";
            case "广东省":
                return "cities_guangdong";
            case "江苏省":
                return "cities_jiangsu";
            case "浙江省":
                return "cities_zhejiang";
            case "四川省":
                return "cities_sichuan";
            case "湖北省":
                return "cities_hubei";
            case "山东省":
                return "cities_shandong";
            default:
                return "";
        }
    }
    private void setupSpinners() {
        // 设置省份Spinner的适配器
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                provinces
        );
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        // 设置城市Spinner的适配器（初始为空）
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"请选择城市"}
        );
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);

        // 默认选择第一个（"请选择省份"）
        spinnerProvince.setSelection(0);
        spinnerCity.setSelection(0);
    }

    private void setupListeners() {
        // 省份选择监听
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // 选择了"请选择省份"
                    selectedProvince = "";
                    updateCitySpinner(new String[]{"请选择城市"});
                } else {
                    selectedProvince = provinces[position];
                    updateCitySpinner(cities[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 什么都不做
            }
        });

        // 城市选择监听
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // 选择了"请选择城市"
                    selectedCity = "";
                } else {
                    String[] cityArray = cities[spinnerProvince.getSelectedItemPosition()];
                    selectedCity = cityArray[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 什么都不做
            }
        });

    }
    /**
     * 更新城市Spinner的数据
     */
    private void updateCitySpinner(String[] cityArray) {
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cityArray
        );
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(cityAdapter);
        spinnerCity.setSelection(0);
    }

}
