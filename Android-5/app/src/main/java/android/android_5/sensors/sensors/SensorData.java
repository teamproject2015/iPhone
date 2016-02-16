package android.android_5.sensors.sensors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class SensorData {
    private long timestamp;
    private float x;
    private float y;
    private float z;
}
