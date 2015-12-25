package cn.ismartv.voice.core.exception;

/**
 * Created by huaijie on 12/23/15.
 */
public class AppNoInstallException extends Exception {

    public AppNoInstallException(String appName) {
        super(appName + "  ===>  未安装!!!");
    }
}
