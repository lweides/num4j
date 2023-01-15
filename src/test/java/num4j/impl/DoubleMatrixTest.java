package num4j.impl;

import static org.junit.jupiter.api.Assertions.*;

import num4j.api.Matrix;
import num4j.exceptions.IncompatibleDimensionsException;
import num4j.unsafe.TheUnsafe;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;

class DoubleMatrixTest {

    @Test
    void shouldCreateMatrixFilledWithZeros() {
        DoubleMatrix noDimensions = DoubleMatrix.zeros();
        assertArrayEquals(new byte[0], noDimensions.data());

        DoubleMatrix oneDimension = DoubleMatrix.zeros(1);
        assertArrayEquals(new byte[8], oneDimension.data());

        DoubleMatrix manyDimensions = DoubleMatrix.zeros(1, 2, 3, 4, 5);
        assertArrayEquals(new byte[(1 * 2 * 3 * 4 * 5) * 8], manyDimensions.data());
    }

    @Test
    void shouldCreateMatrixFilledWithOnes() {
        DoubleMatrix noDimensions = DoubleMatrix.ones();
        assertArrayEquals(new byte[0], noDimensions.data());

        DoubleMatrix oneDimension = DoubleMatrix.ones(1);
        byte[] expectedOne = new byte[8];
        TheUnsafe.write(expectedOne, 0, 1.0);
        assertArrayEquals(expectedOne, oneDimension.data());

        DoubleMatrix manyDimensions = DoubleMatrix.ones(1, 2, 3, 4, 5);
        byte[] expectedMany = new byte[(1 * 2 * 3 * 4 * 5) * 8];
        for (int i = 0; i < manyDimensions.size(); i++) {
            TheUnsafe.write(expectedMany, i, 1.0);
        }
        assertArrayEquals(expectedMany, manyDimensions.data());
    }

    @Test
    void addingZerosToOnesShouldResultInOnes() {
        DoubleMatrix ones = DoubleMatrix.ones(2);
        DoubleMatrix zeros = DoubleMatrix.zeros(2);
        ones.add(zeros);

        byte[] expected = new byte[16];
        TheUnsafe.write(expected, 0, 1.0);
        TheUnsafe.write(expected, 1, 1.0);

        assertArrayEquals(expected, ones.data());
    }

    @Test
    void addingOnesToZerosShouldResultInOnes() {
        DoubleMatrix zeros = DoubleMatrix.zeros(2);
        DoubleMatrix ones = DoubleMatrix.ones(2);
        zeros.add(ones);

        byte[] expected = new byte[16];
        TheUnsafe.write(expected, 0, 1.0);
        TheUnsafe.write(expected, 1, 1.0);

        assertArrayEquals(expected, zeros.data());
    }

    @Test
    void addingOnesToOnesShouldResultInTwos() {
        DoubleMatrix a = DoubleMatrix.ones(2);
        DoubleMatrix b = DoubleMatrix.ones(2);
        a.add(b);

        byte[] expected = new byte[16];
        TheUnsafe.write(expected, 0, 2.0);
        TheUnsafe.write(expected, 1, 2.0);

        assertArrayEquals(expected, a.data());
    }

    @Test
    void addingInCompatibleDimensionsShouldFail() {
        DoubleMatrix oneTimesTwo = DoubleMatrix.zeros(1, 2);
        DoubleMatrix twoTimesOne = DoubleMatrix.zeros(2, 1);
        assertThrows(
            IncompatibleDimensionsException.class,
            () -> oneTimesTwo.add(twoTimesOne)
        );
    }

