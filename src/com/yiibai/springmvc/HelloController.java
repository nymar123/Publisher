package com.yiibai.springmvc;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.ModelMap;

@Controller
public class HelloController{
	 
	 private static final String HOST = "tcp://127.0.0.1:1883"; 
	 private String TOPIC;
	 private String MESSAGE;
	 private static final String clientid = "server";  
	  
	 private MqttClient client;  
	 private MqttTopic topic;  

	 private String userName = "admin";  
	 private String passWord = "password";  
	  
	 private MqttMessage message;  

	    
	@RequestMapping(value="/",method = RequestMethod.GET)
	   public String publish0() {
	      return "publish";
	   }
	
   @RequestMapping(value="/publish",method = RequestMethod.GET)
   public String publish() {
      return "publish";
   }
   
   @RequestMapping(value="/redirect")
   public String doPublish(HttpServletRequest request) throws MqttException {
	  //获取前台传过来的两个参数
	  TOPIC=request.getParameter("topic");
	  MESSAGE=request.getParameter("message");
	  //new mqttClient
	  //MemoryPersistence设置clientid的保存形式，默认为以内存保存
	  client = new MqttClient(HOST, clientid, new MemoryPersistence());  
      //与activeMQ连接的方法
	  connect();
	  //new mqttMessage
      message = new MqttMessage();  
      //设置服务质量
      message.setQos(2);  
      //设置是否在服务器中保存消息体 
      message.setRetained(true);
      //设置消息的内容
      message.setPayload(MESSAGE.getBytes());  
      //发布
      publish(topic, message); 

      System.out.println("已发送");  

      return "result";
   }
   
   private void connect() {  
       // new mqttConnection 用来设置一些连接的属性
	   MqttConnectOptions options = new MqttConnectOptions();  
	   // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接 
	   // 换而言之，设置为false时可以客户端可以接受离线消息
       options.setCleanSession(false);  
       // 设置连接的用户名和密码
       options.setUserName(userName);  
       options.setPassword(passWord.toCharArray());  
       // 设置超时时间  
       options.setConnectionTimeout(10);  
       // 设置会话心跳时间  
       options.setKeepAliveInterval(20);  
       try {  
    	   // 设置回调类
           client.setCallback(new PushCallback());  
           // 连接
           client.connect(options);
           // 获取activeMQ上名为TOPIC的topic
           topic = client.getTopic(TOPIC);  
       } catch (Exception e) {  
           e.printStackTrace();  
       }  
   }  
 
   public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException,  MqttException {  
	   // 发布的方法
	   // new mqttDeliveryToken
       MqttDeliveryToken token = topic.publish(message);
       // 发布
       token.waitForCompletion();  
       System.out.println("message is published completely! "  
               + token.isComplete());  
   }

}
