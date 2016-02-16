package android.android_5.touchlogger;

/**
 */
public enum IconGridEnum {

    GRID_00(1),
    GRID_01(2),
    GRID_02(3),
    GRID_10(4),
    GRID_11(5),
    GRID_12(6),
    GRID_20(7),
    GRID_21(8),
    GRID_22(9),
    GRID_30(999),
    GRID_31(0),
    GRID_32(999),
    GRID_40(999),
    GRID_41(999),
    GRID_42(999);

    public int gridValue;
    public int getValue() {
        return gridValue;
    }

    private IconGridEnum(int s) {
        gridValue = s;
    }

}
