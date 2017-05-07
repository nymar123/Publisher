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
	  TOPIC=request.getParameter("topic");
	  MESSAGE=request.getParameter("message");
	  client = new MqttClient(HOST, clientid, new MemoryPersistence());  
      connect();
      message = new MqttMessage();  
      message.setQos(2);  
      message.setRetained(true);  
      message.setPayload(MESSAGE.getBytes());  
      publish(topic, message); 

      System.out.println("已发送");  

      return "result";
   }
   
   private void connect() {  
       MqttConnectOptions options = new MqttConnectOptions();  
       options.setCleanSession(false);  
       options.setUserName(userName);  
       options.setPassword(passWord.toCharArray());  
       // 设置超时时间  
       options.setConnectionTimeout(10);  
       // 设置会话心跳时间  
       options.setKeepAliveInterval(20);  
       try {  
           client.setCallback(new PushCallback());  
           client.connect(options);  
           topic = client.getTopic(TOPIC);  
       } catch (Exception e) {  
           e.printStackTrace();  
       }  
   }  
 
   public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException,  MqttException {  
       MqttDeliveryToken token = topic.publish(message);  
       token.waitForCompletion();  
       System.out.println("message is published completely! "  
               + token.isComplete());  
   }

}
