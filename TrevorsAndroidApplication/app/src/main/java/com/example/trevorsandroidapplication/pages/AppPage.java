package com.example.trevorsandroidapplication.pages;

import com.example.trevorsandroidapplication.WeightAppMain;

public abstract class AppPage {
    protected final WeightAppMain main;

    public AppPage(WeightAppMain main) {
        this.main = main;
    }

    public abstract void init();
}