    @Test
    void transpose2D() {
        double[] large2DMatrix = IntStream.rangeClosed(1, 4171).mapToDouble(i -> i).toArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(large2DMatrix.length * 8);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        doubleBuffer.put(large2DMatrix);

        DoubleMatrix matrix = new DoubleMatrix(byteBuffer.array(), 97, 43);
        Matrix<Double> transposed = matrix.transpose(1,0);
        double[] expected = new double[] {
                1, 44, 87, 130, 173, 216, 259, 302, 345, 388, 431, 474, 517, 560, 603, 646, 689, 732, 775, 818, 861, 904, 947, 990, 1033, 1076, 1119, 1162, 1205, 1248, 1291, 1334, 1377, 1420, 1463, 1506, 1549, 1592, 1635, 1678, 1721, 1764, 1807, 1850, 1893, 1936, 1979, 2022, 2065, 2108, 2151, 2194, 2237, 2280, 2323, 2366, 2409, 2452, 2495, 2538, 2581, 2624, 2667, 2710, 2753, 2796, 2839, 2882, 2925, 2968, 3011, 3054, 3097, 3140, 3183, 3226, 3269, 3312, 3355, 3398, 3441, 3484, 3527, 3570, 3613, 3656, 3699, 3742, 3785, 3828, 3871, 3914, 3957, 4000, 4043, 4086, 4129,
                2, 45, 88, 131, 174, 217, 260, 303, 346, 389, 432, 475, 518, 561, 604, 647, 690, 733, 776, 819, 862, 905, 948, 991, 1034, 1077, 1120, 1163, 1206, 1249, 1292, 1335, 1378, 1421, 1464, 1507, 1550, 1593, 1636, 1679, 1722, 1765, 1808, 1851, 1894, 1937, 1980, 2023, 2066, 2109, 2152, 2195, 2238, 2281, 2324, 2367, 2410, 2453, 2496, 2539, 2582, 2625, 2668, 2711, 2754, 2797, 2840, 2883, 2926, 2969, 3012, 3055, 3098, 3141, 3184, 3227, 3270, 3313, 3356, 3399, 3442, 3485, 3528, 3571, 3614, 3657, 3700, 3743, 3786, 3829, 3872, 3915, 3958, 4001, 4044, 4087, 4130,
                3, 46, 89, 132, 175, 218, 261, 304, 347, 390, 433, 476, 519, 562, 605, 648, 691, 734, 777, 820, 863, 906, 949, 992, 1035, 1078, 1121, 1164, 1207, 1250, 1293, 1336, 1379, 1422, 1465, 1508, 1551, 1594, 1637, 1680, 1723, 1766, 1809, 1852, 1895, 1938, 1981, 2024, 2067, 2110, 2153, 2196, 2239, 2282, 2325, 2368, 2411, 2454, 2497, 2540, 2583, 2626, 2669, 2712, 2755, 2798, 2841, 2884, 2927, 2970, 3013, 3056, 3099, 3142, 3185, 3228, 3271, 3314, 3357, 3400, 3443, 3486, 3529, 3572, 3615, 3658, 3701, 3744, 3787, 3830, 3873, 3916, 3959, 4002, 4045, 4088, 4131,
                4, 47, 90, 133, 176, 219, 262, 305, 348, 391, 434, 477, 520, 563, 606, 649, 692, 735, 778, 821, 864, 907, 950, 993, 1036, 1079, 1122, 1165, 1208, 1251, 1294, 1337, 1380, 1423, 1466, 1509, 1552, 1595, 1638, 1681, 1724, 1767, 1810, 1853, 1896, 1939, 1982, 2025, 2068, 2111, 2154, 2197, 2240, 2283, 2326, 2369, 2412, 2455, 2498, 2541, 2584, 2627, 2670, 2713, 2756, 2799, 2842, 2885, 2928, 2971, 3014, 3057, 3100, 3143, 3186, 3229, 3272, 3315, 3358, 3401, 3444, 3487, 3530, 3573, 3616, 3659, 3702, 3745, 3788, 3831, 3874, 3917, 3960, 4003, 4046, 4089, 4132,
                5, 48, 91, 134, 177, 220, 263, 306, 349, 392, 435, 478, 521, 564, 607, 650, 693, 736, 779, 822, 865, 908, 951, 994, 1037, 1080, 1123, 1166, 1209, 1252, 1295, 1338, 1381, 1424, 1467, 1510, 1553, 1596, 1639, 1682, 1725, 1768, 1811, 1854, 1897, 1940, 1983, 2026, 2069, 2112, 2155, 2198, 2241, 2284, 2327, 2370, 2413, 2456, 2499, 2542, 2585, 2628, 2671, 2714, 2757, 2800, 2843, 2886, 2929, 2972, 3015, 3058, 3101, 3144, 3187, 3230, 3273, 3316, 3359, 3402, 3445, 3488, 3531, 3574, 3617, 3660, 3703, 3746, 3789, 3832, 3875, 3918, 3961, 4004, 4047, 4090, 4133,
                6, 49, 92, 135, 178, 221, 264, 307, 350, 393, 436, 479, 522, 565, 608, 651, 694, 737, 780, 823, 866, 909, 952, 995, 1038, 1081, 1124, 1167, 1210, 1253, 1296, 1339, 1382, 1425, 1468, 1511, 1554, 1597, 1640, 1683, 1726, 1769, 1812, 1855, 1898, 1941, 1984, 2027, 2070, 2113, 2156, 2199, 2242, 2285, 2328, 2371, 2414, 2457, 2500, 2543, 2586, 2629, 2672, 2715, 2758, 2801, 2844, 2887, 2930, 2973, 3016, 3059, 3102, 3145, 3188, 3231, 3274, 3317, 3360, 3403, 3446, 3489, 3532, 3575, 3618, 3661, 3704, 3747, 3790, 3833, 3876, 3919, 3962, 4005, 4048, 4091, 4134,
                7, 50, 93, 136, 179, 222, 265, 308, 351, 394, 437, 480, 523, 566, 609, 652, 695, 738, 781, 824, 867, 910, 953, 996, 1039, 1082, 1125, 1168, 1211, 1254, 1297, 1340, 1383, 1426, 1469, 1512, 1555, 1598, 1641, 1684, 1727, 1770, 1813, 1856, 1899, 1942, 1985, 2028, 2071, 2114, 2157, 2200, 2243, 2286, 2329, 2372, 2415, 2458, 2501, 2544, 2587, 2630, 2673, 2716, 2759, 2802, 2845, 2888, 2931, 2974, 3017, 3060, 3103, 3146, 3189, 3232, 3275, 3318, 3361, 3404, 3447, 3490, 3533, 3576, 3619, 3662, 3705, 3748, 3791, 3834, 3877, 3920, 3963, 4006, 4049, 4092, 4135,
                8, 51, 94, 137, 180, 223, 266, 309, 352, 395, 438, 481, 524, 567, 610, 653, 696, 739, 782, 825, 868, 911, 954, 997, 1040, 1083, 1126, 1169, 1212, 1255, 1298, 1341, 1384, 1427, 1470, 1513, 1556, 1599, 1642, 1685, 1728, 1771, 1814, 1857, 1900, 1943, 1986, 2029, 2072, 2115, 2158, 2201, 2244, 2287, 2330, 2373, 2416, 2459, 2502, 2545, 2588, 2631, 2674, 2717, 2760, 2803, 2846, 2889, 2932, 2975, 3018, 3061, 3104, 3147, 3190, 3233, 3276, 3319, 3362, 3405, 3448, 3491, 3534, 3577, 3620, 3663, 3706, 3749, 3792, 3835, 3878, 3921, 3964, 4007, 4050, 4093, 4136,
                9, 52, 95, 138, 181, 224, 267, 310, 353, 396, 439, 482, 525, 568, 611, 654, 697, 740, 783, 826, 869, 912, 955, 998, 1041, 1084, 1127, 1170, 1213, 1256, 1299, 1342, 1385, 1428, 1471, 1514, 1557, 1600, 1643, 1686, 1729, 1772, 1815, 1858, 1901, 1944, 1987, 2030, 2073, 2116, 2159, 2202, 2245, 2288, 2331, 2374, 2417, 2460, 2503, 2546, 2589, 2632, 2675, 2718, 2761, 2804, 2847, 2890, 2933, 2976, 3019, 3062, 3105, 3148, 3191, 3234, 3277, 3320, 3363, 3406, 3449, 3492, 3535, 3578, 3621, 3664, 3707, 3750, 3793, 3836, 3879, 3922, 3965, 4008, 4051, 4094, 4137,
                10, 53, 96, 139, 182, 225, 268, 311, 354, 397, 440, 483, 526, 569, 612, 655, 698, 741, 784, 827, 870, 913, 956, 999, 1042, 1085, 1128, 1171, 1214, 1257, 1300, 1343, 1386, 1429, 1472, 1515, 1558, 1601, 1644, 1687, 1730, 1773, 1816, 1859, 1902, 1945, 1988, 2031, 2074, 2117, 2160, 2203, 2246, 2289, 2332, 2375, 2418, 2461, 2504, 2547, 2590, 2633, 2676, 2719, 2762, 2805, 2848, 2891, 2934, 2977, 3020, 3063, 3106, 3149, 3192, 3235, 3278, 3321, 3364, 3407, 3450, 3493, 3536, 3579, 3622, 3665, 3708, 3751, 3794, 3837, 3880, 3923, 3966, 4009, 4052, 4095, 4138,
                11, 54, 97, 140, 183, 226, 269, 312, 355, 398, 441, 484, 527, 570, 613, 656, 699, 742, 785, 828, 871, 914, 957, 1000, 1043, 1086, 1129, 1172, 1215, 1258, 1301, 1344, 1387, 1430, 1473, 1516, 1559, 1602, 1645, 1688, 1731, 1774, 1817, 1860, 1903, 1946, 1989, 2032, 2075, 2118, 2161, 2204, 2247, 2290, 2333, 2376, 2419, 2462, 2505, 2548, 2591, 2634, 2677, 2720, 2763, 2806, 2849, 2892, 2935, 2978, 3021, 3064, 3107, 3150, 3193, 3236, 3279, 3322, 3365, 3408, 3451, 3494, 3537, 3580, 3623, 3666, 3709, 3752, 3795, 3838, 3881, 3924, 3967, 4010, 4053, 4096, 4139,
                12, 55, 98, 141, 184, 227, 270, 313, 356, 399, 442, 485, 528, 571, 614, 657, 700, 743, 786, 829, 872, 915, 958, 1001, 1044, 1087, 1130, 1173, 1216, 1259, 1302, 1345, 1388, 1431, 1474, 1517, 1560, 1603, 1646, 1689, 1732, 1775, 1818, 1861, 1904, 1947, 1990, 2033, 2076, 2119, 2162, 2205, 2248, 2291, 2334, 2377, 2420, 2463, 2506, 2549, 2592, 2635, 2678, 2721, 2764, 2807, 2850, 2893, 2936, 2979, 3022, 3065, 3108, 3151, 3194, 3237, 3280, 3323, 3366, 3409, 3452, 3495, 3538, 3581, 3624, 3667, 3710, 3753, 3796, 3839, 3882, 3925, 3968, 4011, 4054, 4097, 4140,
                13, 56, 99, 142, 185, 228, 271, 314, 357, 400, 443, 486, 529, 572, 615, 658, 701, 744, 787, 830, 873, 916, 959, 1002, 1045, 1088, 1131, 1174, 1217, 1260, 1303, 1346, 1389, 1432, 1475, 1518, 1561, 1604, 1647, 1690, 1733, 1776, 1819, 1862, 1905, 1948, 1991, 2034, 2077, 2120, 2163, 2206, 2249, 2292, 2335, 2378, 2421, 2464, 2507, 2550, 2593, 2636, 2679, 2722, 2765, 2808, 2851, 2894, 2937, 2980, 3023, 3066, 3109, 3152, 3195, 3238, 3281, 3324, 3367, 3410, 3453, 3496, 3539, 3582, 3625, 3668, 3711, 3754, 3797, 3840, 3883, 3926, 3969, 4012, 4055, 4098, 4141,
                14, 57, 100, 143, 186, 229, 272, 315, 358, 401, 444, 487, 530, 573, 616, 659, 702, 745, 788, 831, 874, 917, 960, 1003, 1046, 1089, 1132, 1175, 1218, 1261, 1304, 1347, 1390, 1433, 1476, 1519, 1562, 1605, 1648, 1691, 1734, 1777, 1820, 1863, 1906, 1949, 1992, 2035, 2078, 2121, 2164, 2207, 2250, 2293, 2336, 2379, 2422, 2465, 2508, 2551, 2594, 2637, 2680, 2723, 2766, 2809, 2852, 2895, 2938, 2981, 3024, 3067, 3110, 3153, 3196, 3239, 3282, 3325, 3368, 3411, 3454, 3497, 3540, 3583, 3626, 3669, 3712, 3755, 3798, 3841, 3884, 3927, 3970, 4013, 4056, 4099, 4142,
                15, 58, 101, 144, 187, 230, 273, 316, 359, 402, 445, 488, 531, 574, 617, 660, 703, 746, 789, 832, 875, 918, 961, 1004, 1047, 1090, 1133, 1176, 1219, 1262, 1305, 1348, 1391, 1434, 1477, 1520, 1563, 1606, 1649, 1692, 1735, 1778, 1821, 1864, 1907, 1950, 1993, 2036, 2079, 2122, 2165, 2208, 2251, 2294, 2337, 2380, 2423, 2466, 2509, 2552, 2595, 2638, 2681, 2724, 2767, 2810, 2853, 2896, 2939, 2982, 3025, 3068, 3111, 3154, 3197, 3240, 3283, 3326, 3369, 3412, 3455, 3498, 3541, 3584, 3627, 3670, 3713, 3756, 3799, 3842, 3885, 3928, 3971, 4014, 4057, 4100, 4143,
                16, 59, 102, 145, 188, 231, 274, 317, 360, 403, 446, 489, 532, 575, 618, 661, 704, 747, 790, 833, 876, 919, 962, 1005, 1048, 1091, 1134, 1177, 1220, 1263, 1306, 1349, 1392, 1435, 1478, 1521, 1564, 1607, 1650, 1693, 1736, 1779, 1822, 1865, 1908, 1951, 1994, 2037, 2080, 2123, 2166, 2209, 2252, 2295, 2338, 2381, 2424, 2467, 2510, 2553, 2596, 2639, 2682, 2725, 2768, 2811, 2854, 2897, 2940, 2983, 3026, 3069, 3112, 3155, 3198, 3241, 3284, 3327, 3370, 3413, 3456, 3499, 3542, 3585, 3628, 3671, 3714, 3757, 3800, 3843, 3886, 3929, 3972, 4015, 4058, 4101, 4144,
                17, 60, 103, 146, 189, 232, 275, 318, 361, 404, 447, 490, 533, 576, 619, 662, 705, 748, 791, 834, 877, 920, 963, 1006, 1049, 1092, 1135, 1178, 1221, 1264, 1307, 1350, 1393, 1436, 1479, 1522, 1565, 1608, 1651, 1694, 1737, 1780, 1823, 1866, 1909, 1952, 1995, 2038, 2081, 2124, 2167, 2210, 2253, 2296, 2339, 2382, 2425, 2468, 2511, 2554, 2597, 2640, 2683, 2726, 2769, 2812, 2855, 2898, 2941, 2984, 3027, 3070, 3113, 3156, 3199, 3242, 3285, 3328, 3371, 3414, 3457, 3500, 3543, 3586, 3629, 3672, 3715, 3758, 3801, 3844, 3887, 3930, 3973, 4016, 4059, 4102, 4145,
                18, 61, 104, 147, 190, 233, 276, 319, 362, 405, 448, 491, 534, 577, 620, 663, 706, 749, 792, 835, 878, 921, 964, 1007, 1050, 1093, 1136, 1179, 1222, 1265, 1308, 1351, 1394, 1437, 1480, 1523, 1566, 1609, 1652, 1695, 1738, 1781, 1824, 1867, 1910, 1953, 1996, 2039, 2082, 2125, 2168, 2211, 2254, 2297, 2340, 2383, 2426, 2469, 2512, 2555, 2598, 2641, 2684, 2727, 2770, 2813, 2856, 2899, 2942, 2985, 3028, 3071, 3114, 3157, 3200, 3243, 3286, 3329, 3372, 3415, 3458, 3501, 3544, 3587, 3630, 3673, 3716, 3759, 3802, 3845, 3888, 3931, 3974, 4017, 4060, 4103, 4146,
                19, 62, 105, 148, 191, 234, 277, 320, 363, 406, 449, 492, 535, 578, 621, 664, 707, 750, 793, 836, 879, 922, 965, 1008, 1051, 1094, 1137, 1180, 1223, 1266, 1309, 1352, 1395, 1438, 1481, 1524, 1567, 1610, 1653, 1696, 1739, 1782, 1825, 1868, 1911, 1954, 1997, 2040, 2083, 2126, 2169, 2212, 2255, 2298, 2341, 2384, 2427, 2470, 2513, 2556, 2599, 2642, 2685, 2728, 2771, 2814, 2857, 2900, 2943, 2986, 3029, 3072, 3115, 3158, 3201, 3244, 3287, 3330, 3373, 3416, 3459, 3502, 3545, 3588, 3631, 3674, 3717, 3760, 3803, 3846, 3889, 3932, 3975, 4018, 4061, 4104, 4147,
                20, 63, 106, 149, 192, 235, 278, 321, 364, 407, 450, 493, 536, 579, 622, 665, 708, 751, 794, 837, 880, 923, 966, 1009, 1052, 1095, 1138, 1181, 1224, 1267, 1310, 1353, 1396, 1439, 1482, 1525, 1568, 1611, 1654, 1697, 1740, 1783, 1826, 1869, 1912, 1955, 1998, 2041, 2084, 2127, 2170, 2213, 2256, 2299, 2342, 2385, 2428, 2471, 2514, 2557, 2600, 2643, 2686, 2729, 2772, 2815, 2858, 2901, 2944, 2987, 3030, 3073, 3116, 3159, 3202, 3245, 3288, 3331, 3374, 3417, 3460, 3503, 3546, 3589, 3632, 3675, 3718, 3761, 3804, 3847, 3890, 3933, 3976, 4019, 4062, 4105, 4148,
                21, 64, 107, 150, 193, 236, 279, 322, 365, 408, 451, 494, 537, 580, 623, 666, 709, 752, 795, 838, 881, 924, 967, 1010, 1053, 1096, 1139, 1182, 1225, 1268, 1311, 1354, 1397, 1440, 1483, 1526, 1569, 1612, 1655, 1698, 1741, 1784, 1827, 1870, 1913, 1956, 1999, 2042, 2085, 2128, 2171, 2214, 2257, 2300, 2343, 2386, 2429, 2472, 2515, 2558, 2601, 2644, 2687, 2730, 2773, 2816, 2859, 2902, 2945, 2988, 3031, 3074, 3117, 3160, 3203, 3246, 3289, 3332, 3375, 3418, 3461, 3504, 3547, 3590, 3633, 3676, 3719, 3762, 3805, 3848, 3891, 3934, 3977, 4020, 4063, 4106, 4149,
                22, 65, 108, 151, 194, 237, 280, 323, 366, 409, 452, 495, 538, 581, 624, 667, 710, 753, 796, 839, 882, 925, 968, 1011, 1054, 1097, 1140, 1183, 1226, 1269, 1312, 1355, 1398, 1441, 1484, 1527, 1570, 1613, 1656, 1699, 1742, 1785, 1828, 1871, 1914, 1957, 2000, 2043, 2086, 2129, 2172, 2215, 2258, 2301, 2344, 2387, 2430, 2473, 2516, 2559, 2602, 2645, 2688, 2731, 2774, 2817, 2860, 2903, 2946, 2989, 3032, 3075, 3118, 3161, 3204, 3247, 3290, 3333, 3376, 3419, 3462, 3505, 3548, 3591, 3634, 3677, 3720, 3763, 3806, 3849, 3892, 3935, 3978, 4021, 4064, 4107, 4150,
                23, 66, 109, 152, 195, 238, 281, 324, 367, 410, 453, 496, 539, 582, 625, 668, 711, 754, 797, 840, 883, 926, 969, 1012, 1055, 1098, 1141, 1184, 1227, 1270, 1313, 1356, 1399, 1442, 1485, 1528, 1571, 1614, 1657, 1700, 1743, 1786, 1829, 1872, 1915, 1958, 2001, 2044, 2087, 2130, 2173, 2216, 2259, 2302, 2345, 2388, 2431, 2474, 2517, 2560, 2603, 2646, 2689, 2732, 2775, 2818, 2861, 2904, 2947, 2990, 3033, 3076, 3119, 3162, 3205, 3248, 3291, 3334, 3377, 3420, 3463, 3506, 3549, 3592, 3635, 3678, 3721, 3764, 3807, 3850, 3893, 3936, 3979, 4022, 4065, 4108, 4151,
                24, 67, 110, 153, 196, 239, 282, 325, 368, 411, 454, 497, 540, 583, 626, 669, 712, 755, 798, 841, 884, 927, 970, 1013, 1056, 1099, 1142, 1185, 1228, 1271, 1314, 1357, 1400, 1443, 1486, 1529, 1572, 1615, 1658, 1701, 1744, 1787, 1830, 1873, 1916, 1959, 2002, 2045, 2088, 2131, 2174, 2217, 2260, 2303, 2346, 2389, 2432, 2475, 2518, 2561, 2604, 2647, 2690, 2733, 2776, 2819, 2862, 2905, 2948, 2991, 3034, 3077, 3120, 3163, 3206, 3249, 3292, 3335, 3378, 3421, 3464, 3507, 3550, 3593, 3636, 3679, 3722, 3765, 3808, 3851, 3894, 3937, 3980, 4023, 4066, 4109, 4152,
                25, 68, 111, 154, 197, 240, 283, 326, 369, 412, 455, 498, 541, 584, 627, 670, 713, 756, 799, 842, 885, 928, 971, 1014, 1057, 1100, 1143, 1186, 1229, 1272, 1315, 1358, 1401, 1444, 1487, 1530, 1573, 1616, 1659, 1702, 1745, 1788, 1831, 1874, 1917, 1960, 2003, 2046, 2089, 2132, 2175, 2218, 2261, 2304, 2347, 2390, 2433, 2476, 2519, 2562, 2605, 2648, 2691, 2734, 2777, 2820, 2863, 2906, 2949, 2992, 3035, 3078, 3121, 3164, 3207, 3250, 3293, 3336, 3379, 3422, 3465, 3508, 3551, 3594, 3637, 3680, 3723, 3766, 3809, 3852, 3895, 3938, 3981, 4024, 4067, 4110, 4153,
                26, 69, 112, 155, 198, 241, 284, 327, 370, 413, 456, 499, 542, 585, 628, 671, 714, 757, 800, 843, 886, 929, 972, 1015, 1058, 1101, 1144, 1187, 1230, 1273, 1316, 1359, 1402, 1445, 1488, 1531, 1574, 1617, 1660, 1703, 1746, 1789, 1832, 1875, 1918, 1961, 2004, 2047, 2090, 2133, 2176, 2219, 2262, 2305, 2348, 2391, 2434, 2477, 2520, 2563, 2606, 2649, 2692, 2735, 2778, 2821, 2864, 2907, 2950, 2993, 3036, 3079, 3122, 3165, 3208, 3251, 3294, 3337, 3380, 3423, 3466, 3509, 3552, 3595, 3638, 3681, 3724, 3767, 3810, 3853, 3896, 3939, 3982, 4025, 4068, 4111, 4154,
                27, 70, 113, 156, 199, 242, 285, 328, 371, 414, 457, 500, 543, 586, 629, 672, 715, 758, 801, 844, 887, 930, 973, 1016, 1059, 1102, 1145, 1188, 1231, 1274, 1317, 1360, 1403, 1446, 1489, 1532, 1575, 1618, 1661, 1704, 1747, 1790, 1833, 1876, 1919, 1962, 2005, 2048, 2091, 2134, 2177, 2220, 2263, 2306, 2349, 2392, 2435, 2478, 2521, 2564, 2607, 2650, 2693, 2736, 2779, 2822, 2865, 2908, 2951, 2994, 3037, 3080, 3123, 3166, 3209, 3252, 3295, 3338, 3381, 3424, 3467, 3510, 3553, 3596, 3639, 3682, 3725, 3768, 3811, 3854, 3897, 3940, 3983, 4026, 4069, 4112, 4155,
                28, 71, 114, 157, 200, 243, 286, 329, 372, 415, 458, 501, 544, 587, 630, 673, 716, 759, 802, 845, 888, 931, 974, 1017, 1060, 1103, 1146, 1189, 1232, 1275, 1318, 1361, 1404, 1447, 1490, 1533, 1576, 1619, 1662, 1705, 1748, 1791, 1834, 1877, 1920, 1963, 2006, 2049, 2092, 2135, 2178, 2221, 2264, 2307, 2350, 2393, 2436, 2479, 2522, 2565, 2608, 2651, 2694, 2737, 2780, 2823, 2866, 2909, 2952, 2995, 3038, 3081, 3124, 3167, 3210, 3253, 3296, 3339, 3382, 3425, 3468, 3511, 3554, 3597, 3640, 3683, 3726, 3769, 3812, 3855, 3898, 3941, 3984, 4027, 4070, 4113, 4156,
                29, 72, 115, 158, 201, 244, 287, 330, 373, 416, 459, 502, 545, 588, 631, 674, 717, 760, 803, 846, 889, 932, 975, 1018, 1061, 1104, 1147, 1190, 1233, 1276, 1319, 1362, 1405, 1448, 1491, 1534, 1577, 1620, 1663, 1706, 1749, 1792, 1835, 1878, 1921, 1964, 2007, 2050, 2093, 2136, 2179, 2222, 2265, 2308, 2351, 2394, 2437, 2480, 2523, 2566, 2609, 2652, 2695, 2738, 2781, 2824, 2867, 2910, 2953, 2996, 3039, 3082, 3125, 3168, 3211, 3254, 3297, 3340, 3383, 3426, 3469, 3512, 3555, 3598, 3641, 3684, 3727, 3770, 3813, 3856, 3899, 3942, 3985, 4028, 4071, 4114, 4157,
                30, 73, 116, 159, 202, 245, 288, 331, 374, 417, 460, 503, 546, 589, 632, 675, 718, 761, 804, 847, 890, 933, 976, 1019, 1062, 1105, 1148, 1191, 1234, 1277, 1320, 1363, 1406, 1449, 1492, 1535, 1578, 1621, 1664, 1707, 1750, 1793, 1836, 1879, 1922, 1965, 2008, 2051, 2094, 2137, 2180, 2223, 2266, 2309, 2352, 2395, 2438, 2481, 2524, 2567, 2610, 2653, 2696, 2739, 2782, 2825, 2868, 2911, 2954, 2997, 3040, 3083, 3126, 3169, 3212, 3255, 3298, 3341, 3384, 3427, 3470, 3513, 3556, 3599, 3642, 3685, 3728, 3771, 3814, 3857, 3900, 3943, 3986, 4029, 4072, 4115, 4158,
                31, 74, 117, 160, 203, 246, 289, 332, 375, 418, 461, 504, 547, 590, 633, 676, 719, 762, 805, 848, 891, 934, 977, 1020, 1063, 1106, 1149, 1192, 1235, 1278, 1321, 1364, 1407, 1450, 1493, 1536, 1579, 1622, 1665, 1708, 1751, 1794, 1837, 1880, 1923, 1966, 2009, 2052, 2095, 2138, 2181, 2224, 2267, 2310, 2353, 2396, 2439, 2482, 2525, 2568, 2611, 2654, 2697, 2740, 2783, 2826, 2869, 2912, 2955, 2998, 3041, 3084, 3127, 3170, 3213, 3256, 3299, 3342, 3385, 3428, 3471, 3514, 3557, 3600, 3643, 3686, 3729, 3772, 3815, 3858, 3901, 3944, 3987, 4030, 4073, 4116, 4159,
                32, 75, 118, 161, 204, 247, 290, 333, 376, 419, 462, 505, 548, 591, 634, 677, 720, 763, 806, 849, 892, 935, 978, 1021, 1064, 1107, 1150, 1193, 1236, 1279, 1322, 1365, 1408, 1451, 1494, 1537, 1580, 1623, 1666, 1709, 1752, 1795, 1838, 1881, 1924, 1967, 2010, 2053, 2096, 2139, 2182, 2225, 2268, 2311, 2354, 2397, 2440, 2483, 2526, 2569, 2612, 2655, 2698, 2741, 2784, 2827, 2870, 2913, 2956, 2999, 3042, 3085, 3128, 3171, 3214, 3257, 3300, 3343, 3386, 3429, 3472, 3515, 3558, 3601, 3644, 3687, 3730, 3773, 3816, 3859, 3902, 3945, 3988, 4031, 4074, 4117, 4160,
                33, 76, 119, 162, 205, 248, 291, 334, 377, 420, 463, 506, 549, 592, 635, 678, 721, 764, 807, 850, 893, 936, 979, 1022, 1065, 1108, 1151, 1194, 1237, 1280, 1323, 1366, 1409, 1452, 1495, 1538, 1581, 1624, 1667, 1710, 1753, 1796, 1839, 1882, 1925, 1968, 2011, 2054, 2097, 2140, 2183, 2226, 2269, 2312, 2355, 2398, 2441, 2484, 2527, 2570, 2613, 2656, 2699, 2742, 2785, 2828, 2871, 2914, 2957, 3000, 3043, 3086, 3129, 3172, 3215, 3258, 3301, 3344, 3387, 3430, 3473, 3516, 3559, 3602, 3645, 3688, 3731, 3774, 3817, 3860, 3903, 3946, 3989, 4032, 4075, 4118, 4161,
                34, 77, 120, 163, 206, 249, 292, 335, 378, 421, 464, 507, 550, 593, 636, 679, 722, 765, 808, 851, 894, 937, 980, 1023, 1066, 1109, 1152, 1195, 1238, 1281, 1324, 1367, 1410, 1453, 1496, 1539, 1582, 1625, 1668, 1711, 1754, 1797, 1840, 1883, 1926, 1969, 2012, 2055, 2098, 2141, 2184, 2227, 2270, 2313, 2356, 2399, 2442, 2485, 2528, 2571, 2614, 2657, 2700, 2743, 2786, 2829, 2872, 2915, 2958, 3001, 3044, 3087, 3130, 3173, 3216, 3259, 3302, 3345, 3388, 3431, 3474, 3517, 3560, 3603, 3646, 3689, 3732, 3775, 3818, 3861, 3904, 3947, 3990, 4033, 4076, 4119, 4162,
                35, 78, 121, 164, 207, 250, 293, 336, 379, 422, 465, 508, 551, 594, 637, 680, 723, 766, 809, 852, 895, 938, 981, 1024, 1067, 1110, 1153, 1196, 1239, 1282, 1325, 1368, 1411, 1454, 1497, 1540, 1583, 1626, 1669, 1712, 1755, 1798, 1841, 1884, 1927, 1970, 2013, 2056, 2099, 2142, 2185, 2228, 2271, 2314, 2357, 2400, 2443, 2486, 2529, 2572, 2615, 2658, 2701, 2744, 2787, 2830, 2873, 2916, 2959, 3002, 3045, 3088, 3131, 3174, 3217, 3260, 3303, 3346, 3389, 3432, 3475, 3518, 3561, 3604, 3647, 3690, 3733, 3776, 3819, 3862, 3905, 3948, 3991, 4034, 4077, 4120, 4163,
                36, 79, 122, 165, 208, 251, 294, 337, 380, 423, 466, 509, 552, 595, 638, 681, 724, 767, 810, 853, 896, 939, 982, 1025, 1068, 1111, 1154, 1197, 1240, 1283, 1326, 1369, 1412, 1455, 1498, 1541, 1584, 1627, 1670, 1713, 1756, 1799, 1842, 1885, 1928, 1971, 2014, 2057, 2100, 2143, 2186, 2229, 2272, 2315, 2358, 2401, 2444, 2487, 2530, 2573, 2616, 2659, 2702, 2745, 2788, 2831, 2874, 2917, 2960, 3003, 3046, 3089, 3132, 3175, 3218, 3261, 3304, 3347, 3390, 3433, 3476, 3519, 3562, 3605, 3648, 3691, 3734, 3777, 3820, 3863, 3906, 3949, 3992, 4035, 4078, 4121, 4164,
                37, 80, 123, 166, 209, 252, 295, 338, 381, 424, 467, 510, 553, 596, 639, 682, 725, 768, 811, 854, 897, 940, 983, 1026, 1069, 1112, 1155, 1198, 1241, 1284, 1327, 1370, 1413, 1456, 1499, 1542, 1585, 1628, 1671, 1714, 1757, 1800, 1843, 1886, 1929, 1972, 2015, 2058, 2101, 2144, 2187, 2230, 2273, 2316, 2359, 2402, 2445, 2488, 2531, 2574, 2617, 2660, 2703, 2746, 2789, 2832, 2875, 2918, 2961, 3004, 3047, 3090, 3133, 3176, 3219, 3262, 3305, 3348, 3391, 3434, 3477, 3520, 3563, 3606, 3649, 3692, 3735, 3778, 3821, 3864, 3907, 3950, 3993, 4036, 4079, 4122, 4165,
                38, 81, 124, 167, 210, 253, 296, 339, 382, 425, 468, 511, 554, 597, 640, 683, 726, 769, 812, 855, 898, 941, 984, 1027, 1070, 1113, 1156, 1199, 1242, 1285, 1328, 1371, 1414, 1457, 1500, 1543, 1586, 1629, 1672, 1715, 1758, 1801, 1844, 1887, 1930, 1973, 2016, 2059, 2102, 2145, 2188, 2231, 2274, 2317, 2360, 2403, 2446, 2489, 2532, 2575, 2618, 2661, 2704, 2747, 2790, 2833, 2876, 2919, 2962, 3005, 3048, 3091, 3134, 3177, 3220, 3263, 3306, 3349, 3392, 3435, 3478, 3521, 3564, 3607, 3650, 3693, 3736, 3779, 3822, 3865, 3908, 3951, 3994, 4037, 4080, 4123, 4166,
                39, 82, 125, 168, 211, 254, 297, 340, 383, 426, 469, 512, 555, 598, 641, 684, 727, 770, 813, 856, 899, 942, 985, 1028, 1071, 1114, 1157, 1200, 1243, 1286, 1329, 1372, 1415, 1458, 1501, 1544, 1587, 1630, 1673, 1716, 1759, 1802, 1845, 1888, 1931, 1974, 2017, 2060, 2103, 2146, 2189, 2232, 2275, 2318, 2361, 2404, 2447, 2490, 2533, 2576, 2619, 2662, 2705, 2748, 2791, 2834, 2877, 2920, 2963, 3006, 3049, 3092, 3135, 3178, 3221, 3264, 3307, 3350, 3393, 3436, 3479, 3522, 3565, 3608, 3651, 3694, 3737, 3780, 3823, 3866, 3909, 3952, 3995, 4038, 4081, 4124, 4167,
                40, 83, 126, 169, 212, 255, 298, 341, 384, 427, 470, 513, 556, 599, 642, 685, 728, 771, 814, 857, 900, 943, 986, 1029, 1072, 1115, 1158, 1201, 1244, 1287, 1330, 1373, 1416, 1459, 1502, 1545, 1588, 1631, 1674, 1717, 1760, 1803, 1846, 1889, 1932, 1975, 2018, 2061, 2104, 2147, 2190, 2233, 2276, 2319, 2362, 2405, 2448, 2491, 2534, 2577, 2620, 2663, 2706, 2749, 2792, 2835, 2878, 2921, 2964, 3007, 3050, 3093, 3136, 3179, 3222, 3265, 3308, 3351, 3394, 3437, 3480, 3523, 3566, 3609, 3652, 3695, 3738, 3781, 3824, 3867, 3910, 3953, 3996, 4039, 4082, 4125, 4168,
                41, 84, 127, 170, 213, 256, 299, 342, 385, 428, 471, 514, 557, 600, 643, 686, 729, 772, 815, 858, 901, 944, 987, 1030, 1073, 1116, 1159, 1202, 1245, 1288, 1331, 1374, 1417, 1460, 1503, 1546, 1589, 1632, 1675, 1718, 1761, 1804, 1847, 1890, 1933, 1976, 2019, 2062, 2105, 2148, 2191, 2234, 2277, 2320, 2363, 2406, 2449, 2492, 2535, 2578, 2621, 2664, 2707, 2750, 2793, 2836, 2879, 2922, 2965, 3008, 3051, 3094, 3137, 3180, 3223, 3266, 3309, 3352, 3395, 3438, 3481, 3524, 3567, 3610, 3653, 3696, 3739, 3782, 3825, 3868, 3911, 3954, 3997, 4040, 4083, 4126, 4169,
                42, 85, 128, 171, 214, 257, 300, 343, 386, 429, 472, 515, 558, 601, 644, 687, 730, 773, 816, 859, 902, 945, 988, 1031, 1074, 1117, 1160, 1203, 1246, 1289, 1332, 1375, 1418, 1461, 1504, 1547, 1590, 1633, 1676, 1719, 1762, 1805, 1848, 1891, 1934, 1977, 2020, 2063, 2106, 2149, 2192, 2235, 2278, 2321, 2364, 2407, 2450, 2493, 2536, 2579, 2622, 2665, 2708, 2751, 2794, 2837, 2880, 2923, 2966, 3009, 3052, 3095, 3138, 3181, 3224, 3267, 3310, 3353, 3396, 3439, 3482, 3525, 3568, 3611, 3654, 3697, 3740, 3783, 3826, 3869, 3912, 3955, 3998, 4041, 4084, 4127, 4170,
                43, 86, 129, 172, 215, 258, 301, 344, 387, 430, 473, 516, 559, 602, 645, 688, 731, 774, 817, 860, 903, 946, 989, 1032, 1075, 1118, 1161, 1204, 1247, 1290, 1333, 1376, 1419, 1462, 1505, 1548, 1591, 1634, 1677, 1720, 1763, 1806, 1849, 1892, 1935, 1978, 2021, 2064, 2107, 2150, 2193, 2236, 2279, 2322, 2365, 2408, 2451, 2494, 2537, 2580, 2623, 2666, 2709, 2752, 2795, 2838, 2881, 2924, 2967, 3010, 3053, 3096, 3139, 3182, 3225, 3268, 3311, 3354, 3397, 3440, 3483, 3526, 3569, 3612, 3655, 3698, 3741, 3784, 3827, 3870, 3913, 3956, 3999, 4042, 4085, 4128, 4171,
        };

        doubleBuffer.clear();
        doubleBuffer.put(expected);
        assertArrayEquals(byteBuffer.array(), transposed.data());
    }

