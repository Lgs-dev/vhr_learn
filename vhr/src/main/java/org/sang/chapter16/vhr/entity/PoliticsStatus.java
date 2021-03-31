package org.sang.chapter16.vhr.entity;

public class PoliticsStatus {
    private Integer id;

    private String name;

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
        this.name = name == null ? null : name.trim();
    }

    public PoliticsStatus() {

    }

    public PoliticsStatus(String name) {

        this.name = name;
    }

}