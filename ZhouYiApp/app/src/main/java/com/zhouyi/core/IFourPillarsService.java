package com.zhouyi.core.service;

import com.zhouyi.fourpillars.model.FourPillarsInput;
import com.zhouyi.fourpillars.model.FourPillarsResult;

import java.util.Date;

/**
 * 四柱八字服务接口
 * 定义四柱八字计算与查询的核心功能
 * 负责协调日历服务与分析服务，提供完整的四柱八字解决方案
 *
 * @author li bo
 * @since 2025-11-22
 */
public interface IFourPillarsService {
    /**
     * 计算四柱八字信息
     *
     * @param input 包含出生日期、时间、时区等信息的输入对象
     * @return 完整的四柱八字计算结果，包含四柱、大运、十神等信息
     */
    FourPillarsResult calculateFourPillars(FourPillarsInput input);

    /**
     * 保存四柱八字计算结果到数据库
     *
     * @param result 已计算的四柱八字结果对象
     * @return true表示保存成功，false表示保存失败
     */
    boolean saveFourPillarsResult(FourPillarsResult result);

    /**
     * 根据出生日期查询历史计算结果
     *
     * @param birthDate 出生日期（精确到毫秒的时间戳）
     * @return 匹配的计算结果，若不存在则返回null
     */
    FourPillarsResult getHistoricalResult(Date birthDate);
}
