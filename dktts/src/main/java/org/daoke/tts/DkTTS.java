package org.daoke.tts;

import android.content.Context;
import android.util.Log;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;

import org.daoke.tts.hcicloud.tts.DkHcicloudTts;

/**
 * Created by liyuanbiao on 2016/3/16.
 */
public class DkTTS {

	private Context context;

	private int speed;

	private DkHcicloudTts hcicloudTts;

	private int asrType;

	private boolean openLog;

	public DkTTS(Context context, int asrType, int speed, boolean openLog) {

		this.context = context;
		this.asrType = asrType;
		this.speed = speed;
		this.openLog = openLog;

		init();
//		String version = HciCloudSys.hciGetSdkVersion();
//		Log.e("dnc", "tts version = " + version);
	}

	private void init() {
		switch (asrType) {
			case DkConfig.DK_TTS_HCICLOUD:
				hcicloudTts = new DkHcicloudTts(context, speed, null, openLog);
				break;
		}
	}

	public void TTSPlay(String texttospeech) {
		int asrType = DkConfig.getDkTtsDefault();
		switch (asrType) {
			case DkConfig.DK_TTS_HCICLOUD:
				hcicloudTts.speech(texttospeech);
				break;
		}
	}

	public void TTSStop() {
		int asrType = DkConfig.getDkTtsDefault();
		switch (asrType) {
			case DkConfig.DK_TTS_HCICLOUD:
				hcicloudTts.stop();
				break;
		}
	}

	public void TTSPause() {
		int asrType = DkConfig.getDkTtsDefault();
		switch (asrType) {
			case DkConfig.DK_TTS_HCICLOUD:
				hcicloudTts.pause();
				break;
		}
	}

	public void TTSResume() {
		int asrType = DkConfig.getDkTtsDefault();
		switch (asrType) {
			case DkConfig.DK_TTS_HCICLOUD:
				hcicloudTts.resume();
				break;
		}
	}

	public int getTTSStatus(){
		return  hcicloudTts.getStatus();
	}

}
