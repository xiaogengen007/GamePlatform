package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

public class ReadTxt {
	public static ReadTxt myRead = null;
	private static String filePath = "src/main/resources/wordsForUndercover.txt";
	ReadTxt() {
		this.readTxtFile();
	}
	
	/*
	 * 单子模式，获取一个实体
	 */
	public static ReadTxt getInstance() {
		if (myRead == null) {
			myRead = new ReadTxt();
		}
		return myRead;
	}
	
	private Vector<String> readTxtFile() {
		Vector<String> vv = new Vector<String>();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 读取文件
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					vv.add(lineTxt);
				}
				read.close();
			} else {
				System.out.println("cannot found file!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vv;
	}

	public String getRandomConent() {
		Vector<String> vv = readTxtFile();
		int len = vv.size();
		//System.out.println(len);
		Random r = new Random();
		int now = r.nextInt(len);
		String content = vv.get(now);
		return content;
	}

	public static void main(String argv[]) {
		ReadTxt rt = ReadTxt.getInstance();
		System.out.println(rt.getRandomConent());
	}
}
