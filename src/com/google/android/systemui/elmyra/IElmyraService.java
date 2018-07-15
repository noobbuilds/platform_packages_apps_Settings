package com.google.android.systemui.elmyra;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IElmyraService extends IInterface {

  void launchAssistant() throws RemoteException;

  void registerListener(IBinder iBinder, IBinder iBinder2) throws RemoteException;

  void registerSettingsListener(IBinder iBinder, IBinder iBinder2) throws RemoteException;

  abstract class Stub extends Binder implements IElmyraService {

    public Stub() {
      attachInterface(this, "com.google.android.systemui.elmyra.IElmyraService");
    }

    public static IElmyraService asInterface(IBinder iBinder) {
      if (iBinder == null) {
        return null;
      }
      IInterface queryLocalInterface =
          iBinder.queryLocalInterface("com.google.android.systemui.elmyra.IElmyraService");
      return (!(queryLocalInterface instanceof IElmyraService))
          ? new Proxy(iBinder)
          : (IElmyraService) queryLocalInterface;
    }

    public IBinder asBinder() {
      return this;
    }

      // Surge: set case 1 to FLAG_ONEWAY since its (int, Parcel, Parcel, int
    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
      switch (i) {
        case FLAG_ONEWAY:
          parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraService");
          registerSettingsListener(parcel.readStrongBinder(), parcel.readStrongBinder());
          return true;
        case 2:
          parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraService");
          launchAssistant();
          return true;
        case 3:
          parcel.enforceInterface("com.google.android.systemui.elmyra.IElmyraService");
          registerListener(parcel.readStrongBinder(), parcel.readStrongBinder());
          return true;
        case INTERFACE_TRANSACTION:
          parcel2.writeString("com.google.android.systemui.elmyra.IElmyraService");
          return true;
        default:
          return super.onTransact(i, parcel, parcel2, i2);
      }
    }

      private static class Proxy implements IElmyraService
      {
          private IBinder mRemote;

          Proxy(final IBinder mRemote) {
              this.mRemote = mRemote;
          }

          public IBinder asBinder() {
              return this.mRemote;
          }

          public void launchAssistant() throws RemoteException {
              Parcel obtain = Parcel.obtain();
              try {
                  obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                  this.mRemote.transact(2, obtain, null, 1);
              } finally {
                  obtain.recycle();
              }
          }

          public void registerListener(IBinder iBinder, IBinder iBinder2) throws RemoteException {
              Parcel obtain = Parcel.obtain();
              try {
                  obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                  obtain.writeStrongBinder(iBinder);
                  obtain.writeStrongBinder(iBinder2);
                  this.mRemote.transact(3, obtain, null, 1);
              } finally {
                  obtain.recycle();
              }
          }

          public void registerSettingsListener(IBinder iBinder, IBinder iBinder2)
                  throws RemoteException {
              Parcel obtain = Parcel.obtain();
              try {
                  obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                  obtain.writeStrongBinder(iBinder);
                  obtain.writeStrongBinder(iBinder2);
                  this.mRemote.transact(1, obtain, null, 1);
              } finally {
                  obtain.recycle();
              }
          }
          /*

          @Override
          public void registerGestureListener(final IBinder binder, final IBinder binder2) throws RemoteException {
              final Parcel obtain = Parcel.obtain();
              try {
                  obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                  obtain.writeStrongBinder(binder);
                  obtain.writeStrongBinder(binder2);
                  this.mRemote.transact(1, obtain, (Parcel)null, 1);
              }
              finally {
                  obtain.recycle();
              }
          }

          @Override
          public void registerServiceListener(final IBinder binder, final IBinder binder2) throws RemoteException {
              final Parcel obtain = Parcel.obtain();
              try {
                  obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                  obtain.writeStrongBinder(binder);
                  obtain.writeStrongBinder(binder2);
                  this.mRemote.transact(3, obtain, (Parcel)null, 1);
              }
              finally {
                  obtain.recycle();
              }
          }

          @Override
          public void triggerAction() throws RemoteException {
              final Parcel obtain = Parcel.obtain();
              try {
                  obtain.writeInterfaceToken("com.google.android.systemui.elmyra.IElmyraService");
                  this.mRemote.transact(2, obtain, (Parcel)null, 1);
              }
              finally {
                  obtain.recycle();
              }
          }
          */
      }
  }
}
