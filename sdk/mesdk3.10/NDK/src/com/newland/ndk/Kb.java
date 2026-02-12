package com.newland.ndk;

public class Kb {
	public native int NDK_KbFlush();
	public native int NDK_KbGetCode(int unTime, int pnCode[]);
	public native int NDK_KbHit(int pnCode[]);
//	public native int NDK_KbGetInput(char *pszBuf,unsigned int unMinLen,int unMaxLen,
//		                                    unsigned int *punLen,EM_INPUTDISP emMode,unsigned int unWaitTime,
//		                                    EM_INPUT_CONTRL emControl);
}
