package com.taodev.zhouyi.fourpillars.ui;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel; // 注意：用 AndroidViewModel 才能拿 Context
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.taodev.zhouyi.core.repository.BaziDataLoader;
import com.taodev.zhouyi.domain.BaziRules;
import com.taodev.zhouyi.domain.FourPillarsInput;
import com.taodev.zhouyi.domain.FourPillarsResult;
import com.taodev.zhouyi.facade.BaziFacade;
import com.taodev.zhouyi.calendar.CommonCalendarService;
import com.taodev.zhouyi.fourpillars.analysis.FourPillarsAnalysisService;
import com.taodev.zhouyi.fourpillars.ui.DisplayConverter;
import com.taodev.zhouyi.fourpillars.ui.FourPillarsDisplayModel;
import com.taodev.zhouyi.fourpillars.ui.FourPillarsInputUiModel;

public class FourPillarsViewModel extends AndroidViewModel {

    // 给 Activity 观察的数据
    public final MutableLiveData<FourPillarsDisplayModel> displayData = new MutableLiveData<>();

    // 我们的总指挥
    private BaziFacade baziFacade;

    public FourPillarsViewModel(Application application) {
        super(application);
        initBaziEngine();
    }

    // 1. 初始化引擎 (加载数据、创建服务)
    private void initBaziEngine() {
        // A. 加载 JSON 数据 (这里使用了 Context)
        BaziRules rules = BaziDataLoader.getInstance(getApplication()).getBaziRules();

        // B. 组装服务
        CommonCalendarService calendarService = new CommonCalendarService(getApplication());
        FourPillarsAnalysisService analysisService = new FourPillarsAnalysisService(rules);

        // C. 创建 Facade
        baziFacade = new BaziFacade(calendarService, analysisService);
    }

    // 2. 响应用户点击“排盘”
    public void calculate(FourPillarsInputUiModel uiInput) {
        new Thread(() -> {
            try {
                // A. 把 UI 模型转成 领域模型 (Input)
                FourPillarsInput domainInput = new FourPillarsInput(
                        uiInput.year, uiInput.month, uiInput.day, uiInput.hour,uiInput.minute, uiInput.gender
                );

                // B. 【核心】调用 Facade 进行计算
                FourPillarsResult result = baziFacade.performFullAnalysis(domainInput);

                // C. 【转换】把计算结果 转成 界面显示模型
                FourPillarsDisplayModel displayModel = DisplayConverter.convert(result);
                Log.d("FourPillarsViewModel", "reslut！ :" +displayModel.fourPillars.get(0));

                // D. 更新 LiveData，通知 Activity 刷新
//                displayData.setValue(displayModel);
                displayData.postValue(displayModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
    }

    public LiveData<FourPillarsDisplayModel> getDisplayData() {
        return displayData;
    }
}