package com.bigyj.pool.factory;

import com.bigyj.pool.rejected.AbortRejectedPolicy;

import java.util.concurrent.RejectedExecutionHandler;

public class RejectedExecutionHandlerFactory {
    public static RejectedExecutionHandler getRejectedExecutionHandler(String type,String name){
        return new AbortRejectedPolicy(name);
    }
}
