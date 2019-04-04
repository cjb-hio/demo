package com.xyw.smartlock.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MyRecorder {

	private final MediaRecorder recorder = new MediaRecorder();
	private final String path;
	private String mFileName;
	File directory;
	private static final String PATH = "/sdcard/MyVoiceForder/Record/";
	private static int SAMPLE_RATE_IN_HZ = 8000;

	public MyRecorder(String path) {
		this.path = sanitizePath(path);
	}

	private String sanitizePath(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.contains(".")) {
			path += ".amr";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/myvoice" + path;
	}

	/** 开始录音 */
	public void start() {
		// if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
		// return;
		// }
		// File directory = new File(path).getParentFile();
		// if(!directory.exists()&& !directory.mkdirs()){
		// return;
		// }
		mFileName = PATH + UUID.randomUUID().toString() + ".amr";
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) {
			Log.e("VoiceStart", "SD Card is not mounted,It is  " + state + ".");
		}
		directory = new File(mFileName).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			Log.e("VoiceStart", "Path to file could not be created");
		}

		try {
			// 设置声音的来源
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置声音的输出格式
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			// 设置声音的编码格式
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			// 设置音频采样率
			recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
			// 设置输出文件
			recorder.setOutputFile(path);
			// 准备录音
			recorder.prepare();

			// 开始录音
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 停止录音 */
	public void stop() {
		try {
			// 停止录音
			recorder.stop();
			// 释放资源
			recorder.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 重置 */
	public void reset() {
		try {
			if (directory != null) { 
                // 先将Adapter删除文件名  
                // 删除文件  
                if (directory.exists()) 
                	directory.delete(); 

            } 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getAmplitude() {
		if (recorder != null) {
			return (recorder.getMaxAmplitude());
		} else
			return 0;
	}
}
