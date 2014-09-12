package arta.tests.testing.logic;


public class SimpleExaminer {

    public int examinerID = 0;
    public String lastname = "";
    public String firstname = "";
    public String patronymic = "";

    public String getFullName() {
        StringBuffer fullName = new StringBuffer();
        if (lastname != null){
            fullName.append(lastname);
            fullName.append(" ");
        }
        if (firstname!=null){
            fullName.append(firstname);
            fullName.append(" ");
            if (patronymic!=null){
                fullName.append(patronymic);
            }
        }
        return fullName.toString();
    }

}
