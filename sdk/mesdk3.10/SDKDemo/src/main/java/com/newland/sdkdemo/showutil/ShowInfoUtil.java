package com.newland.sdkdemo.showutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class ShowInfoUtil implements IShowInfo {

    private Context context;

    public ShowInfoUtil(Context context) {
        this.context = context;
    }

    @Override
    public void showMessage(String mess, MessageTag messageType) {
        showMessage(mess, messageType, true);
    }

    @Override
    public void showMessage(String mess, MessageTag messageType, boolean linefeed) {
        if (null == messageType) {
            messageType = MessageTag.NORMAL;
        }
        switch (messageType) {
            case ERROR:
                mess = "<font color='red'>" + mess + "</font>";
                break;
            case TIP:
                mess = "<font color='orange'>" + mess + "</font>";
                break;
            case DATA:
                mess = "<font color='blue'>" + mess + "</font>";
                break;
            default:
                mess = "<font color='black'>" + mess + "</font>";
                break;
        }
        setText(mess, linefeed);
    }

    /*
    缓存base64图片数据
     */
    List<String> imageCache;

    @Override
    public void showImage(String imageData) {
        if (null==imageData||"".equals(imageData))
            return;
        if (null == imageCache)
            imageCache = new ArrayList<>();
        imageCache.add(imageData);
        setText("<img src='" + imageCache.size() + "'/>", true);
    }

    @Override
    public void showImage(Bitmap bmp) {
        if (null == bmp)
            return;
        if (null == imageCache)
            imageCache = new ArrayList<>();
        imageCache.add(CommonUtils.bitmapToBase64(bmp));
        setText("<img src='" + imageCache.size() + "'/>", true);
    }

    @Override
    public void showImage(int resourceId) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resourceId);
        showImage(bmp);
    }


    public void setShowMessageView(TextView showView) {
        this.showTv = showView;
        this.showTv.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private TextView showTv;

    private void setText(String htmlString, boolean linefeed) {
        if (null == showTv)
            return;
        if (null == imageCache)
            imageCache = new ArrayList<>();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                String s = Html.toHtml(new SpannableString(showTv.getText()));
                if (null!=s&&!"".equals(s) && !linefeed) {
                    s = "<br>" + s.substring(13, s.length() - 3);
                }
                showTv.setText(Html.fromHtml(htmlString + s, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        if ((Integer.parseInt(source) - 1) < 0 || Integer.parseInt(source) > imageCache.size())
                            return null;
                        byte[] decode = Base64.decode(imageCache.get(Integer.parseInt(source) - 1), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                        if (bitmap.getWidth() > showTv.getWidth())
                            bitmap = scaleBitmap(bitmap, showTv.getWidth() * 1.0f / bitmap.getWidth());
                        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        return drawable;
                    }
                }, null));
                showTv.scrollTo(0, 0);
            }
        });
    }

    @Override
    public void cleanMessage() {
        if (null != imageCache)
            this.imageCache.clear();
        if (null != showTv) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showTv.setText((CharSequence)null);
                }
            });
        }
    }

//    @Override
//    public void showResult(MethodBean bean) {
//        showMessage(bean.getMethodAno().name() + (bean.isPass() ? "成功" : "失败"), bean.isPass() ? MessageTag.DATA :
//                MessageTag.ERROR);
//    }

    @Override
    public void showError(Exception e) {
        showMessage(e.getCause().getMessage(), MessageTag.ERROR);
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        Log.d("showInfoUtil", "scaleBitmap,ratio: "+ratio);
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }
}
