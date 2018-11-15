package com.util.library.widget.addresspicker.bean;

/**
 * @description: 城市的实体类
 * @author: whb
 * @date: 2018/11/14 11:34
 */
public class City  implements Entity {
    private Long id;
    /**
     * 城市名称
     */
    private String regionname;
    /**
     * 城市编码
     */
    private Long code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return regionname;
    }

    public void setName(String name) {
        this.regionname = name;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    @Override
    public String getRegionName() {
        return getName();
    }

    @Override
    public Long getRegionId() {
        return getId();
    }

    @Override
    public Long getRegionCode() {
        return getCode();
    }
}
