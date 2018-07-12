package V1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * 聊天室客户端
 * @author adminitartor
 *
 */
public class Client {
	/*
	 * java.net.Socket
	 * 封装了TCP通讯协议,中文翻译是:套接字
	 * 使用Socket的大致步骤:
	 * 1:实例化Socket,同时指定连接的服务端
	 *   的IP和端口并与服务端建立连接
	 * 2:通过Socket创建两个流,一个输入流一个
	 *   输出流
	 *   通过输入流读取远端计算机发送过来的
	 *   数据
	 *   通过输出流将数据发送给对方计算机  
	 */
	private Socket socket;
	/**
	 * 用来初始化客户端
	 */
	public Client(){
		try {
			/*
			 * 实例化Socket时需要传入两个
			 * 参数
			 * 1:IP地址,通过IP可以找到网络
			 *   上的指定计算机
			 * 2:端口,用来连接该计算机上的对
			 *   应应用程序  
			 * 实例化Socket的过程就是与服务端
			 * 连接的过程
			 *   
			 */
			System.out.println("正在连接服务端...");
			socket = new Socket(
				"localhost",
				8088
			);
			System.out.println("与服务端建立连接!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 程序的启动方法
	 */
	public void start(){
		try {
			//启动读取服务端消息的线程
			ServerHandler handler
				= new ServerHandler();
			Thread t = new Thread(handler);
			t.start();
			
			OutputStream out
				= socket.getOutputStream();
			OutputStreamWriter osw
				= new OutputStreamWriter(out,"UTF-8");
			PrintWriter pw
				= new PrintWriter(osw,true);
			
			System.out.println("请开始输入内容:");
			Scanner scanner = new Scanner(System.in);
			String message = null;
			
			long lastSend = System.currentTimeMillis()-1000;
			while(true){
				String line = scanner.nextLine();
				if(System.currentTimeMillis()-lastSend>=1000){
					pw.println(line);
					System.out.println("写出完毕!");
				}else{
					System.out.println("您说话速度太快!");
				}
				lastSend = System.currentTimeMillis();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
	/**
	 * 该线程用于读取服务端发送过来的消息
	 * 并输出到控制台
	 * @author adminitartor
	 *
	 */
	private class ServerHandler implements Runnable{
		public void run(){
			try {
				BufferedReader br = new BufferedReader(
					new InputStreamReader(
						socket.getInputStream(),"UTF-8"	
					)	
				);
				String line = null;
				while((line=br.readLine())!=null){
					System.out.println(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}








