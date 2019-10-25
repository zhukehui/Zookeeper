package com.atkehui.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.IZkDataListener;

/**
 * @author eternity
 * @create 2019-10-22 0:14
 *
 * 1 引入pom
 *    <!-- zookeeper -->
 *         <dependency>
 *             <groupId>com.101tec</groupId>
 *             <artifactId>zkclient</artifactId>
 *             <version>0.10</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.apache.zookeeper</groupId>
 *             <artifactId>zookeeper</artifactId>
 *             <version>3.4.9</version>
 *         </dependency>
 *
 * 2  启动zk服务器并保证防火墙关闭，client（win10）可以联通server（Linux上面的zk服务器）
 * 	2.1 ifconfig
 * 	2.2 ipconfig
 *
 *
 * 3  设计实现思想讲解
 *
 *    利用zk的znode节点唯一性，我们生成临时节点完成唯一性标识。
 *
 * 4   建立接口
 * 	public interface ZkLock
 *
 */
public class ZkDistributedLock extends zkAbstractTemplateLock{

	@Override
	public boolean tryZKLock() {
		try {
			zkClient.createEphemeral(path);//创建临时节点，如果创建成功返回true
			return true;
		} catch (Exception e) {
			//e.printStackTrace();//如果有异常，报临时节点已存在的异常
			return false;//如果没有创建成功说明临时节点已存在
		}
	}

	@Override
	public void waitZKLock() {
		IZkDataListener iZkDataListener = new IZkDataListener() {
			
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				if (CountDownLatch != null) {
					CountDownLatch.countDown();//相当于倒计时，每次减一
				}
			}
		};
		zkClient.subscribeDataChanges(path, iZkDataListener);//监控这个节点，有了就等待 handleDataDeleted(String s)删除，没有就马上抢
		if (zkClient.exists(path)) {//如果这个节点已经存在
			
			 //不能干什么事情，必须等待path也即临时节点被删除后才能继续向下运行

            //？？？怎么让程序被卡在这里？？？
			CountDownLatch = new CountDownLatch(1);
			
			try {
				CountDownLatch.await();//等待（不见不散），等到上面线程执行完才执行/等待（不见不散），等到上面线程执行完才执行
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			zkClient.unsubscribeDataChanges(path, iZkDataListener);//释放
		}
	}

}
