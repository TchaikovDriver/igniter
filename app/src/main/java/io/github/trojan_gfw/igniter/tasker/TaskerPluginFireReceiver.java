package io.github.trojan_gfw.igniter.tasker;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginSettingReceiver;

import io.github.trojan_gfw.igniter.tile.ProxyHelper;

public class TaskerPluginFireReceiver extends AbstractPluginSettingReceiver {

    public TaskerPluginFireReceiver() {
        super();
    }

    @Override
    protected boolean isBundleValid(@NonNull Bundle bundle) {
        return bundle.containsKey(TaskerConstants.KEY_START_PROXY);
    }

    @Override
    protected boolean isAsync() {
        return false;
    }

    @Override
    protected void firePluginSetting(@NonNull Context context, @NonNull Bundle bundle) {
        boolean startProxy = bundle.getBoolean(TaskerConstants.KEY_START_PROXY);
        if (startProxy) {
            ProxyHelper.startProxyService(context.getApplicationContext());
        } else {
            ProxyHelper.stopProxyService(context.getApplicationContext());
        }
    }
}
