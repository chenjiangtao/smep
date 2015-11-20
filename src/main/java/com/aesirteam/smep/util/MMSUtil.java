package com.aesirteam.smep.util;

import com.aesirteam.smep.client.MasConstants;
import com.aesirteam.smep.client.message.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Random;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class MMSUtil {
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
	private static Random randGen = new Random();
	private final static char[] numbers = ("0123456789876543210123456789876543210123").toCharArray();
	
	public static MMSFile[] getMMSFile(String[] adjFiles) throws Exception {	
		LinkedList<File> adjFileList = new LinkedList<File>();
		//检查附件列表中的文件路径是否合法，并将.smil放在首位置
		for (String fn: adjFiles) {
			File file = new File(fn);
			if (file.exists() && file.isFile()) {
				if (fn.toLowerCase().endsWith(".smil"))
					adjFileList.addFirst(file);
				else
					adjFileList.addLast(file);
			}
		}
		
		if ( 0 == adjFileList.size())
			throw new Exception(MasConstants.MMS_ADJFILES_NOTEXISTS + "");
		
		MMSFile[] vMMSFile = new MMSFile[adjFileList.size()];
		int fileIndex = 0;
		for (File file : adjFileList) {
			String filename = file.getName();
			int mmsFileType = MMSFile.getMMSFileType(filename);
			if (mmsFileType < 0) continue;
			
			vMMSFile[fileIndex] = new MMSFile();
			vMMSFile[fileIndex].setName(filename);
			vMMSFile[fileIndex].setFilename(file.getAbsolutePath());
			vMMSFile[fileIndex].setType(mmsFileType);
			vMMSFile[fileIndex].setBody(encodeBase64File(file));
			fileIndex++;
		}
		
		return vMMSFile;	
	}
	
	public static String getTransactionId() {
		StringBuffer sb = new StringBuffer();
		sb.append(sdf.format(System.currentTimeMillis()));
		for (int i = 0; i < 6; i++) {
			sb.append(numbers[randGen.nextInt(40)]);
		}
		return sb.toString();
	}
	
	public static void createMMContentFile(String filename, byte data[]) {
		RandomAccessFile fis = null;
		FileLock tryLock = null;
		try {
			fis = new RandomAccessFile(filename, "rw");
			while(true) {
				try {
					tryLock = fis.getChannel().tryLock();
					break;
				} catch (Exception ex) {
					try { Thread.sleep(100); } catch (InterruptedException e) {}
				}
			}
			fis.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != tryLock)
					tryLock.release();

				if (null != fis)
					fis.close();
			} catch (IOException e) {}
		}
	}
	
	public static String encodeBase64File(File file) throws IOException {
		int fileLen = (int)file.length();
		byte[] bytes = new byte[fileLen];
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		int readLen = bis.read(bytes);
		if (fileLen != readLen) throw new IOException("读取文件失败");
		bis.close();
		
		// 对字节数组Base64编码
		return new BASE64Encoder().encode(bytes);
	}
	
	public static void decoderBase64File(String base64Code, String targetPath) throws IOException {
		byte[] bytes = new BASE64Decoder().decodeBuffer(base64Code);
		FileOutputStream out = new FileOutputStream(targetPath);
		out.write(bytes);
		out.close();
	}
	
	public static void main(String args[]) {
		String base64Code = "iVBORw0KGgoAAAANSUhEUgAAAAkAAAAICAYAAAArzdW1AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3NpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo0ODkzMWI3Ny04YjE5LTQzYzMtOGM2Ni0wYzdkODNmZTllNDYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6RDI0OTcxRkU0OEM2MTFFM0I4MTREM0ZBQTFCNDE3NTgiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6RDI0OTcxRkQ0OEM2MTFFM0I4MTREM0ZBQTFCNDE3NTgiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIChNYWNpbnRvc2gpIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6NDA5ZGQxNDktNzdkMi00M2E3LWJjYWYtOTRjZmM2MWNkZDI0IiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjQ4OTMxYjc3LThiMTktNDNjMy04YzY2LTBjN2Q4M2ZlOWU0NiIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PvgIj/QAAABYSURBVHjadI6BCcAgDAS/0jmyih2tm2lHSRZJX6hQQ3w4FP49LKraSHV3ZLDzAuAi3cwaqUhSfvft+EweznHneUdTzPGRmp5hEJFhAo3LaCnjn7blzCvAAH9YOSCL5RZKAAAAAElFTkSuQmCC";
		try {
			decoderBase64File(base64Code, "c:\\logo.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
