/**
 * 临时节点在zk客户端连接关闭后会自动被删除掉
 */
package zkOperator;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author slx
 *
 */
public class CreateZKNodeBlockTemp {

	//连接地址
	private static final String ADDRES = "192.168.1.111:2181";
	//session 会话超时时间为2000毫秒
	private static final int SESSION_OUTTIME = 2000;
	//信号量,阻塞程序执行,用户等待zookeeper连接成功,发送成功信号，countDownLatch初始值为1
	//CountDownLatch能够使一个线程在等待另外一些线程完成各自工作之后，再继续执行
	private static final CountDownLatch countDownLatch = new CountDownLatch(1);


		
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, KeeperException, IOException {
		ZooKeeper zk = new ZooKeeper(ADDRES, SESSION_OUTTIME, new Watcher() {

			public void process(WatchedEvent event) {
				// 获取事件状态
				KeeperState keeperState = event.getState();
				// 获取事件类型
				EventType eventType = event.getType();
				if (KeeperState.SyncConnected == keeperState) {
					if (EventType.None == eventType) {
						for(int i=0;i<10;i++) {
							try {
								Thread.sleep(1000L);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(i+"秒");
						}
						//countDownLatch的值减去1
						countDownLatch.countDown();
						System.out.println("zk 启动连接...");
					}

				}
			}
		});
		// 进行阻塞，
		countDownLatch.await();//countDownLatch的值大于0时，就等待；等于0时唤醒该线程向下执行
		System.out.println("开始创建zookeeper的临时节点");
		String result = zk.create("/fpq_tempNode", "java语言创建zk临时节点".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);
		//CreateMode.EPHEMERAL:临时节点
		//CreateMode.EPHEMERAL_SEQUENTIAL:临时顺序节点
		//CreateMode.PERSISTENT：持久节点
		//CreateMode.PERSISTENT_SEQUENTIAL：持久顺序节点
		//Ids.OPEN_ACL_UNSAFE:所有ip都可访问
		System.out.println("zookeeper的节点‘"+result+"'创建成功！，开始关闭ZK链接");
		for(int i=0;i<10;i++) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(i+"秒");
		}
		zk.close();
		System.out.println("zookeeper链接成功关闭！");

	}

}
