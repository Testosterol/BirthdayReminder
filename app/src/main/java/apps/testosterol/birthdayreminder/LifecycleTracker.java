package apps.testosterol.birthdayreminder;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

public class LifecycleTracker implements LifecycleObserver {

    private static final String TAG = LifecycleTracker.class.getSimpleName();
    private static final LifecycleTracker instance = new LifecycleTracker();

    static LifecycleTracker getInstance(){ return instance;}

    private LifecycleTracker(){
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void libOnInit(){
        Log.d(TAG, "App Create");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void libOnDestroy(){
        Log.d(TAG, "App Destroy");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void libOnStart(){
        Log.d(TAG, "App Start");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void libOnPause(){
        Log.d(TAG, "App Pause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void libOnResume(){
        Log.d(TAG, "App Resume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void libOnStop(){
        Log.d(TAG, "App Stop");
    }



}
