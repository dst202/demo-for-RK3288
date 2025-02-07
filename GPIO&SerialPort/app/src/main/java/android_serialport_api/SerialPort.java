package android_serialport_api;

import com.ed.util.ShellUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class SerialPort {
    private static final String TAG = "SerialPort";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    private static native FileDescriptor open(String str, int i, int i2);

    private static native FileDescriptor open(String str, int i, int i2, int i3);

    public native void close();

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + ShellUtils.COMMAND_LINE_END + ShellUtils.COMMAND_EXIT;
                su.getOutputStream().write(cmd.getBytes());
                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException("(su.waitFor() != 0) || !device.canRead()|| !device.canWrite()");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException(String.valueOf(e.getMessage()) + " SerialPort 57!!");
            }
        }
        this.mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (this.mFd == null) {
            throw new IOException("native open returns null  SerialPort 64");
        }
        this.mFileInputStream = new FileInputStream(this.mFd);
        this.mFileOutputStream = new FileOutputStream(this.mFd);
    }

    public SerialPort(File device, int baudrate, int dataBits, int stopBit) throws SecurityException, IOException {
        System.out.println("------SerialPort " + baudrate);
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + ShellUtils.COMMAND_LINE_END + ShellUtils.COMMAND_EXIT;
                su.getOutputStream().write(cmd.getBytes());
                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException("(su.waitFor() != 0) || !device.canRead()|| !device.canWrite()");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException(String.valueOf(e.getMessage()) + " SerialPort 57!!");
            }
        }
        System.out.println("------SerialPort open " + baudrate);
        this.mFd = open(device.getAbsolutePath(), baudrate, dataBits, stopBit);
        if (this.mFd == null) {
            throw new IOException("native open returns null  SerialPort 64");
        }
        this.mFileInputStream = new FileInputStream(this.mFd);
        this.mFileOutputStream = new FileOutputStream(this.mFd);
    }

    public InputStream getInputStream() {
        return this.mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return this.mFileOutputStream;
    }

    static {
        System.loadLibrary("serialport");
    }
}
