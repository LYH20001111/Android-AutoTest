package com.newland.sdk.me.module.emv;

import android.content.Context;

import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TransferSequenceGenerator{
	
	private DeviceLogger logger = DeviceLoggerFactory.getLogger(TransferSequenceGenerator.class);
	
	private final int MAXSEQ = 99999999;
	
	private final String filename = "trans.seq";
	
	private Context context;
	
	private File target;
	
	private final int MIX = 1;
	
	private Integer lo = 0;
	
	private int seq = 0;

	private static volatile TransferSequenceGenerator seqGen;

	public static TransferSequenceGenerator getInstance(Context context){
		if (seqGen == null) {
			synchronized (TransferSequenceGenerator.class) {
				if (seqGen == null) {
					seqGen = new TransferSequenceGenerator(context);
					return seqGen;
				}
			}
		}
		return seqGen;
	}
	
	private TransferSequenceGenerator(Context context){
		this.context = context;
		init();
	}
	
	private void init(){
		String filepath = context.getFilesDir().getAbsolutePath() + "/" +filename;
		target = new File(filepath);
		if(!target.exists()){
			try {
				target.createNewFile();
			} catch (IOException e) {
				logger.error("failed to create transeq file!",e);
			}
		}
		if(!target.exists()){
			throw new DeviceRTException(ErrorCode.UNKNOWN, "failed to create transeq file!");
		}
	}

	public int next(){
		logger.debug("[next]seq:"+seq+";lo:"+lo);
//		synchronized (lo) {
//			if(seq <= 0 ){
//				readHILO();
//				return seq;
//			}
//		}

		synchronized (lo) {
			seq ++;
		//	lo --;
		}
		if(seq>MAXSEQ){
			seq =1;
		}
		return seq;
	}

	private void readHILO() {
		FileReader fr = null;
		FileWriter fw = null;
		try{
			fr = new FileReader(target);
			BufferedReader br = new BufferedReader(fr);
			String firstline = br.readLine();
			
			if(firstline == null || firstline.length() <= 0){
				fw = new FileWriter(target);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(1 + "\n");
				seq = 1;
			}else{
				try{
					logger.debug("[readHILO] firstline:"+firstline);
					seq = Integer.parseInt(firstline);
				}catch(Exception e){
					logger.warn("failed to parse number:"+firstline);
					seq = 1;
				}
			}
			int nextSeq = seq + MIX;
			if(nextSeq > MAXSEQ){ //简单做，序列号如果跨届了，则跨界部分序号就丢弃。
				seq = 1;
				nextSeq = seq + MIX;
			}
					
			fw = new FileWriter(target);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(nextSeq + "\n");
			
		}catch(Exception e){
			lo = 0;
			seq = 0;
		}finally{
			if(fr != null){
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
			if(fw != null){
				try{
					fw.close();
				}catch(Exception e){
				}
			}
		}
	}
}
