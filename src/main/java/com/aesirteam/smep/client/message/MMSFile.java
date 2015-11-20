package com.aesirteam.smep.client.message;

import java.io.Serializable;
import com.cmcc.mm7.vasp.common.MMConstants.ContentType;
import com.cmcc.mm7.vasp.common.MMContentType;


public class MMSFile  implements Serializable {
	
	private static final long serialVersionUID = 7667603360356234284L;
	
	private final static int TYPE_SMIL = 0;
	private final static int TYPE_TXT = 1;
	private final static int TYPE_JPG = 2;
	private final static int TYPE_PNG = 3;
	private final static int TYPE_GIF = 4;
	private final static int TYPE_MIDI = 5;
	private final static int TYPE_ARM = 6;
	private final static int TYPE_WBMP = 7;
	
	public static int getMMSFileType(String filename) {
		if (filename.endsWith(".smil")) 
			return TYPE_SMIL;
		else if (filename.endsWith(".jpg")) 
			return TYPE_JPG;
		else if (filename.endsWith(".txt")) 
			return TYPE_TXT;
		else if (filename.endsWith(".gif")) 
			return TYPE_GIF;
		else if (filename.endsWith(".png")) 
			return TYPE_PNG;
		else if (filename.endsWith(".mid")) 
			return TYPE_MIDI;
		else if (filename.endsWith(".arm")) 
			return TYPE_ARM;
		else if (filename.endsWith(".wbmp")) 
			return TYPE_WBMP;
		return -1;
	}
		
	public static MMContentType getMMSContentType(int type) {
		switch(type) {
		case TYPE_SMIL: 
			return ContentType.SMIL;
		case TYPE_JPG:
			return ContentType.JPEG;
		case TYPE_TXT:
			return ContentType.TEXT;
		case TYPE_GIF:
			return ContentType.GIF;
		case TYPE_PNG:
			return ContentType.PNG;	
		case TYPE_MIDI:
			return ContentType.MIDI;
		case TYPE_ARM:
			return ContentType.AMR;	
		case TYPE_WBMP:
			return ContentType.WBMP;
		}
		return null;
	}
	
	private String name;
	
	private  String filename;
	
	
	private int type;
	
	private String body ;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public  void setFilename(String filename) {
		this.filename = filename;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
