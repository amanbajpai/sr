package com.ros.smartrocket.utils.image;

/**
 * Helper class for masked request code.
 * <p>
 * Makes request code in such scheme
 * <p>
 * 12bits - big part
 * 4bits - little part
 * <p>
 * 12 + 4
 */
public final class RequestCodeImageHelper {
    public static final int SHIFT = 4;
    public static final int MASK = 0xFFF;
    public static final int LITTLE_MASK = 0x0000000F;

    private RequestCodeImageHelper() {
    }

    public static int makeRequestCode(int bigPartCode, int littlePartCode) {
        return ((bigPartCode & MASK) << SHIFT) | littlePartCode;
    }

    public static int getMaskedBigPart(int bigPartCode) {
        return bigPartCode & MASK;
    }

    public static int getLittlePart(int requestCode) {
        return requestCode & LITTLE_MASK;
    }

    public static int getBigPart(int requestCode) {
        return (requestCode >> SHIFT) & MASK;
    }
}
