package arta.registrar.tutor.logic;

import java.util.Comparator;


public class MarksComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        /*if (((RegistrarDay)o1).date.compare(((RegistrarDay)o2).date) > 0)
            return 1;
        if (((RegistrarDay)o1).date.compare(((RegistrarDay)o2).date) == 0)
            if (((RegistrarDay)o1).type > ((RegistrarDay)o2).type)
                return 1;
            else if (((RegistrarDay)o1).type == ((RegistrarDay)o2).type)
                return 0;*/
        return -1;
    }
}
