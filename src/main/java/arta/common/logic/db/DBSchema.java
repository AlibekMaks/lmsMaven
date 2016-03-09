package arta.common.logic.db;

import arta.common.logic.util.*;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.messages.MessageManager;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.InputStream;


public class DBSchema {

    DataExtractor extractor = new DataExtractor();
    StringTransform trsf = new StringTransform();

    public ArrayList<DBTable> tables = new ArrayList <DBTable> ();
    public ArrayList<DBIndex> indexes = new ArrayList <DBIndex> ();

    public void init(){

        DBTable students = new DBTable("students");
        students.fields.add(new DBField("studentid", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        students.fields.add(new DBField("lastname", FieldType.VARCHAR, Varchar.NAME));
        students.fields.add(new DBField("firstname", FieldType.VARCHAR, Varchar.NAME));
        students.fields.add(new DBField("patronymic", FieldType.VARCHAR, Varchar.NAME));
        students.fields.add(new DBField("adress", FieldType.VARCHAR, Varchar.NAME));
        students.fields.add(new DBField("phone", FieldType.VARCHAR, Varchar.NAME));
        students.fields.add(new DBField("login", FieldType.VARCHAR, Varchar.LOGIN));
        students.fields.add(new DBField("password", FieldType.VARCHAR, Varchar.PASS));
        students.fields.add(new DBField("classID", FieldType.INTEGER));
        students.fields.add(new DBField("startdate", FieldType.DATE));
        students.fields.add(new DBField("birthdate", FieldType.DATE));
        students.fields.add(new DBField("parents", FieldType.VARCHAR, Varchar.NAME));
        students.fields.add(new DBField("staz_overall_startdate", FieldType.DATE));
        students.fields.add(new DBField("staz_society_startdate", FieldType.DATE));
        students.fields.add(new DBField("staz_post_startdate", FieldType.DATE));
        students.fields.add(new DBField("education", FieldType.VARCHAR, Varchar.LONG_NAME));
        students.fields.add(new DBField("edu_uz", FieldType.VARCHAR, Varchar.LONG_NAME));
        students.fields.add(new DBField("edu_profession", FieldType.VARCHAR, Varchar.LONG_NAME));
        students.fields.add(new DBField("edu_qualification", FieldType.VARCHAR, Varchar.LONG_NAME));
        students.fields.add(new DBField("isUpgrade", FieldType.BOOLEAN));
        students.fields.add(new DBField("isDirector", FieldType.BOOLEAN));
        students.fields.add(new DBField("isInMaternityLeave", FieldType.BOOLEAN));
        students.fields.add(new DBField("departmentid", FieldType.INTEGER));
        students.fields.add(new DBField("deleted", FieldType.BOOLEAN, "0"));
        tables.add(students);
        students.indexes.add(new DBIndex("students_index", "studentid"));
        
        DBTable test_result = new DBTable("test_result");
        test_result.fields.add(new DBField("subject_id", FieldType.INTEGER));
        test_result.fields.add(new DBField("testing_id", FieldType.INTEGER));
        test_result.fields.add(new DBField("student_id", FieldType.INTEGER));
        test_result.fields.add(new DBField("questions_count", FieldType.INTEGER));
        test_result.fields.add(new DBField("right_answers_count", FieldType.INTEGER));
        test_result.fields.add(new DBField("isPassed", FieldType.BOOLEAN, "0"));
        test_result.fields.add(new DBField("persentage", FieldType.INTEGER, "0"));
        tables.add(test_result);
        test_result.indexes.add(new DBIndex("test_result_index", "student_id"));
        test_result.indexes.add(new DBIndex("test_result_index2", "subject_id"));
        test_result.indexes.add(new DBIndex("test_result_index3", "testing_id"));

        DBTable handing_over_testing = new DBTable("handing_over_testing");
        handing_over_testing.fields.add(new DBField("id", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        handing_over_testing.fields.add(new DBField("StudentID", FieldType.INTEGER));
        handing_over_testing.fields.add(new DBField("IP", FieldType.VARCHAR, 128));
        handing_over_testing.fields.add(new DBField("enddatetime", FieldType.TIMESTAMP ));
        tables.add(handing_over_testing);

        DBTable docflow = new DBTable("docflow");
        docflow.fields.add(new DBField("id", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        tables.add(docflow);

        DBTable departments = new DBTable("departments");
        departments.fields.add(new DBField("departmentID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        departments.fields.add(new DBField("parent_department_id", FieldType.INTEGER));
        departments.fields.add(new DBField("namekz", FieldType.VARCHAR, Varchar.DESCRIPTION));
        departments.fields.add(new DBField("nameru", FieldType.VARCHAR, Varchar.DESCRIPTION));
        departments.fields.add(new DBField("nameen", FieldType.VARCHAR, Varchar.DESCRIPTION));
        departments.fields.add(new DBField("directorid", FieldType.INTEGER));
        tables.add(departments);
        departments.indexes.add(new DBIndex("index_departmentID", "departmentID"));


        DBTable sessions = new DBTable("sessions");
        sessions.fields.add(new DBField("sessionID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        sessions.fields.add(new DBField("studentID", FieldType.INTEGER, "0"));
        sessions.fields.add(new DBField("maintestingID", FieldType.INTEGER, "0"));
        sessions.fields.add(new DBField("testingID", FieldType.INTEGER, "0"));
        sessions.fields.add(new DBField("testing", FieldType.LONG_BLOB));
        sessions.fields.add(new DBField("modified", FieldType.TIMESTAMP));
        tables.add(sessions);
        sessions.indexes.add(new DBIndex("index_studentID", "studentID"));
        sessions.indexes.add(new DBIndex("index_testingID", "testingID"));


        DBTable tutors = new DBTable("tutors");
        tutors.fields.add(new DBField("tutorID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        tutors.fields.add(new DBField("lastname", FieldType.VARCHAR, Varchar.NAME));
        tutors.fields.add(new DBField("patronymic", FieldType.VARCHAR, Varchar.NAME));
        tutors.fields.add(new DBField("firstname", FieldType.VARCHAR, Varchar.NAME));
        tutors.fields.add(new DBField("adress", FieldType.VARCHAR, Varchar.NAME));
        tutors.fields.add(new DBField("phone", FieldType.VARCHAR, Varchar.NAME));
        tutors.fields.add(new DBField("education", FieldType.VARCHAR, Varchar.DESCRIPTION));
        tutors.fields.add(new DBField("login", FieldType.VARCHAR, Varchar.LOGIN));
        tutors.fields.add(new DBField("password", FieldType.VARCHAR, Varchar.PASS));
        tutors.fields.add(new DBField("birthdate", FieldType.DATE));
        tutors.fields.add(new DBField("startdate", FieldType.DATE));
        tutors.fields.add(new DBField("roleID", FieldType.INTEGER));
        tutors.fields.add(new DBField("ischairman", FieldType.INTEGER));
        tutors.fields.add(new DBField("isvicechairman", FieldType.INTEGER));
        tutors.fields.add(new DBField("ismembers", FieldType.INTEGER));
        tutors.fields.add(new DBField("issecretary", FieldType.INTEGER));
        tutors.fields.add(new DBField("departmentID", FieldType.INTEGER, "0"));
        tutors.fields.add(new DBField("deleted", FieldType.BOOLEAN, "0"));
        tables.add(tutors);
        tutors.indexes.add(new DBIndex("tutors_index", "tutorID"));

        DBTable subjects  = new DBTable("subjects");
        subjects.fields.add(new DBField("subjectID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        subjects.fields.add(new DBField("nameru", FieldType.VARCHAR, Varchar.NAME));
        subjects.fields.add(new DBField("namekz", FieldType.VARCHAR, Varchar.NAME));
        subjects.fields.add(new DBField("nameen", FieldType.VARCHAR, Varchar.NAME));
        subjects.fields.add(new DBField("preferredMark", FieldType.INTEGER));
        subjects.fields.add(new DBField("kaz_test_id", FieldType.INTEGER));
        subjects.fields.add(new DBField("rus_test_id", FieldType.INTEGER));
        tables.add(subjects);
        subjects.indexes.add(new DBIndex("subjectid_index", "subjectID"));

        DBTable testing_all_quetions  = new DBTable("testing_all_quetions");
        testing_all_quetions.fields.add(new DBField("mainTestID", FieldType.INTEGER)); //, false, Key.AUTO_INCREMENT_KEY
        testing_all_quetions.fields.add(new DBField("testID", FieldType.INTEGER));
        testing_all_quetions.fields.add(new DBField("easy", FieldType.INTEGER));
        testing_all_quetions.fields.add(new DBField("middle", FieldType.INTEGER));
        testing_all_quetions.fields.add(new DBField("difficult", FieldType.INTEGER));
        tables.add(testing_all_quetions);
        String[] testing_all_quetionsIndexes = {"mainTestID", "testID"};
        //testing_all_quetions.indexes.add(new DBIndex("testing_all_quetions_indexes", testing_all_quetionsIndexes));
        testing_all_quetions.indexes.add(new DBIndex("testing_all_quetions_mainTestID", "mainTestID"));
        testing_all_quetions.indexes.add(new DBIndex("testing_all_quetions_testID", "testID"));

        DBTable groups = new DBTable("studygroups");
        groups.fields.add(new DBField("groupID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        groups.fields.add(new DBField("classID", FieldType.INTEGER, false, Key.NOT_A_KEY));
        groups.fields.add(new DBField("subjectID", FieldType.INTEGER, false, Key.NOT_A_KEY));
        tables.add(groups);
        groups.indexes.add(new DBIndex("sg_classid", "classID"));

        DBTable subgroups = new DBTable("subgroups");
        subgroups.fields.add(new DBField("subgroupID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        subgroups.fields.add(new DBField("groupID", FieldType.INTEGER, false, Key.NOT_A_KEY));
        subgroups.fields.add(new DBField("tutorID", FieldType.INTEGER, false, Key.NOT_A_KEY));
        subgroups.fields.add(new DBField("groupNumber", FieldType.INTEGER, false, Key.NOT_A_KEY));
        tables.add(subgroups);
        subgroups.indexes.add(new DBIndex("subg_groupid", "groupid"));
        subgroups.indexes.add(new DBIndex("subg_tutorid", "tutorid"));

        DBTable groupmaterials = new DBTable("groupmaterials");
        groupmaterials.fields.add(new DBField("subgroupID", FieldType.INTEGER, false, Key.NOT_A_KEY));
        groupmaterials.fields.add(new DBField("resourceID", FieldType.INTEGER, false, Key.NOT_A_KEY));
        tables.add(groupmaterials);

        DBTable studentgroup = new DBTable("studentgroup");
        studentgroup.fields.add(new DBField("groupID", FieldType.INTEGER, false, Key.NOT_A_KEY));
        studentgroup.fields.add(new DBField("studentid", FieldType.INTEGER, false, Key.NOT_A_KEY));
        tables.add(studentgroup);
        studentgroup.indexes.add(new DBIndex("stud_gr_groupid", "groupid"));
        studentgroup.indexes.add(new DBIndex("stud_gr_studentid", "studentid"));

        DBTable classes = new DBTable("classes");
        classes.fields.add(new DBField("classid", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        classes.fields.add(new DBField("classname", FieldType.VARCHAR, Varchar.LONG_NAME));
        classes.fields.add(new DBField("classnamekz", FieldType.VARCHAR, Varchar.LONG_NAME));
        classes.fields.add(new DBField("examID", FieldType.INTEGER, "0"));
        tables.add(classes);

        DBTable exams = new DBTable("exams");
        exams.fields.add(new DBField("examID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        exams.fields.add(new DBField("examname", FieldType.VARCHAR, Varchar.NAME));
        exams.fields.add(new DBField("question_count", FieldType.INTEGER, "0"));
        exams.indexes.add(new DBIndex("examID_index", "examID"));
        tables.add(exams);

        DBTable tickets = new DBTable("tickets");
        tickets.fields.add(new DBField("ticketID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        tickets.fields.add(new DBField("examID", FieldType.INTEGER, "0"));
        tickets.fields.add(new DBField("ticketNumber", FieldType.INTEGER, "0"));
        tickets.fields.add(new DBField("modified", FieldType.TIMESTAMP ));
        tickets.indexes.add(new DBIndex("ticketID_index", "ticketID"));
        tickets.indexes.add(new DBIndex("examID_index", "examID"));
        tables.add(tickets);

        DBTable ticket_questions = new DBTable("ticket_questions");
        ticket_questions.fields.add(new DBField("ticketQuestionID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        ticket_questions.fields.add(new DBField("ticketID", FieldType.INTEGER, "0"));
        ticket_questions.fields.add(new DBField("questionkz", FieldType.VARCHAR,  Varchar.SIMPLE_QUESTION));
        ticket_questions.fields.add(new DBField("questionru", FieldType.VARCHAR, Varchar.SIMPLE_QUESTION));
        ticket_questions.indexes.add(new DBIndex("ticketID_index", "ticketID"));
        tables.add(ticket_questions);

        DBTable resources = new DBTable("resources");
        resources.fields.add(new DBField("resourceID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        resources.fields.add(new DBField("subjectID", FieldType.INTEGER));
        resources.fields.add(new DBField("name", FieldType.VARCHAR, Varchar.NAME));
        resources.fields.add(new DBField("language", FieldType.INTEGER, false, Key.NOT_A_KEY));
        resources.fields.add(new DBField("filename", FieldType.VARCHAR, Varchar.NAME));
        resources.fields.add(new DBField("mimetype", FieldType.VARCHAR, Varchar.NAME));
        resources.fields.add(new DBField("type", FieldType.VARCHAR, Varchar.NAME));
        resources.fields.add(new DBField("tutorID", FieldType.VARCHAR, Varchar.NAME));
        resources.fields.add(new DBField("length", FieldType.INTEGER ));
        resources.fields.add(new DBField("contentID", FieldType.INTEGER));
        resources.fields.add(new DBField("booktype", FieldType.INTEGER, "0"));
        tables.add(resources);

        DBTable contents = new DBTable("contents");
        contents.fields.add(new DBField("contentID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        contents.fields.add(new DBField("resourceID", FieldType.INTEGER));
        contents.indexes.add(new DBIndex("res_id_index", "resourceID"));
        tables.add(contents);

        DBTable contentparts = new DBTable("contentparts");
        contentparts.fields.add(new DBField("contentID", FieldType.INTEGER));
        contentparts.fields.add(new DBField("partNumber", FieldType.INTEGER));
        contentparts.fields.add(new DBField("content", FieldType.MEDIUM_BLOB));
        contentparts.indexes.add(new DBIndex("cont_id_index", "contentID"));
        contentparts.indexes.add(new DBIndex("part_num_index", "partNumber"));
        tables.add(contentparts);


        DBTable settings = new DBTable("settings");
        settings.fields.add(new DBField("excellent", FieldType.INTEGER));
        settings.fields.add(new DBField("good", FieldType.INTEGER));
        settings.fields.add(new DBField("satisfactory", FieldType.INTEGER));
        settings.fields.add(new DBField("login", FieldType.VARCHAR, Varchar.LOGIN));
        settings.fields.add(new DBField("password", FieldType.VARCHAR, Varchar.PASS));
        settings.fields.add(new DBField("maxmark", FieldType.INTEGER));
        settings.fields.add(new DBField("attestat_threshold_director", FieldType.INTEGER));
        settings.fields.add(new DBField("attestat_threshold_employee", FieldType.INTEGER));
        settings.fields.add(new DBField("usesubjectball", FieldType.BOOLEAN, "0" ));
        settings.fields.add(new DBField("show_report", FieldType.BOOLEAN, "0" ));
        settings.fields.add(new DBField("show_answers", FieldType.BOOLEAN, "0" ));
        settings.fields.add(new DBField("recommend_candidates", FieldType.BOOLEAN, "0" ));
        settings.fields.add(new DBField("student_test_access", FieldType.BOOLEAN, "0" ));
        settings.fields.add(new DBField("recommend_candidates_month", FieldType.INTEGER, "0" ));
        tables.add(settings);


        DBTable registrar = new DBTable("registrar");
        registrar.fields.add(new DBField("studentid", FieldType.INTEGER));
        registrar.fields.add(new DBField("groupID", FieldType.INTEGER));
        registrar.fields.add(new DBField("markdate", FieldType.DATE));
        registrar.fields.add(new DBField("mark", FieldType.INTEGER));
        registrar.fields.add(new DBField("marktype", FieldType.INTEGER));
        registrar.fields.add(new DBField("mainTestingID", FieldType.INTEGER, "0"));
        registrar.fields.add(new DBField("testingID", FieldType.INTEGER, "0"));
        registrar.fields.add(new DBField("modified", FieldType.TIMESTAMP));
        registrar.fields.add(new DBField("status", FieldType.INTEGER));
        registrar.fields.add(new DBField("isPassed", FieldType.BOOLEAN, "0"));
        registrar.fields.add(new DBField("examID", FieldType.INTEGER, "0"));
        registrar.fields.add(new DBField("ticketID", FieldType.INTEGER, "0"));
        tables.add(registrar);
        registrar.indexes.add(new DBIndex("regisrt_studentid", "studentid"));
        registrar.indexes.add(new DBIndex("mainTestingID_index", "mainTestingID"));

        DBTable tests = new DBTable("tests");
        tests.fields.add(new DBField("testID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        tests.fields.add(new DBField("testName", FieldType.VARCHAR, 1024));
        tests.fields.add(new DBField("tutorID", FieldType.INTEGER));
        tests.fields.add(new DBField("subjectID", FieldType.INTEGER));
        tests.fields.add(new DBField("created", FieldType.DATE));
        tests.fields.add(new DBField("modified", FieldType.DATE));
        tests.fields.add(new DBField("easy", FieldType.INTEGER));
        tests.fields.add(new DBField("middle", FieldType.INTEGER));
        tests.fields.add(new DBField("difficult", FieldType.INTEGER));
        tables.add(tests);
        tests.indexes.add(new DBIndex("tests_index", "testID"));

        DBTable associatefirsttag = new DBTable("associatefirsttag");
        associatefirsttag.fields.add(new DBField("tagID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        associatefirsttag.fields.add(new DBField("questionID", FieldType.INTEGER));
        associatefirsttag.fields.add(new DBField("tag", FieldType.VARCHAR, true, Key.NOT_A_KEY, null, Varchar.QUESTION));
        tables.add(associatefirsttag);
        associatefirsttag.indexes.add(new DBIndex("associate_f_questionid", "questionid"));

        DBTable associatesecondtag = new DBTable("associatesecondtag");
        associatesecondtag.fields.add(new DBField("tagID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        associatesecondtag.fields.add(new DBField("firstTagID", FieldType.INTEGER));
        associatesecondtag.fields.add(new DBField("tag", FieldType.VARCHAR, true, Key.NOT_A_KEY, null, Varchar.QUESTION));
        tables.add(associatesecondtag);
        associatesecondtag.indexes.add(new DBIndex("associate_s_firsttag", "firsttagid"));

        DBTable closedanswers = new DBTable("closedanswers");
        closedanswers.fields.add(new DBField("answerID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        closedanswers.fields.add(new DBField("answer", FieldType.VARCHAR, Varchar.QUESTION));
        closedanswers.fields.add(new DBField("questionID", FieldType.INTEGER));
        tables.add(closedanswers);
        closedanswers.indexes.add(new DBIndex("closed_questionid", "questionid"));

        DBTable openanswers = new DBTable("openanswers");
        openanswers.fields.add(new DBField("answerID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        openanswers.fields.add(new DBField("questionID", FieldType.INTEGER));
        openanswers.fields.add(new DBField("answer", FieldType.VARCHAR, Varchar.QUESTION));
        openanswers.fields.add(new DBField("isRight", FieldType.INTEGER, "0"));
        tables.add(openanswers);
        openanswers.indexes.add(new DBIndex("open_questionid", "questionid"));

        DBTable questions = new DBTable("questions");
        questions.fields.add(new DBField("questionID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        questions.fields.add(new DBField("question", FieldType.VARCHAR, Varchar.QUESTION));
        questions.fields.add(new DBField("questionTypeID", FieldType.INTEGER));
        questions.fields.add(new DBField("difficulty", FieldType.INTEGER));
        questions.fields.add(new DBField("testID", FieldType.INTEGER));
        tables.add(questions);
        questions.indexes.add(new DBIndex("questions_testid", "testid"));

        DBTable sequencetags = new DBTable("sequencetags");
        sequencetags.fields.add(new DBField("tagID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        sequencetags.fields.add(new DBField("questionID", FieldType.INTEGER));
        sequencetags.fields.add(new DBField("number", FieldType.INTEGER));
        sequencetags.fields.add(new DBField("tag", FieldType.VARCHAR, Varchar.QUESTION));
        tables.add(sequencetags);
        sequencetags.indexes.add(new DBIndex("sequence_questionid", "questionid"));

        DBTable testimages = new DBTable("testimages");
        testimages.fields.add(new DBField("imageID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        testimages.fields.add(new DBField("image", FieldType.MEDIUM_BLOB));
        testimages.fields.add(new DBField("testID", FieldType.INTEGER));
        testimages.fields.add(new DBField("tutorID", FieldType.INTEGER));
        testimages.fields.add(new DBField("type", FieldType.INTEGER));
        testimages.fields.add(new DBField("width", FieldType.INTEGER));
        testimages.fields.add(new DBField("height", FieldType.INTEGER));
        testimages.fields.add(new DBField("signature", FieldType.VARCHAR, 64));
        testimages.fields.add(new DBField("filetype", FieldType.VARCHAR, 64));
        tables.add(testimages);
        testimages.indexes.add(new DBIndex("testimages_testid", "testid"));

        DBTable testings = new DBTable("testings");
        testings.fields.add(new DBField("testingID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        testings.fields.add(new DBField("tutorID", FieldType.INTEGER));
        testings.fields.add(new DBField("testingDate", FieldType.DATE));
        testings.fields.add(new DBField("starttime", FieldType.TIME));
        testings.fields.add(new DBField("finishtime", FieldType.TIME));
        testings.fields.add(new DBField("testingTime", FieldType.INTEGER));
        testings.fields.add(new DBField("name", FieldType.VARCHAR, Varchar.NAME));
        testings.fields.add(new DBField("mainTestingID", FieldType.INTEGER, "0"));
        testings.fields.add(new DBField("lang", FieldType.INTEGER, "0"));
        testings.fields.add(new DBField("classID", FieldType.INTEGER, "0"));
        testings.fields.add(new DBField("modified", FieldType.TIMESTAMP));
        testings.fields.add(new DBField("timeIsUp", FieldType.BOOLEAN, "0"));

//        testings.fields.add(new DBField("namekz", FieldType.VARCHAR, Varchar.NAME));
//        testings.fields.add(new DBField("nameru", FieldType.VARCHAR, Varchar.NAME));
        tables.add(testings);
        testings.indexes.add(new DBIndex("testings_mainTestingID", "mainTestingID"));


        DBTable testingstudents = new DBTable("testingstudents");
        testingstudents.fields.add(new DBField("testingID", FieldType.INTEGER));
        testingstudents.fields.add(new DBField("studentID", FieldType.INTEGER));
        testingstudents.fields.add(new DBField("status", FieldType.INTEGER, "0"));
        testingstudents.fields.add(new DBField("startTime", FieldType.TIMESTAMP));
        tables.add(testingstudents);
        testingstudents.indexes.add(new DBIndex("test_stud_testingid", "testingid"));
        testingstudents.indexes.add(new DBIndex("test_stud_studentid", "studentid"));

        DBTable testingtime = new DBTable("testingtime");
        testingtime.fields.add(new DBField("ID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        testingtime.fields.add(new DBField("studentID", FieldType.INTEGER, "0"));
        testingtime.fields.add(new DBField("mainTestingID", FieldType.INTEGER, "0"));
        testingtime.fields.add(new DBField("testingID", FieldType.INTEGER, "0"));
        testingtime.fields.add(new DBField("alltime", FieldType.INTEGER, "0"));
        testingtime.fields.add(new DBField("start", FieldType.INTEGER, "0"));
        testingtime.fields.add(new DBField("modified", FieldType.TIMESTAMP));
        tables.add(testingtime);
        testingtime.indexes.add(new DBIndex("index_studentID", "studentID"));

        DBTable testreports = new DBTable("testreports");
        testreports.fields.add(new DBField("reportid", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        testreports.fields.add(new DBField("studentID", FieldType.INTEGER));
        testreports.fields.add(new DBField("testingdate", FieldType.DATE));
        testreports.fields.add(new DBField("mark", FieldType.INTEGER));
        testreports.fields.add(new DBField("tutorID", FieldType.INTEGER));
        testreports.fields.add(new DBField("report", FieldType.MEDIUM_TEXT));
        testreports.fields.add(new DBField("xlsreport", FieldType.LONG_BLOB));
        testreports.fields.add(new DBField("testingID", FieldType.INTEGER));
        testreports.fields.add(new DBField("easy", FieldType.INTEGER, "0"));
        testreports.fields.add(new DBField("middle", FieldType.INTEGER, "0"));
        testreports.fields.add(new DBField("difficult", FieldType.INTEGER, "0"));
        testreports.fields.add(new DBField("starttime", FieldType.TIMESTAMP));
        testreports.fields.add(new DBField("finishtime", FieldType.TIMESTAMP));
        tables.add(testreports);
        testreports.indexes.add(new DBIndex("testreports_studid", "studentid"));
        testreports.indexes.add(new DBIndex("testreports_testingid", "testingid"));

        DBTable testsfortesting = new DBTable("testsfortesting");
        testsfortesting.fields.add(new DBField("testingID", FieldType.INTEGER));
        testsfortesting.fields.add(new DBField("classID", FieldType.INTEGER, "0"));
        testsfortesting.fields.add(new DBField("subjectID", FieldType.INTEGER, "0"));
        testsfortesting.fields.add(new DBField("testID", FieldType.INTEGER));
        testsfortesting.fields.add(new DBField("easy", FieldType.INTEGER));
        testsfortesting.fields.add(new DBField("middle", FieldType.INTEGER));
        testsfortesting.fields.add(new DBField("difficult", FieldType.INTEGER));
        testsfortesting.fields.add(new DBField("mainTestID", FieldType.INTEGER, "0"));
        tables.add(testsfortesting);
        testsfortesting.indexes.add(new DBIndex("testsfortesting_index", "testingID"));

        DBTable testingsforstudents = new DBTable("testingsforstudents");
        testingsforstudents.fields.add(new DBField("mainTestingID", FieldType.INTEGER, "0"));
        testingsforstudents.fields.add(new DBField("testingID", FieldType.INTEGER, "0"));
        testingsforstudents.fields.add(new DBField("studentID", FieldType.INTEGER, "0"));
        testingsforstudents.fields.add(new DBField("subjectID", FieldType.INTEGER, "0"));
        testingsforstudents.fields.add(new DBField("easy", FieldType.INTEGER, "0"));
        testingsforstudents.fields.add(new DBField("middle", FieldType.INTEGER, "0"));
        testingsforstudents.fields.add(new DBField("difficult", FieldType.INTEGER, "0"));
        tables.add(testingsforstudents);
        String[] testingsforstudentsIndexes = {"mainTestingID", "studentID"};
        testingsforstudents.indexes.add(new DBIndex("testingsforstudentsindexes", testingsforstudentsIndexes));


        DBTable messages = new DBTable("messages");
        messages.fields.add(new DBField("messageID", FieldType.INTEGER, false, Key.PRIMARY_KEY));
        messages.fields.add(new DBField("MessageRU", FieldType.VARCHAR, 1024));
        messages.fields.add(new DBField("MessageKZ", FieldType.VARCHAR, 1024));
        messages.fields.add(new DBField("MessageENG", FieldType.VARCHAR, 1024));
        tables.add(messages);

        DBTable logs = new DBTable("logs");
        logs.fields.add(new DBField("logID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        logs.fields.add(new DBField("log", FieldType.TEXT));
        logs.fields.add(new DBField("logdate", FieldType.MEDIUM_BLOB));
        tables.add(logs);

        /*
         * Описанные ниже таблицы предназначены для хранения
         * информации о загруженных в систему учебниках формата SCORM
         */

        DBTable coursestatus = new DBTable("coursestatus");
        coursestatus.fields.add(new DBField("courseID", FieldType.VARCHAR, 256));
        coursestatus.fields.add(new DBField("learnerID", FieldType.VARCHAR, 256));
        coursestatus.fields.add(new DBField("satisfied", FieldType.VARCHAR, 64));
        coursestatus.fields.add(new DBField("measure", FieldType.VARCHAR, 64));
        coursestatus.fields.add(new DBField("completed", FieldType.VARCHAR, 64));
        tables.add(coursestatus);

        DBTable objectives = new DBTable("objectives");
        objectives.fields.add(new DBField("objID", FieldType.VARCHAR, 256));
        objectives.fields.add(new DBField("learnerID", FieldType.VARCHAR, 256));
        objectives.fields.add(new DBField("satisfied", FieldType.VARCHAR, 50));
        objectives.fields.add(new DBField("measure", FieldType.VARCHAR, 50));
        objectives.fields.add(new DBField("scopeID", FieldType.VARCHAR, 256));
        tables.add(objectives);

        DBTable courseinfo = new DBTable("courseinfo");
        courseinfo.fields.add(new DBField("CourseID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        courseinfo.fields.add(new DBField("CourseTitle", FieldType.VARCHAR, Varchar.SCORM_TITLE));
        courseinfo.fields.add(new DBField("Active", FieldType.BOOLEAN));
        courseinfo.fields.add(new DBField("created", FieldType.TIMESTAMP));
        courseinfo.fields.add(new DBField("Start", FieldType.BOOLEAN));
        courseinfo.fields.add(new DBField("TOC", FieldType.INTEGER));
        courseinfo.fields.add(new DBField("resourceID", FieldType.INTEGER));
        tables.add(courseinfo);

        DBTable scocomments = new DBTable("scocomments");
        scocomments.fields.add(new DBField("ActivityID", FieldType.INTEGER));
        scocomments.fields.add(new DBField("CommentID", FieldType.INTEGER));
        scocomments.fields.add(new DBField("Comment", FieldType.TEXT));
        scocomments.fields.add(new DBField("CommentDateTime", FieldType.TIMESTAMP));
        scocomments.fields.add(new DBField("CommentLocation", FieldType.TEXT));
        tables.add(scocomments);

        DBTable usercourseinfo = new DBTable("usercourseinfo");
        usercourseinfo.fields.add(new DBField("UserID", FieldType.VARCHAR, 50));
        usercourseinfo.fields.add(new DBField("CourseID", FieldType.VARCHAR, 50));
        usercourseinfo.fields.add(new DBField("SuspendAll", FieldType.BOOLEAN));
        tables.add(usercourseinfo);


        DBTable ssp_buckettbl = new DBTable("ssp_buckettbl");
        ssp_buckettbl.fields.add(new DBField("ScoID", FieldType.VARCHAR, 50));
        ssp_buckettbl.fields.add(new DBField("CourseID", FieldType.VARCHAR, 50));
        ssp_buckettbl.fields.add(new DBField("BucketID", FieldType.VARCHAR, 50));
        ssp_buckettbl.fields.add(new DBField("BucketType", FieldType.VARCHAR, 50));
        ssp_buckettbl.fields.add(new DBField("Persistence", FieldType.VARCHAR, 50));
        ssp_buckettbl.fields.add(new DBField("Min", FieldType.VARCHAR, 50));
        ssp_buckettbl.fields.add(new DBField("Requested", FieldType.VARCHAR, 50));
        ssp_buckettbl.fields.add(new DBField("Reducible", FieldType.VARCHAR, 50));
        ssp_buckettbl.fields.add(new DBField("ActivityID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        tables.add(ssp_buckettbl);

                                                 
        DBTable ssp_bucketallocatetbl = new DBTable("ssp_bucketallocatetbl");
        ssp_bucketallocatetbl.fields.add(new DBField("CourseID", FieldType.VARCHAR, 50));
        ssp_bucketallocatetbl.fields.add(new DBField("SCOID", FieldType.VARCHAR, 50));
        ssp_bucketallocatetbl.fields.add(new DBField("LearnerID", FieldType.VARCHAR, 50));
        ssp_bucketallocatetbl.fields.add(new DBField("BucketID", FieldType.VARCHAR, 50));
        ssp_bucketallocatetbl.fields.add(new DBField("BucketType", FieldType.VARCHAR, 50));
        ssp_bucketallocatetbl.fields.add(new DBField("Persistence", FieldType.INTEGER));
        ssp_bucketallocatetbl.fields.add(new DBField("Min", FieldType.INTEGER));
        ssp_bucketallocatetbl.fields.add(new DBField("Requested", FieldType.INTEGER));
        ssp_bucketallocatetbl.fields.add(new DBField("Reducible", FieldType.BOOLEAN));
        ssp_bucketallocatetbl.fields.add(new DBField("Status", FieldType.INTEGER));
        ssp_bucketallocatetbl.fields.add(new DBField("AttemptID", FieldType.VARCHAR, 50));
        ssp_bucketallocatetbl.fields.add(new DBField("ManagedBucketIndex", FieldType.INTEGER));
        ssp_bucketallocatetbl.fields.add(new DBField("ReallocateFailure", FieldType.BOOLEAN));
        ssp_bucketallocatetbl.fields.add(new DBField("ActivityID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        tables.add(ssp_bucketallocatetbl);


        DBTable iteminfo = new DBTable("iteminfo");
        iteminfo.fields.add(new DBField("CourseID", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("OrganizationIdentifier", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("ItemIdentifier", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("ResourceIdentifier", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("Launch", FieldType.TEXT));
        iteminfo.fields.add(new DBField("Type", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("Title", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("ParameterString", FieldType.TEXT));
        iteminfo.fields.add(new DBField("PersistState", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("DataFromLMS", FieldType.TEXT));
        iteminfo.fields.add(new DBField("MinNormalizedMeasure", FieldType.VARCHAR, 50));
        iteminfo.fields.add(new DBField("AttemptAbsoluteDurationLimit", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("TimeLimitAction", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("CompletionThreshold", FieldType.VARCHAR, Varchar.NAME));
        iteminfo.fields.add(new DBField("Next", FieldType.BOOLEAN));
        iteminfo.fields.add(new DBField("Previous", FieldType.BOOLEAN));
        iteminfo.fields.add(new DBField("isExit", FieldType.BOOLEAN));
        iteminfo.fields.add(new DBField("ExitAll", FieldType.BOOLEAN));
        iteminfo.fields.add(new DBField("Abandon", FieldType.BOOLEAN));
        iteminfo.fields.add(new DBField("ActivityID", FieldType.INTEGER));
        iteminfo.fields.add(new DBField("Suspend", FieldType.BOOLEAN));
        tables.add(iteminfo);

        DBTable options = new DBTable("options");
        options.fields.add(new DBField("name", FieldType.VARCHAR, 256));
        options.fields.add(new DBField("value", FieldType.VARCHAR, 256));
        tables.add(options);

        DBTable userinfo = new DBTable("userinfo");
        userinfo.fields.add(new DBField("UserID", FieldType.VARCHAR, 256));
        userinfo.fields.add(new DBField("LastName", FieldType.VARCHAR, 256));
        userinfo.fields.add(new DBField("FirstName", FieldType.VARCHAR, 256));
        userinfo.fields.add(new DBField("Admin", FieldType.BOOLEAN));
        userinfo.fields.add(new DBField("Password", FieldType.VARCHAR, 256));
        userinfo.fields.add(new DBField("Active", FieldType.BOOLEAN));
        userinfo.fields.add(new DBField("AudioLevel", FieldType.VARCHAR, 256));
        userinfo.fields.add(new DBField("AudioCaptioning", FieldType.INTEGER));
        userinfo.fields.add(new DBField("DeliverySpeed", FieldType.VARCHAR, 256));
        userinfo.fields.add(new DBField("Language", FieldType.VARCHAR, 256));
        tables.add(userinfo);


        /*
        * Описанные ниже таблицы предназначены для хранения данных форума
        * Таблицы:  forumparts - разделы форума
        *           forumthemes - темы разделов
        *           forummessages - сообщения тем
        *           forumvisitedthemes - посещения тем
        */
        DBTable forumparts = new DBTable("forumparts");
        forumparts.fields.add(new DBField("PartID",  FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        forumparts.fields.add(new DBField("Title", FieldType.VARCHAR, 256));
        forumparts.fields.add(new DBField("CreatedDate", FieldType.TIMESTAMP));
        forumparts.fields.add(new DBField("AuthorID", FieldType.INTEGER));
        forumparts.fields.add(new DBField("RoleID", FieldType.INTEGER));
        forumparts.fields.add(new DBField("ClassID", FieldType.INTEGER));
        forumparts.fields.add(new DBField("lastmessagedate", FieldType.TIMESTAMP));
        tables.add(forumparts);
        String[] partIndexes = {"AuthorID", "RoleID", "ClassID"};
        forumparts.indexes.add(new DBIndex("partindexes", partIndexes));

        DBTable forumvisitedparts = new DBTable("forumvisitedparts");
        forumvisitedparts.fields.add(new DBField("userid", FieldType.INTEGER));
        forumvisitedparts.fields.add(new DBField("roleid", FieldType.INTEGER));
        forumvisitedparts.fields.add(new DBField("partid", FieldType.INTEGER));
        forumvisitedparts.fields.add(new DBField("visitedtime", FieldType.TIMESTAMP));
        tables.add(forumvisitedparts);
        String[] forumVPIndexes = {"userid", "roleid", "partid"};
        forumvisitedparts.indexes.add(new DBIndex("partIndexes", forumVPIndexes));

        DBTable forumthemes  = new DBTable("forumthemes");
        forumthemes.fields.add(new DBField("ThemeID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        forumthemes.fields.add(new DBField("PartID", FieldType.INTEGER));
        forumthemes.fields.add(new DBField("Title", FieldType.VARCHAR, 256));
        forumthemes.fields.add(new DBField("AuthorID", FieldType.INTEGER));
        forumthemes.fields.add(new DBField("RoleID", FieldType.INTEGER));
        forumthemes.fields.add(new DBField("CreatedDate", FieldType.TIMESTAMP));
        forumthemes.fields.add(new DBField("LastMessageDate", FieldType.TIMESTAMP));
        forumthemes.fields.add(new DBField("lastmessageID", FieldType.INTEGER));
        tables.add(forumthemes);
        String[] themeIndexes = {"AuthorID", "RoleID", "lastmessageID"};
        forumthemes.indexes.add(new DBIndex("themeIndexes", themeIndexes));

        DBTable forummessages = new DBTable("forummessages");
        forummessages.fields.add(new DBField("MessageID", FieldType.INTEGER, false, Key.AUTO_INCREMENT_KEY));
        forummessages.fields.add(new DBField("AuthorID", FieldType.INTEGER));
        forummessages.fields.add(new DBField("RoleID", FieldType.INTEGER));
        forummessages.fields.add(new DBField("CreatedDate", FieldType.TIMESTAMP));
        forummessages.fields.add(new DBField("ThemeID", FieldType.INTEGER));
        forummessages.fields.add(new DBField("MessageBody", FieldType.TEXT));
        forummessages.fields.add(new DBField("Title", FieldType.VARCHAR, 256));
        tables.add(forummessages);
        String[] messageIndexes = {"AuthorID", "ThemeID"};
        forummessages.indexes.add(new DBIndex("messageIndexes", messageIndexes));

        DBTable forumvisitedthemes = new DBTable("forumvisitedthemes");
        forumvisitedthemes.fields.add(new DBField("UserID", FieldType.INTEGER));
        forumvisitedthemes.fields.add(new DBField("RoleID", FieldType.INTEGER));
        forumvisitedthemes.fields.add(new DBField("ThemeID", FieldType.INTEGER));
        forumvisitedthemes.fields.add(new DBField("VisitedTime", FieldType.TIMESTAMP));
        tables.add(forumvisitedthemes);
        String[] forumVThIndexes = {"ThemeID", "UserID", "RoleID"};
        forumvisitedthemes.indexes.add(new DBIndex("visited_tur", forumVThIndexes));
    }

    public ArrayList<StringBuffer> check() {

        Connection con = null;
        Statement st = null;
        Statement st1 = null;
        ResultSet res = null;

        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            con.setAutoCommit(false);
            st = con.createStatement();

            ArrayList<DBTable> exist = new ArrayList<DBTable>();

            res = st.executeQuery("show tables");
            while (res.next()) {
                String str = res.getString(1);
                exist.add(new DBTable(str));
            }
            ArrayList<StringBuffer> description = new ArrayList<StringBuffer>();
            for (int i = 0; i < exist.size(); i++) {
                res = st.executeQuery("describe " + exist.get(i).name);
                while (res.next()) {
                    String n = res.getString(1);
                    String type = res.getString(2);
                    boolean isNull = res.getString(3).toLowerCase().equals("yes");
                    String key = res.getString(4);
                    String defaultValue = res.getString(5);
                    if (defaultValue != null && defaultValue.toLowerCase().equals("null")) {
                        defaultValue = null;
                    }
                    String extra = res.getString(6);
                    DBField field = new DBField();
                    field.setFieldName(n);
                    field.setType(type);
                    field.setDefaultValue(defaultValue);
                    if (key.toLowerCase().equals("pri")) {
                        if (extra.toLowerCase().equals("auto_increment")) {
                            field.setKey(Key.AUTO_INCREMENT_KEY);
                        } else {
                            field.setKey(Key.PRIMARY_KEY);
                        }
                    }
                    field.setNull(isNull);
                    exist.get(i).fields.add(field);
                }

            }
            for (int i = 0; i < exist.size(); i++) {
                boolean met = false;
                for (int j = tables.size() - 1; j >= 0; j--) {
                    if (tables.get(j).name.toLowerCase().equals(exist.get(i).name.toLowerCase())) {
                        exist.get(i).compare(tables.get(j), st, description);
                        tables.get(j).checkIndexes(st, res, description);
                        met = true;
                        tables.remove(j);
                        break;
                    }
                }
                if (!met) {
                    if (!exist.get(i).dropTable(st, description)) {
                        con.rollback();
                        break;
                    }
                }
            }

            for (int i = 0; i < tables.size(); i++) {
                if (!tables.get(i).create(st, description)) {
                    con.rollback();
                    break;
                }
            }

            boolean admin = false;
            res = st.executeQuery("SELECT count(*) FROM settings");
            if (res.next() && res.getInt(1)>0) {
                admin = true;
            }

            if (!admin){
                st.execute("INSERT INTO settings(login, password) VALUES ('"+trsf.getDBString(Constants.ADMINISTRATOR_LOGIN)+"', " +
                        "'"+MD5.crypt(Constants.ADMINISTRATOR_PASSWORD)+"') ");
            }
            res = st.executeQuery("SELECT count(*) FROM tutors WHERE roleID&"+Constants.ADMIN+">0");
            if (res.next() && res.getInt(1)==0){
                st.execute("INSERT INTO tutors (lastname, firstname, roleID, login, password, deleted) " +
                        " VALUES ('Administrator', 'Administrator', 6, '"+trsf.getDBString(Constants.ADMINISTRATOR_LOGIN)+"', " +
                        " '"+MD5.crypt(Constants.ADMINISTRATOR_PASSWORD)+"', 0)");
            }


            res = st.executeQuery("SELECT COUNT(*) FROM options");

            if (res.next() && res.getInt(1) == 0 ){
                st.execute("insert into options \n" +
                        "(name, value) values \n" +
                        "('coursesdir', '/home/topa/development/services/tomcat/webapps/books'), \n" +
                        "('courseunpacktmp', '/home/topa/upacktmp'), \n" +
                        "('ziptmp', '/home/topa/ziptmp'), \n" +
                        "('usersdir', '/home/topa/users')");
            }

            res = st.executeQuery("SELECT COUNT(*) FROM userinfo ");
            if (res.next() && res.getInt(1) == 0){
                st.execute("INSERT INTO userinfo (userid,   lastname, firstname, admin, password, active) " +
                        " values ('admin', 'admin', 'admin', 1, 'admin', 1)");
            }


            st1 = con.createStatement();
            res = st.executeQuery("SELECT t.testID, t.easy, t.middle, t.difficult FROM tests t \n" +
                    "  LEFT JOIN testing_all_quetions t1 ON t.testID = t1.mainTestID \n" +
                    "  WHERE t1.mainTestID IS NULL \n" +
                    "  GROUP BY t.testID");
            while(res.next()){
                st1.execute("INSERT INTO testing_all_quetions (mainTestID, testID, easy, middle, difficult) "+
                            "VALUES ("+res.getInt("testID") + ", " +
                                       res.getInt("testID") + ", " +
                                       res.getInt("easy") + ", " +
                                       res.getInt("middle") + ", " +
                                       res.getInt("difficult") + ")");
            }

            con.commit();
            con.close();
            return description;
        } catch (Exception exc) {
            Log.writeLog(exc);
            try {
                con.rollback();
            } catch (Exception e) {
                Log.writeLog(e);
            }
        }
        try {
            con.close();
        } catch (Exception exc) {
            Log.writeLog(exc);
        }
        return new ArrayList<StringBuffer>();
    }

    public synchronized boolean checkMessages(InputStream fileReader) {
        ConnectionPool pool = new ConnectionPool();
        Connection c = pool.getConnection();
        if (c == null) return false;
        try {
            Statement st = c.createStatement();
            byte[] b = new byte[fileReader.available()];
            fileReader.read(b);
            String str = new String(b, "utf8");
            int tmp = 0;
            ResultSet res;
            while (str.indexOf("_") > 0) {
                if (str.indexOf("\r") == 0)
                    str = str.substring(2, str.length());

                if (extractor.getInteger(str.substring(0, str.indexOf("|"))) == 0) {
                    tmp = 1;
                }
                String number = str.substring(tmp, str.indexOf("|"));

                tmp = 0;
                int index = 0;
                try {
                    index = new Integer(number).intValue();
                } catch (Exception e) {
                    index = new Integer(number.substring(1));
                }
                boolean exist = false;
                res = st.executeQuery("SELECT messageID FROM messages WHERE messageID=" + index);
                if (res.next()) {
                    exist = true;
                } else {
                    exist = false;
                }
                if (exist) {
                    st.execute("DELETE FROM messages WHERE messageid=" + index + "");
                }
                str = str.substring(str.indexOf("|") + 1, str.length());
                String russian = str.substring(0, str.indexOf("|"));
                str = str.substring(str.indexOf("|") + 1, str.length());
                String kazakh = str.substring(0, str.indexOf("|"));
                str = str.substring(str.indexOf("|") + 1, str.length());
                String english = str.substring(0, str.indexOf("_"));
                st.execute("INSERT INTO messages (messageID, messageRu, messageKZ, messageEng) VALUES (" + index + ", " +
                        " '" + trsf.getDBString(russian) + "'," +
                        "'" + trsf.getDBString(kazakh) + "', '" + trsf.getDBString(english) + "')");
                str = str.substring(str.indexOf("_") + 1, str.length());
            }
            MessageManager.initializeMessages();
            c.close();
            return true;
        } catch (Exception e) {
            Log.writeLog(e);
            try {
                c.close();
            } catch (Exception e1) {
                Log.writeLog(e1);
            }
        }
        return false;
    }

    private int getCode(String str) {
        if (extractor.getInteger(str).intValue() == 0) {
            if (extractor.getInteger(str.substring(1, str.length())).intValue() == 0) {
                return extractor.getInteger(str.substring(2, str.length())).intValue();
            } else {
                return extractor.getInteger(str.substring(1, str.length())).intValue();
            }
        } else {
            return extractor.getInteger(str).intValue();
        }
    }

    public ArrayList<StringBuffer> checkDataBase() {
        Statement st = null;
        Connection con = null;
        ResultSet res = null;
        try {

            ConnectionPool connectionPool = new ConnectionPool();
            con = connectionPool.getConnection();
            con.setAutoCommit(false);
            st = con.createStatement();

            ArrayList<DBTable> exist = new ArrayList<DBTable>();

            res = st.executeQuery("show tables");
            while (res.next()) {
                String str = res.getString(1);
                exist.add(new DBTable(str));
            }
            ArrayList<StringBuffer> description = new ArrayList<StringBuffer>();
            for (int i = 0; i < exist.size(); i++) {
                res = st.executeQuery("describe " + exist.get(i).name);
                while (res.next()) {
                    String n = res.getString(1);
                    String type = res.getString(2);
                    boolean isNull = res.getString(3).toLowerCase().equals("yes");
                    String key = res.getString(4);
                    String defaultValue = res.getString(5);
                    if (defaultValue != null && defaultValue.toLowerCase().equals("null")) {
                        defaultValue = null;
                    }
                    String extra = res.getString(6);
                    DBField field = new DBField();
                    field.setFieldName(n);
                    field.setType(type);
                    field.setDefaultValue(defaultValue);
                    if (key.toLowerCase().equals("pri")) {
                        if (extra.toLowerCase().equals("auto_increment")) {
                            field.setKey(Key.AUTO_INCREMENT_KEY);
                        } else {
                            field.setKey(Key.PRIMARY_KEY);
                        }
                    }
                    field.setNull(isNull);
                    exist.get(i).fields.add(field);
                }

            }
            for (int i = 0; i < exist.size(); i++) {
                boolean met = false;
                for (int j = tables.size() - 1; j >= 0; j--) {
                    if (tables.get(j).name.toLowerCase().equals(exist.get(i).name.toLowerCase())) {
                        met = true;
                        tables.remove(j);
                        break;
                    }
                }

            }

            for (int i = 0; i < tables.size(); i++) {
                description.add(new StringBuffer("table " + tables.get(i).name + "is absent."));
            }
            con.close();
            return description;
        } catch (Exception exc) {
            Log.writeLog(exc);
            try {
                con.rollback();
            } catch (Exception e) {
                Log.writeLog(e);
            }
        }
        try {
            con.close();
        } catch (Exception exc) {
            Log.writeLog(exc);
        }
        return new ArrayList<StringBuffer>();
    }

}
