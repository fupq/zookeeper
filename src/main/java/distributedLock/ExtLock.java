/**
 * 
 */
package distributedLock;

/**
 * @author slx
 *
 */
public interface ExtLock {

	// 获取锁
	public void getLock();

	// 释放锁
	public void unLock();
}
