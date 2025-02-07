package android_serialport_api;

/* loaded from: classes.dex */
public class ComBean {
    public byte[] bRec;
    public String code;
    public String sComPort;

    public ComBean(String sPort, byte[] buffer, int size) {
        this.bRec = null;
        this.sComPort = "";
        this.code = "";
        this.sComPort = sPort;
        this.bRec = new byte[size];
        for (int i = 0; i < size; i++) {
            this.bRec[i] = buffer[i];
            this.code = String.valueOf(this.code) + ((int) buffer[i]);
            if (i < size - 1) {
                this.code = String.valueOf(this.code) + " ";
            }
        }
    }
}
