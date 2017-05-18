package com.game.model;

import java.util.List;

/**
 * Created by jennifert on 5/18/2017.
 */
public class ServerArea {
    private Integer id;
    private String name;
    private String code;
    private String active;
    private ServerArea mergerServerArea;
    List<ServerArea> childServerAreas;

    public List<ServerArea> getChildServerAreas() {
        return childServerAreas;
    }

    public void setChildServerAreas(List<ServerArea> childServerAreas) {
        this.childServerAreas = childServerAreas;
    }

    public ServerArea getMergerServerArea() {
        return mergerServerArea;
    }

    public void setMergerServerArea(ServerArea mergerServerArea) {
        this.mergerServerArea = mergerServerArea;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "ServerArea{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", active='" + active + '\'' +
                '}';
    }
}
