package arta.common.lock;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 27.01.2008
 * Time: 8:59:02
 * To change this template use File | Settings | File Templates.
 */
public class LockObject {

    public Object objectID;

    public int threadsCount = 0;

    public long time=0;
}
