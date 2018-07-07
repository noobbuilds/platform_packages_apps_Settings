package com.google.android.systemui.elmyra;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IElmyraServiceSettingsListener extends IInterface {

    public static abstract class Stub extends Binder implements IElmyraServiceSettingsListener {
        public Stub() {
            attachInterface(this, "com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
                    onGestureProgress(parcel.readFloat(), parcel.readInt());
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
                    onGestureDetected();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.systemui.elmyra.IElmyraServiceSettingsListener");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void onGestureDetected() throws RemoteException;

    void onGestureProgress(float f, int i) throws RemoteException;
}

