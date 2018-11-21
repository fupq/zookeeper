/**
 * zookeeper实现负责均衡：服务端。
 *   服务器端将启动的服务注册到，zk注册中心上，采用临时节点。客户端从zk节点上获取最新服务节点信息，本地使用负载均衡算法，随机分配服务器。
 */
package zkBalence.zkServer;

import java.net.ServerSocket;
import java.net.Socket;

import org.I0Itec.zkclient.ZkClient;

/**
 * @author slx
 *
 */
public class ZkServerScoekt implements Runnable{

	private static int port = 8094;
	
	public ZkServerScoekt(int port) {
		this.port = port;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//int port = 18080;
		ZkServerScoekt server = new ZkServerScoekt(port);
		Thread thread = new Thread(server);
		thread.start();

	}

	/**
	 * 把启动的服务(ip:port)注册到zookeeper中
	 */
	public void regServer() {
		// 向ZooKeeper注册当前服务器
		ZkClient client = new ZkClient("192.168.1.111:2181", 60000, 1000);
		String parent = "/server";
		String path = parent+"/server" + port;
		if (client.exists(path))
			client.delete(path);
		client.createEphemeral(path, "127.0.0.1:" + port);
	}


	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			regServer();
			System.out.println("Server start port:" + port);
			Socket socket = null;
			while (true) {
				socket = serverSocket.accept();
				new Thread(new ServerHandler(socket)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (Exception e2) {

			}
		}
	}


}
