package arta.filecabinet.html.students;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.HTMLCalendar;
import arta.common.html.util.Select;
import arta.common.logic.util.Message;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;
import arta.filecabinet.logic.students.Student;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.folder.department.Department;
import arta.folder.directorComment.DirectorCommentMessages;
import arta.classes.logic.ClassesManager;
import arta.auth.logic.AuthorizationMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class StudentCard extends TemplateHandler {

    ServletContext servletContext;
    int lang;
    Message message;
    Student student;
    SearchParams params;
    HTMLCalendar calendar;
    StringTransform trsf = new StringTransform();


    public StudentCard(ServletContext servletContext, int lang, Message message, Student student, SearchParams params) {
        this.servletContext = servletContext;
        this.lang = lang;
        this.message = message;
        this.student = student;
        this.params = params;
        calendar = new HTMLCalendar(lang);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("rnd")){
            pw.print(Rand.getRandString());
        } else if (name.equals("message")){
            if (message!= null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("student card")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.STUDENT_CARD, null));
        } else if (name.equals("lastname")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.LAST_NAME, null));
        } else if (name.equals("lastname value")){
            pw.print(trsf.getHTMLString(student.getLastname()));
        } else if (name.equals("maxlength")){
            pw.print(Varchar.NAME);
        } else if (name.equals("firstname")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.FIRST_NAME, null));
        } else if (name.equals("firstname value")){
            pw.print(trsf.getHTMLString(student.getFirstname()));
        } else if (name.equals("patronymic")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.PATRONYMIC, null));
        } else if (name.equals("patronymic value")){
            pw.print(trsf.getHTMLString(student.getPatronymic()));
        } else if (name.equals("class")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.CLASS, null));
        } else if (name.equals("class select")){
            ClassesManager manager = new ClassesManager();
            SearchParams tmp = new SearchParams();
            tmp.countInPart = 0;
            manager.search(tmp);
            Select select = new Select(Select.SIMPLE_SELECT);
            select.width = 80;
            pw.print(select.startSelect("class"));
            pw.print(select.addOption(0, false, MessageManager.getMessage(lang, Constants.NOT_SELECTED, null)));
            for (int i=0; i<manager.classes.size(); i++){
                pw.print(select.addOption(manager.classes.get(i).id+"", manager.classes.get(i).id == student.getClassID(),
                        manager.classes.get(i).name));
            }
            pw.print(select.endSelect());
        } else if (name.equals("department")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.DEPARTMENT, null));
        } else if (name.equals("department select")){
            Department manager = new Department();
            SearchParams tmp = new SearchParams();
            tmp.countInPart = 0;
            manager.search(tmp, lang);
            Select select = new Select(Select.SIMPLE_SELECT);
            select.width = 80;
            pw.print(select.startSelect("department"));
            pw.print(select.addOption(0, false, MessageManager.getMessage(lang, Constants.NOT_SELECTED, null)));
            for (int i=0; i<manager.departments.size(); i++){
                pw.print(select.addOption(manager.departments.get(i).id+"",
                         manager.departments.get(i).id == student.getDepartmentID(),
                         manager.departments.get(i).name));
            }
            pw.print(select.endSelect());
        } else if (name.equals("adress")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.ADRESS, null));
        } else if (name.equals("adress value")){
            pw.print(trsf.getHTMLString(student.getAdress()));
        } else if (name.equals("phone")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.PHONE, null));
        } else if (name.equals("phone value")){
            pw.print(student.getPhone());
        } else if (name.equals("parents")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.PARENTS, null));
        } else if (name.equals("parents value")){
            pw.print(trsf.getHTMLString(student.getParents()));
        } else if (name.equals("birthdate")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.BIRTH_DATE, null));
        } else if (name.equals("birthdate input")){
            calendar.printInput(pw, student.getBirthdate(), "birth", "form");
        } else if (name.equals("stazOverallStart")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.STAZ_START_OVERALL, null));
        } else if (name.equals("stazOverallStart input")){
            calendar.printInput(pw, student.getStazOverallStart(), "staz_o", "form");
        } else if (name.equals("stazSocietyStart")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.STAZ_START_SOCIETY, null));
        } else if (name.equals("stazSocietyStart input")){
            calendar.printInput(pw, student.getStazSocietyStart(), "staz_s", "form");
        } else if (name.equals("stazPostStart")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.STAZ_START_POST, null));
        } else if (name.equals("stazPostStart input")){
            calendar.printInput(pw, student.getStazPostStart(), "staz_p", "form");
        } else if (name.equals("startdate")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.ENTER_DATE, null));
        } else if (name.equals("startdate input")){
            calendar.printInput(pw, student.getStartdate(), "start", "form");
        } else if (name.equals("education")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.EDUCATION, null));
        } else if (name.equals("education value")){
            pw.print(trsf.getHTMLString(student.getEducation()));
        } else if (name.equals("eduUZ")){
        	pw.print(MessageManager.getMessage(lang, FileCabinetMessages.EDU_UZ, null));
        } else if (name.equals("eduUZ value")){
        	pw.print(trsf.getHTMLString(student.getEducationUZ()));
        } else if (name.equals("eduProfession")){
        	pw.print(MessageManager.getMessage(lang, FileCabinetMessages.EDU_PROFESSION, null));
        } else if (name.equals("eduProfession value")){
        	pw.print(trsf.getHTMLString(student.getEducationProfession()));
        } else if (name.equals("eduQualification")){
        	pw.print(MessageManager.getMessage(lang, FileCabinetMessages.EDU_QUALIFICATION, null));
        } else if (name.equals("eduQualification value")){
        	pw.print(trsf.getHTMLString(student.getEducationQualification()));
        } else if (name.equals("hadUpgradedQualification")){
        	pw.print(MessageManager.getMessage(lang, FileCabinetMessages.HAD_UPGRADED_QUALIFICATION, null));
        } else if (name.equals("hadUpgradedQualification value")){//pw.print(student.isHadUpgradedQualification());
            if(student.isHadUpgradedQualification()){pw.print("checked");} else {pw.print("");}
        } else if (name.equals("isDirector")){
        	pw.print(MessageManager.getMessage(lang, FileCabinetMessages.IS_DIRECTOR, null));
        } else if (name.equals("isDirector value")){//pw.print(student.isDirector());
            if(student.isDirector()){pw.print("checked");} else {pw.print("");}
        } else if (name.equals("isInMaternityLeave")){
        	pw.print(MessageManager.getMessage(lang, FileCabinetMessages.IS_IN_MATERNITY_LEAVE, null));
        } else if (name.equals("isInMaternityLeave value")){//pw.print(student.isInMaternityLeave());
            if(student.isInMaternityLeave()){pw.print("checked");} else {pw.print("");}
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("studentID")){
            pw.print(student.getPersonID());
        } else if (name.equals("add link")){
            pw.print("student?"+params.getParamsWithoutRecord()+"&studentID=0");
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        }  else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));
        } else if (name.equals("return link")){
            pw.print("students?"+params.getFullParams());
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("enter all data")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS));
        } else if (name.equals("prev record")){
            pw.print("student?" + params.getParamsWithoutRecord() + "&" + params.recordNumberStr + "=" + params.getPreviousRecordNumber());
        }  else if (name.equals("next record")){
            pw.print("student?" + params.getParamsWithoutRecord() + "&" + params.recordNumberStr + "=" + params.getNextRecordNumber());
        } else if (name.equals("first record")){
            pw.print("student?" + params.getParamsWithoutRecord() + "&" + params.recordNumberStr + "=0");
        }  else if (name.equals("first title")){
           pw.print(MessageManager.getMessage(lang, Constants.FIRST));
        } else if (name.equals("next title")){
           pw.print(MessageManager.getMessage(lang, Constants.NEXT));
        } else if (name.equals("prev title")){
           pw.print(MessageManager.getMessage(lang, Constants.PREVIOUS));
        } else if (name.equals("last title")){
           pw.print(MessageManager.getMessage(lang, Constants.LAST)); 
        }  else if (name.equals("last record")){
            pw.print("student?" + params.getParamsWithoutRecord() + "&" + params.recordNumberStr + "=" + params.getLastRecordNumber());
        } else if (name.equals("auth")){
            if (student.getPersonID() != 0){
                pw.println("<td class=\"imgtd\">");
                    pw.println("<a href=\"adminauth?roleID="+Constants.STUDENT+"&personID="+student.getPersonID()+"&"+params.getFullParams()+
                    "&nocache="+Rand.getRandString()+"\" class=\"href\">");
                    pw.println("<img src=\"images/key.gif\" width=\""+Constants.MENU_IMAGE_SIZE+"px\" " +
                            " height=\""+Constants.MENU_IMAGE_SIZE+"px\" border=0 " +
                            " title=\""+MessageManager.getMessage(lang, AuthorizationMessages.AUTH_PARAMS, null)+"\" />");
                    pw.println("</a>");
                pw.println("</td>");
            }
        } else if (name.equals("director comment")){
            if (student.getPersonID() != 0){
                pw.println("<td class=\"imgtd\">");
                    pw.println("<a href=\"directorComment?studentID="+student.getPersonID()+"&"+params.getFullParams()+
                    "&nocache="+Rand.getRandString()+"\" class=\"href\">");
                    pw.println("<img src=\"images/testing.tests.gif\" width=\""+Constants.MENU_IMAGE_SIZE+"px\" " +
                            " height=\""+Constants.MENU_IMAGE_SIZE+"px\" border=0 " +
                            " title=\""+MessageManager.getMessage(lang, DirectorCommentMessages.DIRECTOR_COMMENT, null)+"\" />");
                    pw.println("</a>");
                pw.println("</td>");
            }
        }
    }
}
