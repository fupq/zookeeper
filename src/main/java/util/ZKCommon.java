/**
 * 封装zookeeper的操作
 */
package util;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * @author slx
 *
 */
public class ZKCommon implements Watcher{

	//连接地址
	private static final String ADDRES = "192.168.1.111:2181";
	//session 会话超时时间为2000毫秒
	private static final int SESSION_OUTTIME = 2000;
	//信号量,阻塞程序执行,用户等待zookeeper连接成功,发送成功信号，countDownLatch初始值为1
	//CountDownLatch能够使一个线程在等待另外一些线程完成各自工作之后，再继续执行
	private static final CountDownLatch countDownLatch = new CountDownLatch(1);
	//定义全局的zk
	private ZooKeeper zk;
	
	/**
	 * 创建zk连接
	 * @param connectString zk服务端连接创，ip:端口
	 * @param sessionTimeout 连接超时，单位毫秒
	 * @param watcher 通知事件
	 * @return Zookeeper
	 * @throws IOException
	 */
	public ZooKeeper createZKConnection(String connectString, int sessionTimeout) throws IOException {
		try {
			zk = new ZooKeeper(connectString, sessionTimeout, this);
			//countDownLatch的值减去1
			//countDownLatch.countDown();
			// 进行阻塞，
			countDownLatch.await();//countDownLatch的值大于0时，就等待；等于0时唤醒该线程向下执行
			System.out.println("zk 启动连接...");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return zk;
	}

	/**
	 * 关闭zk链接
	 */
	public void closeZK() {
		try {
			if(zk != null) {
				zk.close();
				System.out.println("zookeeper链接成功关闭！");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 	创建持久节点
	 * @param path 节点路径
	 * @param data 节点数据
	 * @return true：创建成功，false:创建失败
	 */
	public Boolean createPersistentNode(String path,String data) {
		try {
			exists(path,true);
			String result = zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
			System.out.println("zookeeper的节点(path:"+path+",data:"+data+")‘"+result+"'创建成功！");
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	
	/**
	 * 设置zookeeper是否开启事件通知
	 * @param path 节点路径
	 * @param needWatch：true:开启监听，false:关闭监听
	 * @return
	 */
	public Stat exists(String path,boolean needWatch) {
		try {
			return this.zk.exists(path, needWatch);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 修改节点
	 * @param path
	 * @param data
	 * @return
	 */
	public Boolean updateNode(String path,String data) {
		try {
			exists(path,true);
			zk.setData(path, data.getBytes(),-1);
			System.out.println("zookeeper的节点(path:"+path+",data:"+data+")修改成功！");
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * 事件通知
	 */
	@Override
	public void process(WatchedEvent event) {
		System.out.println();
		System.out.println("************ 事件通知开始**************");
		
		// 1.获取事件状态
		KeeperState keeperState = event.getState();
		//2.获取节点的路径
		String path = event.getPath();
		// 3.获取事件类型
		EventType eventType = event.getType();
		System.out.println("*****进入process()事件通知方法，事件状态:"+keeperState+",节点的路径:"+path+",事件类型:"+eventType);
		// 4.判断为连接状态
		if (KeeperState.SyncConnected == keeperState) {
			// 4.判断连接状态是否为连接成功
			if (EventType.None == eventType) {
				//countDownLatch的值减去1
				countDownLatch.countDown();
				System.out.println("zk 启动连接...");
			}else if (EventType.NodeCreated == eventType) {
				//节点创建成功
				System.out.println("获取事件通知：zk节点（path:"+path+"）创建成功");
			}else if (EventType.NodeDataChanged == eventType) {
				//节点被修改
				System.out.println("获取事件通知：zk节点（path:"+path+"）已被修改");
			}else if (EventType.NodeDeleted == eventType) {
				//节点被删除
				System.out.println("获取事件通知：zk节点（path:"+path+"）已被删除");
			}else if (EventType.NodeChildrenChanged == eventType) {
				//子节点被删除
				System.out.println("获取事件通知：zk节点（path:"+path+"）的子节点已被删除");
			}
			System.out.println("************ 事件通知结束**************");
			System.out.println();
		}
	}
}
