package com.atkehui.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.ZkClient;
//自定义分布式锁 名字规范
public abstract class zkAbstractTemplateLock implements zkLock{
	public static final String ZKSERVER = "192.168.234.188";
	public static final int TIME_OUT = 30 * 1000 ;//连接超时时间
	ZkClient zkClient = new ZkClient(ZKSERVER,TIME_OUT);//连接zookeeper服务器
	
	protected String path = "/myZKLock";//用于创建znode节点的 key
	protected CountDownLatch CountDownLatch = null;
	
	@Override
	public void lock() {
		if (tryZKLock()) {//如果创建节点成功
			System.out.println(Thread.currentThread().getName()+"\t 抢占锁成功！");
		}else {
			waitZKLock();//如果没有抢到锁就等待
			
			lock();//继续排队抢占锁
		}
	}
	
	//模板设计模式，固定化的流程升级到父类定死规范，但是，具体落地实现方法下放给子类各自实现。

	public abstract boolean tryZKLock();
	public abstract void waitZKLock();

	@Override
	public void unlock() {
		if (zkClient != null) {
			zkClient.close();//等价于在zk服务器上执行quit退出命令
		}
		System.out.println(Thread.currentThread().getName()+"\t 释放锁成功！");
	}

}
