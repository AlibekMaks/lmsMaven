package arta.auth.logic;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 12.03.2008
 * Time: 15:09:01
 * To change this template use File | Settings | File Templates.
 */
public class SessionListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        Hashtable hashtable = (Hashtable)session.getServletContext().getAttribute("users");
        if (hashtable != null){
            hashtable.remove(session.getId());
        }
    }
}
