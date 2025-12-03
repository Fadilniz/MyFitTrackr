package week11.st078050.finalproject.viewmodel

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class StepCounterViewModel(application: Application)
    : AndroidViewModel(application), SensorEventListener {

    private val sensorManager =
        application.getSystemService(Application.SENSOR_SERVICE) as SensorManager

    private val stepSensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _steps = MutableLiveData(0f)
    val steps: LiveData<Float> get() = _steps

    private var initialReading = -1f

    fun start() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val total = event.values[0]

            if (initialReading < 0) {
                // SAVE offset ONCE only
                initialReading = total
            }

            _steps.value = total - initialReading
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
