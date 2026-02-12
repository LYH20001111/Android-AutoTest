package com.newland.nsdkdemo.internal;

import android.content.Context;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.utils.NativeDebugLevel;
import com.newland.nsdk.core.api.internal.NSDKModuleManager;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.LogLevel;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.common.MainActivity;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.fragment.ModuleInfo;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.internal.fragment.GuestDisplayManagerFragment;
import com.newland.nsdkdemo.internal.fragment.BarcodeScannerFragment;
import com.newland.nsdkdemo.internal.fragment.BeeperFragment;
import com.newland.nsdkdemo.internal.fragment.CardReaderFragment;
import com.newland.nsdkdemo.internal.fragment.CashBoxFragment;
import com.newland.nsdkdemo.internal.fragment.ContactCardFragment;
import com.newland.nsdkdemo.internal.fragment.ContactlessCardFragment;
import com.newland.nsdkdemo.internal.fragment.CryptoFragment;
import com.newland.nsdkdemo.internal.fragment.DeviceManagerFragment;
import com.newland.nsdkdemo.internal.fragment.ECDHEFragment;
import com.newland.nsdkdemo.internal.fragment.EthernetFragment;
import com.newland.nsdkdemo.internal.fragment.KeyManagerFragment;
import com.newland.nsdkdemo.internal.fragment.LEDFragment;
import com.newland.nsdkdemo.internal.fragment.PINEntry2Fragment;
import com.newland.nsdkdemo.internal.fragment.PINEntryFragment;
import com.newland.nsdkdemo.internal.fragment.PrinterFragment;
import com.newland.nsdkdemo.internal.fragment.RKIFragment;
import com.newland.nsdkdemo.internal.fragment.RouteManagerFragment;
import com.newland.nsdkdemo.internal.fragment.SettingsManagerFragment;
import com.newland.nsdkdemo.internal.fragment.SerialPortFragment;

import java.util.ArrayList;

public class Initiator {
    private NSDKModuleManager moduleManager;
    private Context context;
    public static Initiator getInstance() {
        return InitiatorHolder.singletonHolder;
    }

    public void init(Context context){
        this.context = context;
        this.moduleManager = NSDKModuleManagerImpl.getInstance();
        moduleManager.setDebugMode(LogLevel.VERBOSE);

        try {
            moduleManager.setNativeDebugMode(NativeDebugLevel.ALL_ON);

            moduleManager.init(context);
        } catch (NSDKException e) {
            e.printStackTrace();
            if (e.getCode() == ErrorCode.NEED_UPDATE) {
                ((MainActivity)context).showMessage(context.getString(R.string.msg_internal_update_error) + e.getMessage(), MessageTag.ERROR);
            } else {
                ((MainActivity)context).showMessage(String.format("%s(%s)", context.getString(R.string.msg_internal_init_error), e.getMessage()), MessageTag.ERROR);
            }
        }
    }

    public void addFragments(ArrayList<ModuleInfo> moduleFragments) {
        moduleFragments.add(new ModuleInfo(new BarcodeScannerFragment(this.context), R.string.tv_scanner_f, R.drawable.scanner));
        moduleFragments.add(new ModuleInfo(new CardReaderFragment(this.context), R.string.tv_cardreader_f, R.drawable.cardreader));
        moduleFragments.add(new ModuleInfo(new ContactCardFragment(this.context), R.string.tv_iccard_f, R.drawable.iccard));
        moduleFragments.add(new ModuleInfo(new ContactlessCardFragment(this.context), R.string.tv_rfcard_f, R.drawable.rfcard));
        moduleFragments.add(new ModuleInfo(new KeyManagerFragment(this.context), R.string.tv_keyManager_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new PINEntryFragment(this.context), R.string.tv_pinInput_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new PINEntry2Fragment(this.context), R.string.tv_pinInput2_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new CryptoFragment(this.context), R.string.tv_cipher_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new PrinterFragment(this.context), R.string.tv_printer_f, R.drawable.printer));
        moduleFragments.add(new ModuleInfo(new BeeperFragment(this.context), R.string.tv_buzzer_f, R.drawable.buzzer));
        moduleFragments.add(new ModuleInfo(new LEDFragment(this.context), R.string.tv_light_f, R.drawable.light));
        moduleFragments.add(new ModuleInfo(new DeviceManagerFragment(this.context), R.string.tv_terminalmanage_f, R.drawable.ternaml));
        moduleFragments.add(new ModuleInfo(new SettingsManagerFragment(this.context), R.string.tv_settings_f, R.drawable.cardreader));
        moduleFragments.add(new ModuleInfo(new RKIFragment(this.context), R.string.tv_rki_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new CashBoxFragment(this.context), R.string.tv_cashbox_f, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new SerialPortFragment(this.context), R.string.tv_uart3port_f, R.drawable.serialport));
        moduleFragments.add(new ModuleInfo(new EthernetFragment(this.context), R.string.tv_ethernet, R.drawable.ethernet));
        moduleFragments.add(new ModuleInfo(new RouteManagerFragment(this.context), R.string.tv_route_manager, R.drawable.serialport));
        moduleFragments.add(new ModuleInfo(new ECDHEFragment(this.context), R.string.ecdhe, R.drawable.pin));
        moduleFragments.add(new ModuleInfo(new GuestDisplayManagerFragment(this.context), R.string.tv_guest_display_manager, R.drawable.pin));
    }

    public void release() {
        moduleManager.destroy();
    }

    private static class InitiatorHolder {
        private static Initiator singletonHolder = new Initiator();
    }
}
