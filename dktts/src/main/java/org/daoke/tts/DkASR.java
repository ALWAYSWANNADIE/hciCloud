package org.daoke.tts;

import android.content.Context;
import android.util.Log;

import org.daoke.tts.hcicloud.asr.DkHcicloudAsr;

import java.util.Locale;

/**
 * Created by liyuanbiao on 2016/3/17.
 */
public class DkASR {

	private Context context;
	private DkASRInterface asrInterfacel;

	private DkHcicloudAsr hcicloudAsr;
	private DkASRsoundInterface soundCallback;
	private int asrType;

	public DkASR(Context context, int asrType, DkASRInterface asrInterfacel,DkASRsoundInterface soundCallback) {

		this.context = context;
		this.asrType = asrType;
		this.asrInterfacel = asrInterfacel;
		this.soundCallback = soundCallback;
		init();
	}

	private void init() {
		switch (asrType) {
			case DkConfig.DK_TTS_HCICLOUD:
				hcicloudAsr = new DkHcicloudAsr(context);
				hcicloudAsr.setAsrCallBack(this.asrInterfacel);
				hcicloudAsr.setAsrsoundCallBack(this.soundCallback);
//				hcicloudAsr.startReco();
				break;
		}
	}

	public void ASRStop(){
		int asrType = DkConfig.getDkTtsDefault();
		switch (asrType) {
			case DkConfig.DK_TTS_HCICLOUD:
				hcicloudAsr.stop();
				break;
		}
	}

	public void ASRStart(){
		int asrType = DkConfig.getDkTtsDefault();
		switch (asrType) {
			case DkConfig.DK_TTS_HCICLOUD:
				hcicloudAsr.startReco();
				break;
		}
	}
}
