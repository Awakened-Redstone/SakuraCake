package com.awakenedredstone.sakuracake.util;

import net.minecraft.util.math.Vec3d;

public class CherryMath {
    private static final double c_log2_1023 = logN(1023.0, 2);

    public static double wrap(double value, double min, double max) {
        if (value > max) return (value - 1) % max;
        else if (value < min) return max - (min - value - 1);
        else return value;
    }

    public static long wrap(long value, long min, long max) {
        if (value > max) return (value - 1) % max;
        else if (value < min) return max - (min - value - 1);
        else return value;
    }

    public static int wrap(int value, int min, int max) {
        if (value > max) return (value - 1) % max;
        else if (value < min) return max - (min - value - 1);
        else return value;
    }

    /**
     * With the min of 0 and max of 15,
     * if from is 0 and to is 15 the result must be -1,
     * the distance is of 1 to the left
     * <p>
     * Objective 1, find the shortest distance between min and max,
     * negative to the left, positive to the right
     * <p>
     * from and to must always be within min and max
     */
    public static int unwrap(int from, int to, int min, int max) {
        int range = max - min ;
        int distance = to - from;

        if (Math.abs(distance) > range / 2) {
            if (distance > 0) {
                distance -= range;
            } else {
                distance += range;
            }
        }

        return distance;
    }

    public static double easeOutExpo(double x) {
        if (x == 0) return 0;
        if (x == 1) return 1;
        return 1 - Math.pow(2, 10.0 - c_log2_1023 - 10.0 * x) + 1 / 1023.;
    }

    public static double logN(double N, double base) {
        return (Math.log(N) / Math.log(2));
    }
}
