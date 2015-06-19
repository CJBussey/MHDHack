package de.fraunhofer.idmt.pitch_detect;


public class PitchDetect extends Object
{
	static {
		System.loadLibrary("pitch_detection_idmt");
	}
	
	public static final int MONOPHONIC_DETECT=0;
	public static final int POLYPHONIC_DETECT=1;
	
	public static native void createPitchDetect(int sampleRate);
	
	public static native void deletePitchDetect();
	
	public static native void setPitchDetectVersion(int version);
	
	public static native void setReferenceFrequencies(float[] frequencies);
	
	public static native float[] processSampleBuff(short[] buffer);
}