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
	public static final String url = "https://book.douban.com/tag/%E7%BC%96%E7%A8%8B?type=S&start=$currentPage";//��ȡ��URL��ַ

	public DoubanParser() {

	}

	public synchronized void next(int currentPage) {
		String content = null;
		System.out.println("������ȡ��" + currentPage/20 + "ҳ����");
		String url = this.url.replace("$currentPage", currentPage + "");
		CloseableHttpClient httpCilent = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpCilent.execute(httpGet);//ִ������
			content = EntityUtils.toString(httpResponse.getEntity());// ��÷��صĽ��
			if(content!=null){
			   this.writeToTXT(content);//ÿ����ȡ���ݶ�д�뵽txt�ļ�������һ����ƥ�����ݲ����뵽excel�ļ�
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToTXT(String str) { //����ȡ���������ݴ��뵽����TXT�ļ�
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

	private static String readString() {  //�ӱ���txt�ļ���ȡ��������ȡ����
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

	public static void main(String[] args) { //��txt�ļ���ȡ���ݣ�ִ�д˷���ǰ��Ҫִ��GetData���ȡ���ݵ�txt�ļ�
		String allContent = readString();
		Parser parse = new Parser();
		parse.parse(allContent); //ƥ����Ӧ�����ݲ�������excel�ļ�
	}
}
