package arta.registrar.student.html;

import arta.common.logic.messages.MessageManager;
import arta.common.logic.util.Date;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Constants;
import arta.registrar.tutor.logic.RegistrarMessages;
import arta.registrar.tutor.logic.JournalHeaderItem;
import arta.registrar.tutor.logic.JournalHeader;
import arta.registrar.student.logic.StudentRegistrarSubject;
import arta.timetable.designer.logic.LessonConstants;

import java.io.PrintWriter;
import java.util.ArrayList;

public class StudentRegistrarTable {

    PrintWriter pw;
    int daysCount;
    int startDayNumber;
    int lang;
    int year;
    int month;
    JournalHeader header;
    ArrayList<StudentRegistrarSubject> subjects;
    StringTransform trsf = new StringTransform();

    public StudentRegistrarTable(PrintWriter pw, int lang, JournalHeader header,
                                 ArrayList<StudentRegistrarSubject> subjects) {
        this.pw = pw;
        this.lang = lang;
        this.header = header;
        this.subjects = subjects;
    }

    public void writeTable(int month, int year){
        Date date = new Date();
        date.month = month;
        date.year  = year;
        date.day = 1;
        daysCount= date.getNumberOfDaysAMonth();
        startDayNumber = date.getDayfWeekNumber();
        pw.print("<table border=1 width=\"100%\" bordercolor=\"#000000\" class=\"table\">");
        header.writeHeader(pw, lang, trsf, Constants.STUDENT, startDayNumber);
        for (int i=0; i<subjects.size(); i++){
            writeSubject(subjects.get(i));
        }
        pw.print("</table>");
    }

    private void writeSubject(StudentRegistrarSubject subject){

        pw.print("<tr>");
        pw.print("<td nowrap>");
        pw.print(subject.subjectName);
        pw.print("</td>");
        int index = 0;

        int j = 0;

        for (int i = 0; i < header.items.size(); i ++){
            pw.print("<td align=\"center\">");
            if (j < subject.marks.size()){
                if (header.items.get(i).day == subject.marks.get(j).day){
                    if (header.items.get(i).type == subject.marks.get(j).markType){
                        if (header.items.get(i).type == Constants.SIMPLE_MARK ||
                                header.items.get(i).testingID == subject.marks.get(j).testingID){
                            pw.print(subject.marks.get(j).getMark(lang));
                            j ++;
                        }
                    }
                }
            }
            pw.print("</td>");
        }       
        pw.print("</tr>");
    }
}
