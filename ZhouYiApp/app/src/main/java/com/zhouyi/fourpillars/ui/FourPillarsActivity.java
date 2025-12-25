package com.zhouyi.fourpillars.ui;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.RadioGroup;
import android.widget.ArrayAdapter;
import java.util.Calendar;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FourPillarsActivity extends AppCompatActivity {

    private RadioGroup genderGroup;
    private RadioGroup calendarGroup;
    private TextView dateLabel;
    private Spinner timeSpinner;
    private EditText locationEditText;
    private Button calculateButton;
    private TextView destinyText;

    private String selectedDate = "1995-01-31";
    private boolean isGregorian = true;

    // 时辰数据
    private String[] timePeriods = {
            "子时 23:00-00:59", "丑时 01:00-02:59", "寅时 03:00-04:59",
            "卯时 05:00-06:59", "辰时 07:00-08:59", "巳时 09:00-10:59",
            "午时 11:00-12:59", "未时 13:00-14:59", "申时 15:00-16:59",
            "酉时 17:00-18:59", "戌时 19:00-20:59", "亥时 21:00-22:59"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourpillars_input);

        initViews();
        setupSpinner();
        setupListeners();
    }

    private void initViews() {
        genderGroup = findViewById(R.id.genderGroup);
        calendarGroup = findViewById(R.id.calendarGroup);
        dateLabel = findViewById(R.id.dateLabel);
        timeSpinner = findViewById(R.id.timeSpinner);
        locationEditText = findViewById(R.id.locationEditText);
        calculateButton = findViewById(R.id.calculateButton);
        destinyText = findViewById(R.id.destinyText);

        // 设置默认选中男性
        genderGroup.check(R.id.maleRadio);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, timePeriods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        // 设置默认选中午时（索引6）
        timeSpinner.setSelection(6);
    }

    private void setupListeners() {
        // 日期选择
        dateLabel.setOnClickListener(v -> showDatePickerDialog());

        // 日历类型切换
        calendarGroup.setOnCheckedChangeListener((group, checkedId) -> {
            isGregorian = (checkedId == R.id.gregorianRadio);
            updateDateLabel();
        });

        // 开始排盘按钮
        calculateButton.setOnClickListener(v -> calculateBazi());

        // 地址选择
        locationEditText.setOnClickListener(v -> showLocationDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(selectedDate);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(),
                            "%d-%02d-%02d", year, month + 1, dayOfMonth);
                    updateDateLabel();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateDateLabel() {
        String calendarType = isGregorian ? "新历" : "农历";
        String displayDate = formatDateForDisplay(selectedDate);
        dateLabel.setText(String.format("%s：%s", calendarType, displayDate));
    }

    private String formatDateForDisplay(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择出生地点");

        final String[] provinces = {"北京", "上海", "广东", "江苏", "浙江", "其他"};

        builder.setItems(provinces, (dialog, which) -> {
            locationEditText.setText(provinces[which]);
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void calculateBazi() {
        // 获取用户输入
        boolean isMale = genderGroup.getCheckedRadioButtonId() == R.id.maleRadio;
        String birthTime = timeSpinner.getSelectedItem().toString();
        String location = locationEditText.getText().toString();

        // 显示加载状态
        calculateButton.setText("排盘中...");
        calculateButton.setEnabled(false);

        // 模拟排盘计算（实际应用中这里应该是真正的八字计算逻辑）
        new Handler().postDelayed(() -> {
            // 这里应该是实际的八字计算逻辑
            String baziResult = generateBaziResult(isMale, selectedDate, birthTime, location);

            // 显示结果
            destinyText.setText(baziResult);

            // 恢复按钮状态
            calculateButton.setText("开始排盘");
            calculateButton.setEnabled(true);

            // 显示结果对话框
            showResultDialog(baziResult);

        }, 1500);
    }

    private String generateBaziResult(boolean isMale, String date, String time, String location) {
        // 简化的八字结果生成（实际应用需要复杂的八字计算逻辑）
        String gender = isMale ? "男" : "女";
        String calendarType = isGregorian ? "公历" : "农历";

        return String.format("八字排盘结果\n" +
                        "性别：%s\n" +
                        "出生日期：%s\n" +
                        "出生时辰：%s\n" +
                        "出生地点：%s\n" +
                        "四柱：乙亥 戊寅 壬戌 丙午\n" +
                        "命理分析：需根据具体算法生成详细分析",
                gender, dateLabel.getText(), time,
                location.isEmpty() ? "未填写" : location);
    }

    private void showResultDialog(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("八字排盘结果");
        builder.setMessage(result);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("分享", (dialog, which) -> {
            // 分享功能
            shareResult(result);
        });
        builder.show();
    }

    private void shareResult(String result) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "我的八字排盘结果：\n" + result);
        startActivity(Intent.createChooser(shareIntent, "分享八字结果"));
    }
}