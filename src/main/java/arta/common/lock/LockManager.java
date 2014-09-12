package arta.common.lock;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: topa
 * Date: 27.01.2008
 * Time: 8:59:41
 * To change this template use File | Settings | File Templates.
 */
public class LockManager {

    private ArrayList<LockObject> objects = new ArrayList<LockObject>();

    public void execute(Object newObjectID){
        boolean needWait = false;
        LockObject object = null;
        synchronized(objects){
            for (int i=0; i < objects.size(); i++){
                if (objects.get(i).objectID.equals(newObjectID)){
                    objects.get(i).threadsCount++ ;
                    needWait = true;
                    object = objects.get(i);
                    break;
                }
            }
            if (!needWait){
                LockObject lockObject = new LockObject();
                lockObject.objectID = newObjectID;
                lockObject.threadsCount = 1;
                lockObject.time = System.nanoTime();
                objects.add(lockObject);
            }
        }
        if (needWait){
            System.out.println("Thread "+Thread.currentThread().getName()+" is waiting...");
            try{
                synchronized(object){
                    object.wait();
                }
            } catch (Exception exc){
                exc.printStackTrace();
            }
            System.out.println("Thread "+Thread.currentThread().getName()+" awaked ");
        }
    }

    public void finished(Object objectID){
        LockObject object = null;
        synchronized(objects){
            for (int i=0; i < objects.size(); i++){
                if (objects.get(i).objectID.equals(objectID)){
                    objects.get(i).threadsCount --;
                    if (objects.get(i).threadsCount == 0){
                        objects.remove(i);
                        break;
                    } else {
                        object = objects.get(i);
                    }
                }
            }
        }
        if (object != null){
            synchronized(object){
                System.out.println("Try to notify ");
                object.notify();
            }
        }
    }

}
