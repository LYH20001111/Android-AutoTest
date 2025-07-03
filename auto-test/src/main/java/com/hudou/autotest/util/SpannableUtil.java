package com.hudou.autotest.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;


public class SpannableUtil {

    /**
     * 将content中的partContent设置为color
     * @param context 上下文
     * @param content 内容
     * @param partContent 部分内容
     * @param color 部分内容设置的颜色
     * @return SpannableString
     */
    public static SpannableString setStringColor(Context context, @NonNull String content, @NonNull String partContent, int color){
        SpannableString spannableString = new SpannableString(content);
        int start = content.indexOf(partContent);
        int end = start + partContent.length();
        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(color)), start, end, 0);
        return spannableString;
    }


}
