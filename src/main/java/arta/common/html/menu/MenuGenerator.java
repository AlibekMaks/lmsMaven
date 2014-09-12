package arta.common.html.menu;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Rand;
import arta.chat.servlet.ChatServlet;
import arta.exams.ExamMessages;
import arta.groups.logic.SubGroup;

import java.io.PrintWriter;
import java.util.ArrayList;


public class MenuGenerator {

    public static final int ICON_WIDTH = 32;
    public static final int ICON_HEIGHT = 32;
    public static final int BUTTON_HEIGHT = 40;


    private ArrayList <HTMLMenu> menu = new ArrayList <HTMLMenu> ();

    public void writeUserMenu(PrintWriter pw, int lang, int roleID){
        if (pw == null || lang == 0 || roleID == 0) return;
        if (roleID == Constants.STUDENT);
           /* fillStudentMenu(lang);*/
        else
            fillTutorMenu(lang, roleID);
        writeMenu(pw);                                                   
    }

    private void fillTutorMenu(int lang, int roleID){

        HTMLMenu filecabinet = null;
        HTMLMenu testing = null;
        HTMLMenu studyProcess = null;
        HTMLMenu classes = null;
        HTMLMenu settings = null;


        if ((roleID & Constants.ADMIN) > 0){
            filecabinet = new HTMLMenu(MessageManager.getMessage(lang, MenuMessages.FILE_CABINET));

            filecabinet.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.TUTORS, null), "tutors.gif",
                "tutors", true, MessageManager.getMessage(lang, MenuMessages.TUTORS_DESCRIPTION)));
            filecabinet.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.STUDENTS, null), "students.gif",
                    "students", true, MessageManager.getMessage(lang, MenuMessages.STUDENTS_DESCRIPTION)));
            menu.add(filecabinet);
        }


        testing = new HTMLMenu(MessageManager.getMessage(lang, MenuMessages.TESTING, null));
        menu.add(testing);

        if ((roleID & Constants.ADMIN) > 0){
            testing.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, ExamMessages.EXAMS, null),
                    "exam.gif",
                    "exams", true,
                    MessageManager.getMessage(lang, ExamMessages.EXAMS)));

            testing.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.TEST_DESIGNER, null),
                    "test.designer.gif",
                    "testsedit", true,
                    MessageManager.getMessage(lang, MenuMessages.TESTS_DESIGNER_DESCRIPTION)));
        }

        if ((roleID & Constants.ADMIN) >= 0){
            testing.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.PURPOSE_OF_TESTS, null),
                    "testings.gif",
                    "appointtests?option=2", true, //params=new&
                    MessageManager.getMessage(lang, MenuMessages.TESTINGS_DESCRIPTION)));
        }

        testing.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.SHEDULED_TESTS, null),
                "journal.gif",
                "testings?params=new", true,
                MessageManager.getMessage(lang, MenuMessages.TESTINGS_DESCRIPTION)));

        testing.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.REPORTS, null),
                "reports.gif",
                "testreports?newSearch=true", true,
                MessageManager.getMessage(lang, MenuMessages.TEST_REPORTS_DESCRIPTION)));

        studyProcess = new HTMLMenu(MessageManager.getMessage(lang, MenuMessages.STUDYPROCESS, null));
        if ((roleID & Constants.ADMIN)!=0){
            menu.add(studyProcess);
        }

        if ((roleID & Constants.ADMIN)<0){
            studyProcess.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.REGISTRAR, null),
                    "tutor.register.gif",
                    "tutorgroups", true,
                    MessageManager.getMessage(lang, MenuMessages.TUTOR_REGISTER_DESCRIPTION)));

            studyProcess.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.STUDY_ROOM, null),
                    "study.room.gif",
                    "chat?option=" + ChatServlet.GET_STUDY_ROOMS_LIST, true,
                    MessageManager.getMessage(lang, MenuMessages.STUDY_ROOM_DESCRIPTION)));

            studyProcess.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.STUDY_MATERIALS, null),
                    "tutor.boks.gif",
                    "tutorbooks", true, MessageManager.getMessage(lang, MenuMessages.STUDY_MATERIALS_DESCRIPTION)));
       }

        if ((roleID & Constants.ADMIN)>0){
            studyProcess.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.REGIONS, null), "regions.gif",
                    "departments", true, MessageManager.getMessage(lang, MenuMessages.COURSES_DESCRIPTION)));

            studyProcess.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.SUBJECTS, null), "subjects.gif",
                    "subjects", true, MessageManager.getMessage(lang, MenuMessages.COURSES_DESCRIPTION)));

            studyProcess.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.CLASSES, null), "classes.gif",
                    "classes", true, MessageManager.getMessage(lang, MenuMessages.GROUPS_DESCRIPTION)));




            settings = new HTMLMenu(MessageManager.getMessage(lang, MenuMessages.SETTINGS));
            menu.add(settings);
            settings.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.OTHER_SETTINGS, null),
                    "settings.gif", "settings", true,                     
                    MessageManager.getMessage(lang, MenuMessages.SETTINGS_DESCRIPTION)));
        }

    }

