package com.navare.prashant.hospitalinventory.util;

import java.util.concurrent.Semaphore;

/**
 * Created by prashant on 23-Nov-15.
 */
public class DBLock {
    private static final DBLock INSTANCE = new DBLock();
    Semaphore mSemaphore = new Semaphore(1);
    private DBLock() {

    }
    public static DBLock getInstance() {
        return INSTANCE;
    }

    public void lock() {
        mSemaphore.acquireUninterruptibly();
    }

    public void unlock() {
        mSemaphore.release();
    }
}
