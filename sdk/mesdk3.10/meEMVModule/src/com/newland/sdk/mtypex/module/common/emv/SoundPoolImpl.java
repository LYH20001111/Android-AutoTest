package com.newland.sdk.mtypex.module.common.emv;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SoundPoolImpl {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger(SoundPoolImpl.class);
    private static Map<Integer, SoundPoolImpl> instances = new HashMap<Integer, SoundPoolImpl>();
    private int id;
    private SoundPool soundPool;
    private int clickResId, beepResId;
    private boolean playIng;
    private Object playSync = new Object();

    private SoundPoolImpl(int id) {
        this.id = id;
    }

    public static SoundPoolImpl getInstance(int id) {
        synchronized (SoundPoolImpl.class) {
            SoundPoolImpl soundPool = instances.get(id);
            if (null == soundPool) {
                soundPool = new SoundPoolImpl(id);
                instances.put(id, soundPool);
            }
            return soundPool;
        }
    }

    public void initLoad(Context context,int rawID) {
        devicelogger.debug("current soundPool object:" + soundPool);
        if (null != soundPool)
            return;
        if (id == 0) {
            soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);//Ring
            clickResId = soundPool.load(context.getApplicationContext(), rawID, 1);
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);//Media
            beepResId = soundPool.load(context.getApplicationContext(), rawID, 1);
        }
    }

    public void unLoad() {
        soundPool.release();
        instances.remove(id);
    }

    public void play(final int count, final int time, final int interval) {
        if (id == 0) {
            soundPool.play(clickResId, 1, 1, 0, 0, 1);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setPlayIng(true);
                    devicelogger.debug(">>>count=" + count + " time=" + time + " interval=" + interval);
                    int loop = time / 200;
                    if (loop <= 0) {
                        loop = 1;
                    }
                    for (int i = 0; i < count; i++) {
                        if (!getPlayIng())
                            break;
                        soundPool.play(beepResId, 1, 1, 1, loop - 1, 1);
                        if (!getPlayIng())
                            break;
                        if (count > 1) {
                            Thread.sleep(loop * 200);
                            Thread.sleep(interval);
                        }
                    }
                } catch (Exception e) {
                    devicelogger.error(e.getMessage());
                    try {
                        stop();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop() {
        setPlayIng(false);
        soundPool.autoPause();
    }

    private void setPlayIng(boolean playIng) {
        synchronized (playSync) {
            this.playIng = playIng;
        }
    }

    private boolean getPlayIng() {
        synchronized (playSync) {
            return playIng;
        }
    }
}
