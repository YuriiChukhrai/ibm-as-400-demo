package com.yc.qa.as400.util;

/**
 * @author limit (Yurii Chukhrai)
 */
public final class Constants {

    public final static String SCREEN_DELIMITER = "+++++++++++++++++++++++ AS400 screens delimiter +++++++++++++++++++++++";
    public final static long TIME_SLEEP_3000 = 3_000L;
    public final static long TIME_SLEEP_500 = 500L;

    public final static long TIME_SLEEP_1000 = 1000L;
    public final static int TIME_SLEEP_MULTIPLIER_7 = 7;
    public final static int TIME_SLEEP_MULTIPLIER_15 = 15;

    //RegEx

    /**
     * Non-breaking space character - NBSP.
     * */
    public final static String NBSP_REGEX = "\u00a0";
    public final static String CONTROL_CHARACTERS_REGEX = "[^\\p{ASCII}\\p{C}]|[^\\x00-\\x7F]";

    /**
     * Unicode character properties - Other
     */
    public final static String UNICODE_CHARACTER_PROPERTIES_OTHER = "\\p{C}";

    // MSG's
    public final static String EMPTY_INPUT_MSG = "TID [%s] Input parameter = 'null' or 'Empty'";

    public final static String LONG_INPUT_STRING_MSG = "TID [%d] Length of string more than length of current field [%s]. String [%s] / [%s]";

}
