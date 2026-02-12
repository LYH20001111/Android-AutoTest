package com.newland.nsdkdemo.internal.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.barcodedecoder.DecodingCallback;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.barcodedecoder.BarcodeDecoder;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;

public class DecodeUtil {
    public static byte[] convertBitmapToYUV420(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 创建YUV数据数组
        byte[] yuv = new byte[width * height * 3 / 2];

        // 获取RGB数据
        int[] argb = new int[width * height];
        bitmap.getPixels(argb, 0, width, 0, 0, width, height);

        // RGB到YUV转换
        encodeYUV420SP(yuv, argb, width, height);

        return yuv;
    }

    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pixel = argb[index];

                // Extract R, G and B
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = (pixel) & 0xFF;

                // Y = 0.299 * R + 0.587 * G + 0.114 * B
                int Y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                // Cb = -0.168736 * R - 0.331264 * G + 0.5 * B + 128
                int U = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                // Cr = 0.5 * R - 0.418688 * G - 0.081312 * B + 128
                int V = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));

                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }
                index ++;

            }
        }
    }
}
