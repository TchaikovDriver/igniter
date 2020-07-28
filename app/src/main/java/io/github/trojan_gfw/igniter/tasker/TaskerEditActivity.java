package io.github.trojan_gfw.igniter.tasker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractFragmentPluginActivity;

import io.github.trojan_gfw.igniter.R;

public class TaskerEditActivity extends AbstractFragmentPluginActivity {
    private boolean mIsEditCondition;
    private Switch mStartProxyActionSwitch;
    private RadioButton mExpectProxyStartRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasker_edit);

        mStartProxyActionSwitch = findViewById(R.id.tasker_edit_start_proxy_switch);
        mIsEditCondition = isEditCondition();
        mExpectProxyStartRb = findViewById(R.id.tasker_edit_condition_expect_proxy_start_rb);
        if (mIsEditCondition) {
            mStartProxyActionSwitch.setVisibility(View.GONE);
        } else {
            findViewById(R.id.tasker_edit_condition_rg).setVisibility(View.GONE);
        }
    }

    private boolean isEditCondition() {
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }
        return com.twofortyfouram.locale.api.Intent.ACTION_EDIT_CONDITION.equals(intent.getAction());
    }

    @Override
    public boolean isBundleValid(@NonNull Bundle bundle) {
        return true;
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull Bundle bundle, @NonNull String s) {
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        Bundle bundle = new Bundle();
        if (mIsEditCondition) {
            bundle.putString(TaskerConstants.CONDITION_PROXY_STATUS, mExpectProxyStartRb.isChecked() ?
                    TaskerConstants.STATUS_STARTED : TaskerConstants.STATUS_STOPPED);
        } else {
            bundle.putBoolean(TaskerConstants.KEY_START_PROXY, mStartProxyActionSwitch.isChecked());
        }
        return bundle;
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull Bundle bundle) {
        if (mIsEditCondition) {
            return String.format("Expect proxy %s", mExpectProxyStartRb.isChecked() ? "started" : "stopped");
        } else {
            boolean startProxy = bundle.getBoolean(TaskerConstants.KEY_START_PROXY);
            return String.format("%s Igniter", startProxy ? "Start" : "Stop");
        }
    }
}