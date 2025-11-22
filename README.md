ZhouYiApp - 四柱八字模块项目概述这是《周易程序 V1.0》的 Android 应用 - 专注于“四柱八字”（Four Pillars）模块。它根据用户输入实现精确的历法计算、四柱排盘、五行旺衰分析、十神和流年大运。版本：V1.3 (2025-11-22)
团队：崔炎朋: 引擎层（算法、接口、POJO）
李博: 服务/数据层（门面、仓库、JSON 数据）
林娇娇: UI/VM 层（活动、视图模型、XML、转换）

遵守《阿里巴巴 Java 开发手册》：严禁拼音英文混合，使用接口化设计以确保可扩展性。目录结构修改原因从旧版到 V1.3 的修改原因命名规范化：替换拼音（如 "bazi"）为纯英文（如 "fourpillars"），符合阿里巴巴规范。
分层解耦：将接口、实现、POJO 和 UI 分开，遵循 MVVM 和单一职责原则。确保引擎层可独立测试、可复用（无 Android 依赖）。
责任清晰：明确标注责任人，避免阻塞（如接口由实现者定义，而非 UI 负责人）。
Context 隔离：仅数据层使用 Context，其他层纯 POJO，提高可测试性。
可扩展性：历法引擎可复用于未来模块（如紫微斗数），符合概要设计“开放性”要求。

旧结构已废弃——立即按新结构调整所有代码。


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
|   |   |   |   |   |-- zhouyi/
|   |   |   |   |   |   |
|   |   |   |   |   |   |-- core/                              // 公共异常 + 顶层业务接口
|   |   |   |   |   |   |   |-- exception/
|   |   |   |   |   |   |   |   └── CalculationException.java      // 林娇娇
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   └── service/                       // 李博定义并实现
|   |   |   |   |   |   |       ├── IFourPillarsService.java         // 李博
|   |   |   |   |   |   |       └── IFourPillarsRepository.java      // 李博
|   |   |   |   |   |   |
|   |   |   |   |   |   |-- domain/                            // 所有实体类（POJO）—— 崔炎朋定义
|   |   |   |   |   |   |   ├── Pillar.java                     // 崔炎朋
|   |   |   |   |   |   |   ├── LuckPillar.java                 // 崔炎朋
|   |   |   |   |   |   |   ├── FourPillarsInput.java           // 崔炎朋
|   |   |   |   |   |   |   ├── FourPillarsResult.java          // 崔炎朋
|   |   |   |   |   |   |   ├── FourPillarsChart.java           // 崔炎朋
|   |   |   |   |   |   |   └── ...（其他实体类）               // 崔炎朋
|   |   |   |   |   |   |
|   |   |   |   |   |   |-- engine/                            // 引擎层接口 —— 崔炎朋定义
|   |   |   |   |   |   |   ├── ICalendarService.java               // 崔炎朋
|   |   |   |   |   |   |   └── IFourPillarsAnalysisService.java    // 崔炎朋
|   |   |   |   |   |   |
|   |   |   |   |   |   |-- calendar/                          // 通用历法引擎实现 —— 崔炎朋
|   |   |   |   |   |   |   └── CommonCalendarService.java          // 崔炎朋
|   |   |   |   |   |   |
|   |   |   |   |   |   |-- fourpillars/
|   |   |   |   |   |       |
|   |   |   |   |   |       ├── analysis/                      // 八字分析引擎实现 —— 崔炎朋
|   |   |   |   |   |       |   └── FourPillarsAnalysisService.java // 崔炎朋
|   |   |   |   |   |       |
|   |   |   |   |   |       ├── data/                          // 数据仓库 —— 李博
|   |   |   |   |   |       |   └── FourPillarsRepository.java       // 李博
|   |   |   |   |   |       |
|   |   |   |   |   |       ├── service/                       // 门面/业务组装层 —— 李博
|   |   |   |   |   |       |   └── FourPillarsService.java          // 李博
|   |   |   |   |   |       |
|   |   |   |   |   |       └── ui/                            // UI + ViewModel + Adapter + Converter —— 林娇娇
|   |   |   |   |   |           ├── FourPillarsActivity.java         // 林娇娇
|   |   |   |   |   |           ├── FourPillarsViewModel.java        // 林娇娇
|   |   |   |   |   |           ├── FourPillarsInputUiModel.java     // 林娇娇（可选：用户输入模型）
|   |   |   |   |   |           ├── FourPillarsDisplayModel.java     // 林娇娇（可选：展示输出模型）
|   |   |   |   |   |           ├── DisplayConverter.java            // 林娇娇（可选：输入输出转换辅助类）
|   |   |   |   |   |           └── adapter/
|   |   |   |   |   |               └── LuckPillarAdapter.java       // 林娇娇
|   |   |   |
|   |   |   |-- res/
|   |   |   |   └── layout/                              // 林娇娇负责所有XML
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
|   |       └── java/com/zhouyi/...
|   |           ├── calendar/
|   |           │   └── CommonCalendarServiceTest.java       // 崔炎朋
|   |           └── fourpillars/analysis/
|   |               └── FourPillarsAnalysisServiceTest.java  // 崔炎朋