/*
  private void fillStudentMenu(int lang){

        HTMLMenu studyProcess;
        HTMLMenu studyRoom;

        studyProcess = new HTMLMenu(MessageManager.getMessage(lang, MenuMessages.STUDYPROCESS));

        studyProcess.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.REGISTRAR, null),
                "journal.gif",
                "studentregistrar", true, MessageManager.getMessage(lang, MenuMessages.STUDENT_REGISTRAR_DESCRIPTION)));

        studyProcess.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.LIBRARY),
                "library.gif",
                "library", true, MessageManager.getMessage(lang, MenuMessages.LIBRARY_DESCRIPTION)));

        menu.add(studyProcess);

        studyRoom = new HTMLMenu(MessageManager.getMessage(lang, MenuMessages.STUDY_ROOM));

        studyRoom.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.ROOM),
                "study.room.gif",
                "chat?option=" + ChatServlet.GET_STUDY_ROOMS_LIST, true, 
                MessageManager.getMessage(lang, MenuMessages.STUDY_ROOM_DESCRIPTION)));

        menu.add(studyRoom);
    }*/

    public void writeCommonMenu(int lang, PrintWriter pw){

        HTMLMenu menu = new HTMLMenu(null);

        menu.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.TO_MAIN), null,
                "main", false, null));

      /*  menu.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, Constants.FORUM_MESSAGE), null,
                "forum", false, null));*/

        menu.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.EXIT), null,
                "exit", false, null));
        
        menu.writeHorizontalMenu(pw);
                
    }

    private void writeMenu(PrintWriter pw){
        pw.print("<table border=0  cellpadding=0 cellspacing=0 width=\"100%\" " +
                " style=\"border-style:none;border-collapse:collapse\" >");

        for (int i = 0; i < menu.size(); i ++){
            menu.get(i).writeHTML(pw);
        }

        pw.print("</table>");
    }


    public void writeStudyRoomMenu(int lang, PrintWriter pw, int roomID, int roleID){

        HTMLMenu extra = new HTMLMenu(MessageManager.getMessage(lang, Constants.EXTRA));

        if (roleID == Constants.STUDENT){
            extra.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.REGISTRAR),
                    "journal.gif",
                    "studentregistrar", true,
                    MessageManager.getMessage(lang, MenuMessages.STUDENT_REGISTRAR_DESCRIPTION),
                    HTMLMenuItem.BLANK_WINDOW));
            extra.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.ROOM),
                    "study.room.gif",
                    "chat?option=" + ChatServlet.GET_STUDY_ROOMS_LIST, true,
                    MessageManager.getMessage(lang, MenuMessages.STUDY_ROOM_DESCRIPTION),
                    HTMLMenuItem.DOCUMENT_TOP));
        } else {
            extra.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.REGISTRAR, null),
                    "tutor.register.gif",
                    "tutorregistrar?studygroupID=" + SubGroup.getStudyGroupID(roomID) + "&subgroupID="+roomID+
                                "&nocache=" + Rand.getRandString(), true,
                    MessageManager.getMessage(lang, MenuMessages.TUTOR_REGISTER_DESCRIPTION),
                    HTMLMenuItem.BLANK_WINDOW));
            extra.addItem(new HTMLMenuItem(MessageManager.getMessage(lang, MenuMessages.STUDY_ROOM, null),
                    "study.room.gif",
                    "chat?option=" + ChatServlet.GET_STUDY_ROOMS_LIST, true,
                    MessageManager.getMessage(lang, MenuMessages.STUDY_ROOM_DESCRIPTION),
                    HTMLMenuItem.DOCUMENT_TOP));
        }


        menu.add(extra);

        writeMenu(pw);

    }


    
}

