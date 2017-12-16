package com.naughtypiggy.android.stock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by yangyu on 16/12/17.
 */

public class NewSymbolAddReceiver extends BroadcastReceiver{
    private ReceiveInterface mReceiver;

    public interface ReceiveInterface {
        void receiveIntent(Intent intent);
    }

    public NewSymbolAddReceiver(ReceiveInterface receiveInterface) {
        this.mReceiver = receiveInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mReceiver.receiveIntent(intent);
    }
}
