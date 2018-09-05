package com.platomix.lib.playerengine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.platomix.lib.playerengine.core.local.LoadMusicCallback;
import com.platomix.lib.playerengine.core.local.PlaylistEntity;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class MusicLoader {
	
	private static final int DEF_MIN_MUSIC_DURATION = 20000;
	private int minMusicDuration = DEF_MIN_MUSIC_DURATION;
	private static final String TAG = "MusicLoader";

	private static MusicLoader musicLoader;
	private static ContentResolver contentResolver;
	private Uri contentUri = Media.EXTERNAL_CONTENT_URI;
	private Cursor cursor;
	private LoadMusicTask loadMusicTask;
	private Executor executor;
	private LoadMusicCallback callback;
	
	private String[] imageColumns = { MediaStore.Audio.Media._ID, // 歌曲ID
			MediaStore.Audio.Media.TITLE,// 歌曲名称
			MediaStore.Audio.Media.ARTIST,// 歌曲的歌手名
			MediaStore.Audio.Media.DATA, // 歌曲文件的路径
			MediaStore.Audio.Media.DURATION,// 歌曲的总播放时长
			MediaStore.Audio.Media.SIZE,// 歌曲的总播放时长
			MediaStore.Audio.Media.ALBUM,// 歌曲专辑名
			MediaStore.Audio.Media.ALBUM_ID,// 歌曲专辑ID
	};

	public static MusicLoader instance(ContentResolver pContentResolver) {
		if (musicLoader == null) {
			contentResolver = pContentResolver;
			musicLoader = new MusicLoader();
		}
		return musicLoader;
	}

	private MusicLoader() {
		executor = Executors.newFixedThreadPool(1);
	}
	
	public void setMinMusicDuration(int minMusicDuration) {
		this.minMusicDuration = minMusicDuration;
	}

	private class LoadMusicTask extends AsyncTask<String, Void, List<PlaylistEntity>>{
		String pathFilter;
		public LoadMusicTask(String pathString) {
			// TODO Auto-generated constructor stub
			pathFilter=pathString;
		}
		@Override
		protected List<PlaylistEntity> doInBackground(String... arg0) {
			return getMusicList(pathFilter);
		}
		
		@Override
		protected void onPostExecute(List<PlaylistEntity> result) {
			super.onPostExecute(result);
			if(callback != null){
				callback.onLoadMusic(result);
			}
		}
	}
	
	/**
	 * @param callback LoadMusicCallback监听
	 * @param pathFilter 指定路径 ，目前为指定一个文件名
	 */
	public void loadMusic(LoadMusicCallback callback,String pathFilter){
		this.callback = callback;
		if(loadMusicTask != null && !loadMusicTask.isCancelled()){
			loadMusicTask.cancel(true);
			loadMusicTask = null;
		}
		loadMusicTask = new LoadMusicTask(pathFilter);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
    		forAPI11();
    	}else{
    		loadMusicTask.execute("");
    	}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
    private void forAPI11(){
		loadMusicTask.executeOnExecutor(executor,"");
    }

	private List<PlaylistEntity> getMusicList(String pathFilter) {
		List<PlaylistEntity> musicList = new ArrayList<PlaylistEntity>(); // 歌曲信息列表
		cursor = contentResolver.query(contentUri, imageColumns, null, null,
				null);
		if (cursor == null) {
			Log.v(TAG, "Line(51	)	Music Loader cursor == null.");
		} else if (!cursor.moveToFirst()) {
			Log.v(TAG,
					"Line(54)	Music Loader cursor.moveToFirst() returns false.");
		} else {
			int idCol = cursor.getColumnIndex(Media._ID);
			int displayNameCol = cursor.getColumnIndex(Media.TITLE);
			int artistCol = cursor.getColumnIndex(Media.ARTIST);
			int urlCol = cursor.getColumnIndex(Media.DATA);
			int durationCol = cursor.getColumnIndex(Media.DURATION);
			int sizeCol = cursor.getColumnIndex(Media.SIZE);
			int albumCol = cursor.getColumnIndex(Media.ALBUM);
			int albumidCol = cursor.getColumnIndex(Media.ALBUM_ID);
			do {
				String title = cursor.getString(displayNameCol);
				long id = cursor.getLong(idCol);
				int duration = cursor.getInt(durationCol);
				if (minMusicDuration > duration&&duration!=0) {
					// 过滤过小的音乐文件，有的手机(小米5)获取得到的duration为0。
					continue;
				}
				long size = cursor.getLong(sizeCol);
				String artist = cursor.getString(artistCol);
				String url = cursor.getString(urlCol);
				String album = cursor.getString(albumCol);
				long albumid = cursor.getLong(albumidCol);

				if (url.indexOf(pathFilter)>=0) {
					
					PlaylistEntity musicInfo = new PlaylistEntity(id, title);
					musicInfo.setDuration(duration);
					musicInfo.setSize(size);
					musicInfo.setArtist(artist);
					musicInfo.setUrl(url);
					musicInfo.setAlbum(album);
					musicInfo.setAlbumid(albumid);
					musicList.add(musicInfo);
				}

			} while (cursor.moveToNext());
		}
		close();
		return musicList;
	}

	private void close() {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
}
