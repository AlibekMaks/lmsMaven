package arta.folder.directorComment;

import java.io.PrintWriter;

import javax.servlet.ServletContext;

import arta.common.html.handler.FileReader;
import arta.common.html.handler.PageContentHandler;
import arta.common.html.handler.Parser;
import arta.common.html.handler.TemplateHandler;
import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Constants;
import arta.filecabinet.logic.students.StudentSearchParams;
import arta.tests.common.TestMessages;

public class DirectorCommentHandler extends PageContentHandler {

    int lang;
    int roleID;
    StringBuffer commentBody;
    ServletContext servletContext;
    StudentSearchParams params;
    int studentID;
    String return_link;

    public DirectorCommentHandler(StringBuffer commentBody, int roleID, int lang, 
                                 ServletContext servletContext, int studentID,
                                 StudentSearchParams params,
                                 String return_link) {
        this.commentBody = commentBody;
        this.roleID = roleID;
        this.lang = lang;
        this.servletContext = servletContext;
        this.studentID = studentID;
        this.params = params;
        this.return_link = return_link;
    }

    public void getHeader(PrintWriter out) { }

    public void getBodyFunction(PrintWriter pw) { }

    public void getMainPart(PrintWriter pw) {
        new Parser(new FileReader("directorComment/director.comment.txt").read(servletContext), pw,
        		new InnerHandler()).parse();
    }

    public void getScript(PrintWriter pw) {

    }

    public int getLanguage() {
        return lang;
    }

    public int getRole() {
        return roleID;
    }
    
    class InnerHandler extends TemplateHandler {

        public void replace(String name, PrintWriter pw) {
            if (name.equals("return link")){
                //pw.print("student?studentID="+studentID+params.getFullParams());
                pw.print(return_link);
            } else if (name.equals("return title")){
                pw.print(MessageManager.getMessage(lang, Constants.BACK, null));
            } else if (name.equals("print link")){
                pw.print("directorComment?studentID="+studentID+"&print=true");
            } else if (name.equals("print title")){
                pw.print(MessageManager.getMessage(lang, TestMessages.VERSION_FOR_PRINT, null));
            } else if (name.equals("comment body")){
                if (commentBody != null)
                    pw.print(commentBody);
                else
                    pw.print("&nbsp;");
            } else if(name.equals("page header")){
                //pw.print(MessageManager.getMessage(lang, TestMessages.TESTING_ABOUT_REPORT));
            } else if(name.equals("size")){
                pw.print(Constants.MENU_IMAGE_SIZE);
            }
        }
    }
}
