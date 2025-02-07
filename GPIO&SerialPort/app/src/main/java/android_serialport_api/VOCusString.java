package android_serialport_api;

/* loaded from: classes.dex */
public final class VOCusString {
    public boolean isOpen;
    public boolean isUsed;
    String status;
    String tag;
    String val;

    public VOCusString(String val, String tag) {
        this.status = null;
        this.val = null;
        this.tag = null;
        this.isUsed = false;
        this.isOpen = false;
        this.val = val;
        this.tag = tag;
    }

    public VOCusString(String status, String val, String tag) {
        this.status = null;
        this.val = null;
        this.tag = null;
        this.isUsed = false;
        this.isOpen = false;
        this.status = status;
        this.val = val;
        this.tag = tag;
    }

    public VOCusString(String val, String tag, boolean isUsed) {
        this.status = null;
        this.val = null;
        this.tag = null;
        this.isUsed = false;
        this.isOpen = false;
        this.val = val;
        this.tag = tag;
        this.isUsed = isUsed;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return super.equals(obj);
        }
        if (((VOCusString) obj).val == null) {
            return super.equals(obj);
        }
        return this.val.equalsIgnoreCase(((VOCusString) obj).val);
    }

    public String getVal() {
        return this.val;
    }

    public String toString() {
        return this.val;
    }
}
