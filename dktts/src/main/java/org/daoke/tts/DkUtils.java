package org.daoke.tts;

/**
 * Created by liyuanbiao on 2016/3/16.
 */
public class DkUtils {

	public static final String HciCloudSys_class_name = "com.sinovoice.hcicloudsdk.api.HciCloudSys";

	public static boolean checkHcicloud() {
		try {
			Class.forName(HciCloudSys_class_name);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
