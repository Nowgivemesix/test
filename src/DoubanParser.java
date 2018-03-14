package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class DoubanParser {
	public static final String url = "https://book.douban.com/tag/%E7%BC%96%E7%A8%8B?type=S&start=$currentPage";//爬取的URL地址

	public DoubanParser() {

	}

	public synchronized void next(int currentPage) {
		String content = null;
		System.out.println("正在爬取第" + currentPage/20 + "页数据");
		String url = this.url.replace("$currentPage", currentPage + "");
		CloseableHttpClient httpCilent = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpCilent.execute(httpGet);//执行请求
			content = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
			if(content!=null){
			   this.writeToTXT(content);//每次爬取数据都写入到txt文件，方便一次性匹配数据并存入到excel文件
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToTXT(String str) { //将爬取的所有内容存入到本地TXT文件
		FileOutputStream o = null;
		String path = "D://";
		String filename = "aaa.txt";
		byte[] buff = new byte[] {};
		try {
			File file = new File(path + filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			buff = str.getBytes();
			o = new FileOutputStream(file, true);
			o.write(buff);
			o.flush();
			o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String readString() {  //从本地txt文件获取到所需爬取数据
		int len = 0;
		StringBuffer str = new StringBuffer("");
		File file = new File("D://aaa.txt");
		try {
			FileInputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader in = new BufferedReader(isr);
			String line = null;
			while ((line = in.readLine()) != null) {
				if (len != 0) {
					str.append("\r\n" + line);
				} else {
					str.append(line);
				}
				len++;
			}
			in.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str.toString();
	}

	public static void main(String[] args) { //从txt文件读取数据，执行此方法前需要执行GetData类获取数据到txt文件
		String allContent = readString();
		Parser parse = new Parser();
		parse.parse(allContent); //匹配相应的数据并且生成excel文件
	}
}
