这个文件用于将平太阳时转换为真太阳时。

考虑闰年的情况，闰年2月29日使用与2月28日相同的时差值。

文件说明

1. 数据结构

键：月-日 格式（如 "01-01" 表示1月1日）

值：时差值，单位：分钟

正值：真太阳时比平太阳时晚

负值：真太阳时比平太阳时早


2. 重要特征

最大正时差：11月约+16分钟

最大负时差：2月约-18分钟

零时差点：4月15日前后

数据精度：精确到0.1分钟（6秒）



3. 闰年判断示例

public double getEquationOfTime(int month, int day, int year) {
    String key = String.format("%02d-%02d", month, day);
    
    // 处理闰年2月29日
    if (month == 2 && day == 29 && isLeapYear(year)) {
        key = "02-29";
    }
    
    return eotData.get(key);
}

private boolean isLeapYear(int year) {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
}