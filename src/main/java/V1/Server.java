package V1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * 聊天室服务端
 * @author adminitartor
 *
 */
public class Server {
	/*
	 * java.net.ServerSocket
	 * 运行在Server端的ServerSocket
	 * 主要作用有两个:
	 * 1:向系统申请服务端口,客户端就是通过
	 *   这个端口与之连接的
	 * 2:监听该服务端口,一旦客户端通过这个
	 *   端口请求连接,则创建一个Socket与该
	 *   客户端进行通讯  
	 */
	private ServerSocket server;
	/*
	 * 存放所有客户端输出流,用于广播消息
	 */
	private Collection<PrintWriter> allOut;
	
	public Server(){
		try {
			/*
			 * 实例化ServerSocket的同时
			 * 申请服务端口.若该端口已经
			 * 被其他程序占用,则会抛出异常
			 * address already in use
			 */
			System.out.println("请在启动服务端...");
			server = new ServerSocket(8088);
			allOut = new ArrayList<PrintWriter>();
			System.out.println("服务端启动完毕!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		try {
			/*
			 * ServerSocket提供的方法:
			 * Socket accept()
			 * 该方法是一个阻塞方法,调用到
			 * 该方法后程序"卡住",并等待
			 * 客户端的连接,一旦一个客户端连
			 * 接了,那么就会返回一个Socket
			 * 实例,通过该实例即可与连接的客户
			 * 端进行通讯
			 */
			while(true){
				System.out.println("等待客户端连接....");
				Socket socket = server.accept();
				System.out.println("一个客户端连接了!");
				
				//创建线程
				ClientHandler handler 
					= new ClientHandler(socket);
				Thread t = new Thread(handler);
				t.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
	}
	/**
	 * 处理与一个客户端的交互
	 * @author adminitartor
	 *
	 */
	private class ClientHandler 
						implements Runnable{
		private Socket socket;
		
		//记录当前客户端的地址信息
		private String host;
		
		public ClientHandler(Socket socket){
			this.socket = socket;
			InetAddress address 
				= socket.getInetAddress();
			host = address.getHostAddress();
		}
		
		public void run(){
			PrintWriter pw = null;
			try {
				System.out.println("一个线程启动了!");
				InputStream in = socket.getInputStream();
				InputStreamReader isr
					= new InputStreamReader(in,"UTF-8");
				BufferedReader br
					= new BufferedReader(isr);	
				
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw
					= new OutputStreamWriter(out,"UTF-8");
				pw = new PrintWriter(osw,true);				
				//将该客户端的输出流存入allOut
//				allOut = Arrays.copyOf(allOut, allOut.length+1);
//				allOut[allOut.length-1] = pw;
				allOut.add(pw);
				
				String line = null;
				while((line = br.readLine())!=null){
					System.out.println("客户端说:"+line);
					//回复所有客户端
					for(PrintWriter o : allOut){
						o.println(host +"说:"+line);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				/*
				 * 处理客户端断开连接后的操作
				 */
				System.out.println("一个客户端断线了");
				
				//将该客户端的输出流从共享集合中删除
				allOut.remove(pw);
				
				try {
					/*
					 * Socket关闭后,输出流与输入流也就关闭了
					 */
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}











