/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package me.vinachiong.androidlib.webview.aidl;
// Declare any non-default types here with import statements

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 参考AIDL的Binder IPC模板类
 * 手撸一遍
 * 定义通信接口类
 */
public interface IMyBinder extends android.os.IInterface {

    /**
     * 接口的默认空实现
     */
    public static class Default implements IMyBinder {

        @Override
        public String sayHello(int age, String name) throws RemoteException {
            return null;
        }

        @Override
        public IBinder asBinder() {
            return null;
        }
    }

    /**
     * Stub抽象类，Binder机制中，服务端要继承并实现的类
     */
    public static abstract class Stub extends Binder implements IMyBinder {

        static final int TRANSACTION_sayHello = IBinder.FIRST_CALL_TRANSACTION + 0;

        private static final String DESCRIPTOR = "me.vinachiong.lib.webview.aidl.IMyBinder";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                // 默认请求code, 询问当前Binder对象的描述文本
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_sayHello: {
                    data.enforceInterface(descriptor);
                    int age = data.readInt();
                    String name = data.readString();
                    String result = this.sayHello(age, name);
                    reply.writeNoException();
                    reply.writeString(result);
                    return true;
                }
                default:
                    return super.onTransact(code,data,reply,flags);
            }
        }

        /**
         * 静态内部类
         * @param obj 传入服务端的IBinder实例
         * @return 类型转换为IMyBinder接口的实现
         */
        public static IMyBinder asInterface(IBinder obj) {
            if (null == obj) return null;

            // 尝试获取本地实现的IInterface代理对象
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if ((iin != null) && iin instanceof IMyBinder) {
                return (IMyBinder) iin;
            }
            return new Proxy(obj);
        }

        public static boolean setDefaultImpl(IMyBinder impl) {
            if (null != Proxy.sDefaultImpl) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IMyBinder getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }

        /**
         * 给客户端使用的接口实现
         * 内部代理了对 远端Binder对象的参数封装、调用、结果返回
         */
        private static class Proxy implements IMyBinder {
            public static IMyBinder sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public String sayHello(int age, String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(age);
                    _data.writeString(name);

                    boolean _status = mRemote.transact(Stub.TRANSACTION_sayHello, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().sayHello(age, name);
                    }
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }
    }

    /**
     * 通信方法：sayHello
     * @param age 调用方的年龄
     * @param name 调用方的名称
     * @return 返回服务端向客户端的问候内容
     * @throws android.os.RemoteException 远程调用异常
     */
    public String sayHello(int age, String name) throws RemoteException;

}
