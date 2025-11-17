### 项目目录结构
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
|   |   |   |   |   |   |-- core/ (林娇娇: 核心接口/契约)
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- exception/
|   |   |   |   |   |   |   |   |-- CalculationException.java
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- service/
|   |   |   |   |   |   |   |   |-- IFourPillarsService.java  (契约：李博实现)
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- repository/
|   |   |   |   |   |   |   |   |-- IFourPillarsRepository.java (契约：李博实现)
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- engine/
|   |   |   |   |   |   |   |   |-- ICalendarService.java     (契约：崔炎朋实现)
|   |   |   |   |   |   |   |   |-- IFourPillarsAnalysisService.java (契约：崔炎朋实现)
|   |   |   |   |   |   |
|   |   |   |   |   |   |-- common/ (崔炎朋: 通用历法实现)
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- calendar/
|   |   |   |   |   |   |   |   |-- CommonCalendarService.java (实现 ICalendarService)
|   |   |   |   |   |   |
|   |   |   |   |   |   |-- fourpillars/ (四柱八字 专属功能)
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- model/ (林娇娇: POJO)
|   |   |   |   |   |   |   |   |-- FourPillarsInput.java
|   |   |   |   |   |   |   |   |-- FourPillarsResult.java
|   |   |   |   |   |   |   |   |-- FourPillarsChart.java
|   |   |   |   |   |   |   |   |-- Pillar.java
|   |   |   |   |   |   |   |   |-- LuckPillar.java
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- ui/ (林娇娇: UI + ViewModel)
|   |   |   |   |   |   |   |   |-- FourPillarsActivity.java
|   |   |   |   |   |   |   |   |-- FourPillarsViewModel.java
|   |   |   |   |   |   |   |   |-- LuckPillarAdapter.java
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- service/ (李博: 服务实现)
|   |   |   |   |   |   |   |   |-- FourPillarsService.java (实现 IFourPillarsService)
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- data/ (李博: 数据实现)
|   |   |   |   |   |   |   |   |-- FourPillarsRepository.java (实现 IFourPillarsRepository)
|   |   |   |   |   |   |   |   |-- DatabaseHelper.java
|   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |-- analysis/ (崔炎朋: 八字分析实现)
|   |   |   |   |   |   |   |   |-- FourPillarsAnalysisService.java (实现 IFourPillarsAnalysisService)
|   |   |   |
|   |   |   |-- res/
|   |   |   |   |-- layout/ (林娇娇: XML 布局)
|   |   |   |   |   |-- activity_fourpillars_input.xml
|   |   |   |   |   |-- activity_fourpillars_result.xml
|   |   |   |   |   |-- item_luck_pillar.xml
|   |   |   |
|   |   |   |-- assets/ (李博: JSON 数据)
|   |   |   |   |-- jieqi/
|   |   |   |   |   |-- jieqi_XXXX.json
|   |   |   |   |-- bazi_rules.json (或 fourpillars_rules.json)
|   |   |   |   |-- eot.json
|   |   |   |   |-- interpretations.json
|   |   |   |
|   |   |   |-- AndroidManifest.xml
|   |   |
|   |   |-- test/
|   |   |   |-- java/
|   |   |   |   |-- com/
|   |   |   |   |   |-- zhouyi/
|   |   |   |   |   |   |-- common/ (崔炎朋: 单元测试)
|   |   |   |   |   |   |   |-- calendar/
|   |   |   |   |   |   |   |   |-- CommonCalendarServiceTest.java
|   |   |   |   |   |   |-- fourpillars/ (崔炎朋: 单元测试)
|   |   |   |   |   |   |   |-- analysis/
|   |   |   |   |   |   |   |   |-- FourPillarsAnalysisServiceTest.java