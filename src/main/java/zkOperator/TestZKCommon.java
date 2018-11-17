/**
 * 
 */
package zkOperator;

import java.io.IOException;

import util.ZKCommon;

/**
 * @author slx
 *
 */
public class TestZKCommon {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ZKCommon zkc = new ZKCommon();
		try {
			zkc.createZKConnection("192.168.1.111", 2000);
			zkc.createPersistentNode("/fpq/perZKC6", "fpq:通过封装的工具创建zk节点");
			zkc.updateNode("/fpq/perZKC6", "fpq:通过封装的工具修改zk节点update");
			/*try {
				Thread.sleep(60000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			zkc.closeZK();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
