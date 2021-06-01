package me.vinachiong.androidlib.proxy


import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

/**
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Layout(@LayoutRes val layoutResId: Int)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class BindView(@IdRes val viewId: Int)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnClick(@IdRes val viewId: Int)