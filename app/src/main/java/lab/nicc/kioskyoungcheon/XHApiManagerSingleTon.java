package lab.nicc.kioskyoungcheon;

import android.preference.SwitchPreference;

import java.util.Timer;

/**
 * Created by user on 2019-01-23.
 */

public class XHApiManagerSingleTon {
    private XHApiManagerSingleTon(){

    }
    private static boolean aBoolean = true;
    private static SwitchPreference switchPreference = null;
    private XHApiManagerSingleTon(boolean aBoolean) {
    }
    private static class LazyHolder{
        public static XHApiManagerSingleTon instance = new XHApiManagerSingleTon();
        public static XHApiManagerSingleTon instance2 = new XHApiManagerSingleTon(aBoolean);
            }

    public static XHApiManagerSingleTon getInstance(){
        return LazyHolder.instance;
    }
    public static XHApiManagerSingleTon getInstance(Boolean a, Timer timer){
        aBoolean = a;
        return LazyHolder.instance2;
    }
    public boolean getBoolean(){
        return aBoolean;
    }
    public void setBoolean(boolean a){
        aBoolean = a;
    }
}
