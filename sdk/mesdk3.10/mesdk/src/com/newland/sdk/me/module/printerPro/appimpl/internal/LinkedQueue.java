package com.newland.sdk.me.module.printerPro.appimpl.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/7/17
 */
public class LinkedQueue {

    private List mLinkedQueue;

    private Object mLinkedQueueSync = new Object();

    public LinkedQueue(){
        mLinkedQueue = new ArrayList();
    }

    public boolean isEmpty(){
        synchronized (mLinkedQueueSync){
            return mLinkedQueue.isEmpty();
        }
    }

    public int size(){
        synchronized (mLinkedQueueSync) {
            return mLinkedQueue.size();
        }
    }

    public boolean add(Object object){
        synchronized (mLinkedQueueSync) {
            return mLinkedQueue.add(object);
        }
    }

    public Object poll(){
        synchronized (mLinkedQueueSync) {
//            return mLinkedQueue.poll();
            Object object = mLinkedQueue.get(0);
            mLinkedQueue.remove(0);
            return object;
        }
    }

    public void clear(){
        synchronized (mLinkedQueueSync) {
            mLinkedQueue.clear();
        }
    }
}
