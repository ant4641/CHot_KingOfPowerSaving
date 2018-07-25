package com.example.owner.myapplication;

/**
 * Created by Kavin on 2017/9/4.
 */

public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.tab_rasp:
                message += "Rasp Pie";
                break;
            case R.id.tab_ard:
                message += "Arduino";
                break;
            case R.id.tab_soc:
                message += "Socket";
                break;
            case R.id.tab_eca:
                message += "ECA rule";
                break;
            case R.id.tab_dev:
                message += "Device";
                break;
        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }
}
