package arta.common.logic.util;


public class Constants {

    public static final String RETURN_TO_MAIN_PAGE = "<html><script language=javascript>top.location='index'" +
            "</script></html>";

    /**
     * Размер кнопок вложенного менб на страничках
     * Например, "добавить эелемент".
     */
    public static final int MENU_IMAGE_SIZE = 32;

    public static final int TEST_CLIPBOARD_IMAGE_OBJECT = 0;

    public static final int TEST_ANY_OBJECT = 1;

    /**
     * Максимальное время в миллисекундах, в течение которого
     * в базе хранятся изображения несохраненного объекта (теста)
     * Если объект (тест) не был сохранен в течение этого времени
     * то изображения (и др объекты) будуь удалены
     */
    public static final long MAX_IMAGE_NOT_SAVED_LIFE_TIME = 86400000l;

    public static boolean SHOW_TEST_REPORT = true;

    public static final int SUBJECT_BOOK = 0;
    public static final int TUTOR_BOOK = 1;

    public static final boolean IS_DEMO_VERSION = false;
    public static final int STUDENTS_COUNT = 4;
    public static final int MAX_FILE_SIZE = 8390656;

    public static final int MAX_SUBGROUPS_NUMBER = 10;
    public  static final String ADMINISTRATOR_LOGIN  = "Admin";
    public  static final String ADMINISTRATOR_PASSWORD  = "Admin";

    /**
     * Роль пользователя - студента
     */
    public static final int STUDENT = 1;

    /**
     * Роль пользователя преподавателя
     */
    public static final int TUTOR = 2;
    public static final int ISCHAIRMAN = 1;
    public static final int NOTCHAIRMAN = 0;

    /**
     * Включается в роль преподавателя с правами администратора
     * т.е. у преподавателя с правами администратора значение роли - 6
     */
    public static final int ADMIN = 4;

    public static int MAX_MARK_VALUE = 100;
    public static boolean USE_SUBJECT_BALL = false;
    public static boolean SHOW_REPORT = false;
    public static boolean RECOMMEND_CANDIDATES = false;
    public static boolean SHOW_ANSWER = false;
    public static int EXCELLENT_MARK = 90;
    public static int GOOD_MARK = 80;
    public static int SATISFACTORY_MARK = 60;

    public static final int SIMPLE_MARK = 1;
    public static final int TEST_MARK = 2;

    public static final int KAZ_TEST = 1;
    public static final int RUS_TEST = 2;

    public static final int TUTOR_MESSAGE = 29;
    public static final int STUDENT_MESSAGE = 28;

    public static final int BACK = 46;
    public static final int PREVIOUS = 47;
    public static final int FIRST = 48;
    public static final int NEXT = 49;
    public static final int LAST = 50;
    public static final int CREATE = 51;
    public static final int SEARCH = 53;
    public static final int FOUND = 54;
    public static final int FIO = 55;
    public static final int EDIT = 56;
    public static final int APPOINT_BUTTON_VALUE = 218239;
    public static final int DELETE = 57;
    public static final int ALL_SELECTED = 58;
    public static final int ALL_EXTRACTED = 59;
    public static final int DOWNLOAD_A_TEMPLATE_FOR_IMPORT = 218258;

    public static final int TODAY = 60;
    public static final int JANUARY = 61;
    public static final int FEBRUARY = 62;
    public static final int MARCH = 63;
    public static final int APRIL = 64;
    public static final int MAY = 65;
    public static final int JUNE = 66;
    public static final int JULY = 67;
    public static final int AUGUST = 68;
    public static final int SEPTEMBER = 69;
    public static final int OCTOBER = 70;
    public static final int NOVEMBER = 71;
    public static final int DECEMBER = 72;

    public static final int JANUARY_SHORT = 73;
    public static final int FEBRUARY_SHORT = 74;
    public static final int MARCH_SHORT = 75;
    public static final int APRIL_SHORT = 76;
    public static final int MAY_SHORT = 77;
    public static final int JUNE_SHORT = 78;
    public static final int JULY_SHORT = 79;
    public static final int AUGUST_SHORT = 80;
    public static final int SEPTEMBER_SHORT = 81;
    public static final int OCTOBER_SHORT = 82;
    public static final int NOVEMBER_SHORT = 83;
    public static final int DECEMBER_SHORT = 84;

