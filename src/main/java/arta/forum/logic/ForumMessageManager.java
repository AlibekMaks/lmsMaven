package arta.forum.logic;

import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Constants;
import arta.common.logic.util.Date;
import arta.common.logic.util.Time;
import arta.common.logic.messages.MessageManager;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: ${Indira}
 * Date: ${21.02.2008}
 * Time: 13:16:01
 * To change this template use File | Settings | File Templates.
 */
public class ForumMessageManager  {

    public ArrayList<ForumMessage> messages = new ArrayList<ForumMessage>();


    public void search (SearchParams params, int themeid, int userid, int roleid, int lang) {

        Connection  con = null;

        ResultSet result = null;
        Statement state = null;

        ResultSet res = null;
        Statement st = null;
        String author = "";


        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            state = con.createStatement();

            String job = "";

            StringBuffer query = new StringBuffer("SELECT messageid, authorid, roleid, messagebody,"
                    + " title, DATE(createddate), TIME (createddate) from forummessages WHERE themeid="
                    + themeid + " order by createddate ");

            StringBuffer countQuery = new StringBuffer("SELECT count(*) FROM forummessages WHERE themeid=" + themeid);

            result = state.executeQuery(countQuery.toString());
            if (result.next()) {
                params.recordsCount = result.getInt(1);
            }

            if (params.countInPart > 0) {
                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount) {
                    params.partNumber = partsCount - 1;
                    if (params.partNumber < 0)
                        params.partNumber = 0;
                }
                query.append(" limit ");
                query.append(params.countInPart * params.partNumber);
                query.append(", ");
                query.append(params.countInPart);
            }

            res = st.executeQuery(query.toString());



            while(res.next()) {

                StringBuffer queryPerson = new StringBuffer("SELECT lastname, firstname, patronymic ");
                int authorID = res.getInt(2);
                int roleID = res.getInt(3);

                if (roleID != Constants.STUDENT) {
                    queryPerson.append(" FROM tutors where tutorid=");
                } else {
                    queryPerson.append(" FROM students where studentid=");
                }
                queryPerson.append(authorID);
                result = state.executeQuery(queryPerson.toString());
                if (result.next()) {
                    author = Person.extractName(result.getString("lastname"), result.getString("firstname"), 
                            result.getString("patronymic"));
                }

                if ( roleID != Constants.STUDENT) {
                     job = MessageManager.getMessage(lang, Constants.TUTOR_MESSAGE);
                } else {
                    
                    result = state.executeQuery("SELECT classname FROM classes c " +
                            "left join students s on s.classid=c.classid " +
                            "where studentid=" + res.getInt(3));

                    if (result.next()) {
                        job = result.getString(1);
                    }
                }

                Date date = new Date();
                Time time = new Time();

                date.loadDate(res.getString(6), Date.FROM_DB_CONVERT);
                time.loadDBTime(res.getString(7));


                messages.add(new ForumMessage(res.getInt(1), author, res.getInt(3),
                        job, res.getString(4), res.getString(5), date.getDate() + " " + time.getValue()));//getDBValue()));
            }

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (state != null) state.close();
                if (res != null) res.close();
                if (result != null) result.close();
            } catch(Exception exc) {
                Log.writeLog(exc);
            }

        }

    }

}
