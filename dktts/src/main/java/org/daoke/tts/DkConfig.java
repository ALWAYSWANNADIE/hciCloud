package org.daoke.tts;

/**
 * Created by liyuanbiao on 2016/3/16.
 */
public class DkConfig {

	public static int DK_TTS_DEFAULT = 1; //语音引擎定义
	public static final int DK_TTS_NONE = 0;
	public static final int DK_TTS_HCICLOUD = 1;
	public static final int DK_TTS_IFLYTEK = 2;
	public static final int DK_TTS_AISPEECH = 3;
	public static final int DK_TTS_BAIDU = 4;

	public static int getDkTtsDefault() {
		return DK_TTS_DEFAULT;
	}

	public static void setDkTtsDefault(int dkTtsDefault) {
		DK_TTS_DEFAULT = dkTtsDefault;
	}
}
