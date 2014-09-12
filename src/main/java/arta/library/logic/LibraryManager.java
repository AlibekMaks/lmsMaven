package arta.library.logic;

import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.Constants;
import arta.common.logic.util.SimpleObject;
import arta.common.logic.sql.ConnectionPool;
import arta.books.logic.Book;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class LibraryManager {

    public ArrayList<SimpleObject> books = new ArrayList<SimpleObject>();

    public void search(LibrarySearchParams params){
        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try{

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            if (!params.searchInSubjectBooks && !params.searchInTutorBooks)
                return;

            ArrayList<String> fields = new ArrayList<String>();

            StringBuffer sql = new StringBuffer("SELECT resources.name , " +
                    " resources.resourceID ");

            StringBuffer countSQL = new StringBuffer("SELECT count(*) ");

            StringBuffer condition = new StringBuffer(" FROM resources ");
            if (params.searchInTutorName){
                condition.append(" LEFT JOIN tutors ON tutors.tutorID=resources.tutorID ");
                fields.add("tutors.lastname");
                fields.add("tutors.firstname");
                fields.add("tutors.patronymic");
            }

            if (params.searchInSubjectBooks || params.searchInBookName){
                fields.add("resources.name");
            }

            condition.append(" WHERE ( resourceID>0 ");
            if(params.searchInSubjectBooks && !(params.searchInTutorBooks||params.searchInBookName)){
                condition.append(" AND type=");
                condition.append(Constants.SUBJECT_BOOK);
            } else if (!params.searchInSubjectBooks && (params.searchInTutorBooks||params.searchInBookName)){
                condition.append(" AND type=");
                condition.append(Constants.TUTOR_BOOK);
            }

            condition.append(" AND booktype="+ Book.FILE_TYPE+" ");
            
            if (params.search != null &&  params.search.length() > 0){
                SearchParser parser = new SearchParser();
                parser.parseSearchString(new StringBuffer(params.search));
                condition.append(" AND (");
                condition.append(parser.getCondition(fields));
                condition.append(") ");
            }

            if (params.lang > 0){
                condition.append(" AND language=");
                condition.append(params.lang);
                condition.append(" ");
            }

            condition.append(")");

            countSQL.append(condition);
            res = st.executeQuery(countSQL.toString());
            if (res.next())
                params.recordsCount = res.getInt(1);

            if (params.countInPart > 0){
                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount)
                    params.partNumber = partsCount - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;
                condition.append(" LIMIT ");
                condition.append(params.countInPart * params.partNumber);
                condition.append(", ");
                condition.append(params.countInPart);                
            }

            sql.append(condition);
            countSQL.append(condition);


            res = st.executeQuery(sql.toString());
            while (res.next()){
                SimpleObject book = new SimpleObject();
                book.id = res.getInt(2);
                book.name = res.getString(1);
                books.add(book);
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
