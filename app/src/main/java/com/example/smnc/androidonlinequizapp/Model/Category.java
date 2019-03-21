package com.example.smnc.androidonlinequizapp.Model;

public class Category {
    private String Name;
    private String Resim;

    public Category() {
    }

    public Category(String Name, String Resim) {
        this.Name = Name;
        this.Resim = Resim;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getResim() {
        return this.Resim;
    }

    public void setResim(String Image) {
        this.Resim = Resim;
    }
}
