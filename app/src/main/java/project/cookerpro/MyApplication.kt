package project.cookerpro

import android.app.Application
import project.cookerpro.data.DataSource

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DataSource.initialize(applicationContext)
    }
}