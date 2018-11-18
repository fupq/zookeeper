/**
 * 采用多线程模拟多用户分布式获取订单，测试是否有重复订单
 */
package distributedLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * @author slx
 *
 */
public class ZookeeperDistributeLockUserOperateUse implements Runnable {

	private static Logger logger = Logger.getLogger(ZookeeperDistributeLockUserOperateUse.class);
	
	private OrderNumGenerator orderNumGenerator = new OrderNumGenerator();
	
	//重入锁，锁可传递
	//private Lock lock = new ReentrantLock();
	ExtLock lock = new ZookeeperDistrbuteLock();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("多线程生成number");
		//ZookeeperDistributeLockUserOperateUse userOperate = new ZookeeperDistributeLockUserOperateUse();
		// OrderService orderService = new OrderService();
		for (int i = 0; i < 100; i++) {
			new Thread(new ZookeeperDistributeLockUserOperateUse()).start();
		}
	}


	@Override
	public void run() {
		//第一种方法：使用同步锁，this锁
		/*synchronized (this) {
			getNumber();
		}*/
		
		//第二种方法，使用自定义的分布式锁ZookeeperDistrbuteLock
		try {
			lock.getLock();
			getNumber();
			
		}catch(Exception e) {
			logger.error("获取订单号出错",e);
		}finally {
			//释放资源
			lock.unLock();
		}
	}

	public void getNumber() {
		try {
			String number = orderNumGenerator.getOrderNum();
			logger.info("线程:" + Thread.currentThread().getName() + ",生成订单id:" + number);
		} catch (Exception e) {
			logger.error("线程:" + Thread.currentThread().getName() + "获取订单时出错",e);
		} finally {
			
		}
	}
}
