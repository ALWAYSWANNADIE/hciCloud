package org.daoke.tts.hcicloud.asr;


import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.recorder.ASRCommonRecorder;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;

/**
 * Created by shenxingzhao on 2016/3/15.
 */
public class DkAsrRecorderAdapter implements ASRRecorderListener {
    @Override
    public void onRecorderEventStateChange(ASRCommonRecorder.RecorderEvent recorderEvent) {

    }

    @Override
    public void onRecorderEventRecogFinsh(ASRCommonRecorder.RecorderEvent recorderEvent, AsrRecogResult asrRecogResult) {

    }

    @Override
    public void onRecorderEventRecogProcess(ASRCommonRecorder.RecorderEvent recorderEvent, AsrRecogResult asrRecogResult) {

    }

    @Override
    public void onRecorderEventError(ASRCommonRecorder.RecorderEvent recorderEvent, int i) {

    }

    @Override
    public void onRecorderRecording(byte[] bytes, int i) {

    }
}
