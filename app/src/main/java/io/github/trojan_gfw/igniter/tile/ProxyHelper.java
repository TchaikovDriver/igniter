package io.github.trojan_gfw.igniter.tile;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.support.v4.content.ContextCompat;

import io.github.trojan_gfw.igniter.BuildConfig;
import io.github.trojan_gfw.igniter.Globals;
import io.github.trojan_gfw.igniter.MainActivity;
import io.github.trojan_gfw.igniter.ProxyService;
import io.github.trojan_gfw.igniter.R;
import io.github.trojan_gfw.igniter.TrojanConfig;
import io.github.trojan_gfw.igniter.TrojanHelper;
import io.github.trojan_gfw.igniter.common.os.MultiProcessSP;

public abstract class ProxyHelper {
    public static void startProxyService(Context context) {
        TrojanConfig cacheConfig = TrojanHelper.readTrojanConfig(Globals.getTrojanConfigPath());
        if (cacheConfig == null) {
            startLauncherActivity(context);
            return;
        }
        cacheConfig.setCaCertPath(Globals.getCaCertPath());
        if (BuildConfig.DEBUG) {
            TrojanHelper.ShowConfig(Globals.getTrojanConfigPath());
        }
        if (!cacheConfig.isValidRunningConfig()) {
            startLauncherActivity(context);
            return;
        }
        Intent i = VpnService.prepare(context.getApplicationContext());
        if (i != null) {
            startLauncherActivity(context);
        } else {
            Intent intent = new Intent(context, ProxyService.class);
            intent.putExtra(ProxyService.CLASH_EXTRA_NAME, MultiProcessSP.getEnableClash(true));
            ContextCompat.startForegroundService(context, intent);
        }
    }

    private static void startLauncherActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void stopProxyService(Context context) {
        Intent intent = new Intent(context.getString(R.string.stop_service));
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
