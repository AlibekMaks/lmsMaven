package arta.adminpanel.savepoint.html;

import arta.adminpanel.savepoint.SavePointsManager;
import arta.common.html.handler.TemplateHandler;
import arta.common.html.handler.FileReader;
import arta.common.html.handler.Parser;
import arta.common.logic.util.Message;
import arta.common.logic.util.Rand;

import javax.servlet.ServletContext;
import java.io.PrintWriter;


public class SavePointsListHandler extends TemplateHandler {

    String folderName;
    ServletContext servletContext;
    SavePointsManager manager ;
    Message message;
    int option;

    StringBuffer str;

    public SavePointsListHandler(String folderName, Message message, int option, ServletContext servletContext) {
        this.servletContext = servletContext;
        this.message = message;
        this.option = option;
        this.folderName = folderName;
        manager = new SavePointsManager();
        manager.setCopiesFolder(folderName);
        manager.getCopiesList(message);
        FileReader fileOpener = new FileReader("savepoint/savepoint.txt");
        str = fileOpener.read(servletContext);
    }

    public void replace(String name, PrintWriter pw) {
        if (name.equals("random")){
            pw.print(Rand.getRandString());
        } else if (name.equals("savepoints")){
            for (int i=0; i<manager.copies.size(); i++){
                StringBuffer tmp = new StringBuffer(str);
                SingleSavePointHandler handler = new SingleSavePointHandler(manager.copies.get(i));
                Parser parser = new Parser(tmp, pw, handler);
                parser.parse();
            }
        } else if (name.equals("message")){
            if (message!=null)
                message.writeMessage(pw);
        } else if (name.equals("change folder")){
            if (option == 3){
                ChangeFolderHandler handler = new ChangeFolderHandler(folderName);
                FileReader fileOpener = new FileReader("savepoint/folder.change.txt");
                Parser parser = new Parser(fileOpener.read(servletContext), pw, handler);
                parser.parse();
            }
        } else if(name.equals("current folder")){
            pw.print(folderName);
        } else if (name.equals("confirm create")){
            pw.print("Вы действительно хотите создать точку восстановления");
        } else if (name.equals("full")){
            pw.print("Создать полную точку восстановления");
        } else if (name.equals("short")){
            pw.print("Создать сокращенную точку восстановления");
        } else if (name.equals("change folder link")){
            pw.print("Изменить текущую диретокрию");
        } else if (name.equals("current folder is")){
            pw.print("Текущая папка");
        } else if (name.equals("name")){
            pw.print("Название");
        } else if (name.equals("roll back")){
            pw.print("Восстановить");
        } else if (name.equals("delete")){
            pw.print("Удалить");
        }
    }

}
