package com.taodev.zhouyi.fourpillars.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taodev.zhouyi.R;
import com.taodev.zhouyi.domain.Gender;
import com.taodev.zhouyi.fourpillars.ui.adapter.LuckPillarAdapter;
import com.taodev.zhouyi.domain.Pillar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//  这是一个标准的 MVVM View 层 Activity
public class FourPillarsResultActivity extends AppCompatActivity {

    // 1. 定义 UI 控件
    private TextView tvBasicInfo;
    private TextView tvOtherInfo;
    private TextView tvYearColumn,tvMonthColumn,tvDayColumn,tvHourColumn;
    private TextView tvYearKongwang,tvMonthKongwang,tvDayKongwang,tvHourKongwang;
    private TextView tvYearLifeStage,tvMonthLifeStage,tvDayLifeStage,tvHourLifeStage;
    private TextView tvYearTenGod, tvYearStem, tvYearBranch, tvYearHiddenStems, tvYearNaYin;
    private TextView tvMonthTenGod, tvMonthStem, tvMonthBranch, tvMonthHidden, tvMonthNaYin;
    private TextView tvDayTenGod, tvDayStem, tvDayBranch, tvDayHidden, tvDayNaYin;
    private TextView tvHourTenGod, tvHourStem, tvHourBranch, tvHourHidden, tvHourNaYin;

    private TextView tvYearPillar, tvMonthPillar, tvDayPillar, tvHourPillar;
    private RecyclerView rvLuckPillars; // 显示大运的列表

