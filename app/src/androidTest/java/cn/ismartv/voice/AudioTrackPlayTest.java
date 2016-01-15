package cn.ismartv.voice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.test.AndroidTestCase;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by huaijie on 1/15/16.
 */
public class AudioTrackPlayTest extends AndroidTestCase {


    public void testPlayPcm() {
        try {
            InputStream inputStream = getContext().getAssets().open("16k_test.pcm");
            byte[] content = new byte[inputStream.available()];
            inputStream.read(content);
            inputStream.close();

            AudioTrack tr = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    22050,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    content.length,
                    AudioTrack.MODE_STREAM);
            tr.play();
            tr.write(content, 0, content.length);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
