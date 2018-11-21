/**
 * zookeeper实现负责均衡：客户端
 * 
 */
package zkBalence.zkClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.log4j.Logger;

/**
 * @author slx
 *
 */
public class ZkServerClient {
	
	private static Logger logger = Logger.getLogger(ZkServerClient.class);
	
	public static List<String> listServer = new ArrayList<String>();

	/**
	 * 同步锁对象
	 */
	private static String str = new String();
	
	/**
	 * 请求数
	 */
	private static long requestCount = 1L;
	
	public static void main(String[] args) {
		initServer();
		ZkServerClient 	client= new ZkServerClient();
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String name;
			try {
				name = console.readLine();
				if ("exit".equals(name)) {
					System.exit(0);
				}
				client.send(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 注册所有server
	public static void initServer() {
		listServer.clear();
		//listServer.add("127.0.0.1:18080");
		final ZkClient client = new ZkClient("192.168.1.111:2181", 60000, 1000);
		//读取集群服务器下的服务器在zookeeper上的注册的所有服务信息
		String parent = "/server";
		List<String> children = client.getChildren(parent);
		for(String chidPath:children) {
			listServer.add((String)client.readData(parent+"/"+chidPath));
		}
		logger.info("******* listServer:"+listServer.toString());
		//监听zookeeper当节点变化时需要更新变化点
		client.subscribeChildChanges(parent, new IZkChildListener() {
			
			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				listServer.clear();
				for(String chidPath:currentChilds) {
					listServer.add((String)client.readData(parentPath+"/"+chidPath));
				}
				logger.info("******* 事件通知 listServer:"+listServer.toString());
			}
		});
	}

	// 获取当前server信息
	public static String getServer() {
		//负责均衡轮休算法：请求数%服务数
		int serverCount = listServer.size();
		int index = (int) (requestCount%serverCount);
		String serverHost = listServer.get(index);
		//高并发时，进行现场安全的处理
		synchronized (str) {
			requestCount++;
		}
		return serverHost;
	}
	
	public void send(String name) {

		String server = ZkServerClient.getServer();
		String[] cfg = server.split(":");

		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			socket = new Socket(cfg[0], Integer.parseInt(cfg[1]));
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			out.println(name);
			while (true) {
				String resp = in.readLine();
				if (resp == null)
					break;
				else if (resp.length() > 0) {
					System.out.println("Receive : " + resp);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
