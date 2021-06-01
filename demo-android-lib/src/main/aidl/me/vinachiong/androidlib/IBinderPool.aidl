// IBinderPool.aidl
package me.vinachiong.androidlib;

// Declare any non-default types here with import statements

interface IBinderPool {
    /**
     * 服务提供端维护一个 IBinder池，用于查找目标binder
     */
    IBinder queryBinder(int binderCode);
}