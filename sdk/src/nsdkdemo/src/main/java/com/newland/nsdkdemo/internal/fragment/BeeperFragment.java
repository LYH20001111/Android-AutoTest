package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.beeper.Beeper;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;

public class BeeperFragment extends InternalBaseFragment {

    private Beeper beeper;

    public BeeperFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_buzzer_f);
    }

    @Override
    public void initData() {
        beeper = (Beeper) moduleManager.getModule(ModuleType.BEEPER);
    }

    @Override
    public Object getModule() {
        return BeeperFragment.this;
    }

    private static final int INDEX_START = 1;
    private static final int SET_VOLUME = 2;

    @MethodGridEntity(btnnameid = R.string.buzzer_play, functionid = INDEX_START)
    private void beep() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.dialog_tv_beeper_title), null, R.layout.dialog_beep_params_input, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                EditText frequency = dialogView.findViewById(R.id.edit_beeper_frequency);
                EditText duration = dialogView.findViewById(R.id.edit_beeper_duration);
                if (frequency.getText().toString().isEmpty() || duration.getText().toString().isEmpty()) {
                    showMessage("Beeper duration and frequency shall be >0.");
                } else {
                    int beeperFrequency = Integer.parseInt(frequency.getText().toString());
                    int beeperDuration = Integer.parseInt(duration.getText().toString());

                    try {
                        beeper.beep(beeperFrequency, beeperDuration);
                    } catch (NSDKException e) {
                        e.printStackTrace();
                        showErrorMessage(e, "beep");
                    }
                }


            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.buzzer_set_volume, functionid = SET_VOLUME)
    private void setVolume() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.buzzer_set_volume), null, R.layout.dialog_set_volume, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                EditText editVolume = view.findViewById(R.id.edit_beeper_volume);
                int volume = editVolume.getText().toString().isEmpty() ? 5 : Integer.parseInt(editVolume.getText().toString());
                try {
                    beeper.setVolume(volume);
                    showMessage(context.getString(R.string.buzzer_set_volume));
                } catch (NSDKException e) {
                    showErrorMessage(e, e.getMessage());
                }
            }
        });
    }
}
