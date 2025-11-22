package com.zhouyi.core.service;

import com.zhouyi.fourpillars.model.FourPillarsResult;

import java.util.Date;
import java.util.List;

/**
 * 四柱八字数据仓库接口
 * 定义数据持久化操作规范
 * 负责四柱计算结果的存储、查询、删除等数据管理功能
 * 实现类需处理SQLite数据库交互细节
 *
 * @author li bo
 * @since 2025-11-22
 */
public interface IFourPillarsRepository {
    /**
     * 保存四柱八字计算结果到数据库
     *
     * @param result 包含完整计算结果的FourPillarsResult对象
     * @return true表示保存成功，false表示保存失败（如数据库错误）
     */
    boolean save(FourPillarsResult result);

    /**
     * 根据出生日期查询历史计算结果
     *
     * @param birthDate 精确到毫秒的出生日期时间戳
     * @return 匹配的计算结果对象，若不存在则返回null
     */
    FourPillarsResult getByBirthDate(Date birthDate);

    /**
     * 查询所有已保存的四柱八字计算结果
     *
     * @return 按创建时间倒序排列的结果列表，若无数据则返回空列表
     */
    List<FourPillarsResult> getAllResults();

    /**
     * 根据出生日期删除计算结果
     *
     * @param birthDate 要删除记录的出生日期时间戳
     * @return true表示删除成功（或记录不存在），false表示删除失败
     */
    boolean delete(Date birthDate);
}
