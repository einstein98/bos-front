package com.heima.front.producer;
/** 
* @author 作者 Eins98
* @version 创建时间：2017年8月12日 下午3:41:13 
*/

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class SmsMqProducer {

	private JmsTemplate template;

	public JmsTemplate getTemplate() {
		return template;
	}

	@Autowired
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	public void sendCustSignUpMessage(Map<String, String> map) {
		template.send("customerSignUp", new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage msg = session.createMapMessage();
				msg.setString("telephone", map.get("telephone"));
				msg.setString("code", map.get("code"));
				return msg;
			}
		});
	}

	public void sendDeliveryInfoMessage(Map<String, String> map) {
		template.send("deliveryInfo", new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage msg = session.createMapMessage();
				msg.setString("telephone", map.get("telephone"));
				msg.setString("name", map.get("name"));
				msg.setString("code", map.get("code"));
				msg.setString("time", map.get("time"));
				return msg;
			}
		});
	}
}
