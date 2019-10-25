package com.atkehui.zookeeper;

public class ZookeeperService {
	NumberUtil numberUtil = new NumberUtil();
	private zkLock zklock = new ZkDistributedLock();
	
	public void getNumber() {
		zklock.lock();
		try {
			System.out.println("订单编号"+ numberUtil.getOrderNumber());
		} finally {
			zklock.unlock();
		}
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			new Thread(() -> {
				new ZookeeperService().getNumber();
			},String.valueOf(i)).start();
		}
	}
}
