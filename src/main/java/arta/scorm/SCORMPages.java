package arta.scorm;

import arta.common.logic.server.Server;
import arta.common.logic.util.Rand;
import arta.common.SCORMMessages;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 10.04.2008
 * Time: 8:58:40
 * To change this template use File | Settings | File Templates.
 */
public class SCORMPages {

    public static String ERROR_PAGE = "../specialstate/error.htm";

    public static String LAUNCH_ERROR = "../specialstate/error.htm";

    public static String LAUNCH_BLOCKED = "../specialstate/blocked.htm";

    public static String LAUNCH_TOC = "../specialstate/viewTOC.htm";

    public static String LAUNCH_COURSECOMPLETE = "../specialstate/coursecomplete.htm";

    public static String LAUNCH_INVALIDNAVEVENT = "../specialstate/invalidevent.htm";

    public static String LAUNCH_ENDSESSION = "../specialstate/endsession.htm";

    public static String LAUNCH_NOTHING = "../specialstate/nothing.htm";

    public static String LAUNCH_DEADLOCK = "../specialstate/deadlock.htm";

    public static String LAUNCH_SEQ_ABANDON = "../specialstate/abandon.htm";

    public static String LAUNCH_SEQ_ABANDONALL = "../specialstate/abandonAll.htm";

    public static String getPageURL(String code){
        if (code.equals(ERROR_PAGE)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SELECT_SOME_MENU_OPTION_TO_CONTINE
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_ERROR)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SELECT_SOME_MENU_OPTION_TO_CONTINE
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_BLOCKED)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SELECT_SOME_MENU_OPTION_TO_CONTINE
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_TOC)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SELECT_SOME_MENU_OPTION_TO_CONTINE
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_COURSECOMPLETE)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.COURSE_HAS_BEEN_FINISHED
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_INVALIDNAVEVENT)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SELECT_SOME_MENU_OPTION_TO_CONTINE
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_ENDSESSION)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SESSION_HAS_BEEN_ENDED
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_NOTHING)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SELECT_SOME_MENU_OPTION_TO_CONTINE
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_DEADLOCK)){
            return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SELECT_SOME_MENU_OPTION_TO_CONTINE
                    + "&rand=" + Rand.getRandString();
        } else if (code.equals(LAUNCH_SEQ_ABANDON)){

        } else if (code.equals(LAUNCH_SEQ_ABANDONALL)){

        }
        return Server.MAIN_URL + "scormmsg?id=" + SCORMMessages.SELECT_SOME_MENU_OPTION_TO_CONTINE
                    + "&rand=" + Rand.getRandString();
    }
}
 