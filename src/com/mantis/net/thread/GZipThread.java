/**
 * 
 */
package com.mantis.net.thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * @author ywang 2014年4月22日
 */
public class GZipThread extends Thread {

	private List<File> pool;
	private static int filesCompressed = 0;

	public GZipThread(List<File> pool) {
		this.pool = pool;
	}

	private static synchronized void incrementFilesCompressed() {
		filesCompressed++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		while (filesCompressed != GZipAllFiles.getNumberOfFilesToBeCompressed()) {
			File input = null;

			synchronized (pool) {
				while (pool.isEmpty()) {
					if (filesCompressed == GZipAllFiles
							.getNumberOfFilesToBeCompressed()) {
						System.out.println("Thread ending");
						return;
					}

					try {
						pool.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				input = (File) pool.remove(pool.size() - 1);
				incrementFilesCompressed();
			}

			//不压缩已经压缩过的文件
			if (!input.getName().endsWith(".gz")) {
				try {

					InputStream in = new FileInputStream(input);
					in = new BufferedInputStream(in);

					File output = new File(input.getParent(), input.getName()
							+ ".gz");
					if (!output.exists()) {//不覆盖已经存在的文件
						OutputStream out = new FileOutputStream(output);
						out = new GZIPOutputStream(out);
						out = new BufferedOutputStream(out);

						int b;
						while ((b = in.read()) != -1) {
							out.write(b);
						}

						out.flush();
						out.close();
						in.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
