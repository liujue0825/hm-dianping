package com.hmdp.utils;

public interface ILock {

    /**
     * 尝试获取锁
     * @param timeoutSec:锁持有的过期时间
     * @return:true表示获取到锁，false表示获取锁失败
     */
    public boolean tryLock(long timeoutSec);

    /**
     * 删除锁
     */
    public void unlock();
}
