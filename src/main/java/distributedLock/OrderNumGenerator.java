/**
 * 分布式事物所
 */
package distributedLock;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 分布式成更幂等性的订单号（不重复）
 * @author slx
 *
 */
public class OrderNumGenerator {

	// 生成订单号规则
	private static long count = 0L;
		
	public String getOrderNum() {
		SimpleDateFormat simpt = new SimpleDateFormat("yyyyMMddHHmmss");
		return simpt.format(new Date()) + "-" + ++count;
	}
}
