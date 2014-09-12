package arta.registrar.tutor.logic;

import java.util.Comparator;


public class DayComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        if (((JournalHeaderItem)o1).day > ((JournalHeaderItem)o2).day)
            return 1;
        if (((JournalHeaderItem)o1).day < ((JournalHeaderItem)o2).day)
            return -1;
        return 0;
    }
}
