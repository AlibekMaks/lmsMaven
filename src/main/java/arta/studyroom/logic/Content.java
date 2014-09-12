package arta.studyroom.logic;

import arta.common.logic.util.SimpleObject;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import arta.common.logic.sql.ConnectionPool;
import arta.filecabinet.logic.Person;
import arta.books.logic.Book;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;


public class Content {

    public ArrayList <SimpleObject> tutorbooks = new ArrayList<SimpleObject>();
    public ArrayList <SimpleObject> SCORMBooks = new ArrayList <SimpleObject> ();
    public ArrayList <SimpleObject> books = new ArrayList<SimpleObject>();

    Person person;


    public Content(Person person) {
        this.person = person;
    }

    public void load(int subgroupID){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT resources.resourceID as id, " +
                    " resources.name as name FROM " +
                    " groupmaterials LEFT JOIN resources ON groupmaterials.resourceID=resources.resourceID " +
                    " WHERE groupmaterials.subgroupID=" + subgroupID + " " +
                    " AND resources.type="+ Constants.TUTOR_BOOK+"");
            while (res.next()){
                SimpleObject book = new SimpleObject();
                book.id = res.getInt("id");
                book.name = res.getString("name");
                tutorbooks.add(book);
            }

            res = st.executeQuery("SELECT resources.name as name, " +
                    " resources.resourceID as id " +
                    " FROM resources LEFT JOIN studygroups ON studygroups.subjectID=resources.subjectID " +
                    " LEFT JOIN subgroups ON subgroups.groupID=studygroups.groupID " +
                    " WHERE type=" + Constants.SUBJECT_BOOK + " AND booktype=" + Book.FILE_TYPE + " " +
                    " AND subgroups.subgroupID="+subgroupID+" " +
                    " ORDER BY resources.name ");
            while (res.next()){
                SimpleObject book = new SimpleObject();
                book.id = res.getInt("id");
                book.name = res.getString("name");
                books.add(book);
            }


            res = st.executeQuery("SELECT resources.name as name, " +
                    " courseinfo.courseid as id " +
                    " FROM resources LEFT JOIN studygroups ON studygroups.subjectID=resources.subjectID " +
                    " LEFT JOIN subgroups ON subgroups.groupID=studygroups.groupID " +
                    " left join courseinfo ON courseinfo.resourceid=resources.resourceID " +
                    " WHERE type=" + Constants.SUBJECT_BOOK + " AND booktype=" + Book.SCORM_TYPE + " " +
                    " AND subgroups.subgroupID="+subgroupID+" " +
                    " ORDER BY resources.name ");
            while (res.next()){
                SimpleObject book = new SimpleObject();
                book.id = res.getInt("id");
                book.name = res.getString("name");
                SCORMBooks.add(book);
            }




        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try{
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
            } catch (Exception exc){
                Log.writeLog(exc);
            }
        }
    }

}
