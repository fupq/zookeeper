/**
 *  定义ZookeeperAbstractLock的子类，完成获取锁和等待锁的具体实现
 */
package distributedLock;

import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.IZkDataListener;
import org.apache.log4j.Logger;

public class ZookeeperDistrbuteLock extends ZookeeperAbstractLock {

	private static Logger logger = Logger.getLogger(ZookeeperDistrbuteLock.class);
	
	/**
	 * 获取锁
	 */
	@Override
	boolean tryLock() {
		try {
			//创建临时节点
			zkClient.createEphemeral(lockPath);
			return true;
		}catch(Exception e) {
			//logger.error("创建临时节点失败",e);
			return false;
		}
	}

	/**
	 * 等待锁
	 */
	@Override
	void waitLock() {
		/**
		 * 使用zkClient中的监听接口（对zookeeper的API的封装）来实现事件通知
		 */
		IZkDataListener iZKDataListener = new IZkDataListener() {

			/**
			 *  节点改变时触发此方式，进行执行操作
			 */
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				
			}

			/**
			 *  节点被删除时触发此方式，进行执行操作
			 */
			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				if(countDownLatch != null) {
					//将count的值减去1，唤醒操作
					countDownLatch.countDown();
				}
			}
			
		};
		//注册节点lockPath到zkClient的事件监听中，申请被事件监听
		zkClient.subscribeDataChanges(lockPath, iZKDataListener);
		if(zkClient.exists(lockPath)) {
			//如果节点在lockPath存在，则创建信号量，设置值为1，
			countDownLatch = new CountDownLatch(1);
			try {
				//进行等待,等待唤醒
				countDownLatch.await();
			}catch(Exception e) {
				logger.error("等待唤醒出错",e);
			}
		}
		//将节点lockPath从zkClient的监听中删除掉，不再监听该节点
		zkClient.unsubscribeDataChanges(lockPath, iZKDataListener);
	}

}
