/**
 * 
 */
package com.mantis.net.thread;

import java.io.File;
import java.util.Vector;

/**
 * @author ywang 2014年4月22日
 */
public class GZipAllFiles {

	public final static int THREAD_COUNT = 2;

	private static int filesToBeCompressed = -1;

	public static void main(String[] args) {
		Vector<File> pool = new Vector<File>();
		GZipThread[] threads = new GZipThread[THREAD_COUNT];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new GZipThread(pool);
			threads[i].start();
		}

		int totalFiles = 0;
		for (int i = 0; i < threads.length; i++) {
			File f = new File(args[i]);
			if (f.exists()) {
				if (f.isDirectory()) {
					File[] files = f.listFiles();
					for (int j = 0; j < files.length; j++) {
						if (!files[j].isDirectory()) { // 不递归处理目录
							totalFiles++;
							synchronized (pool) {
								pool.add(0, files[j]);
								pool.notifyAll();
							}
						}
					}
				}
			} else {
				totalFiles++;
				synchronized (pool) {
					pool.add(0, f);
					pool.notifyAll();
				}
			}
		}

		filesToBeCompressed = totalFiles;

		// 确保让所有等待线程知道没有更多文件会添加到pool中
		for (int i = 0; i < threads.length; i++) {
			threads[i].interrupt();
		}
	}

	/**
	 * @return
	 */
	public static int getNumberOfFilesToBeCompressed() {
		return filesToBeCompressed;
	}

}
