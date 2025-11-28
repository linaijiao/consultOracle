# 周易程序 - 四柱八字模块

## 项目简介
《周易程序 V1.0》Android 应用，目前开发四柱八字排盘核心模块。  
包含完整历法计算、四柱排盘、五行旺衰、十神、大运等功能。

**开发团队**（3人小分队）：
- 崔炎朋：所有算法引擎 + 接口 + POJO（核心）
- 李博：FourPillarsService、Repository、assets 数据
- 林爱娇：页面、ViewModel、XML、数据转换

## 当前状态
- 所有设计文档已更新到 V1.3 并统一
- 目前允许直接 push 到 main（等代码稳定后再开分支流程）

## 目录结构

### V1.3修改说明
- com.taodev.zhouyi.calendar          → 专心做历法，干净，可直接抽成独立 library
- com.taodev.zhouyi.domain            → 所有 POJO 统一放这儿，以后紫微斗数也能直接用 Pillar
- com.taodev.zhouyi.engine            → 所有底层算法接口都在这儿，一目了然
- com.taodev.zhouyi.fourpillars.ui    → 只放四柱八字专属的 UI 和转换逻辑

```text
ZhouYiApp/
|
|-- app/
|   |
|   |-- src/
|   |   |
|   |   |-- main/
|   |   |   |
|   |   |   |-- java/
|   |   |   |   |-- com/
|   |   |   |   |   |-- taodev/               <-- 新增中间层
|   |   |   |   |   |   |-- zhouyi/
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- core/                              // 公共异常 + 顶层业务接口
|   |   |   |   |   |   |   |   |-- exception/
|   |   |   |   |   |   |   |   |   └── CalculationException.java      // 林爱娇
|   |   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |   └── service/                       // 李博定义并实现
|   |   |   |   |   |   |   |       ├── IFourPillarsService.java         // 李博
|   |   |   |   |   |   |   |       └── IFourPillarsRepository.java      // 李博
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- domain/                            // 所有实体类（POJO）—— 崔炎朋定义
|   |   |   |   |   |   |   |   ├── Pillar.java                     // 崔炎朋
|   |   |   |   |   |   |   |   ├── LuckPillar.java                 // 崔炎朋
|   |   |   |   |   |   |   |   ├── FourPillarsInput.java           // 崔炎朋
|   |   |   |   |   |   |   |   ├── FourPillarsResult.java          // 崔炎朋
|   |   |   |   |   |   |   |   ├── FourPillarsChart.java           // 崔炎朋
|   |   |   |   |   |   |   |   └── ...（其他实体类）               // 崔炎朋
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- engine/                            // 引擎层接口 —— 崔炎朋定义
|   |   |   |   |   |   |   |   ├── ICalendarService.java               // 崔炎朋
|   |   |   |   |   |   |   |   └── IFourPillarsAnalysisService.java    // 崔炎朋
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- calendar/                          // 通用历法引擎实现 —— 崔炎朋
|   |   |   |   |   |   |   |   └── CommonCalendarService.java          // 崔炎朋
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- fourpillars/
|   |   |   |   |   |   |       |
|   |   |   |   |   |   |       ├── analysis/                      // 八字分析引擎实现 —— 崔炎朋
|   |   |   |   |   |   |       |   └── FourPillarsAnalysisService.java // 崔炎朋
|   |   |   |   |   |   |       |
|   |   |   |   |   |   |       ├── data/                          // 数据仓库 —— 李博
|   |   |   |   |   |   |       |   └── FourPillarsRepository.java       // 李博
|   |   |   |   |   |   |       |
|   |   |   |   |   |   |       ├── service/                       // 门面/业务组装层 —— 李博
|   |   |   |   |   |   |       |   └── FourPillarsService.java          // 李博
|   |   |   |   |   |   |       |
|   |   |   |   |   |   |       └── ui/                            // UI + ViewModel + Adapter —— 林爱娇
|   |   |   |   |   |   |           ├── FourPillarsActivity.java         // 林爱娇
|   |   |   |   |   |   |           ├── FourPillarsViewModel.java        // 林爱娇
|   |   |   |   |   |   |           ├── FourPillarsInputUiModel.java     // 林爱娇
|   |   |   |   |   |   |           ├── FourPillarsDisplayModel.java     // 林爱娇
|   |   |   |   |   |   |           ├── DisplayConverter.java            // 林爱娇
|   |   |   |   |   |   |           └── adapter/
|   |   |   |   |   |   |               └── LuckPillarAdapter.java       // 林爱娇
|   |   |   |
|   |   |   |-- res/
|   |   |   |   └── layout/                              // 林爱娇负责所有XML
|   |   |   |       ├── activity_fourpillars_input.xml
|   |   |   |       ├── activity_fourpillars_result.xml
|   |   |   |       └── item_luck_pillar.xml
|   |   |   |
|   |   |   └── assets/                                  // 李博负责所有JSON数据文件
|   |   |       ├── jieqi/
|   |   |       │   └── jieqi_1900.json ~ jieqi_2100.json
|   |   |       ├── eot.json
|   |   |       └── fourpillars_rules.json
|   |   |
|   |   |-- test/
|   |       └── java/com/taodev/zhouyi/...  <-- 测试包名也要记得对应修改
|   |           ├── calendar/
|   |           │   └── CommonCalendarServiceTest.java       // 崔炎朋
|   |           └── fourpillars/analysis/
|   |               └── FourPillarsAnalysisServiceTest.java  // 崔炎朋
