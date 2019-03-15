package arta.settings.html;

import arta.classes.logic.ClassesManager;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.util.Select;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.filecabinet.logic.FileCabinetMessages;
import arta.filecabinet.logic.SearchParams;
import arta.settings.logic.SettingsMessages;
import arta.settings.logic.Settings;
import arta.tests.common.TestMessages;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class SettingsHandler extends TemplateHandler {

    int lang;
    ServletContext servletContext;
    Message message;
    Settings settings;


    public SettingsHandler(int lang, ServletContext servletContext, Message message, Settings settings) {
        this.lang = lang;
        this.servletContext = servletContext;
        this.message = message;
        this.settings = settings;
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("save")){
            pw.print(MessageManager.getMessage(lang, Constants.SAVE, null));
        } else if (name.equals("excellent mark")){
            pw.print(MessageManager.getMessage(lang, Constants.MIN_EXC_MARK, null));
        } else if (name.equals("excellent value")){
            pw.print(settings.excellent);
        } else if (name.equals("good mark")){
            pw.print(MessageManager.getMessage(lang, Constants.MIN_GOOD_MARK, null));
        } else if (name.equals("good value")){
            pw.print(settings.good);
        } else if (name.equals("satisfactory")){
            pw.print(MessageManager.getMessage(lang, Constants.MIN_SATISF_MARK, null));
        } else if (name.equals("satisfactory value")){
            pw.print(settings.satisfactory);
        } 
        
        else if (name.equals("attestat threshold for directors")){
            pw.print(MessageManager.getMessage(lang, Constants.ATTESTAT_THRESHOLD_DIRECTOR, null));
        } else if (name.equals("att_d value")){
            pw.print(settings.attestatThresholdForDirectors);
        }
        
        else if (name.equals("attestat threshold for employees")){
            pw.print(MessageManager.getMessage(lang, Constants.ATTESTAT_THRESHOLD_EMPLOYEE, null));
        } else if (name.equals("att_e value")){
            pw.print(settings.attestatThresholdForEmployee);
        }
        
        else if (name.equals("page header")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.SETTINGS_PAGE_HEADER, null));
        } else if (name.equals("size")){
            pw.print(Constants.MENU_IMAGE_SIZE);
        } else if (name.equals("message")){
            if (message != null && !message.isEmpty()){
                pw.print("<tr><td>");
                message.writeMessage(pw);
                pw.print("<td></tr>");
            }
        } else if (name.equals("usb label value")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.USB_LABEL_VALUE, null));
        } else if (name.equals("usb value")){
            if(settings.usesubjectball){
                pw.print("checked");
            } else {pw.print("");}
        }  else if (name.equals("usb_div display")){
            if(settings.usesubjectball){
                pw.print("block");
            } else {
                pw.print("none");
            }
        } else if (name.equals("usb_total label value")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.USB_TOTAL_LABEL_VALUE, null));
        } else if (name.equals("usb_total value")){
            if(settings.usetotalball){
                pw.print("checked");
            } else {pw.print("");}
        }  else if (name.equals("usb_total_tr display")){
            if(settings.usesubjectball){
                pw.print("block");
            } else {
                pw.print("none");
            }
        } else if (name.equals("usb_total_div display")){
            if(!settings.usesubjectball){
                pw.print("block");
            }else{
                if(settings.usetotalball){
                    pw.print("block");
                } else {
                    pw.print("none");
                }
            }
        } else if (name.equals("show_report msg")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.SEE_A_DETAILED_REPORT_TO_THE_APPLICANT_AFTER_THE_TESTING, null));
        } else if (name.equals("show_report value")){
            if(settings.show_report){
                pw.print("checked");
            } else {pw.print("");}

        } else if (name.equals("show_answers msg")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.SHOW_ANSWERS_AFTER_TEST, null));
        } else if (name.equals("show_answers value")){
            if(settings.show_answers){
                pw.print("checked");
            } else {pw.print("");}

        } else if (name.equals("student_test_access msg")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.BLOCK_ACCESS_TO_THE_STUDENT_DURING_THE_DELIVER_TEST, null));
        } else if (name.equals("student_test_access value")){
            if(settings.student_test_access){
                pw.print("checked");
            } else {pw.print("");}

        } else if (name.equals("recommend_candidates msg")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.RECOMMEND_CANDIDATES_FOR_CERTIFICATION, null));
        } else if (name.equals("recommend_candidates value")){
            if(settings.recommend_candidates){
                pw.print("checked");
            } else {pw.print("");}
        }  else if (name.equals("recommend_candidates_div display")){
            if(settings.recommend_candidates){
                pw.print("block");
            } else {
                pw.print("none");
            }

        } else if (name.equals("recommend_candidates_every every msg")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.EVERY, null));
        } else if (name.equals("recommend_candidates_every select")){
                Select select = new Select(Select.SIMPLE_SELECT);
                select.width = 20;
                pw.print(select.startSelect("recommend_candidates_month"));
                for (int i=1; i<=12; i++){
                    if(i<12){
                        pw.print(select.addOption(i, (i == settings.recommend_candidates_month), Integer.toString(i)) );
                    } else {
                        pw.print(select.addOption(i, (i == settings.recommend_candidates_month), MessageManager.getMessage(lang, FileCabinetMessages.MORE_THAN_ONE_YEAR)) );
                    }
                }
                pw.print(select.endSelect());
        } else if (name.equals("recommend_candidates_every month msg")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.MONTH, null));

        } else if (name.equals("max mark value")){
            pw.print(MessageManager.getMessage(lang, SettingsMessages.MAX_MARK_VALUE, null));
        } else if (name.equals("maxmark value")){
            pw.print(settings.maxMarkValue);
        } else if (name.equals("return title")){
            pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
        }
    }
}
