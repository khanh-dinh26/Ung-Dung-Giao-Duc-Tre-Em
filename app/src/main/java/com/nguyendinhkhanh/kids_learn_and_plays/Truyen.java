package com.nguyendinhkhanh.kids_learn_and_plays;

public class Truyen {
    private String id;
    private String tenTruyen;
    private String linkAnh;
    private String linkAudio;
    private int requiredStars;

    public Truyen() { }

    public Truyen(String id, String tenTruyen, String linkAnh, String linkAudio, int requiredStars) {
        this.id = id;
        this.tenTruyen = tenTruyen;
        this.linkAnh = linkAnh;
        this.linkAudio = linkAudio;
        this.requiredStars = requiredStars;
    }

    public String getId() { return id; }
    public String getTenTruyen() { return tenTruyen; }
    public String getLinkAnh() { return linkAnh; }
    public String getLinkAudio() { return linkAudio; }
    public int getRequiredStars() { return requiredStars; }
}