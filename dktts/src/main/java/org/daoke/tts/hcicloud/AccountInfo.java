package org.daoke.tts.hcicloud;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountInfo {

    private static AccountInfo mInstance;


    private String appKey;
    private String developerKey;
    private String cloudUrl;
    private List<String> capKey;


    public AccountInfo(String developerKey, String appKey, List<String> capKey, String cloudUrl) {
        this.developerKey = developerKey;
        this.appKey = appKey;
        this.capKey = capKey;
        this.cloudUrl = cloudUrl;
    }


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public List<String> getCapKey() {
        return capKey;
    }

    public void setCapKey(List<String> capKey) {
        this.capKey = capKey;
    }

    public String getCloudUrl() {
        return cloudUrl;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public String getDeveloperKey() {
        return developerKey;
    }

    public void setDeveloperKey(String developerKey) {
        this.developerKey = developerKey;
    }

    private AccountInfo() {
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "appKey='" + appKey + '\'' +
                ", developerKey='" + developerKey + '\'' +
                ", cloudUrl='" + cloudUrl + '\'' +
                ", capKey=" + capKey +
                '}';
    }

    public static AccountInfo getInstance() {
        if (mInstance == null) {
            synchronized (AccountInfo.class) {
                if (mInstance == null) {
                    mInstance = new AccountInfo();
                }
            }
        }
        return mInstance;
    }


    /**
     * 加载用户的注册信息
     */
    public boolean loadAccountInfo(Context context) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open("AccountInfo.txt"), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (!TextUtils.isEmpty(line.trim()) && !line.startsWith("#") && line.indexOf("=") != -1) {
                    String[] keyValue = line.split("=");
                    if (keyValue != null && keyValue.length == 2) {
                        if (TextUtils.isEmpty(keyValue[0]) || TextUtils.isEmpty(keyValue[1])) {
                            Log.e("AccountInfo", keyValue[0] + " is null");
                            return false;
                        }

                        if (TextUtils.equals(keyValue[0], "appKey")) {
                            setAppKey(keyValue[1]);
                        } else if (TextUtils.equals(keyValue[0], "developerKey")) {
                            setDeveloperKey(keyValue[1]);
                        } else if (TextUtils.equals(keyValue[0], "cloudUrl")) {
                            setCloudUrl(keyValue[1]);
                        } else if (TextUtils.equals(keyValue[0], "capKey")) {
                            List<String> capKeys;
                            if (keyValue[1].indexOf(",") == -1) {
                                capKeys = new ArrayList<>();
                                capKeys.add(keyValue[1]);
                            } else {
                                capKeys = new ArrayList<>(Arrays.asList(keyValue[1].split(",")));
                            }
                            setCapKey(capKeys);
                        }

                    }
                }
            }
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
            }
        }
        return true;
    }


}
