package com.newland.nsdk.core.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {
    private int majorVersion = -1;
    private int minorVersion = -1;
    private int patchVersion = -1;
    private static Pattern VERSION_PATTERN = Pattern.compile("(.*)(\\d+).(\\d+).(\\d+)(.*)");

    public Version(){};
    public Version(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }

    /**
     * 判断是否比目标版本低
     *
     * @param version 要进行比较的版本
     * @return
     */
    public boolean isLower(Version version) {
        if (this.majorVersion > version.getMajorVersion()) {
            return false;
        }

        if (this.majorVersion == version.getMajorVersion()) {
            return this.minorVersion <= version.getMinorVersion() && (this.minorVersion < version.getMinorVersion() || this.patchVersion < version.getPatchVersion());
        }

        return true;
    }

    /**
     * 判断是否比目标版本低
     *
     * @return
     */
    public boolean isLower(int majorVersion, int minorVersion, int patchVersion) {
        if (this.majorVersion > majorVersion) {
            return false;
        }

        if (this.majorVersion == majorVersion) {
            return this.minorVersion <= minorVersion && (this.minorVersion < minorVersion || this.patchVersion < patchVersion);
        }

        return true;
    }

    public static Version getVersion(String versionString) {
        Matcher matcher = VERSION_PATTERN.matcher(versionString);

        Version version = new Version();
        if (matcher.matches()) {
            // 只取版本号部分
            // matcher.group(0) 是匹配都的完整版本号
            // matcher.group(1) 是 major 版本号前的所有字符
            // matcher.group(5) 是 patch 版本号后的所有字符
            version.setMajorVersion(Integer.parseInt(matcher.group(2)));
            version.setMinorVersion(Integer.parseInt(matcher.group(3)));
            version.setPatchVersion(Integer.parseInt(matcher.group(4)));
        }
        return version;
    }

    @Override
    public String toString(){
        return String.format("%d.%d.%d", this.majorVersion, this.minorVersion, this.patchVersion);
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public void setPatchVersion(int patchVersion) {
        this.patchVersion = patchVersion;
    }
}
