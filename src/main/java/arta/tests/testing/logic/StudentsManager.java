package arta.tests.testing.logic;

import arta.common.logic.util.Log;
import arta.common.logic.util.StringTransform;
import arta.common.logic.util.Languages;
import arta.common.logic.sql.ConnectionPool;
import arta.filecabinet.logic.students.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 26.03.2008
 * Time: 17:02:56
 * To change this template use File | Settings | File Templates.
 */
public class StudentsManager {

    TestingStudentsSearchParams params;
    Testing testing;
    int tutorID;
    int lang;

    StringTransform trsf = new StringTransform();

    public ArrayList<TestingStudent> students = new ArrayList <TestingStudent> ();


    public StudentsManager(TestingStudentsSearchParams params, Testing testing, int tutorID, int lang) {
        this.params = params;
        this.testing = testing;
        this.tutorID = tutorID;
        this.lang = lang;
    }

    public void search(){

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            con = new ConnectionPool().getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer(
                    "SELECT students.lastname as ln, " +
                            " students.firstname as fn, " +
                            " students.patronymic as p, " +
                            " students.studentID as id, " +
                            " classes.classname as classname, " +
                            " subjects.name"+ Languages.getLang(lang) +" as subject, " +
                            " subgroups.subgroupID as subID " +
                            " FROM students LEFT JOIN classes ON classes.classID=students.classID " +
                            " LEFT JOIN studygroups ON studygroups.classID=classes.classID " +
                            " JOIN subgroups ON subgroups.groupID=studygroups.groupID " +
                            " JOIN studentgroup ON studentgroup.studentID=students.studentID AND studentgroup.groupID=subgroups.subgroupID " +
                            " JOIN subjects ON subjects.subjectID=studygroups.subjectID ");

            StringBuffer countQuery = new StringBuffer("SELECT COUNT(*) FROM " +
                    " students LEFT JOIN classes ON classes.classID=students.classID " +
                    " LEFT JOIN studygroups ON studygroups.classID=classes.classID " +
                    " JOIN subgroups ON subgroups.groupID=studygroups.groupID " +
                    " JOIN studentgroup ON studentgroup.studentID=students.studentID AND studentgroup.groupID=subgroups.subgroupID "+
                    " JOIN subjects ON subjects.subjectID=studygroups.subjectID ");

            StringBuffer condition = new StringBuffer();

            StringBuffer addedStudents = new StringBuffer();

            for (int i = 0; i < testing.students.size(); i ++){
                if (i > 0)
                    addedStudents.append(", ");
                addedStudents.append(testing.students.get(i));
            }

            condition.append(" WHERE ( subgroups.tutorID=" + tutorID);

            if (addedStudents.length() > 0){
                addedStudents.insert(0, "(");
                addedStudents.append(")");
                condition.append(" AND students.studentID NOT IN ");
                condition.append(addedStudents);
            }


            condition.append(" AND classes.classID=" + params.classID);

            condition.append(" AND subjects.subjectID=" + params.subjectID);

            if (params.search != null && params.search.length() > 0){
                condition.append(" AND (students.lastname like '%"+trsf.getDBString(params.search)+"%' " +
                        " OR students.firstname LIKE '%"+trsf.getDBString(params.search)+"%') ");
            }

            condition.append(") ");

            query.append(condition);
            countQuery.append(condition);

            query.append(" ORDER BY classes.classname, " +
                    " students.lastname, students.firstname, students.patronymic, students.studentID");

            if (params.countInPart != 0){
                query.append(" LIMIT ");
                query.append(params.countInPart*params.partNumber);
                query.append(", ");
                query.append(params.countInPart);
            }

            res = st.executeQuery(query.toString());

            while (res.next()){
                TestingStudent student = new TestingStudent();
                student.studentID = res.getInt("id");
                student.studentName = Student.extractName(res.getString("ln"), res.getString("fn"), res.getString("p"));
                student.className = res.getString("classname");
                student.subjectName = res.getString("subject");
                student.subgroupID = res.getInt("subID");
                students.add(student);
            }


            res = st.executeQuery(countQuery.toString());
            if (res.next()){
                params.recordsCount = res.getInt(1);
            }

        } catch(Exception exc){
            Log.writeLog(exc);
        } finally{
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            }catch(Exception exc){
                Log.writeLog(exc);
            }
        }

    }

}
