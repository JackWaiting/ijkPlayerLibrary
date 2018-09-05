package com.platomix.lib.playerengine.api;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 播放列表
 */
public class Playlist implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String TAG = "Playlist";

    /**
     * 音乐播放顺序
     */
    private ArrayList<Integer> playOrder = null;


    /**
     * 播放模式
     */
    private PlaybackMode playlistPlaybackMode = PlaybackMode.ALL;


    /**
     * 播放列表
     */
    protected ArrayList<String> playlist = null;

    /**
     * 当前播放的位置
     */
    protected int selected = -1;

    public Playlist() {
        playOrder = new ArrayList<Integer>();
        playlist = new ArrayList<String>();
        calculateOrder(true);
    }

    /**
     * 获得播放模式
     *
     * @return PlaylistPlaybackMode 枚举类
     */
    public PlaybackMode getPlaylistPlaybackMode() {
        return playlistPlaybackMode;
    }

    /**
     * 设置播放模式
     *
     * @param playmode {@link PlaybackMode} 播放模式枚举类
     */
    public void setPlaylistPlaybackMode(
            PlaybackMode playmode) {
        if (playmode == null) {
            Log.w(TAG, "Playbackmode is null!");
            return;
        }
        if (playlistPlaybackMode == playmode) {
            return;
        }
        boolean force = false;
        switch (playmode) {
            case ALL:
            case SINGLE_REPEAT:
                if (playlistPlaybackMode == PlaybackMode.SHUFFLE) {
                    force = true;
                }
                break;
            case SHUFFLE:
                force = true;
                break;
        }
        playlistPlaybackMode = playmode;
        calculateOrder(force);
    }

    /**
     * 添加链接到列表中
     *
     * @param uri 歌曲链接，本地地址或网路链接
     */
    public void addTrackUri(String uri) {
        playlist.add(uri);
        playOrder.add(size() - 1);
    }

    /**
     * 删除歌曲链接
     *
     * @param uri 删除歌曲的uri
     */
    public void removeTrackUri(String uri) {
        int position = playlist.indexOf(uri);
        playlist.remove(uri);
        playOrder.remove(position);
    }

    /**
     * 添加歌曲链接结合
     *
     * @param uris 添加歌曲链接结合
     */
    public void addTrackUris(List<String> uris) {
        int size = uris.size();
        for (int i = 0; i < size; i++) {
            addTrackUri(uris.get(i));
        }
    }

    /**
     * 检查列表是否为空
     *
     * @return boolean true为空
     */
    public boolean isEmpty() {
        return playlist.size() == 0;
    }

    /**
     * 从列表中选择下一首
     */
    public void selectNext() {
        if (!isEmpty()) {
            if (playlistPlaybackMode == PlaybackMode.SHUFFLE) {
                calculateOrder(true);
            } else {
                selected++;
                selected %= playlist.size();
            }
        }
    }

    /**
     * 上一首
     */
    public void selectPrev() {
        if (!isEmpty()) {
            if (playlistPlaybackMode == PlaybackMode.SHUFFLE) {
                calculateOrder(true);
            } else {
                selected--;
                if (selected < 0) {
                    selected = playlist.size() - 1;
                }
            }

        }
    }

    /**
     * 根据位置选择
     *
     * @param index 位置索引
     */
    public void select(int index) {
        if (!isEmpty()) {
            if (index >= 0 && index < playlist.size()) {
                selected = playOrder.indexOf(index);
            }
        }
    }

    /**
     * 添加或选择
     *
     * @param uri 歌曲uri
     */
    public void selectOrAdd(String uri) {
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).equals(uri)) {
                select(i);
                return;
            }
        }

        addTrackUri(uri);
        select(playlist.size() - 1);
    }

    /**
     * 获取当前歌曲在列表中的位置
     *
     * @return int value (如果返回-1则列表为空)
     */
    private int getIndex() {
        if (isEmpty()) {
            selected = -1;
        }
        if (selected == -1 && !isEmpty()) {
            selected = 0;
        }
        return selected;
    }

    /**
     * 获取当前歌曲在列表中的位置
     *
     * @return int value (如果返回-1则列表为空)
     */
    public int getSelectedIndex() {
        int index = getIndex();
        if (index == -1) {
            return index;
        }
        index = playOrder.get(index);
        return index;
    }

    /**
     * 获取当前播放链接
     *
     * @return String
     */
    public String getSelectedUri() {
        String uri = null;
        int index = getIndex();
        if (index == -1) {
            return null;
        }
        index = playOrder.get(index);
        if (index == -1) {
            return null;
        }
        uri = playlist.get(index);

        return uri;

    }


    /**
     * 列表数量
     *
     * @return 列表数量
     */
    public int size() {
        return playlist == null ? 0 : playlist.size();
    }

    /**
     * 获取列表中index位置的歌曲地址
     *
     * @param index 位置
     * @return 歌曲地址
     */
    public String getTrackUri(int index) {
        return playlist.get(index);
    }

    /**
     * 列表中所有链接
     *
     * @return 列表中所有链接
     */
    public String[] getAllTracks() {
        String[] out = new String[playlist.size()];
        playlist.toArray(out);
        return out;
    }

    /**
     *  从列表中删除
     *
     * @param position 要删除的位置
     */
    public void remove(int position) {
        if (playlist != null && position < playlist.size() && position >= 0) {

            if (selected >= position) {
                selected--;
            }

            playlist.remove(position);
            playOrder.remove(position);
        }
    }

    /**
     * 需要时改变列表顺序
     *
     * @param force
     */
    private void calculateOrder(boolean force) {
        if (playOrder.isEmpty() || force) {
            int oldSelected = 0;

            if (!playOrder.isEmpty()) {
                oldSelected = playOrder.get(selected);
                playOrder.clear();
            }

            for (int i = 0; i < size(); i++) {
                playOrder.add(i, i);
            }

            if (playlistPlaybackMode == null) {
                playlistPlaybackMode = PlaybackMode.ALL;
            }

            Log.d(TAG, "Playlist 设置播放模式为" + playlistPlaybackMode);
            switch (playlistPlaybackMode) {
                case ALL:
                case SINGLE_REPEAT:
                    selected = oldSelected;
                    break;
                case SHUFFLE:
                    Collections.shuffle(playOrder);
                    selected = playOrder.indexOf(selected);
                    break;
            }
        }
    }

    /**
     * 是否到了最后一个
     *
     * @return boolean true为最后一个
     */
    public boolean isLastTrackOnList() {
        if (selected == size() - 1)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "Playlist [mPlaylistPlaybackMode=" + playlistPlaybackMode
                + ", playlist=" + playlist + ", selected=" + selected + "]";
    }


}
