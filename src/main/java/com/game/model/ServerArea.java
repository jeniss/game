package com.game.model;

/**
 * Created by jennifert on 5/18/2017.
 */
public class ServerArea {
    private Integer id;
    private Integer parentId;
    private String name;
    private String code;
    private String active;
    private Integer mergeredId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Integer getMergeredId() {
        return mergeredId;
    }

    public void setMergeredId(Integer mergeredId) {
        this.mergeredId = mergeredId;
    }
}
