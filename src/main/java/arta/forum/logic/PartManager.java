package arta.forum.logic;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.Date;
import arta.common.logic.util.Constants;
import arta.common.logic.util.StringTransform;
import arta.common.logic.messages.MessageManager;
import arta.filecabinet.logic.SearchParams;
import arta.filecabinet.logic.Person;
import arta.forum.ForumMessages;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class PartManager {

    public ArrayList<Part> parts = new ArrayList<Part>();
    String className;
    StringBuffer author;

    public void search (SearchParams params, Person person, int lang) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;
        Statement state = null;
        ResultSet rs = null;
        Statement stat = null;
        ResultSet result = null;
        ResultSet r = null;
        Statement s = null;
        ResultSet classesRS = null;
        Statement classesST = null;
        ResultSet checkNewResultSet = null;
        Statement checkNewStatement = null;

        StringTransform trsf = new StringTransform();

        boolean  isNew;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();
            state = con.createStatement();
            stat = con.createStatement();
            s = con.createStatement();
            checkNewStatement = con.createStatement();
            classesST = con.createStatement();

            StringBuffer classes = new StringBuffer();
            StringBuffer additionalQuery = new StringBuffer();
            int classID = 0;

            if (person.getRoleID() == Constants.TUTOR ) {
                classesRS = classesST.executeQuery("select distinct classid from studygroups g " +
                        "left join subgroups s on g.groupid=s.groupid " +
                        "where tutorid=" + person.getPersonID());
                while (classesRS.next()) {
                    classes.append(classesRS.getString(1));
                    classes.append(",");
                }
                if (classes.length() != 0)
                    classes.delete(classes.length()-1, classes.length());
                
                additionalQuery.append(" AND (classid=0 OR classid in (").append(classes).append(")) ");
            } else if (person.getRoleID() == Constants.STUDENT) {
                classesRS = classesST.executeQuery("SELECT classid FROM students where studentid="
                        + person.getPersonID());
                if (classesRS.next())
                    classID = classesRS.getInt(1);
                additionalQuery.append(" AND (classid=0 OR classid=").append(classID).append(") ");
            }

            StringBuffer mainQuery = new StringBuffer("select p.partid, p.title, p.authorid, p.classid, "
                    + "DATE (p.createddate), p.roleid, lastmessagedate>v.visitedtime ");
            StringBuffer countQuery = new StringBuffer("select count(*) ");
            StringBuffer condition = new StringBuffer(" from forumparts p left join " +
                    " forumvisitedparts v on v.partid=p.partid and v.userid=" + person.getPersonID()
                    + " and v.roleid=" + person.getRoleID() + " where title like '%"
                    + trsf.getDBString(params.getSearch()) + "%' ");

            condition.append(additionalQuery);
            mainQuery.append(condition);
            countQuery.append(condition);
            mainQuery.append(" order by p.title ");

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

            result = stat.executeQuery(mainQuery.toString());
            Date date = new Date();


            while (result.next()) {

                StringBuffer query = new StringBuffer("SELECT lastname, firstname, patronymic ");
                int authorID = result.getInt(3);
                int roleID = result.getInt(6);

                if (authorID == 0) {
                    author = new StringBuffer(MessageManager.getMessage(lang, ForumMessages.PART_OF_GROUP));
                }

                if (roleID != Constants.STUDENT) {
                    query.append(" FROM tutors WHERE tutorid=").append(authorID);
                    rs = state.executeQuery(query.toString());
                    if (rs.next()) {
                        author = new StringBuffer(Person.extractName(rs.getString("lastname"), rs.getString("firstname"),
                                rs.getString("patronymic")));
                    }
                } else {
                    query.append(" FROM students WHERE studentid=").append(authorID);
                    rs = state.executeQuery(query.toString());
                    if (rs.next()) {
                        author = new StringBuffer(Person.extractName(rs.getString("lastname"), rs.getString("firstname"),
                                rs.getString("patronymic")));
                    }
                }


                if (result.getString(7) == null) {
                    isNew = true;
                } else {
                    isNew = !result.getString(7).equals("0");
                }

                checkNewResultSet = checkNewStatement.executeQuery("select title from forumthemes where partid="
                        + result.getInt(1));

                if (!checkNewResultSet.next()) {
                    isNew = false;
                } else {
                    if (isNew) {
                        r = s.executeQuery("select m.authorid, m.roleid from forumthemes th "
                                + "left join forummessages m on th.lastmessageid=m.messageid left join "
                                + "forumvisitedthemes v on th.themeid=v.themeid and v.roleid=" + person.getRoleID()
                                + " and v.userid=" + person.getPersonID() + " where partid=" + result.getInt(1)
                                + " and v.visitedtime<th.lastmessagedate");
                        while (r.next()) {
                            if (person.getPersonID() == r.getInt(1) && person.getRoleID()== r.getInt(2)) {
                                isNew = false;
                            }
                        }
                    }
                }

                date.loadDate(result.getString(5), Date.FROM_DB_CONVERT);
                parts.add(new Part(result.getInt(1), result.getString(2), author.toString(),
                        date.getDate(), isNew, result.getInt(7)));
            }

        } catch (Exception exc){
            Log.writeLog(exc);
        } finally {
            try {
                if (con != null) con.close();
                if (st!= null) st.close();
                if (res != null) res.close();
                if (rs != null ) rs.close();
                if (state != null) state.close();
                if (result != null ) result.close();
                if (stat != null) stat.close();
                if (r != null) r.close();
                if (s != null) s.close();
                if (checkNewStatement != null) checkNewStatement.close();
                if (checkNewResultSet != null) checkNewResultSet.close();
                if (classesRS != null) classesRS.close();
                if (classesST != null) classesST.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }

    }

    public String getPartName (int partid) {

        String partName = "";

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();


            res = st.executeQuery("SELECT title FROM forumparts WHERE partid=" + partid);

            if (res.next()) {
                partName = res.getString(1);
            }
        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try {
                if (con!= null) con.close();
                if (st != null) st.close();
                if (res != null) res.close();

            } catch(Exception exc) {
                Log.writeLog(exc);
            }
        }

        return partName;

    }

     public void updateVisitedDataForParts (int partid, Person person) {

        Connection con = null;
        Statement state = null;
        ResultSet result = null;
        Statement statement = null;
        ResultSet checkNewResultSet = null;
        Statement checkNewStatement = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            state = con.createStatement();
            statement = con.createStatement();
            checkNewStatement = con.createStatement();

            checkNewResultSet = checkNewStatement.executeQuery("select title from forumthemes where partid=" + partid);

            if (!checkNewResultSet.next()) {

                result = state.executeQuery("select visitedtime from forumvisitedparts where userid="
                        + person.getPersonID() + " and roleid=" + person.getRoleID() + " and partid = " + partid);
                if (!result.next()) {
                    statement.execute("insert into forumvisitedparts (userid, roleid, partid, visitedtime)"
                            + " values (" + person.getPersonID() + ", " + person.getRoleID() +", "
                            + partid +", current_timestamp)");
                } else {
                    statement.execute("update forumvisitedparts set visitedtime = current_timestamp where userid="
                            + person.getPersonID() + " and roleid=" + person.getRoleID() + " and partid="
                            + partid);
                }
            }

        } catch(Exception exc) {
            Log.writeLog(exc);
        } finally{
            try {
                if (con != null) con.close();
                if (state != null) state.close();
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (checkNewResultSet != null) checkNewResultSet.close();
                if (checkNewStatement != null) checkNewStatement.close();
            } catch(Exception e) {
                Log.writeLog(e);
            }

        }

    }


}


