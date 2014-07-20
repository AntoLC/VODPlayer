package com.caliente.android.vod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.caliente.vod.util.BDOpenHelper;

@SuppressLint({ "NewApi", "UseSparseArrays" })
public class BDVod
{
	private static final String NAME_BD="VOD";
	private static final int VERSION_BD=1;
	
	private SQLiteDatabase BD;
	private BDOpenHelper bdOpenHelper;
	
	public BDVod(Context context) {
		// TODO Auto-generated constructor stub
		bdOpenHelper = new BDOpenHelper(context, NAME_BD, null, VERSION_BD, constructStructureBD());
	}
	
	private Map<String, ArrayList<String>> constructStructureBD()
	{
		Map<String, ArrayList<String>> table = new HashMap<String, ArrayList<String>>();
		
		// TABLE video
		ArrayList<String> colonnes = new ArrayList<String>();
		colonnes.add("id INTEGER PRIMARY KEY AUTOINCREMENT");
		colonnes.add("nid INTEGER(11)");
		colonnes.add("last_view DATETIME DEFAULT NULL");
		colonnes.add("UNIQUE(nid) ON CONFLICT REPLACE");
		table.put("video", colonnes);
		
		// TABLE categorie
		colonnes = new ArrayList<String>();
		colonnes.add("id INTEGER PRIMARY KEY AUTOINCREMENT");
		colonnes.add("tid VARCHAR(30)");
		colonnes.add("name VARCHAR(30)");
		colonnes.add("UNIQUE(tid) ON CONFLICT REPLACE");
		table.put("categorie", colonnes);
		
		// TABLE video_categorie
		colonnes = new ArrayList<String>();
		colonnes.add("id INTEGER PRIMARY KEY AUTOINCREMENT");
		colonnes.add("nid INTEGER");
		colonnes.add("tid INTEGER");
		colonnes.add("UNIQUE(nid, tid) ON CONFLICT IGNORE");
		table.put("video_categorie", colonnes);
		
		return table;
	}
	
	public SQLiteDatabase open(){
		return BD = bdOpenHelper.getWritableDatabase();
	}
	
	public SQLiteDatabase getBD(){
		return BD;
	}
	
	public void close(){
		BD.close();
	}
	
	public long insertVideo(int nid)
	{
		ContentValues valeurs = new ContentValues();
		valeurs.put("nid", nid);
		return BD.insert("video", null, valeurs);
	}
	
	public long insertCategorie(int tid, String name)
	{
		ContentValues valeurs = new ContentValues();
		valeurs.put("tid", tid);
		valeurs.put("name", name);
		return BD.insert("categorie", null, valeurs);
	}
	
	public long insertVideoCategorie(int nid, int tid)
	{
		ContentValues valeurs = new ContentValues();
		valeurs.put("nid", nid);
		valeurs.put("tid", tid);
		return BD.insert("video_categorie", null, valeurs);
	}
	
	public int countCategories()
	{
		Cursor c = BD.query(true, "categorie", new String[]{"tid", "name"}, null, null, null, null, null, null, null);
		if(c.getCount() == 0)
			return 0;
		
		int count=c.getCount();
		
		c.close();
		
		return count;
	}
	
	public int countVideo()
	{
		Cursor c = BD.query(true, "video", new String[]{"nid"}, null, null, null, null, null, null, null);
		if(c.getCount() == 0)
			return 0;
		
		int count=c.getCount();
		
		c.close();
		
		return count;
	}
	
	public Map<Integer, String> selectCategories()
	{
		Cursor c = BD.query(true, "categorie", new String[]{"tid", "name"}, null, null, null, null, "name", null, null);
		if(c.getCount() == 0)
			return null;
		
		Map<Integer, String> categorie = new HashMap<Integer, String>();
		
		c.moveToFirst();
		do{
			categorie.put(c.getInt(0), c.getString(1));
		}while(c.moveToNext());
		
		c.close();
		
		return categorie;
	}
	
	public ArrayList<Integer> selectTidByName(String nameCategorie)
	{
		Cursor c = BD.query(true, "categorie", new String[]{"tid"}, "LOWER(name)=LOWER('"+nameCategorie+"')", null, null, null, "tid", null, null);
		if(c.getCount() == 0)
			return null;
		
		ArrayList<Integer> tidByName = new ArrayList<Integer>();
		c.moveToFirst();
		do{
			tidByName.add(c.getInt(0));
		}while(c.moveToNext());
		
		c.close();
		
		return tidByName;
	}
	
	public ArrayList<Integer> selectNidByCategories(int tid)
	{
		Cursor c = BD.query(true, "video_categorie", new String[]{"nid"}, "tid="+tid, null, null, null, "nid", null, null);
		if(c.getCount() == 0)
			return null;
		
		ArrayList<Integer> nidByCat = new ArrayList<Integer>();
		
		c.moveToFirst();
		do{
			nidByCat.add(c.getInt(0));
		}while(c.moveToNext());
		
		c.close();
		
		return nidByCat;
	}
}