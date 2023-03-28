package pt.pl.estg.ei.regulapp

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest constructor() {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext: Context? = InstrumentationRegistry.getInstrumentation().getTargetContext()
        Assert.assertEquals("pt.pl.estg.ei.myapplication", appContext.getPackageName())
    }
}