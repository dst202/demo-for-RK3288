package android_serialport_api;


/* loaded from: classes.dex */
public class SerialControl extends SerialHelper {
    @Override // com.bjw.ComAssistant.SerialHelper
    protected void onDataReceived(ComBean ComRecData) {
        MainActivity.instance.sendComMsg(ComRecData.sComPort, ComRecData.code);
    }
}
