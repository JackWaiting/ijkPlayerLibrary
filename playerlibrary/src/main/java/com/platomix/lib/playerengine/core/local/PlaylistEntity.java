package com.platomix.lib.playerengine.core.local;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaylistEntity implements Parcelable {
	private long id;
	private String title;
	private int duration;
	private long size;
	private String artist;
	private String url;
	private String album;
	private long albumid;

	public PlaylistEntity()
	{
	}

	public PlaylistEntity(long pId, String pTitle)
	{
		id = pId;
		title = pTitle;
	}

	public String getArtist()
	{
		return artist;
	}

	public void setArtist(String artist)
	{
		this.artist = artist;
	}

	public long getSize()
	{
		return size;
	}

	public void setSize(long size)
	{
		this.size = size;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getAlbum()
	{
		return album;
	}

	public void setAlbum(String album)
	{
		this.album = album;
	}

	public long getAlbumid()
	{
		return albumid;
	}

	public void setAlbumid(long albumid)
	{
		this.albumid = albumid;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeLong(id);
		dest.writeString(title);
		dest.writeString(artist);
		dest.writeString(url);
		dest.writeInt(duration);
		dest.writeLong(size);
	}

	public static final Parcelable.Creator<PlaylistEntity> CREATOR = new Creator<PlaylistEntity>()
	{
		@Override
		public PlaylistEntity[] newArray(int size)
		{
			return new PlaylistEntity[size];
		}

		@Override
		public PlaylistEntity createFromParcel(Parcel source)
		{
			PlaylistEntity musicInfo = new PlaylistEntity();
			musicInfo.setId(source.readLong());
			musicInfo.setTitle(source.readString());
			musicInfo.setArtist(source.readString());
			musicInfo.setUrl(source.readString());
			musicInfo.setDuration(source.readInt());
			musicInfo.setSize(source.readLong());
			return musicInfo;
		}
	};
}