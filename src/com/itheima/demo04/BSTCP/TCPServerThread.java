package com.itheima.demo04.BSTCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
    3.2 模拟B\S服务器
    创建BS版本TCP服务器
    访问 127.0.0.1:8080/java_net/web/index.html
 */
public class TCPServerThread {
    public static void main(String[] args) throws IOException {
        //创建一个服务器ServerSocket,和系统要指定的端口号
        ServerSocket server = new ServerSocket(8080);

        /*
            TCPServer版本的没有显示图片?
            浏览器解析服务器回写的html页面,页面中如果有图片,那么浏览器就会单独的开启一个线程,读取服务器的图片
            我们就的让服务器一直处于监听状态,客户端请求一次,服务器就回写一次
         */
        while(true){
            //使用accept方法获取到请求的客户端对象(浏览器)
            Socket socket = server.accept();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //使用Socket对象中的方法getInputStream,获取到网络字节输入流InputStream对象
                        InputStream is = socket.getInputStream();
                        //使用网络字节输入流InputStream对象中的方法read读取客户端的请求信息
                        /*byte[] bytes = new byte[1024];
                        int len = 0;
                        while((len = is.read(bytes))!=-1){
                            System.out.println(new String(bytes,0,len));
                        }*/

                        //把is网络字节输入流对象,转换为字符缓冲输入流
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        //把客户端请求信息的第一行读取出来 GET /java_net/web/index.html HTTP/1.1
                        String line = br.readLine();
                        System.out.println(line);
                        //把读取的信息进行切割,只要中间部分 /java_net/web/index.html
                        String[] arr = line.split(" ");
                        //把路径前边的/java_net/去掉,进行截取 web/index.html, 当然实际上这里应该是要判断项目名的, 我们没做
                        String htmlpath = arr[1].substring(10);

                        //创建一个本地字节输入流,构造方法中绑定要读取的html路径
                        FileInputStream fis = new FileInputStream(htmlpath);
                        //使用Socket中的方法getOutputStream获取网络字节输出流OutputStream对象
                        OutputStream os = socket.getOutputStream();

                        // 写入HTTP协议响应头,固定写法
                        os.write("HTTP/1.1 200 OK\r\n".getBytes());
                        os.write("Content-Type:text/html\r\n".getBytes());
                        // 必须要写入空行,否则浏览器不解析
                        os.write("\r\n".getBytes());

                        //一读一写复制文件,把服务读取的html文件回写到客户端
                        int len = 0;
                        byte[] bytes = new byte[1024];
                        while((len = fis.read(bytes))!=-1){
                            os.write(bytes,0,len);
                        }

                        //释放资源
                        fis.close();
                        socket.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();


        }


        //server.close();
    }
}
