package org.daoke.tts.hcicloud.tts;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.sinovoice.hcicloudsdk.android.tts.player.TTSPlayer;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.common.AuthExpireTime;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.hwr.HwrInitParam;
import com.sinovoice.hcicloudsdk.common.tts.TtsConfig;
import com.sinovoice.hcicloudsdk.common.tts.TtsInitParam;
import com.sinovoice.hcicloudsdk.player.TTSCommonPlayer;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 16F4 on 2016/3/8.
 */
public class DkHcicloudTts {

    private final String appKey = "585d5445";
    private final String developerKey = "bf6916d1cbd60ec84fa99c13ed29b7b4";
    private final String cloudUrl = "api.hcicloud.com:8888";
    private final String capKey = "tts.cloud.wangjing";


    private static final String FLAG_START = "start";
    private static final String FLAG_PAUSE = "pause";
    private static final String FLAG_RESUME = "resume";


    private TtsConfig ttsConfig = null;
    private TTSPlayer mTtsPlayer = null;
    private String strConfig = null;
    private InitParam initParam = null;
    private Context context;
    private float speechSpeed;
    private boolean openDebug;
    private boolean isInitPlayer;
    private TTSPlayerListener eventListener;
    private static boolean debug = false;

    public static void setDebug(boolean debug) {
        DkHcicloudTts.debug = debug;
    }

    public Context getContext() {
        return context;
    }

    public DkHcicloudTts(Context context, float speechSpeed, TTSPlayerListener eventListener, boolean openDebug) {
        this.context = context;
        this.speechSpeed = speechSpeed;
        this.eventListener = eventListener;
        this.openDebug = openDebug;
        translate(context, eventListener);
    }

    public void initTTS(int speechSpeed, DkTtsEventProcess eventListener) {
        if (ttsConfig != null) {
            ttsConfig.addParam(TtsConfig.PARAM_KEY_SPEED, String.valueOf(speechSpeed));
        }
        this.eventListener = eventListener;
    }

    public void setSpeed(int speechSpeed) {
        if (ttsConfig != null) {
            ttsConfig.addParam(TtsConfig.PARAM_KEY_SPEED, String.valueOf(speechSpeed));
        }
        this.speechSpeed = speechSpeed;
    }

    public void destory() {
        if (null != mTtsPlayer && isInitPlayer) {
            mTtsPlayer.release();
            HciCloudSys.hciRelease();
            isInitPlayer = false;
        }
    }

    private void translate(Context context) {
        translate(context, null);
    }

    private void translate(Context context, TTSPlayerListener ttsPlayerListener) {

        initParam = getInitParam(context);
        strConfig = initParam.getStringConfig();
        int errCode = HciCloudSys.hciInit(strConfig, context);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
            return;
        }

