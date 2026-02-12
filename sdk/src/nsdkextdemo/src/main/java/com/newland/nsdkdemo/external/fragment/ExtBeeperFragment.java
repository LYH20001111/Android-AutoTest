package com.newland.nsdkdemo.external.fragment;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.beeper.ExtBeeper;
import com.newland.nsdk.core.api.external.devicemanager.BeeperTone;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.fragment.BaseFragment;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

public class ExtBeeperFragment extends ExtBaseFragment {

    private ExtBeeper mBeeper;
    private static final int INDEX_BEEP = 1;
    private static final int INDEX_BEEP_FREQUENCY_DURATION = 2;

    public ExtBeeperFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extbeeper_f);
    }

    @Override
    public void initData() {
        mBeeper = (ExtBeeper) moduleManager.getModule(ModuleType.EXT_BEEPER);
    }

    @Override
    public Object getModule() {
        return ExtBeeperFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_extbeeper_beep, functionid = INDEX_BEEP)
    private void beep() {
        try {
            mBeeper.beep(BeeperTone.DEFAULT, 500);
            showMessage(context.getString(R.string.msg_extdevicebasic_beepsuccess), MessageTag.NORMAL);
        } catch (NSDKException e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_extdevicebasic_beepfail), MessageTag.NORMAL);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_extbeeper_beep, functionid = INDEX_BEEP_FREQUENCY_DURATION)
    private void beepWithFrequencyAndDuration() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.tv_extbeeper_beep), null, R.layout.dialog_ext_beeper2, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                EditText editFrequency = view.findViewById(R.id.edit_ext_beeper_frequency);
                int frequency = Integer.parseInt(editFrequency.getText().toString());
                EditText editDuration = view.findViewById(R.id.edit_ext_beeper_duration);
                int duration = Integer.parseInt(editDuration.getText().toString());

                try {
                    mBeeper.beep(frequency, duration);
                } catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.tv_extbeeper_beep));
                }
            }
        });
    }
}
