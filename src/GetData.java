package core;

public class GetData {
	public static void main(String[] args) { 
		int page = 0;
		for (int i = 0; i <= 30; i++) { //开始爬取数据
			MyThread t = new MyThread(page++);
			page=page+20;
			t.start();
		}
	}
}
