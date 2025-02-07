package android_serialport_api;

import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class TimeOutlistener {
    private static final String TAG = "TimeOutlistener";
    public String cmd;
    public String port;
    public int timeOutTime;
    TimerTask timer;

    public TimeOutlistener() {
        this.timer = null;
        this.timeOutTime = UIMsg.m_AppUI.MSG_APP_SAVESCREEN;
        this.port = null;
        this.cmd = null;
    }

    public TimeOutlistener(int timeOutTime) {
        this.timer = null;
        this.timeOutTime = UIMsg.m_AppUI.MSG_APP_SAVESCREEN;
        this.port = null;
        this.cmd = null;
        this.timeOutTime = timeOutTime;
    }

    public TimeOutlistener(int timeOutTime, String port) {
        this.timer = null;
        this.timeOutTime = UIMsg.m_AppUI.MSG_APP_SAVESCREEN;
        this.port = null;
        this.cmd = null;
        this.timeOutTime = timeOutTime;
        this.port = port;
    }

    public TimeOutlistener(int timeOutTime, String port, String cmd) {
        this.timer = null;
        this.timeOutTime = UIMsg.m_AppUI.MSG_APP_SAVESCREEN;
        this.port = null;
        this.cmd = null;
        this.timeOutTime = timeOutTime;
        this.port = port;
        this.cmd = cmd;
    }

    public void timeout() {
        LogOut.i(TAG, "接受反馈超时");
    }

    public void startTimeOutListener() {
        cancelTimer();
        LogOut.i(TAG, "启动指令反馈超时监听 端口 : " + this.port + " ; 时间 : " + this.timeOutTime);
        this.timer = new TimerTask() { // from class: com.ed.util.TimeOutlistener.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                TimeOutlistener.this.timeout();
            }
        };
        new Timer().schedule(this.timer, this.timeOutTime);
    }

    public void start() {
        startTimeOutListener();
    }

    public void cancelTimer() {
        if (this.timer != null) {
            LogOut.i(TAG, "销毁指令反馈超时监听");
            this.timer.cancel();
            this.timer = null;
        }
    }
}
