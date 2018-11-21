/**
 * zookeeper实现负责均衡：服务端。
 *   服务器端将启动的服务注册到，zk注册中心上，采用临时节点。客户端从zk节点上获取最新服务节点信息，本地使用负载均衡算法，随机分配服务器。
 */
package zkBalence.zkServer;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author slx
 *
 */
public class ServerScoekt implements Runnable{

	private static int port = 18080;
	
	public ServerScoekt(int port) {
		this.port = port;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//int port = 18080;
		ServerScoekt server = new ServerScoekt(port);
		Thread thread = new Thread(server);
		thread.start();

	}


	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
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
