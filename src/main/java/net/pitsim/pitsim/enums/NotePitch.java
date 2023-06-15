package net.pitsim.pitsim.enums;

public enum NotePitch {
	PITCH_0(0.500F),
	PITCH_1(0.530F),
	PITCH_2(0.561F),
	PITCH_3(0.595F),
	PITCH_4(0.630F),
	PITCH_5(0.667F),
	PITCH_6(0.707F),
	PITCH_7(0.749F),
	PITCH_8(0.794F),
	PITCH_9(0.841F),
	PITCH_10(0.891F),
	PITCH_11(0.944F),
	PITCH_12(1.000F),
	PITCH_13(1.059F),
	PITCH_14(1.122F),
	PITCH_15(1.189F),
	PITCH_16(1.260F),
	PITCH_17(1.335F),
	PITCH_18(1.414F),
	PITCH_19(1.498F),
	PITCH_20(1.587F),
	PITCH_21(1.682F),
	PITCH_22(1.782F),
	PITCH_23(1.888F),
	PITCH_24(2.000F);

	NotePitch(float pitch) {
		this.pitch = pitch;
	}

	private final float pitch;

	public float getPitch() {
		return pitch;
	}
}
