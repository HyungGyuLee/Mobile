package net.giosis.common

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle


class ApplicationActivityLifecycle: ActivityLifecycleCallbacks {
    var currentTopActivity: Activity? = null
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false

    private var foregroundAction: (() -> Unit)? = null
    private var backgroundAction: (() -> Unit)? = null

    fun applicationStateBackground(action: () -> Unit) {
        backgroundAction = action
    }

    fun applicationStateForeground(action: () -> Unit) {
        foregroundAction = action
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentTopActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentTopActivity = activity
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // enter foreground
            foregroundAction?.let { it() }
        }
    }

    override fun onActivityResumed(activity: Activity) {
        currentTopActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        currentTopActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations;
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // enter background
            backgroundAction?.let { it() }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}