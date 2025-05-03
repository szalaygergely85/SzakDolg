package com.example.szakdolg.logging;

public class CrashLog {
        private String message;
        private String stackTrace;
        private String device;
        private String androidVersion;

    public CrashLog(String message, String stackTrace, String device, String androidVersion) {
        this.message = message;
        this.stackTrace = stackTrace;
        this.device = device;
        this.androidVersion = androidVersion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }
}
