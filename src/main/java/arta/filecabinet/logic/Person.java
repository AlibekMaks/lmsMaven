package arta.filecabinet.logic;

import arta.common.logic.util.Constants;
import arta.common.logic.util.Message;
import arta.common.logic.util.Date;
import arta.common.logic.db.Varchar;
import arta.common.logic.messages.MessageManager;

import java.util.Properties;


public abstract class Person {

    protected String firstname = null;
    protected String lastname = null;
    protected String patronymic = null;
    protected String adress = null;
    protected String phone = null;
    protected Date startdate;
    protected Date birthdate;
    protected int personID = -1;
    protected int roleID = 0;
    protected int ischairman = 0;
    protected int isvicechairman = 0;
    protected int ismembers = 0;
    protected int issecretary = 0;
    protected int departmentID = 0;
    public boolean isAdministrator = false;

    public void checkIsAdministrator(){
        this.isAdministrator = ((roleID & Constants.ADMIN) > 0);
    }


    protected boolean check(Message message, int lang){
        boolean result = true;
        if (lastname == null || lastname.length()==0){
            message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.ENTER_LAST_NAME, null));
            result = false;
        } else {
            if (lastname.length()>Varchar.NAME/2){
                result = false;
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, FileCabinetMessages.LAST_NAME, null));
                message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.TOO_LONG_VALUE, prop));
            }
        }
        if (firstname == null || firstname.length()==0){
            message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.ENTER_FIRST_NAME, null));
            result = false;
        } else {
            if (firstname.length()>Varchar.NAME/2){
                result = false;
                Properties prop = new Properties();
                prop.setProperty("field", MessageManager.getMessage(lang, FileCabinetMessages.FIRST_NAME, null));
                message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.TOO_LONG_VALUE, prop));
            }
        }
        if (patronymic!=null && patronymic.length()>Varchar.NAME/2){
            result = false;
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, FileCabinetMessages.PATRONYMIC, null));
            message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.TOO_LONG_VALUE, prop));
        }
        if (adress!=null && adress.length()>Varchar.NAME/2){
            result = false;
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, FileCabinetMessages.ADRESS, null));
            message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.TOO_LONG_VALUE, prop));
        }
        if (phone!=null && phone.length()>Varchar.NAME/2){
            result = false;
            Properties prop = new Properties();
            prop.setProperty("field", MessageManager.getMessage(lang, FileCabinetMessages.PHONE, null));
            message.addReason(MessageManager.getMessage(lang,  FileCabinetMessages.TOO_LONG_VALUE, prop));
        }
        return result;
    }

    public abstract int getRoleID();

  //  public abstract int getChairman();

    public abstract String getFullName();

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    public void setAdress(String adress) { this.adress = adress; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setPersonID(int personID) { this.personID = personID; }

    public String getFirstname() {
        if (firstname == null) return "";
        return firstname;
    }

    public String getLastname() {
        if (lastname == null) return "";
        return lastname;
    }

    public String getPatronymic() {
        if (patronymic == null) return "";
        return patronymic;
    }

    public String getAdress() {
        if (adress == null) return "";
        return adress;
    }

    public String getPhone() {
        if (phone == null) return "";
        return phone;
    }

    public int getPersonID() {
        return personID;
    }


    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public void setCheirman(int ischairman) { this.ischairman = ischairman;}
    public void setViceCheirman(int isvicechairman) { this.isvicechairman = isvicechairman;}
    public void setMembers(int ismembers) { this.ismembers = ismembers;}
    public void setSecretary(int issecretary) { this.issecretary = issecretary;}

    public Date getBirthdate() {
        return birthdate;
    }

    public Date getStartdate() {
        return startdate;
    }

    public abstract String getShortName();

    public static String extractName(String lastname, String firstname, String patronymic){
        StringBuffer result = new StringBuffer();
        if (lastname != null && lastname.length() > 0){
            result.append(lastname);
        }
        if (firstname != null && firstname.length() > 0){
            result.append(" ");
            result.append(firstname.substring(0,1));
            result.append(".");
            if (patronymic != null && patronymic.length() > 0){
                result.append(patronymic.substring(0, 1));
                result.append(".");
            }
        }
        return result.toString();
    }

    public abstract void loadName();

    public int getDepartmentID(){
        return this.departmentID;
    }

    public void setDepartmentID(int departmentID){
        this.departmentID = departmentID;
    }


}
