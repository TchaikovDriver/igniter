package io.github.trojan_gfw.igniter.tasker;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.twofortyfouram.locale.api.Intent;
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginConditionReceiver;

import io.github.trojan_gfw.igniter.LogHelper;
import io.github.trojan_gfw.igniter.ProxyService;
import io.github.trojan_gfw.igniter.connection.TrojanConnection;
import io.github.trojan_gfw.igniter.proxy.aidl.ITrojanService;

public class QueryReceiver extends AbstractPluginConditionReceiver implements TrojanConnection.Callback {
    private static final String TAG = "QueryReceiver";
    private final Object mLock = new Object();
    private volatile boolean mProxyStatusAcquired;
    private TrojanConnection mTrojanConnection;
    private volatile ITrojanService mTrojanService;

    public QueryReceiver() {
        super();
    }

    @Override
    protected boolean isBundleValid(@NonNull Bundle bundle) {
        return bundle.containsKey(TaskerConstants.CONDITION_PROXY_STATUS);
    }

    @Override
    protected boolean isAsync() {
        return true;
    }

    @Override
    public void onServiceConnected(ITrojanService service) {
        mTrojanService = service;
        synchronized (mLock) {
            mProxyStatusAcquired = true;
            mLock.notify();
        }
    }

    @Override
    public void onServiceDisconnected() {
        mTrojanService = null;
    }

    @Override
    public void onStateChanged(int state, String msg) {
    }

    @Override
    public void onTestResult(String testUrl, boolean connected, long delay, @NonNull String error) {
    }

    @Override
    public void onBinderDied() {
    }

    private void asyncConnectProxyService(Context context) {
        new Thread(()-> mTrojanConnection.connect(context, QueryReceiver.this)).start();
    }

    @Override
    protected int getPluginConditionResult(@NonNull Context context, @NonNull Bundle bundle) {
        /*int messageID = bundle.getInt(TaskerPlugin.Event.PASS_THROUGH_BUNDLE_MESSAGE_ID_KEY);
        if (-1 == messageID) {
            setResultCode(Intent.RESULT_CONDITION_UNKNOWN);
            return Intent.RESULT_CONDITION_UNKNOWN;
        }*/
        mProxyStatusAcquired = false;
        mTrojanConnection = new TrojanConnection(false);
        LogHelper.i(TAG, "connecting ProxyService");
        asyncConnectProxyService(context.getApplicationContext());
        while (!mProxyStatusAcquired) {
            synchronized (mLock) {
                try {
                    LogHelper.i(TAG, "waiting connection success");
                    mLock.wait(8000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        LogHelper.i(TAG, "Finish waiting");
        int state = ProxyService.STATE_NONE;
        if (mProxyStatusAcquired && mTrojanService != null) {
            try {
                state = mTrojanService.getState();
                LogHelper.i(TAG, "State is " + state);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            return Intent.RESULT_CONDITION_UNKNOWN;
        }
        mTrojanConnection.disconnect(context.getApplicationContext());
        boolean proxyStarted = state == ProxyService.STARTED;
        String expectedCondition = bundle.getString(TaskerConstants.CONDITION_PROXY_STATUS);
        boolean expectedStarted = TaskerConstants.STATUS_STARTED.equals(expectedCondition);
        if (expectedStarted) {
            return proxyStarted ? Intent.RESULT_CONDITION_SATISFIED : Intent.RESULT_CONDITION_UNSATISFIED;
        } else {
            return proxyStarted ? Intent.RESULT_CONDITION_UNSATISFIED : Intent.RESULT_CONDITION_SATISFIED;
        }
    }
    private static final android.content.Intent INTENT_REQUEST_REQUERY = new android.content.Intent(
            com.twofortyfouram.locale.api.Intent.ACTION_REQUEST_QUERY).putExtra(
            com.twofortyfouram.locale.api.Intent.EXTRA_STRING_ACTIVITY_CLASS_NAME,
            TaskerEditActivity.class.getName());
}
