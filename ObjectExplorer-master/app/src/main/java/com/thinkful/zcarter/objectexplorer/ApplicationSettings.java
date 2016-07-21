package com.thinkful.zcarter.objectexplorer;

/**
 * Created by amritachowdhury on 7/19/16.
 */
public class ApplicationSettings {
    private static ApplicationSettings ourInstance = new ApplicationSettings();

    public static ApplicationSettings getInstance() {
        return ourInstance;
    }

    private ApplicationSettings() {
    }
}
