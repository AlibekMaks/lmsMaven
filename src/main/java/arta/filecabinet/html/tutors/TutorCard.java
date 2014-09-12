package arta.filecabinet.html.tutors;

import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.HTMLCalendar;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.db.Varchar;
import arta.common.logic.util.Rand;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.StringTransform;
import arta.filecabinet.logic.tutors.Tutor;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.auth.logic.AuthorizationMessages;
import arta.folder.department.Department;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class TutorCard extends TemplateHandler {

    Tutor tutor;
    ServletContext servletContext;
    int lang;
    SearchParams params;
    HTMLCalendar calendar;
    Message message;
    StringTransform trsf = new StringTransform();


    public TutorCard(Tutor tutor, ServletContext servletContext, int lang,
                SearchParams params, Message message) {
        this.tutor = tutor;
        this.servletContext = servletContext;
        this.lang = lang;
        this.params = params;
        this.message = message;
        calendar = new HTMLCalendar(lang);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("lastname")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.LAST_NAME, null));
        } else if (name.equals("lastname value")){
            pw.print(trsf.getHTMLString(tutor.getLastname()));
        } else if (name.equals("firstname")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.FIRST_NAME, null));
        } else if (name.equals("firstname value")){
            pw.print(trsf.getHTMLString(tutor.getFirstname()));
        } else if (name.equals("patronymic")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.PATRONYMIC, null));
        } else if (name.equals("patronymic value")){
            pw.print(trsf.getHTMLString(tutor.getPatronymic()));
        } else if (name.equals("adress")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.ADRESS, null));
        } else if (name.equals("adress value")){
            pw.print(trsf.getHTMLString(tutor.getAdress()));
        } else if (name.equals("phone")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.PHONE, null));
        } else if (name.equals("phone value")){
            pw.print(trsf.getHTMLString(tutor.getPhone()));
        } else if (name.equals("education")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.EDUCATION, null));
        } else if (name.equals("education value")){
            pw.print(trsf.getHTMLString(tutor.getEducation()));
        } else if (name.equals("birthdate")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.BIRTH_DATE, null));
        } else if (name.equals("birthdate input")){
            calendar.printInput(pw, tutor.getBirthdate(), "birth", "form");
        } else if (name.equals("startdate")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.START_DATE, null));
        } else if (name.equals("startdate input")){
            calendar.printInput(pw, tutor.getStartdate(), "start", "form");
        } else if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("hidden inputs")){
            params.writeHiddenInputs(pw);
        } else if (name.equals("maxlength")){
            pw.print(Varchar.NAME);
        } else if (name.equals("rnd")){
            pw.print(Rand.getRandString());
        } else if (name.equals("isadmin")){
            pw.print(MessageManager.getMessage(lang, Constants.ADMNISTRATOR, null));
        } else if (name.equals("isadmin checeked")){
            if ((tutor.getRoleID() & Constants.ADMIN) > 0)
                pw.print(" checked ");
        } else if (name.equals("message")){
            if (message!=null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("</td></tr>");
            }
        } else if (name.equals("tutorID")){
            pw.print(tutor.getPersonID());
        } else if (name.equals("add link")){
            pw.print("tutor?tutorID=0&"+params.getFullParams());
        } else if (name.equals("add title")){
            pw.print(MessageManager.getMessage(lang, Constants.ADD, null));            
        } else if (name.equals("return link")){
            pw.print("tutors?"+params.getFullParams());
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));                        
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("tutor card")){
            pw.print(MessageManager.getMessage(lang, FileCabinetMessages.TUTOR_CARD));
        } else if (name.equals("enter all data")){
            pw.print(MessageManager.getMessage(lang, Constants.FILL_IN_ALL_OF_THE_REQUIRED_FIELDS));
        } else if (name.equals("first record")){
            pw.print("tutor?" + params.getParamsWithoutRecord() + "&" + params.recordNumberStr + "=0" );
        } else if (name.equals("first title")){
            pw.print(MessageManager.getMessage(lang, Constants.FIRST));
        } else if (name.equals("prev record")){
            pw.print("tutor?" + params.getParamsWithoutRecord() + "&" + params.recordNumberStr + "=" + params.getPreviousRecordNumber());
        } else if (name.equals("prev title")){
            pw.print(MessageManager.getMessage(lang, Constants.PREVIOUS));
        } else if (name.equals("next record")){
            pw.print("tutor?" + params.getParamsWithoutRecord() + "&" + params.recordNumberStr + "=" + params.getNextRecordNumber());
        } else if (name.equals("next title")){
            pw.print(MessageManager.getMessage(lang, Constants.NEXT));
        } else if (name.equals("last record")){
            pw.print("tutor?" + params.getParamsWithoutRecord() + "&" + params.recordNumberStr + "=" + params.getLastRecordNumber());
        } else if (name.equals("last title")){
            pw.print(MessageManager.getMessage(lang, Constants.LAST));
        }  else if (name.equals("auth")){
            if (tutor.getPersonID() != 0){
                pw.println("<td class=\"imgtd\">");
                    pw.println("<a href=\"adminauth?roleID="+Constants.TUTOR+"&personID="+tutor.getPersonID()+"&"+params.getFullParams()+
                    "&nocache="+Rand.getRandString()+"\" class=\"href\" >");
                    pw.println("<img src=\"images/key.gif\" width=\""+Constants.MENU_IMAGE_SIZE+"px\" " +
                            " height=\""+Constants.MENU_IMAGE_SIZE+"px\" border=0 " +
                            " title=\""+MessageManager.getMessage(lang, AuthorizationMessages.AUTH_PARAMS, null)+"\" >");
                    pw.println("</a>");
                pw.println("</td>");
            }
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
                        manager.departments.get(i).id == tutor.getDepartmentID(),
                        manager.departments.get(i).name));
            }
            pw.print(select.endSelect());
        }
    }
}
