package org.daoke.tts.hcicloud.tts;

import com.sinovoice.hcicloudsdk.player.TTSCommonPlayer;
import com.sinovoice.hcicloudsdk.player.TTSPlayerListener;

/**
 * Created by shenxingzhao on 2016/3/15.
 */
public class DkTtsEventProcess implements TTSPlayerListener {

    @Override
    public void onPlayerEventPlayerError(TTSCommonPlayer.PlayerEvent playerEvent,
                                         int errorCode) {
    }

    @Override
    public void onPlayerEventProgressChange(TTSCommonPlayer.PlayerEvent playerEvent,
                                            int start, int end) {
    }

    @Override
    public void onPlayerEventStateChange(TTSCommonPlayer.PlayerEvent playerEvent) {
    }

}
