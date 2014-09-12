package arta.common;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 14.03.2008
 * Time: 10:08:14
 * To change this template use File | Settings | File Templates.
 */
public class SCORMMessages {

    public static final int USER_CANNNOT_BE_REGISTERED_FOR_PREVIOUSLY_REGESTERED_COURSE = 1;

    /**
     *  Отсутствует файл imsmanifest.xml
     */
    public static final int MANIFEST_FILE_DOES_NOT_EXIST = 385;

    /**
     *  Не верный форма файла imsmanifest.xml
     */
    public static final int MANIFEST_FILE_IS_NOT_WELL_FORMED = 386;

    /**
     * Неверное содержание корневой директории
     */
    public static final int ROOT_ELEMENTS_DOES_NOT_BELONG_TO_THE_EXPECTED_NAMESPACE = 387;

    /**
     * Не все используемые файлы содержатся в корневой директории
     */
    public static final int CONTROL_DOCUMENTS_ARE_NOT_LOCATED_AT_ROOT_PACKAGE = 388;

    /**
     * Файл imsmanifest.xml некорректен для схем
     */
    public static final int XML_FILE_NOT_VALID_AGAINST_SCHEMAS = 389;

    /**
     * Файл imsmanifest.xml не соответствует требованиям определенным в стандарте SCORM 2004 3rd Edition
     */
    public static final int NOT_VALID_TO_SCORM_2004_3_REQUIREMENTS = 390;

    public static final int SUSPEND_BUTTON = 6000;
    public static final int QUITE_BUTTON = 6001;
    public static final int GLOSSARY_BUTTON = 6002;
    public static final int PREVIOUS_BUTTON = 6003;
    public static final int NEXT_BUTTON = 6004;    
    public static final int SELECT_SOME_MENU_OPTION_TO_CONTINE = 6005;    
    public static final int PLEASE_WAIT = 6006;    
    public static final int COURSE_HAS_BEEN_FINISHED = 6007;
    public static final int SESSION_HAS_BEEN_ENDED = 6008;

    public static final int TO_START_FOLLOW_THIS_LINK = 6009;
    public static final int START = 6010;    

}
