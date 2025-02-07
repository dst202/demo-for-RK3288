package android_serialport_api;





import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public abstract class SerialHelper {
    private byte[] _bLoopData;
    private boolean _isOpen;
    private int dataBits;
    private int iBaudRate;
    private int iDelay;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private SerialPort mSerialPort;
    private int parity;
    private String sPort;
    private int stopBit;

    protected abstract void onDataReceived(ComBean comBean);

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public void setStopBit(int stopBit) {
        this.stopBit = stopBit;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public SerialHelper(String sPort, int iBaudRate) {
        this.sPort = "/dev/s3c2410_serial0";
        this.iBaudRate = 9600;
        this._isOpen = false;
        this._bLoopData = new byte[]{marshall_t.marshall_func_code_trace};
        this.iDelay = UIMsg.d_ResultType.SHORT_URL;
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }

    public SerialHelper() {
        this("/dev/s3c2410_serial0", 9600);
    }

    public SerialHelper(String sPort) {
        this(sPort, 9600);
    }

    public SerialHelper(String sPort, String sBaudRate) {
        this(sPort, Integer.parseInt(sBaudRate));
    }

    public void open() throws Exception {
        ReadThread readThread = null;
        byte b = 0;
        System.out.println("----- open-------- ");
        if (this.parity > 0) {
            this.mSerialPort = new SerialPort(new File(this.sPort), this.iBaudRate, this.parity, this.dataBits, this.stopBit);
        } else {
            this.mSerialPort = this.dataBits > 0 ? new SerialPort(new File(this.sPort), this.iBaudRate, this.dataBits, this.stopBit) : new SerialPort(new File(this.sPort), this.iBaudRate, 0);
        }
        this.mOutputStream = this.mSerialPort.getOutputStream();
        this.mInputStream = this.mSerialPort.getInputStream();
        this.mReadThread = new ReadThread(this, readThread);
        this.mReadThread.start();
        this.mSendThread = new SendThread(this, b == true ? 1 : 0);
        this.mSendThread.setSuspendFlag();
        this.mSendThread.start();
        this._isOpen = true;
    }

    public void close() {
        if (this.mReadThread != null) {
            this.mReadThread.interrupt();
        }
        if (this.mSerialPort != null) {
            this.mSerialPort.close();
            this.mSerialPort = null;
        }
        this._isOpen = false;
    }

    public void send(byte[] bOutArray) {
        try {
            if (this.mOutputStream != null) {
                this.mOutputStream.write(bOutArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendHex(String sHex) {
        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    public void sendTxt(String sTxt) {
        try {
            String[] ss = sTxt.split(" ");
            byte[] bOutArray = new byte[ss.length];
            for (int i = 0; i < bOutArray.length; i++) {
                if (ss[i] == null || !ss[i].equals("")) {
                    int v = Integer.parseInt(ss[i]);
                    bOutArray[i] = (byte) v;
                } else {
                    int v2 = Integer.parseInt(ss[i]);
                    bOutArray[i] = (byte) v2;
                }
            }
            String code = "";
            for (int i2 = 0; i2 < bOutArray.length; i2++) {
                code = String.valueOf(code) + ((int) bOutArray[i2]);
                if (i2 < bOutArray.length - 1) {
                    code = String.valueOf(code) + " ";
                }
            }
            send(bOutArray);
        } catch (Exception e) {
            e.printStackTrace();
            MainActivity.instance.sendErrorMsg(e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ReadThread extends Thread {
        private ReadThread() {
        }

        /* synthetic */ ReadThread(SerialHelper serialHelper, ReadThread readThread) {
            this();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (SerialHelper.this.mInputStream != null) {
                        byte[] buffer = new byte[512];
                        int size = SerialHelper.this.mInputStream.read(buffer);
                        if (size > 0) {
                            ComBean ComRecData = new ComBean(SerialHelper.this.sPort, buffer, size);
                            SerialHelper.this.onDataReceived(ComRecData);
                        }
                        try {
                            Thread.sleep(30L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                } catch (Throwable e2) {
                    e2.printStackTrace();
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SendThread extends Thread {
        public boolean suspendFlag;

        private SendThread() {
            this.suspendFlag = true;
        }

        /* synthetic */ SendThread(SerialHelper serialHelper, SendThread sendThread) {
            this();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (this.suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                SerialHelper.this.send(SerialHelper.this.getbLoopData());
                try {
                    Thread.sleep(SerialHelper.this.iDelay);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }

        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }

    public int getBaudRate() {
        return this.iBaudRate;
    }

    public boolean setBaudRate(int iBaud) {
        if (this._isOpen) {
            return false;
        }
        this.iBaudRate = iBaud;
        return true;
    }

    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    public String getPort() {
        return this.sPort;
    }

    public boolean setPort(String sPort) {
        if (this._isOpen) {
            return false;
        }
        this.sPort = sPort;
        return true;
    }

    public boolean isOpen() {
        return this._isOpen;
    }

    public byte[] getbLoopData() {
        return this._bLoopData;
    }

    public void setbLoopData(byte[] bLoopData) {
        this._bLoopData = bLoopData;
    }

    public void setTxtLoopData(String sTxt) {
        this._bLoopData = sTxt.getBytes();
    }

    public void setHexLoopData(String sHex) {
        this._bLoopData = MyFunc.HexToByteArr(sHex);
    }

    public int getiDelay() {
        return this.iDelay;
    }

    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }

    public void startSend() {
        if (this.mSendThread != null) {
            this.mSendThread.setResume();
        }
    }

    public void stopSend() {
        if (this.mSendThread != null) {
            this.mSendThread.setSuspendFlag();
        }
    }
}