    // 2. 定义 ViewModel 和 Adapter
    private FourPillarsViewModel viewModel;
    private LuckPillarAdapter luckAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourpillars_result); // 确保 XML名字对

        // A. 初始化视图控件 (请确保 XML 里有这些 ID)
        initViews();

        // B. 初始化 RecyclerView (大运列表)
        initRecyclerView();

        // C. 初始化 ViewModel
        viewModel = new ViewModelProvider(this).get(FourPillarsViewModel.class);

        // D. 接收 Intent 数据并组装成对象
        FourPillarsInputUiModel input = getInputData();

        if (input != null) {
            // E. 核心：让 ViewModel 开始计算
            viewModel.calculate(input);
        }

        // F.  核心：观察数据变化，刷新界面
        // 只要 ViewModel 算好了，这里的代码就会自动执行
        viewModel.displayData.observe(this, model -> {
            if (model == null) return;

            // 1. 设置顶部基础信息
            tvBasicInfo.setText(model.basicInfoText);

            // 2. 设置四柱 (年、月、日、时)
            if (model.pillars != null) { // 假设 DisplayModel 有 Pillar[] fourPillars
                setFourPillars(model.pillars);
            }

            // 3. 设置大运列表 (把数据喂给 Adapter)
            if (model.luckPillars != null && !model.luckPillars.isEmpty()) {
                luckAdapter.setLuckPillars(model.luckPillars);
            } else {
                Log.e("ResultActivity", "大运数据为空");
            }
            // 4. 设置起运，交运日期
            tvOtherInfo.setText(model.startLuckInfo);
        });
    }

    private void setFourPillars(List<FourPillarsDisplayModel.PillarUiModel> pillars) {
        if (pillars == null || pillars.size() < 4) return;
        tvYearColumn.setText(pillars.get(0).getColumnName());
        // 年柱
        tvYearTenGod.setText(pillars.get(0).getHeadTenGod());
        tvYearStem.setText(pillars.get(0).getStemName());
        tvYearBranch.setText(pillars.get(0).getBranchName());
        tvYearLifeStage.setText(pillars.get(0).getLifeStage());
        tvYearNaYin.setText("[" + pillars.get(0).getNaYin() + "]");
        tvYearKongwang.setText(pillars.get(0).getKongWang());

        // 藏干
        StringBuilder yearHiddenBuilder = new StringBuilder();
        List<FourPillarsDisplayModel.HiddenStemUiItem> yearHiddenList = pillars.get(0).getHiddenStemItems();
        if (yearHiddenList != null) {
            for (FourPillarsDisplayModel.HiddenStemUiItem stem : yearHiddenList) {
                if (yearHiddenBuilder.length() > 0) {
                    yearHiddenBuilder.append(" "); // 空格分隔，或 ","
                }
                yearHiddenBuilder.append(stem);
            }
        }

        tvYearHiddenStems.setText(pillars.get(0).hiddenStemString);

        // 月柱（复制改索引）
        tvMonthColumn.setText(pillars.get(1).getColumnName());
        tvMonthTenGod.setText(pillars.get(1).getHeadTenGod());
        tvMonthStem.setText(pillars.get(1).getStemName());
        tvMonthBranch.setText(pillars.get(1).getBranchName());
        tvMonthLifeStage.setText(pillars.get(1).getLifeStage());
        tvMonthNaYin.setText("[" + pillars.get(1).getNaYin() + "]");
        tvMonthKongwang.setText(pillars.get(1).getKongWang());


        StringBuilder monthHiddenBuilder = new StringBuilder();
        List<FourPillarsDisplayModel.HiddenStemUiItem> monthHiddenList = pillars.get(1).getHiddenStemItems();
        if (monthHiddenList != null) {
            for (FourPillarsDisplayModel.HiddenStemUiItem stem : monthHiddenList) {
                if (monthHiddenBuilder.length() > 0) monthHiddenBuilder.append(" ");
                monthHiddenBuilder.append(stem);
            }
        }
        tvMonthHidden.setText(pillars.get(1).hiddenStemString);

        // 日柱
        tvDayColumn.setText(pillars.get(2).getColumnName());
        tvDayTenGod.setText(pillars.get(2).getHeadTenGod());
        tvDayStem.setText(pillars.get(2).getStemName());
        tvDayBranch.setText(pillars.get(2).getBranchName());
        tvDayLifeStage.setText(pillars.get(2).getLifeStage());
        tvDayNaYin.setText("[" + pillars.get(2).getNaYin() + "]");
        tvDayKongwang.setText(pillars.get(2).getKongWang());

        StringBuilder dayHiddenBuilder = new StringBuilder();
        List<FourPillarsDisplayModel.HiddenStemUiItem> dayHiddenList = pillars.get(2).getHiddenStemItems();
        if (dayHiddenList != null) {
            for (FourPillarsDisplayModel.HiddenStemUiItem stem : dayHiddenList) {
                if (dayHiddenBuilder.length() > 0) dayHiddenBuilder.append(" ");
                dayHiddenBuilder.append(stem);
            }
        }
        tvDayHidden.setText(pillars.get(2).hiddenStemString);

        // 时柱
        tvHourColumn.setText(pillars.get(3).getColumnName());
        tvHourTenGod.setText(pillars.get(3).getHeadTenGod());
        tvHourStem.setText(pillars.get(3).getStemName());
        tvHourBranch.setText(pillars.get(3).getBranchName());
        tvHourLifeStage.setText(pillars.get(3).getLifeStage());
        tvHourNaYin.setText("[" + pillars.get(3).getNaYin() + "]");
        tvHourKongwang.setText(pillars.get(3).getKongWang());

        StringBuilder hourHiddenBuilder = new StringBuilder();
        List<FourPillarsDisplayModel.HiddenStemUiItem> hourHiddenList = pillars.get(3).getHiddenStemItems();

        if (hourHiddenList != null) {
            for (FourPillarsDisplayModel.HiddenStemUiItem stem : hourHiddenList) {
                if (hourHiddenBuilder.length() > 0) hourHiddenBuilder.append(" ");
                hourHiddenBuilder.append(stem);
            }
        }
        tvHourHidden.setText(pillars.get(3).hiddenStemString);
    }

    // --- 辅助方法 ---

    private void initViews() {
        // 这里的 ID 必须和你 activity_fourpillars_result.xml 里的对应！
        //  xml 里还没有这些 ID，请去添加
        tvBasicInfo = findViewById(R.id.base_info);
        tvOtherInfo = findViewById(R.id.other_info);

//        tvYearPillar = findViewById(R.id.layout_year);
        // 四柱容器（LinearLayout）
        LinearLayout layoutYear = findViewById(R.id.layout_year);
        LinearLayout layoutMonth = findViewById(R.id.layout_month);
        LinearLayout layoutDay = findViewById(R.id.layout_day);
        LinearLayout layoutHour = findViewById(R.id.layout_hour);

        tvYearColumn = layoutYear.findViewById(R.id.tv_pillar_column);
        tvYearTenGod = layoutYear.findViewById(R.id.tv_stem_tengod);
        tvYearStem = layoutYear.findViewById(R.id.tv_stem);
        tvYearBranch = layoutYear.findViewById(R.id.tv_branch);
        tvYearHiddenStems = layoutYear.findViewById(R.id.tv_hidden_stems);
        tvYearLifeStage = layoutYear.findViewById(R.id.tv_pillar_lifeStage);
        tvYearNaYin = layoutYear.findViewById(R.id.tv_pillar_nayin);
        tvYearKongwang = layoutYear.findViewById(R.id.tv_pillar_kongwang);

        // 月柱（复制粘贴改变量名）
        tvMonthColumn = layoutMonth.findViewById(R.id.tv_pillar_column);
        tvMonthTenGod = layoutMonth.findViewById(R.id.tv_stem_tengod);
        tvMonthStem = layoutMonth.findViewById(R.id.tv_stem);
        tvMonthBranch = layoutMonth.findViewById(R.id.tv_branch);
        tvMonthHidden = layoutMonth.findViewById(R.id.tv_hidden_stems);
        tvMonthLifeStage = layoutMonth.findViewById(R.id.tv_pillar_lifeStage);
        tvMonthNaYin = layoutMonth.findViewById(R.id.tv_pillar_nayin);
        tvMonthKongwang = layoutMonth.findViewById(R.id.tv_pillar_kongwang);


        // 日柱
        tvDayColumn = layoutDay.findViewById(R.id.tv_pillar_column);
        tvDayTenGod = layoutDay.findViewById(R.id.tv_stem_tengod);
        tvDayStem = layoutDay.findViewById(R.id.tv_stem);
        tvDayBranch = layoutDay.findViewById(R.id.tv_branch);
        tvDayHidden = layoutDay.findViewById(R.id.tv_hidden_stems);
        tvDayLifeStage = layoutDay.findViewById(R.id.tv_pillar_lifeStage);
        tvDayNaYin = layoutDay.findViewById(R.id.tv_pillar_nayin);
        tvDayKongwang = layoutDay.findViewById(R.id.tv_pillar_kongwang);

        // 时柱
        tvHourColumn = layoutHour.findViewById(R.id.tv_pillar_column);
        tvHourTenGod = layoutHour.findViewById(R.id.tv_stem_tengod);
        tvHourStem = layoutHour.findViewById(R.id.tv_stem);
        tvHourBranch = layoutHour.findViewById(R.id.tv_branch);
        tvHourHidden = layoutHour.findViewById(R.id.tv_hidden_stems);
        tvHourLifeStage = layoutHour.findViewById(R.id.tv_pillar_lifeStage);
        tvHourNaYin = layoutHour.findViewById(R.id.tv_pillar_nayin);
        tvHourKongwang = layoutHour.findViewById(R.id.tv_pillar_kongwang);

        rvLuckPillars = findViewById(R.id.rv_luck_list);
    }

    private void initRecyclerView() {
        // 设置布局管理器 (HORIZONTAL 代表横着排，如果是竖着的列表改成 VERTICAL)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvLuckPillars.setLayoutManager(layoutManager);
        rvLuckPillars.setNestedScrollingEnabled(false);
        // 初始化 Adapter (先传一个空列表防止报错)
        luckAdapter = new LuckPillarAdapter(new ArrayList<>());
        rvLuckPillars.setAdapter(luckAdapter);
    }
    // 从 Intent 中提取数据，兼容旧的 String 传递方式和新的 Object 传递方式
    private FourPillarsInputUiModel getInputData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return null;


        // 优先尝试直接获取对象 (如果 InputActivity 已经改成传对象了)
        Serializable obj = extras.getSerializable("input_data");
        if (obj instanceof FourPillarsInputUiModel) {
            return (FourPillarsInputUiModel) obj;
        }

        // 兼容模式：如果传过来的是一堆 String，我们需要在这里把它们变成 Input 对象
        try {
            String name = extras.getString("name");
            String genderStr = extras.getString("gender", "男");
            Gender gender = "男".equals(genderStr) ? Gender.MALE : Gender.FEMALE;

            int year = Integer.parseInt(extras.getString("year"));
            int month = Integer.parseInt(extras.getString("month"));
            int day = Integer.parseInt(extras.getString("day"));
            int hour = Integer.parseInt(extras.getString("hour"));
            int minute = Integer.parseInt(extras.getString("minute"));
            String province = extras.getString("province");
            String city = extras.getString("city");

            // 创建对象
            return new FourPillarsInputUiModel(name,gender,year, month, day, hour, minute, province,city);

        } catch (Exception e) {
            Toast.makeText(this, "数据传递格式错误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }
}