    @Test
    void transpose3D() {
        double[] small3DMatrix = IntStream.rangeClosed(1, 16).mapToDouble(i -> i).toArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(small3DMatrix.length * 8);
        DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
        doubleBuffer.put(small3DMatrix);

        DoubleMatrix matrix = new DoubleMatrix(byteBuffer.array(), 2,2,4);
        Matrix<Double> transposed = matrix.transpose(1,2,0);
        // original 0 1 2
        double[] expected = new double[] {
                1, 9,
                2, 10,
                3, 11,
                4, 12,

                5, 13,
                6, 14,
                7, 15,
                8, 16
        };

        doubleBuffer.clear();
        doubleBuffer.put(expected);
        assertArrayEquals(byteBuffer.array(), transposed.data());
    }

    @Test
    void matrixMultiplication2D() {
        DoubleMatrix m1 = DoubleMatrix.zeros(6, 2);
        for (int i = 1; i <= 12 ; i++) {
            TheUnsafe.write(m1.data(), i-1, i);
        }

        DoubleMatrix m2 = DoubleMatrix.zeros(2,3);
        for (int i = 1; i <= 6 ; i++) {
            TheUnsafe.write(m2.data(), i-1, i);
        }

        Matrix<Double> m3 = m1.mmul2D(m2);
        int[] expected = new int[] {
                9, 12, 15,
                19, 26, 33,
                29, 40, 51,
                39, 54, 69,
                49, 68, 87,
                59, 82, 105
        };

        //TODO add checking after API change
    }
}