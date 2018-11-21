/**
 * 
 */
package zkBalence;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

/**
 * @author slx
 *
 */
public class JavaZookeeper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ZkClient zkClient = new ZkClient("192.168.1.111",600000,10000); 
		zkClient.createPersistent("/server", "zookeeper let balence tobe real!");
		//zkClient.create("/balence/201811202355", "zookeeper let balence to reale",CreateMode.PERSISTENT);
		zkClient.close();
	}

}
