package org.daoke.tts.hcicloud.asr;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.LogPrinter;
import android.widget.Toast;

import com.sinovoice.hcicloudsdk.android.asr.recorder.ASRRecorder;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.common.AuthExpireTime;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrGrammarId;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogItem;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.recorder.ASRCommonRecorder;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;

import org.apache.commons.logging.Log;
import org.daoke.tts.DkASRInterface;
import org.daoke.tts.DkASRsoundInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by 16F4 on 2016/3/8.
 */
public class DkHcicloudAsr {

	private final String appKey = "585d5445";
	private final String developerKey = "bf6916d1cbd60ec84fa99c13ed29b7b4";
	private final String cloudUrl = "api.hcicloud.com:8888/";
	private final String capKey = "asr.cloud.freetalk";


	private static final String TAG = "HciCloudAsrRecorderActivity";

	private ASRRecorder mAsrRecorder;

	private String grammar = null;

	// 配置识别参数
	private final AsrConfig asrConfig = new AsrConfig();

	private Handler handler;

	private DkASRInterface asrCallBack;
	private DkASRsoundInterface soundCallback;

	private boolean mDebug;
	private Context context;

	public void setAsrCallBack(DkASRInterface asrCallBack) {
		this.asrCallBack = asrCallBack;
	}
	public void setAsrsoundCallBack(DkASRsoundInterface soundCallback) {
		this.soundCallback = soundCallback;
	}
	public DkHcicloudAsr(Context context) {
		handler = new Handler(Looper.myLooper());
		this.context = context;
		initInfo(context);
	}


	public void initInfo(Context context) {

		InitParam initParam = getInitParam(context);
		String strConfig = initParam.getStringConfig();

		int errCode = HciCloudSys.hciInit(strConfig, context);
		if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
			Toast.makeText(context, "hciInit error: " + HciCloudSys.hciGetErrorInfo(errCode), Toast.LENGTH_SHORT).show();
			return;
		}

		// 获取授权/更新授权文件 :
		errCode = checkAuthAndUpdateAuth();
		if (errCode != HciErrorCode.HCI_ERR_NONE) {
			// 由于系统已经初始化成功,在结束前需要调用方法hciRelease()进行系统的反初始化
			Toast.makeText(context, "CheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode), Toast.LENGTH_SHORT).show();
			HciCloudSys.hciRelease();
			return;
		}



		// 初始化录音机
		mAsrRecorder = new ASRRecorder();
		// 配置初始化参数
		AsrInitParam asrInitParam = new AsrInitParam();
		String dataPath = context.getFilesDir().getPath().replace("files", "lib");
		asrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capKey);
		asrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
		asrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, "android_so");


		// 设置初始化参数
		mAsrRecorder.init(asrInitParam.getStringConfig(),
				new ASRResultProcess());


		// PARAM_KEY_CAP_KEY 设置使用的能力
		asrConfig.addParam(AsrConfig.PARAM_KEY_CAP_KEY, capKey);
		// PARAM_KEY_AUDIO_FORMAT 音频格式根据不同的能力使用不用的音频格式
		asrConfig.addParam(AsrConfig.PARAM_KEY_AUDIO_FORMAT,
				AsrConfig.HCI_ASR_AUDIO_FORMAT_PCM_16K16BIT);
		// PARAM_KEY_ENCODE 音频编码压缩格式，使用OPUS可以有效减小数据流量
		asrConfig.addParam(AsrConfig.PARAM_KEY_ENCODE, "opus");
		// 其他配置，此处可以全部选取缺省值


	}

	private int checkAuthAndUpdateAuth() {
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

	private ASRRecorderListener asrRecorderListener;

	public void setAsrRecorderListener(ASRRecorderListener asrRecorderListener) {
		this.asrRecorderListener = asrRecorderListener;
	}

	private static List<String> getAsrResult(AsrRecogResult asrRecogResult) {
		ArrayList<AsrRecogItem> recogItemList = asrRecogResult.getRecogItemList();
		if (recogItemList == null) return null;
		List<String> asrResultList = new ArrayList<>();
		for (AsrRecogItem asrRecogItem : recogItemList) {
			String recogResult = asrRecogItem.getRecogResult();
			if (!TextUtils.isEmpty(recogResult)) {
				asrResultList.add(recogResult);
			}
		}
		return asrResultList;
	}

	private class ASRResultProcess implements ASRRecorderListener {

		public void onRecorderEventError(final ASRCommonRecorder.RecorderEvent recorderEvent, final int errorCode) {


		}
		@Override
		public void onRecorderEventRecogFinsh(final ASRCommonRecorder.RecorderEvent recorderEvent,
											  final AsrRecogResult asrRecogResult) {
			if (asrCallBack != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						asrCallBack.ASRCallBack(getAsrResult(asrRecogResult));
					}
				});
			}

			if (asrRecorderListener != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						asrRecorderListener.onRecorderEventRecogFinsh(recorderEvent, asrRecogResult);
					}
				});
			}
		}



		@Override
		public void onRecorderEventStateChange(final ASRCommonRecorder.RecorderEvent recorderEvent) {

		}

		@Override
		public void onRecorderRecording(final byte[] volumedata, final int volume) {

			if (asrRecorderListener != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						asrRecorderListener.onRecorderRecording(volumedata, volume);
					}
				});
			}
			if (soundCallback != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						soundCallback.ASRCallBack(volume);
					}
				});
			}
		}
		@Override
		public void onRecorderEventRecogProcess(final ASRCommonRecorder.RecorderEvent recorderEvent,
												final AsrRecogResult asrRecogResult) {
			if (asrCallBack != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						asrCallBack.ASRCallBack(getAsrResult(asrRecogResult));
					}
				});
			}

		}
	}


	public void stop(){
		if(mAsrRecorder != null && mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_RECORDING)
			mAsrRecorder.stopAndRecog();
	}

	public void startReco(){
		if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_IDLE) {
			asrConfig.addParam(AsrConfig.PARAM_KEY_REALTIME, "yes");
			mAsrRecorder.start(asrConfig.getStringConfig(), grammar);
		} else {
		}
	}

	public void destory() {
		if (null != mAsrRecorder) {
			mAsrRecorder.release();
			HciCloudSys.hciRelease();
		}
	}






}
