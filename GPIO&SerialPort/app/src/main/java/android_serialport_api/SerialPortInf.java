package android_serialport_api;

import com.ed.util.TimeOutlistener;

/* loaded from: classes.dex */
public interface SerialPortInf {
    void onDataReceived(String str, String str2, int i);

    boolean sendCmd(String str, String str2, byte b, TimeOutlistener timeOutlistener);
}
