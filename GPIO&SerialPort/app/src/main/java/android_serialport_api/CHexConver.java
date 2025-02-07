package android_serialport_api;

import android.annotation.SuppressLint;
import java.util.Locale;

@SuppressLint({"DefaultLocale"})
/* loaded from: classes.dex */
public class CHexConver {
    public static final char[] BToA = "0123456789abcdef".toCharArray();
    private static final String TAG = "CHexConver";
    private static final String mHexStr = "0123456789ABCDEF";

    public static String hexStr2Str(String hexStr) {
        String hexStr2 = hexStr.toString().trim().replace(" ", "").toUpperCase(Locale.US);
        char[] hexs = hexStr2.toCharArray();
        byte[] bytes = new byte[hexStr2.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int iTmp = mHexStr.indexOf(hexs[i * 2]) << 4;
            bytes[i] = (byte) ((iTmp | mHexStr.indexOf(hexs[(i * 2) + 1])) & 255);
        }
        return new String(bytes);
    }

    public static byte[] hexString2Bytes(String src) {
        String src2 = src.trim().replace(" ", "").toUpperCase(Locale.US);
        int l = src2.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = Integer.valueOf(src2.substring(i * 2, (i * 2) + 2), 16).byteValue();
        }
        return ret;
    }

    @SuppressLint({"DefaultLocale"})
    public static String String2Hex(byte[] b) {
        String resultHex = "";
        for (byte b2 : b) {
            String hex = String.valueOf(Integer.toHexString(b2 & 255)) + " ";
            if (hex.length() == 2) {
                hex = String.valueOf('0') + hex;
            }
            resultHex = String.valueOf(resultHex) + hex.toUpperCase();
        }
        return resultHex;
    }

    @SuppressLint({"DefaultLocale"})
    public static String String2Hex(byte[] b, int off, int len) {
        String resultHex = "";
        for (int i = 0; i < len; i++) {
            String hex = String.valueOf(Integer.toHexString(b[i] & 255)) + " ";
            if (hex.length() == 2) {
                hex = String.valueOf('0') + hex;
            }
            resultHex = String.valueOf(resultHex) + hex.toUpperCase();
        }
        return resultHex;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 255;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(String.valueOf(hv) + " ");
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexString(byte[] src, int off, int size) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = off; i < size; i++) {
            int v = src[i] & 255;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(String.valueOf(hv) + " ");
        }
        return stringBuilder.toString();
    }

    public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }

    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = String.valueOf(temp) + ((char) Integer.valueOf(src.substring(i * 2, (i * 2) + 2), 16).byteValue());
        }
        return temp;
    }

    @SuppressLint({"DefaultLocale"})
    public static String calculateSumNormal(String hexStr, String hexAdd) {
        int hexSum = 0;
        String hexStr2 = hexStr.trim().toUpperCase();
        LogOut.i(TAG, "hexStr : " + hexStr2);
        String[] hexStrSp = hexStr2.replaceAll(" ", ",").split(",");
        for (String str : hexStrSp) {
            hexSum ^= Integer.parseInt(str, 16);
        }
        return Integer.toHexString(Integer.parseInt(hexAdd, 16) + hexSum).toUpperCase();
    }

    public static String calculateSumNormal(String[] hexStrSp, String hexAdd) {
        int hexSum = 0;
        for (String str : hexStrSp) {
            hexSum ^= Integer.parseInt(str, 16);
        }
        return Integer.toHexString(Integer.parseInt(hexAdd, 16) + hexSum).toUpperCase();
    }

    @SuppressLint({"DefaultLocale"})
    public static int calculateSumNormal_10(String hexStr) {
        int hexSum = 0;
        String[] hexStrSp = hexStr.trim().toUpperCase().replaceAll(" ", ",").split(",");
        for (String str : hexStrSp) {
            hexSum += Integer.parseInt(str, 16);
        }
        return hexSum;
    }

    @SuppressLint({"DefaultLocale"})
    public static String calculateSumSpecial(String hexStr, String hexAdd) {
        int hexSum = 0;
        String[] hexStrSp = hexStr.trim().toUpperCase().replaceAll(" ", ",").split(",");
        for (String str : hexStrSp) {
            hexSum ^= Integer.parseInt(str, 16);
        }
        return Integer.toHexString(Integer.parseInt(hexAdd, 16) + hexSum).toUpperCase();
    }

    @SuppressLint({"DefaultLocale"})
    public static byte[] str2Bcd(String asc) {
        int j;
        int k;
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = ScanCallback.CODE_SUCCESS + asc;
            len = asc.length();
        }
        byte[] bArr = new byte[len];
        if (len >= 2) {
            len /= 2;
        }
        byte[] bbt = new byte[len];
        byte[] abt = asc.getBytes();
        for (int p = 0; p < asc.length() / 2; p++) {
            if (abt[p * 2] >= 48 && abt[p * 2] <= 57) {
                j = abt[p * 2] - 48;
            } else if (abt[p * 2] >= 97 && abt[p * 2] <= 122) {
                j = (abt[p * 2] - 97) + 10;
            } else {
                j = (abt[p * 2] - 65) + 10;
            }
            if (abt[(p * 2) + 1] >= 48 && abt[(p * 2) + 1] <= 57) {
                k = abt[(p * 2) + 1] - 48;
            } else if (abt[(p * 2) + 1] >= 97 && abt[(p * 2) + 1] <= 122) {
                k = (abt[(p * 2) + 1] - 97) + 10;
            } else {
                k = (abt[(p * 2) + 1] - 65) + 10;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static String BCD2ASC(byte[] bytes) {
        return BCD2ASC(bytes, bytes.length);
    }

    public static String BCD2ASC(byte[] bytes, int size) {
        return BCD2ASC(bytes, 0, size);
    }

    public static String BCD2ASC(byte[] bytes, int off, int size) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = off; i < size; i++) {
            int h = (bytes[i] & marshall_t.marshall_packet_option_rfu_mask) >>> 4;
            int l = bytes[i] & 15;
            temp.append(BToA[h]).append(BToA[l]);
        }
        return temp.toString();
    }

    @SuppressLint({"DefaultLocale"})
    public static byte[] hexStrToBCDByte(String hexString) {
        return str2Bcd(String.valueOf(Integer.parseInt(hexString, 16)));
    }

    public static String BCD2BCDHex(String[] bcdArr) {
        String bcdhex = "";
        for (String str : bcdArr) {
            bcdhex = String.valueOf(bcdhex) + bytesToHexString(BCD2ASC(hexStrToBCDByte(str)).getBytes());
        }
        return bcdhex;
    }

    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((int) ((byte) ((bytes[i] & marshall_t.marshall_packet_option_rfu_mask) >>> 4)));
            temp.append((int) ((byte) (bytes[i] & 15)));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase(ScanCallback.CODE_SUCCESS) ? temp.toString().substring(1) : temp.toString();
    }

    public static String bcdHex2AsciiHex(String bcdhex) {
        String[] bcdArr;
        if (bcdhex.contains(" ")) {
            bcdArr = bcdhex.replaceAll(" ", ",").trim().split(",");
        } else {
            bcdArr = new String[]{bcdhex};
        }
        return BCD2BCDHex(bcdArr).trim().replaceAll("  ", " ");
    }

    public static String bytetoString(byte[] bytearray, int size) {
        String result = "";
        for (int i = 0; i < size; i++) {
            char temp = (char) bytearray[i];
            result = String.valueOf(result) + temp;
        }
        return result;
    }
}