    public static final int MONDAY_SHORT = 85;
    public static final int TUESDAY_SHORT = 86;
    public static final int WEDNSDAY_SHORT = 87;
    public static final int THUSDAY_SHORT = 88;
    public static final int FRIDAY_SHORT = 89;
    public static final int SATURDAY_SHORT = 90;
    public static final int SUNDAY_SHORT = 91;
    public static final int SAVE = 96;
    public static final int ADMNISTRATOR = 98;
    public static final int CHAIRMAN = 2002;
    public static final int VICECHAIRMAN = 2003;
    public static final int MEMBERS = 2004;
    public static final int SECRETATY = 2005;
    public static final int SAVED = 44;
    public static final int NOT_SAVED = 45;    
    public static final int NAME = 102;
    public static final int NAMERU = 2000;
    public static final int NAMEKZ = 2001;
    public static final int INDEX_ID = 218235;
    public static final int INDEX_NUMBER = 218358;
    public static final int ENTER_FIELD_VALUE = 109;
    public static final int DELETD = 111;
    public static final int NOT_DELETED = 112;
    public static final int FAILED_TO_DELETE_THE_CURRENT_STUDENT = 218288;
    public static final int AS_PART_OF_THE_STUDENT_HAS_A_DESIGNATED_TEST = 218289;
    public static final int SINCE_THIS_REGION_IS_NEED = 218287;
    public static final int PATH = 115;
    public static final int LANGUAGE = 116;
    public static final int OPEN = 121;
    public static final int DOWNLOAD = 122;
    public static final int ADD = 133;
    public static final int XLS_IMPORT = 460;
    public static final int SELECT = 134;

    public static final int NUMBER = 147;
    public static final int START = 148;
    public static final int END = 149;
    public static final int HOUR_SHORT = 150;
    public static final int MINUTE_SHORT = 151;
    public static final int ACCEPT = 159;
    
    public static final int APPOINT = 174;
    public static final int CHANGE = 175;
    public static final int SEND = 183;
    public static final int SEARCH_IN = 191;
    public static final int VIEW_REPORT = 206;
    public static final int VIEW_ATTESTAT = 218194;
    public static final int REPORT_ON_GENERATION_LOGINS_AND_PASSWRODS = 208;
    public static final int CONFIRM_LOGINS_GEBERATE1 = 209;
    public static final int CONFIRM_LOGINS_GEBERATE2 = 210;
    public static final int DO_YOU_REALLY_WANT_TO_DELETE_STUDENT = 211;
    public static final int MORE_THAN_A_YEAR = 1;
    public static final int X_MONTHS_AND_X_DAYS = 2;
    public static final int X_MONTHS = 3;
    public static final int X_DAYS = 4;
    public static final int X_TODAY = 5;

    public static final int DO_YOU_REALLY_WANT_ABORT_TESTING_TO_STUDENT = 218272;
    public static final int HOME = 212;
    public static final int EXTRA = 213;
    public static final int TYPE = 216;
    public static final int YES = 223;
    public static final int NO = 224;

    public static final int MIN_EXC_MARK = 225;
    public static final int MIN_GOOD_MARK = 226;
    public static final int MIN_SATISF_MARK = 227;
    public static final int VIEW = 228;
    public static final int TEST = 229;
    public static final int STUDENT_CABINET = 230;
    public static final int TUTOR_CABINET = 231;

    public static final int FILE_NOT_FOUND = 238;
    /*
    parameter name
     */

    public static final int CONFIRM_DELETE = 240;
    public static final int NO_INFORMATION_FOUND_ON_YOUR_QUEY = 262;
    public static final int REASONS= 264;
    public static final int INFORMATION_HAS_BEEN_CHANGED = 265;
    public static final int INFORMATION_HAS_BEEN_DELETED = 266;
    public static final int GO_TO_THE_UPPER_LEVEL_AND_SEARCH_AGAIN = 268;

    /**
     * Заполните все обязательные поля!
     */
    public static final int FILL_IN_ALL_OF_THE_REQUIRED_FIELDS = 378;

    public static final int FORUM_MESSAGE = 397;

	public static final int ATTESTAT_THRESHOLD_DIRECTOR = 218204;

	public static final int ATTESTAT_THRESHOLD_EMPLOYEE = 218205;

	public static final int TESTING_VERDICT_HEADER = 218208;

	public static final int TESTING_VERDICT_GOOD = 218209;

	public static final int TESTING_VERDICT_BAD = 218210;

	public static final int VIEW_TESTING_ANALYSIS = 218212;



    public static final int VISIT_OUR_WEBSITE = 218246;

    public static final int FOR_STUDENTS_LISTED_BELLOW_CAN_NOT_BE_UNDONE_TESTING = 218273;


    public static final int NOT_SELECTED = 218280;

    public static final int REGION_WITH_THE_SAME_NAME_ALREDY_EXISTS = 218285;


    public static final int ENTER_THE_NAME_OF_TESTING = 218303;

    public static final int SELECT_ALL = -1;


}
