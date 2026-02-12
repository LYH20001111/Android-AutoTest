package com.newland.sdkdemo.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newland.basetest.annotation.FragmentAno;
import com.newland.basetest.annotation.MethodAno;
import com.newland.basetest.pinc.MessageTag;
import com.newland.basetest.pinc.MethodBean;
import com.newland.basetest.view.msgDialog.MessageDialog;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.cardreader.RFCardInfo;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.scanner.DefaultScannerLayout;
import com.newland.sdk.module.scanner.ScannerExtParams;
import com.newland.sdk.module.scanner.ScannerListener;
import com.newland.sdk.module.scanner.ScannerModule;
import com.newland.sdk.module.scanner.ScannerType;
import com.newland.sdk.module.swiper.MagStripeCardModule;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdkdemo.FragmentBase;


@FragmentAno(name = "磁条卡", numId = 3)
public class MagCardOption extends FragmentBase {
    private MagStripeCardModule magStripeCardModule;
    private CardReaderModule cardReaderModule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        magStripeCardModule = getModuleManage().getMagStripeCardModule();
        cardReaderModule = getModuleManage().getCardReaderModule();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @MethodAno(name = "开始刷卡", numId = 0)
    public void startScan(MethodBean bean) {
        CardReaderExtParams cardReaderExtParams = new CardReaderExtParams();
        cardReaderExtParams.setCheckUnionCard(false);//不校验磁银行卡道规则
        cardReaderModule.openCardReader(new CardType[]{CardType.MSGCARD}, 30, new CardReaderListener() {
            @Override
            public void onTimeout() {
                showMessage("开启读卡器超时",MessageTag.ERROR);
            }

            @Override
            public void onCancel() {
                showMessage("取消读卡",MessageTag.ERROR);
            }

            @Override
            public void onError(int errorcode, String msg) {
                showMessage("开启读卡器异常，异常码："+errorcode+";异常信息："+msg,MessageTag.ERROR);
            }

            @Override
            public void onFindMagCard(boolean b) {
                showMessage("识别到磁条卡",MessageTag.NORMAL);
                SwipResult swipResult = magStripeCardModule.readPlainResult(new SwiperReadModel[]{SwiperReadModel.FIRST_TRACK,SwiperReadModel.SECOND_TRACK,SwiperReadModel.THIRD_TRACK});
                if(swipResult!=null){
                    showMessage("一磁道信息："+(swipResult.getFirstTrackData()==null?"":(new String(swipResult.getFirstTrackData()))),MessageTag.NORMAL);
                    showMessage("二磁道信息："+(swipResult.getSecondTrackData()==null?"":(new String(swipResult.getSecondTrackData()))),MessageTag.NORMAL);
                    showMessage("三磁道信息："+(swipResult.getThirdTrackData()==null?"":(new String(swipResult.getThirdTrackData()))),MessageTag.NORMAL);
                }else{
                    showMessage("读取磁道信息异常，磁道数据空",MessageTag.ERROR);
                }
            }

            @Override
            public void onFindICCard() {
                showMessage("识别到IC卡",MessageTag.ERROR);
            }

            @Override
            public void onFindRFCard(@Nullable RFCardType rfCardType, @Nullable RFCardInfo rfCardInfo) {
                showMessage("识别到非接卡",MessageTag.ERROR);
            }
        },cardReaderExtParams);
    }

    @MethodAno(name = "取消读卡", numId = 1)
    public void cancelCardReader(MethodBean bean) {
        cardReaderModule.cancelCardReader();
        showMessage("取消读卡完成", MessageTag.NORMAL);
    }

}
