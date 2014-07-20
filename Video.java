package com.caliente.android.vod;

import android.graphics.Bitmap;

public class Video
{
	private String id_name=null, titre=null, desc=null, path_img=null, path_mp4=null;
	private boolean paye=false;
	private int nid=0;
	private Bitmap bitmap=null;
	
	public Video(String _id_name, int _nid, String _titre, String _desc, String _path_img, String _path_mp4, boolean _paye)
	{
		id_name=_id_name;
		nid=_nid;
		titre=_titre;
		desc=_desc;
		path_img=_path_img;
		path_mp4=_path_mp4;
		paye=_paye;
	}

	public String getIdName() {
		return id_name;
	}

	public int getNid() {
		return nid;
	}

	public String getTitre() {
		return titre;
	}

	public String getDesc() {
		return desc;
	}

	public String getPathImg() {
		return path_img;
	}
	
	public boolean getPaye() {
		return paye;
	}
	
	public void setPaye(boolean _paye) {
		paye=_paye;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap _bitmap) {
		bitmap=_bitmap;
	}

	public String getPathMp4() {
		return path_mp4;
	}
}