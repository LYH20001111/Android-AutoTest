package com.newland.sdk.me.cmd.serializer;

import java.io.ByteArrayOutputStream;
 
 
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
 
import com.newland.sdk.mtypex.serializer.Serializer;

public class BitmapSerializer  implements Serializer {
	@Override
	public byte[] pack(Object obj) throws Exception {
		byte[] result=null; 
		if(obj instanceof Bitmap){
			Bitmap bitmap=(Bitmap)obj;
			result=bitmapToBytes(bitmap);
		}
		return result;
	}
	
	private static byte[] bitmapToBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		// 将Bitmap压缩成PNG编码，质量为100%存储
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		return os.toByteArray();
	} 
	public static Bitmap BytesToBitmap(byte[] bytes,int offset,int length) {
		if (bytes != null)
			 return BitmapFactory.decodeByteArray(bytes, offset, length);
		return null;
	} 

	@Override
	public Object unpack(byte[] input, int offset, int len) throws Exception {
		Bitmap bitmap=null; 
		bitmap=BytesToBitmap(input,offset,len); 
		return bitmap;
	} 
}
