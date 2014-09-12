package arta.folder.department;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Languages;
import arta.common.logic.util.Log;
import arta.common.logic.util.SearchParser;
import arta.common.logic.util.SimpleObject;
import arta.filecabinet.logic.SearchParams;

public class Department {
	private int departmentID;
	private int parentDepartmentID;
	private String name;
	private String parentName;
	private int directorID;
	private String directorName;
	
	public static Department loadById(int departmentID, int lang) {
		Department dep = new Department();
		dep.setDepartmentID(departmentID);
		dep.load(lang);
		
		if (dep.parentDepartmentID == 0 && 
				dep.name == null && 
				dep.parentName == null && 
				dep.directorID == 0 && 
				dep.directorName == null) {
			return null;
		}
		return dep;
	}
	
	public void load(int lang) {
		
		String langSuffix;
		if (lang == Languages.ENGLISH) {
			langSuffix = "en";
		} else if (lang == Languages.KAZAKH) {
			langSuffix = "kz";
		} else {
			langSuffix = "ru";
		}
		
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		
		try {
			con = new ConnectionPool().getConnection();
			st = con.createStatement();
			
			rs = st.executeQuery(
					"SELECT d.name"+langSuffix+" as name, " +
							"d.directorid as dirid, " +
							"s.directorName as dirname, " +
							"d.parent_department_id as parid, " +
							"p.nameru as parname " +
					"FROM departments d " +
					"LEFT JOIN (" +
						"SELECT " +
							"studentid, " +
							"concat(lastname, ' ', firstname, CASE WHEN patronymic IS NOT NULL THEN concat(' ',patronymic) ELSE '' END) directorName " +
							"FROM students" +
						") s ON s.studentid = d.directorid " +
					"LEFT JOIN departments p ON d.parent_department_id = p.departmentID " +
					"WHERE d.departmentid=" + departmentID);
			
			if (rs.next()) {
				this.name = rs.getString("name");
				this.directorID = rs.getInt("dirid");
				this.directorName = rs.getString("dirname");
				this.parentDepartmentID = rs.getInt("parid");
				this.parentName = rs.getString("parname");
			}
			
		} catch (SQLException e) {
			System.out.println("sdkjfskdf exc");
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (st != null) st.close();
				if (con != null) con.close();
			} catch (SQLException e) {
			}
		}
	}

	public int getDepartmentID() {
		return departmentID;
	}

	public void setDepartmentID(int departmentID) {
		this.departmentID = departmentID;
	}

	public int getParentDepartmentID() {
		return parentDepartmentID;
	}

	public void setParentDepartmentID(int parentDepartmentID) {
		this.parentDepartmentID = parentDepartmentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public int getDirectorID() {
		return directorID;
	}

	public void setDirectorID(int directorID) {
		this.directorID = directorID;
	}

	public String getDirectorName() {
		return directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}



    public ArrayList<SimpleObject> departments = new ArrayList<SimpleObject>();

    public String getCondition(SearchParams params, int lang) {
        StringBuffer condition = new StringBuffer(" FROM departments ");
        if (params.search != null && params.search.length() > 0) {
            SearchParser parser = new SearchParser();
            ArrayList<String> tokens = parser.parseSearchString(new StringBuffer(params.search));
            if (tokens != null && tokens.size() > 0) {
                condition.append(" WHERE( ");
                for (int i = 0; i < tokens.size(); i++) {
                    if (i > 0)
                        condition.append(" OR ");
                    condition.append(" name"+Languages.getLang(lang)+" LIKE '%");
                    condition.append(tokens.get(i));
                    condition.append("%' ");
                }
                condition.append(")");
            }
        }
        return condition.toString();
    }

    public void search(SearchParams params, int lang) {

        Connection con = null;
        Statement st = null;
        ResultSet res = null;

        String ORDER = " ORDER BY name"+Languages.getLang(lang)+" ";

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            st = con.createStatement();

            StringBuffer query = new StringBuffer("SELECT departmentID as id, name"+Languages.getLang(lang)+" as name ");
            StringBuffer queryCount = new StringBuffer("SELECT COUNT(*) ");

            String condition = getCondition(params, lang);

            query.append(condition);
            queryCount.append(condition);

            query.append(ORDER);

            res = st.executeQuery(queryCount.toString());
            if (res.next())
                params.recordsCount = res.getInt(1);

            if (params.countInPart>0){
                int partsNumber = params.getPartsNumber();
                if (params.partNumber >= partsNumber)
                    params.partNumber = partsNumber - 1;
                if (params.partNumber < 0)
                    params.partNumber = 0;
                query.append(" LIMIT "+params.countInPart*params.partNumber+","+params.countInPart+" ");
            }

            res = st.executeQuery(query.toString());
            while (res.next()){
                SimpleObject object = new SimpleObject();
                object.id = res.getInt("id");
                object.name = res.getString("name");
                departments.add(object);
            }

        } catch (Exception exc) {
            Log.writeLog(exc);
        } finally {
            try {
                if (res != null) res.close();
                if (st != null) st.close();
                if (con != null && !con.isClosed()) con.close();
            } catch (Exception exc) {
                Log.writeLog(exc);
            }
        }
    }
	
	
	
}
