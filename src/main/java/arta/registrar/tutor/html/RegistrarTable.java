package arta.registrar.tutor.html;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Date;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.registrar.tutor.logic.JournalHeaderItem;
import arta.registrar.tutor.logic.RegistrarMessages;
import arta.registrar.tutor.logic.RegistrarStudent;
import arta.registrar.tutor.logic.JournalHeader;
import arta.timetable.designer.logic.LessonConstants;

import java.io.PrintWriter;
import java.util.ArrayList;


public class RegistrarTable {

    PrintWriter pw;
    int daysCount;
    int startDayNumber;
    int lang;
    JournalHeader header;
    ArrayList<RegistrarStudent> students;
    Date curdate;
    int year;
    int month;
    int studygroupID;
    int subgroupID;
    StringTransform trsf = new StringTransform();

    public RegistrarTable(PrintWriter pw, int daysCount, int startDayNumber, int lang,
                          JournalHeader header, ArrayList<RegistrarStudent> students,
                          Date curdate, int year, int month, int studygroupID, int subgroupID) {
        this.pw = pw;
        this.daysCount = daysCount;
        this.startDayNumber = startDayNumber;
        this.lang = lang;
        this.header = header;
        this.students = students;
        this.curdate = curdate;
        this.year = year;
        this.month = month;
        this.studygroupID = studygroupID;
        this.subgroupID = subgroupID;
    }

    Date today = new Date();

    public void writeTable(){
        pw.print("<table border=1 bordercolor=\"#000000\" width=\"100%\" class=\"table\">");
        header.writeHeader(pw, lang, trsf, Constants.TUTOR, startDayNumber);
        curdate.year = year;
        curdate.month = month;
        for (int i=0; i<students.size(); i++){
            writeStudent(students.get(i));
        }
        pw.print("</table>");
    }



    private void writeStudent(RegistrarStudent student){



        pw.print("<tr>");
        pw.print("<td nowrap>");
        pw.print(student.studentName);
        pw.print("</td>");

        int j = 0;

        for (int i = 0; i < header.items.size(); i ++){
            boolean hasMark = false;
            pw.print("<td align=\"center\">");
                if (j < student.marks.size()){
                    if (student.marks.get(j).day == header.items.get(i).day){
                        if (student.marks.get(j).markType == header.items.get(i).type ){
                            if (student.marks.get(j).markType == Constants.SIMPLE_MARK){
                                hasMark = true;
                                pw.print("<a href=\"tutorregistrar?month="+month+"&year="+year+"&action=-3&" +
                                        "studentID="+student.studentID+"&day="+header.items.get(i).day+"&" +
                                        "studygroupID="+studygroupID+"&subgroupID="+subgroupID+"\" " +
                                        "  class=\"href\" id=\"del_"+student.studentID+"_"+i+"\" " +
                                        " onclick='return confirmDelete(\"del_"+student.studentID+"_"+i+"\")'>");
                                pw.print(student.marks.get(j).getMark(lang));
                                pw.print("</a>");
                                j ++;
                            } else if (student.marks.get(j).testingID == header.items.get(i).testingID){
                                pw.print(student.marks.get(j).getMark(lang));
                                j ++;
                            }                            
                        }
                    }
                }
                if (!hasMark && header.items.get(i).type == Constants.SIMPLE_MARK){
                    curdate.day = header.items.get(i).day;
                    if (curdate.compareTo(today) <= 0)
                        pw.print("<input style=\"width:20px;\" class=\"input\" align=\"center\" " +
                                " name=\"mark_"+student.studentID+"_"+curdate.getDate()+"\">");
                }
            pw.print("</td>");
        }

        pw.print("</tr>");
    }

}
