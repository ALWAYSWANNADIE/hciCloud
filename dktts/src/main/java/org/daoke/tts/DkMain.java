package org.daoke.tts;

import android.content.Context;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;

import org.apache.commons.logging.Log;
import org.daoke.tts.hcicloud.tts.DkHcicloudTts;

/**
 * Created by liyuanbiao on 2016/3/16.
 */
final public class DkMain {
	//zzc ADD
	private static DkHcicloudTts TTSinstance = null;
	private static DkASR ASRinstance = null;

	public static DkHcicloudTts getTTSInstance(Context context, float speechSpeed, TTSPlayerListener eventListener, boolean openDebug) {
		if (TTSinstance == null) {
			synchronized (DkHcicloudTts.class) {
				if (TTSinstance == null) {
					TTSinstance = new DkHcicloudTts(context,speechSpeed,eventListener,openDebug);
				}
			}
		}
		return TTSinstance;
	}


	public static DkASR getASRInstance(int asrType, int ttsSpeed,DkASRsoundInterface soundCallback, DkASRInterface asrCallBack, boolean openLog) {
		if (ASRinstance == null) {
			synchronized (DkASR.class) {
				if (ASRinstance == null) {
					ASRinstance = new DkASR(context,asrType, asrCallBack,soundCallback);
				}
			}
		}
		return ASRinstance;
	}

	private static Context context = null;

	private static DkTTS dkTTS;
	private static DkASR dkASR;

	public DkMain(Context context) {
		if (null == this.context) {
			this.context = context;
		}
	}

	public void init(int asrType, int ttsSpeed,DkASRsoundInterface soundCallback, DkASRInterface asrCallBack, boolean openLog) {
		DkConfig.setDkTtsDefault(asrType);
//		switch (DkConfig.DK_TTS_DEFAULT) {
//			case DkConfig.DK_TTS_HCICLOUD:


//				dkTTS = new DkTTS(context,asrType, ttsSpeed, openLog);
				dkASR = new DkASR(context,asrType, asrCallBack,soundCallback);
//				break;
//		}
	}

	public void exit() {
		switch (DkConfig.DK_TTS_DEFAULT) {
			case DkConfig.DK_TTS_HCICLOUD:
				if(ASRinstance != null){

					ASRinstance.ASRStop();
				}
				if(dkTTS != null){

					dkTTS.TTSStop();
				}
//				dkTTS.TTSStop();
				HciCloudSys.hciRelease();
				break;
		}
		DkConfig.setDkTtsDefault(DkConfig.DK_TTS_NONE);
//		ASRinstance = null;
//		TTSinstance = null;
		this.context = null;
	}

	public static boolean isDkMainInited() {
		return (null != DkMain.context);
	}

	public static void TTSPlay(String texttospeech) {
		if (true == DkMain.isDkMainInited()) {
			dkTTS.TTSPlay(texttospeech);
		}
	}

	public static void TTSStop() {
		if (true == DkMain.isDkMainInited()) {
			dkTTS.TTSStop();
		}
	}


//	public static final int PLAYER_STATE_NOT_INIT = 0;
//	public static final int PLAYER_STATE_IDLE = 1;
//	public static final int PLAYER_STATE_PLAYING = 2;
//	public static final int PLAYER_STATE_PAUSE = 3;
//	public static final int PLAYER_STATE_ERROR = 4;
	public int TTSStatus(){
		if(dkTTS != null){
			return  dkTTS.getTTSStatus();
		}
		return  -1;
	}


	public static void TTSPause() {
		if (true == DkMain.isDkMainInited()) {
			dkTTS.TTSPause();
		}
	}

	public static void TTSResume() {
		if (true == DkMain.isDkMainInited()) {
			dkTTS.TTSResume();
		}
	}

	public static void ASRStop() {
		if (true == DkMain.isDkMainInited()) {
			dkASR.ASRStop();
		}
	}



}
