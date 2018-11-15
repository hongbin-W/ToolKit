package com.util.library.widget.addresspicker.bean;

/**
 * @description: 省份实体类
 * @author: whb
 * @date: 2018/11/14 11:31
 */
public class Province implements Entity {

    private Long id;
    /**
     * 省份名称
     */
    private String regionname;
    /**
     * 省份编码
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
