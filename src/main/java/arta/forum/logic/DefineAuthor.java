package arta.forum.logic;

import arta.filecabinet.logic.Person;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Constants;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Created by IntelliJ IDEA.
 * User: ${Indira}
 * Date: ${21.02.2008}
 * Time: 9:14:50
 * To change this template use File | Settings | File Templates.
 */
public class DefineAuthor {

    int authorID;
    int roleID;
    int classID;

    String lastName;
    String firstName;
    String className;

    public DefineAuthor(int authorID, int roleID, int classID) {
        this.authorID = authorID;
        this.roleID = roleID;
        this.classID = classID;
    }


    public void loadName() {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT lastname, firstname ") ;

            if (authorID == 0) {
                StringBuffer queryClasses = new StringBuffer("SELECT classname FROM classes WHERE classid=" + classID);
                res = st.executeQuery(queryClasses.toString());
                if (res.next()) {
                    className = res.getString(1);
                }
            } else  if (roleID == Constants.ADMIN || roleID == 6) {
                query.append(" FROM tutors WHERE tutorid=");
            } else {
                query.append(" FROM students WHERE studentid=");
            }

            query.append(authorID);
            res = st.executeQuery(query.toString());

            if (res.next()) {
                this.lastName = res.getString(1);
                this.firstName= res.getString(2);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally{
            try {
                con.close();
            } catch(Exception e ) {
                e.printStackTrace();
            }

        }

    }


    public int getRoleID() {
        return roleID;
    }

    public String getLastlName() {
        return lastName; 
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getClassName() {
        return className;
    }

    public String getFullName () {
        return firstName + " " + lastName;
    }

}
