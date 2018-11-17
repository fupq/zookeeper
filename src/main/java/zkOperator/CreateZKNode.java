/**
 * 
 */
package zkOperator;

import java.io.IOException;

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
public class CreateZKNode {

	//连接地址
	private static final String ADDRES = "192.168.1.111:2181";
	//session 会话超时时间为2000毫秒
	private static final int SESSION_OUTTIME = 2000;

		
	/** 使用java代码创建到ZK的链接，然后创建ZK节点
	 * @param args
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, KeeperException, IOException {
		System.out.println("开始创建链接到zookeeper的客户端链接！");
		ZooKeeper zk = new ZooKeeper(ADDRES, SESSION_OUTTIME, new Watcher() {
			public void process(WatchedEvent event) {
				// 获取事件状态
				KeeperState keeperState = event.getState();
				// 获取事件类型
				EventType eventType = event.getType();
				if (KeeperState.SyncConnected == keeperState) {
					if (EventType.None == eventType) {//zk的事件类型为null,表示启动zk
						for(int i=0;i<10;i++) {
							try {
								Thread.sleep(1000L);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(i+"秒");
						}
						System.out.println("zk 启动连接...");
					}
				}
			}
		});

		System.out.println("zookeeper链接成功关闭！");
		String result = zk.create("/fpq_createZKNode3", "使用java代码创建ZK节点".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);//设置创建的zk节点是持久节点CreateMode.PERSISTENT
		System.out.println("zookeeper的节点‘"+result+"'创建成功！，开始关闭ZK链接");
		zk.close();
		System.out.println("zookeeper链接成功关闭！");

	}

}
