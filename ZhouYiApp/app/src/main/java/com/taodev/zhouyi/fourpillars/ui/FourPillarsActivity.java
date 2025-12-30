package com.taodev.zhouyi.fourpillars.ui;
import com.taodev.zhouyi.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

        // Initialize Year Spinner
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 100; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Initialize Month Spinner
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Initialize Day Spinner
        List<String> days = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            days.add(String.valueOf(i));
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Initialize Hour Spinner
        List<String> hours = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            hours.add(String.valueOf(i));
        }
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHour.setAdapter(hourAdapter);

        // Initialize Minute Spinner
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            minutes.add(String.valueOf(i));
        }
        ArrayAdapter<String> minuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minutes);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMinute.setAdapter(minuteAdapter);

        // Province Spinner Listener (You would need to fetch the corresponding cities)
        spinnerProvince.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = parent.getItemAtPosition(position).toString();
                //TODO: Fetch cities based on selected province and populate the city spinner
                List<String> cityList = new ArrayList<>();
                cityList.add("北京"); //Sample city to show the spinner working
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(FourPillarsActivity.this, android.R.layout.simple_spinner_item, cityList);
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCity.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {

            }
        });

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

                // 验证数据（您可以添加更详细的验证）
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
}