        errCode = checkAuthAndUpdateAuth();
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            HciCloudSys.hciRelease();
            return;
        }

        initPlayer(context, ttsPlayerListener);

    }

    public void speech(String speech) {
        synth(speech, context);
    }


    private void showToast(String text) {
        if (debug) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    public void destoryTtsPlayer() {
        if (mTtsPlayer != null) {
            mTtsPlayer.release();
        }
        HciCloudSys.hciRelease();
    }

    private void synth(String text, Context context) {

        ttsConfig = new TtsConfig();
        ttsConfig.addParam(TtsConfig.PARAM_KEY_AUDIO_FORMAT, "pcm16k16bit");
        ttsConfig.addParam(TtsConfig.PARAM_KEY_CAP_KEY, capKey);
        ttsConfig.addParam(TtsConfig.PARAM_KEY_SPEED, String.valueOf(speechSpeed));
        ttsConfig.addParam("property", "cn_xiaokun_common");
        if (mTtsPlayer != null && (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PLAYING
                || mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PAUSE)) {
            mTtsPlayer.stop();
        }
        if (mTtsPlayer != null && (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_IDLE)) {
            mTtsPlayer.play(text, ttsConfig.getStringConfig());
        } else {
            showToast("播放器内部状态错误");
        }
    }


    /**
     * function     对信息进行朗读
     * param
     * startFLAG   开始，暂停，重新开始标记
     * start ：开始
     * pause ：暂停
     * goOn  ：重新开始
     * return
     * null
     */
    private void readAlound(String startFLAG, Context context) {
        if (mTtsPlayer != null) {
            switch (startFLAG) {

                case FLAG_PAUSE:
                    if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PLAYING) {
                        mTtsPlayer.pause();
                    }
                    break;
                case FLAG_RESUME:
                    if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PAUSE) {
                        mTtsPlayer.resume();
                    }
                    break;
            }


        }
    }

    public int getStatus() {
        return mTtsPlayer == null ? -1 : mTtsPlayer.getPlayerState(); //mTtsPlayer == null ? -1 :
    }

    public void pause() {
        readAlound(FLAG_PAUSE, context);
    }

    public void resume() {
        readAlound(FLAG_RESUME, context);
    }

    public void stop() {
        if (mTtsPlayer != null) {
            if (mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PLAYING
                    || mTtsPlayer.getPlayerState() == TTSCommonPlayer.PLAYER_STATE_PAUSE) {
                mTtsPlayer.stop();
            }
        }
    }


    private InitParam getInitParam(Context context) {
        String authDirPath = context.getFilesDir().getAbsolutePath();
        InitParam initparam = new InitParam();
        initparam.addParam(InitParam.PARAM_KEY_AUTH_PATH, authDirPath);
        initparam.addParam(InitParam.PARAM_KEY_AUTO_CLOUD_AUTH, "no");
        initparam.addParam(InitParam.PARAM_KEY_CLOUD_URL, cloudUrl);
        initparam.addParam(InitParam.PARAM_KEY_DEVELOPER_KEY, developerKey);
        initparam.addParam(InitParam.PARAM_KEY_APP_KEY, appKey);
        String sdcardState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            String sdPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            String packageName = context.getPackageName();

            String logPath = sdPath + File.separator + "sinovoice"
                    + File.separator + packageName + File.separator + "log"
                    + File.separator;
            File fileDir = new File(logPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            initparam.addParam(InitParam.PARAM_KEY_LOG_FILE_PATH, logPath);
            initparam.addParam(InitParam.PARAM_KEY_LOG_FILE_COUNT, "5");
            initparam.addParam(InitParam.PARAM_KEY_LOG_FILE_SIZE, "1024");
            initparam.addParam(InitParam.PARAM_KEY_LOG_LEVEL, "5");
        }

        return initparam;
    }

    private int checkAuthAndUpdateAuth() {

        // 获取系统授权到期时间
        int initResult;
        AuthExpireTime objExpireTime = new AuthExpireTime();
        initResult = HciCloudSys.hciGetAuthExpireTime(objExpireTime);
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            Date date = new Date(objExpireTime.getExpireTime() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.CHINA);
            if (objExpireTime.getExpireTime() * 1000 > System
                    .currentTimeMillis()) {
                return initResult;
            }

        }
        initResult = HciCloudSys.hciCheckAuth();
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            return initResult;
        } else {
            return initResult;
        }
    }

    private boolean initPlayer(Context context) {
        return initPlayer(context, null);
    }

    private boolean initPlayer(Context context, TTSPlayerListener ttsPlayerListener) {


        TtsInitParam ttsInitParam = new TtsInitParam();
        String dataPath = context.getFilesDir().getAbsolutePath().replace("files", "lib");
        ttsInitParam.addParam(TtsInitParam.PARAM_KEY_DATA_PATH, dataPath);
        ttsInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capKey);
        ttsInitParam.addParam(HwrInitParam.PARAM_KEY_FILE_FLAG, "android_so");
        mTtsPlayer = new TTSPlayer();
        isInitPlayer=true;
        ttsConfig = new TtsConfig();
        mTtsPlayer.init(ttsInitParam.getStringConfig(), ttsPlayerListener == null ? new DkTtsEventProcess() : ttsPlayerListener);
        if (mTtsPlayer.getPlayerState() == TTSPlayer.PLAYER_STATE_IDLE) {
            return true;
        } else {
            return false;
        }
    }


}
