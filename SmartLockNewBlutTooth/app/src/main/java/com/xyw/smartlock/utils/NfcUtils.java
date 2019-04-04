package com.xyw.smartlock.utils;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.tech.NfcA;
import android.os.Vibrator;
import android.provider.Settings;

import com.xyw.smartlock.R;


/**
 * Created by HP on 2017/4/14.
 */

public class NfcUtils {
    public NfcAdapter mAdapter;
    public NfcManager mNfcManager;
    private static NfcUtils nfcUtils;
    private Context mContext;
    public PendingIntent mPendingIntent;
    public IntentFilter[] mFilters;
    public String mTechLists[][];
    private MediaPlayer success, faile;
    private AudioManager mAudioManager;
    private boolean shouldPlayBeep, shouldVibrator;
    private Vibrator mVibrator;

    public static NfcUtils getInstance() {
        if (null == nfcUtils) {
            synchronized (NfcUtils.class) {
                if (null == nfcUtils) {
                    nfcUtils = new NfcUtils();
                }
            }
        }
        return nfcUtils;
    }

    public void init(Context context) {
        this.mContext = context;
        mNfcManager = (NfcManager) mContext.getSystemService(Context.NFC_SERVICE);
        mAdapter = mNfcManager.getDefaultAdapter();
        initMedia();
        initVibrator();
    }

    /**
     * 震动
     */
    private void initVibrator() {
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * 提示音 用mediaPlayer替换SoundPool
     */
    private void initMedia() {
        success = new MediaPlayer();
        faile = new MediaPlayer();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            shouldPlayBeep = true;
            shouldVibrator = true;
        } else if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            shouldPlayBeep = false;
            shouldVibrator = true;
        } else {
            shouldPlayBeep = false;
            shouldVibrator = false;
        }
        setMedia(success, R.raw.locktrue);
        setMedia(faile, R.raw.lockfalse);
    }

    private void setMedia(final MediaPlayer mediaPlayer, int rawid) {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.seekTo(0);
            }
        });
        AssetFileDescriptor file = mContext.getResources().openRawResourceFd(rawid);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void starSuccessMedia() {
        if (shouldPlayBeep && success != null) {
            success.start();
        }
        if (mVibrator != null && shouldVibrator) {
            mVibrator.vibrate(new long[]{300, 10, 10, 300}, -1);
        }
    }

    public void startFaileMedia() {
        if (shouldPlayBeep && faile != null) {
            faile.start();
        }
        if (mVibrator != null && shouldVibrator) {
            mVibrator.vibrate(new long[]{300, 10, 10, 300}, -1);
        }
    }

    public boolean isHasNFC() {
        if (mContext == null || mNfcManager == null || mAdapter == null) return false;
        return true;
    }

    public boolean isOpenNFC() {
        mNfcManager = (NfcManager) mContext.getSystemService(Context.NFC_SERVICE);
        mAdapter = mNfcManager.getDefaultAdapter();
        if (mNfcManager == null || mAdapter == null) return false;
        return mAdapter.isEnabled();
    }

    public void requestOpenNFC(final Context context) {
        new AlertDialog.Builder(context).setMessage(R.string.request_open_nfc).setPositiveButton(mContext.getString(R.string.set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    /**
     * Nfc功能
     */
    public void setNfcForeground(Class<?> clazz, Context context) {
        // 初始化PendingIntent，当有NFC设备连接上的时候，就交给当前Activity处理
        mPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, clazz).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mFilters == null)
            mFilters = new IntentFilter[]{
                    new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};
        if (mTechLists == null)
            mTechLists = new String[][]{new String[]{NfcA.class.getName()}};
    }
}
