package com.bhcj.telling.model.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 卦象解析数据模型类
 * 包含卦象的完整信息，包括名称、解释、爻辞等
 * 实现Serializable接口以支持序列化传输
 * 
 * @author bhcj
 * @version 1.0
 */
public class GuaWords implements Serializable {

    /** 卦象名称 */
    private String name;
    
    /** 卦象标题/序号 */
    private String title;
    
    /** 卦象解释 */
    private String explain;
    
    /** 详细解释 */
    private String dsExplain;
    
    /** 象辞 */
    private String xCi;
    
    /** 象曰 */
    private String xYue;

    /** 解释列表 */
    private List<Entity> explainList;
    
    /** 结果列表 */
    private List<Entity> jgList;


    /**
     * 获取完整的卦象解释信息
     * 包含卦象名称、解释和象辞
     * 
     * @return 完整的解释信息字符串
     */
    public String getAllExplain() {
        StringBuilder sb = new StringBuilder(0);
        sb.append(name).append("：").append(explain).append("\n").append(xCi);
        return sb.toString();
    }

    /**
     * 获取卦象简要信息
     * 包含卦象名称、解释、象辞和象曰，并截断为简要形式
     * 
     * @return 简要信息字符串
     */
    public String getBrief() {
        StringBuilder sb = new StringBuilder(0);
        sb.append(name).append("：").append(explain).append(xCi).append(xYue);
        sb.delete(sb.length() - 3, sb.length()).append("...");
        return sb.toString();
    }

    /**
     * 获取列表数据数组
     * 将解释列表和结果列表合并为一个字符串数组
     * 
     * @return 包含所有解释和结果信息的字符串数组
     */
    public String[] getListData() {
        String[] array = new String[this.getJgList().size() + this.getExplainList().size()];
        int i = 0;
        for (Entity entity : this.getExplainList()) {
            array[i++] = entity.getName() + "：" + entity.getText();
        }
        for (Entity entity : this.getJgList()) {
            array[i++] = entity.getName() + "：" + entity.getText();
        }
        return array;
    }

    /**
     * 获取卦象标题
     * 
     * @return 卦象标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 获取完整的卦象标题
     * 格式为"易经第{title}{name}详解"
     * 
     * @return 完整的卦象标题
     */
    public String getFullTitle() {
        return "易经第" + title + name + "详解";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getDsExplain() {
        return dsExplain;
    }

    public void setDsExplain(String dsExplain) {
        this.dsExplain = dsExplain;
    }

    public String getxCi() {
        return xCi;
    }

    public void setxCi(String xCi) {
        this.xCi = xCi;
    }

    public String getxYue() {
        return xYue;
    }

    public void setxYue(String xYue) {
        this.xYue = xYue;
    }

    public List<Entity> getExplainList() {
        return explainList;
    }

    public void setExplainList(List<Entity> explainList) {
        this.explainList = explainList;
    }

    public List<Entity> getJgList() {
        return jgList;
    }

    public void setJgList(List<Entity> jgList) {
        this.jgList = jgList;
    }

    /**
     * 卦象解释条目实体类
     * 用于存储卦象解释的具体条目信息
     */
    public static class Entity {
        /** 条目编码 */
        private String code;
        
        /** 条目名称 */
        private String name;
        
        /** 条目内容 */
        private String text;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
