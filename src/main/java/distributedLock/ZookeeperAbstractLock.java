/**
 * 分布式锁：利用zookeeper的临时节点（关闭zk的链接后节点自动删除）和事物通知机制（节点发生增删改时会通知客户端）来实现
 */
package distributedLock;

import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;

public abstract class ZookeeperAbstractLock implements ExtLock {

	private static Logger logger = Logger.getLogger(ZookeeperAbstractLock.class);
	
	// 集群连接地址
	protected String CONNECTION = "192.168.1.111:2181";
	// zk客户端连接，需要引入zkClient的依赖
	protected ZkClient zkClient = new ZkClient(CONNECTION);
	// path路径
	protected String lockPath = "/zkDistributedLock";
	protected CountDownLatch countDownLatch = new CountDownLatch(1);
	
	/**
	 * 获取分布式锁
	 */
	@Override
	public void getLock() {
		if (tryLock()) {
			logger.info("####获取锁成功######");
		} else {
			waitLock();//等待分布式锁
			getLock();
		}
	}

	
	/**
	 * 获取锁;抽象方法，具体实现由子类完成，zookeeper的事物通知
	 * @return true:已经获取到分布式锁，false:未获取到锁
	 */
	abstract boolean tryLock();

	/**
	 * 等待锁;抽象方法，具体实现由子类完成，zookeeper的事物通知
	 */
	abstract void waitLock();
		
	/**
	 *  释放分布式锁
	 */
	@Override
	public void unLock() {
		if (zkClient != null) {
			logger.info("#######释放锁#########");
			zkClient.close();
		}
	}

}
