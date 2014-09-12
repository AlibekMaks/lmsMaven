package arta.forum.logic;

import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;
import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class ThemeManager {

    public ArrayList<Theme> themes = new ArrayList<Theme>();
    String author;
    String lastAuthor;
    String themeTitle;

    public void search (SearchParams params, int userid, int partid, int roleid) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        Statement state = null;
        ResultSet result = null;
        Statement lastAuthorStatement = null;
        ResultSet lastAuthorResultSet = null;
        StringTransform trsf = new StringTransform();
        boolean isNew;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            state = con.createStatement();
            lastAuthorStatement = con.createStatement();

            StringBuffer mainQuery = new StringBuffer("select th.themeid, th.title, th.authorid, th.roleid, "
                    + "(select count(*) from forummessages where themeid=th.themeid), DATE(m.createddate),"
                    + " TIME(m.createddate), m.authorid, m.roleid, th.lastmessageid, "
                    + "th.lastmessagedate>v.visitedtime ");
            StringBuffer countQuery = new StringBuffer("select count(*) ");
            StringBuffer condition = new StringBuffer(" from forumthemes th "
                    + " left join forumvisitedthemes v on th.themeid=v.themeid and v.userid="
                    + userid + " and v.roleid=" + roleid
                    + " left join forummessages m on th.lastmessageid=m.messageid "
                    + " where th.partid=" + partid + " and th.title like '%"
                    + trsf.getDBString(params.getSearch()) + "%'");

            mainQuery.append(condition);
            countQuery.append(condition);
            mainQuery.append(" order by th.title ");

            res = st.executeQuery(countQuery.toString());
            if (res.next()) {
                params.recordsCount = res.getInt(1);
            }

            if (params.countInPart > 0) {
                int partsCount = params.getPartsNumber();
                if (params.partNumber >= partsCount) {
                    params.partNumber = partsCount - 1;
                    if (params.partNumber < 0)
                        params.partNumber = 0;
                }
                mainQuery.append(" limit ");
                mainQuery.append(params.countInPart * params.partNumber);
                mainQuery.append(", ");
                mainQuery.append(params.countInPart);
            }

            res = st.executeQuery(mainQuery.toString());

            while(res.next()) {

                StringBuffer query = new StringBuffer("SELECT lastname, firstname, patronymic ");
                int authorID = res.getInt(3);
                int roleID = res.getInt(4);
                int lastAuthorID = res.getInt(8);
                int lastRoleID = res.getInt(9);

                if ((roleID & Constants.ADMIN) > 0 || roleID == Constants.TUTOR) {
                    query.append(" FROM tutors where tutorid=");
                } else {
                    query.append(" FROM students where studentid=");
                }
                query.append(authorID);
                result = state.executeQuery(query.toString());
                if (result.next()) {
                    author = Person.extractName(result.getString("lastname"), result.getString("firstname"),
                            result.getString("patronymic"));
                }

                StringBuffer lastQuery = new StringBuffer("SELECT lastname, firstname, patronymic ");
                if ((lastRoleID & Constants.ADMIN) >0 || roleID == Constants.TUTOR) {
                    lastQuery.append(" FROM tutors where tutorid=");
                } else {
                    lastQuery.append(" FROM students where studentid=");
                }
                lastQuery.append(lastAuthorID);
                lastAuthorResultSet = lastAuthorStatement.executeQuery(lastQuery.toString());
                if (lastAuthorResultSet.next()) {
                    lastAuthor = Person.extractName(lastAuthorResultSet.getString("lastname"),
                            lastAuthorResultSet.getString("firstname"),
                            lastAuthorResultSet.getString("patronymic"));
                }

                Date date = new Date();
                Time time = new Time();

                date.loadDate(res.getString(6), Date.FROM_DB_CONVERT);
                time.loadDBTime(res.getString(7));

                if (res.getString(11) == null) {
                    isNew = true;
                } else {
                    isNew = res.getBoolean(11);

                    if (isNew) {
                        if (userid == lastAuthorID && roleid == lastRoleID)
                            isNew = false;
                    }
                }

                themes.add(new Theme(res.getInt(1), res.getString(2), author,
                        res.getInt(5), date.getDate() + " " + time.getValue(), lastAuthor, isNew));

            }

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try {
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
                if (result != null ) result.close();
                if (state != null) state.close();
                if (lastAuthorResultSet != null) lastAuthorResultSet.close();
                if (lastAuthorStatement != null) lastAuthorStatement.close();
            } catch(Exception exc) {
                Log.writeLog(exc);
            }
        }

    }


    public String search (int themeid) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {
            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("SELECT title FROM forumthemes WHERE themeid=" + themeid);

            if (res.next()) {
                themeTitle = res.getString(1);
            }

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();
            } catch(Exception exc) {
                Log.writeLog(exc);
            }
        }

        return  themeTitle;
    }


     public int getPartID (int themeid) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        int partID = 0;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            res = st.executeQuery("select partid from forumthemes where themeid=" + themeid);

            if (res.next()) {
                partID = res.getInt(1);
            }

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try{
                if (con != null) con.close();
                if (st != null) st.close();
                if (res != null ) res.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
     return partID;
    }


    public void updateVisitedDataForThemes(int themeid, Person person) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        ResultSet rs = null;
        Statement stat = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Statement state = null;
        ResultSet result = null;
        int partid = 0;
        int count;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            stat = con.createStatement();
            statement = con.createStatement();
            state = con.createStatement();

            res = st.executeQuery("select visitedtime from forumvisitedthemes where userid="
                    + person.getPersonID() + " and roleid=" + person.getRoleID() + " and themeid=" + themeid);

            if (!res.next()) {
                st.execute("insert into forumvisitedthemes (userid, roleid, themeid, "
                        + "visitedtime) values (" + person.getPersonID() + "," + person.getRoleID()
                        + ", " + themeid + ",current_timestamp)");
            } else {
                st.execute("update forumvisitedthemes set visitedtime = current_timestamp where userid="
                    + person.getPersonID() + " and roleid=" + person.getRoleID() + " and themeid=" + themeid);
            }

            rs = stat.executeQuery("select partid from forumthemes where themeid=" + themeid);
            if (rs.next()) {
                partid = rs.getInt(1);
            }

            resultSet = statement.executeQuery("select count(*) from forumthemes th " +
                    "left join forumvisitedthemes v on th.themeid=v.themeid and v.userid=" + person.getPersonID() +
                    " and v.roleid=" + person.getRoleID() + " and partid=" + partid + " where v.visitedtime < " +
                    "th.lastmessagedate or visitedtime is null and th.partid=" + partid);

            if (resultSet.next()) {
                
                count = resultSet.getInt(1);

                if (count == 0) {
                 result = state.executeQuery("select visitedtime from forumvisitedparts where userid="
                                + person.getPersonID() + " and roleid=" + person.getRoleID() + " and partid = " + partid);
                    if (!result.next()) {
                        st.execute("insert into forumvisitedparts (userid, roleid, partid, visitedtime)"
                                + " values (" + person.getPersonID() + ", " + person.getRoleID() +", "
                                + partid +", current_timestamp)");
                    } else {
                        st.execute("update forumvisitedparts set visitedtime = current_timestamp where userid="
                                + person.getPersonID() + " and roleid=" + person.getRoleID() + " and partid="
                                + partid);
                    }
                }
            }

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (con!= null) con.close();
                if (st != null) st.close();
                if (state != null) state.close();
                if (statement != null) statement.close();
                if (res != null) res.close();
                if (result != null) result.close();
                if (resultSet != null) resultSet.close();
                if (rs != null) rs.close();
                if (stat != null) stat.close();
            } catch(Exception exc) {
                Log.writeLog(exc);
            }
        }
    }
   
}