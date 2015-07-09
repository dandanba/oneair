package com.thingstec.ble;
import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
	private static HashMap<String, String> attributes = new HashMap();
	public static String SANYUKI_CAR_MEASUREMENT = "0000ffe4-0000-1000-8000-00805f9b34fb";
	static {
		attributes.put(SANYUKI_CAR_MEASUREMENT, "SANYUKI CAR MEASUREMENT");
	}

	public static String lookup(String uuid, String defaultName) {
		String name = attributes.get(uuid);
		return name == null ? defaultName : name;
	}
}
