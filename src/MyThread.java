package core;

public class MyThread extends Thread {
	private int currentpage;
    public MyThread(int page) {
    	currentpage=page;
	}
	public void run() {
    	new DoubanParser().next(currentpage);
    }
}
