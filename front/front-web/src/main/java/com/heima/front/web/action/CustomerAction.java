package com.heima.front.web.action;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import com.heima.front.domain.Customer;
import com.heima.front.producer.SmsMqProducer;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * @author 作者 Eins98
 * @version 创建时间：2017年8月12日 下午3:58:00
 */

@Controller
@Scope("prototype")
@Namespace("/")
@ParentPackage("default")
public class CustomerAction extends ActionSupport implements ModelDriven<Customer> {
	private static final long serialVersionUID = 5466197459195542696L;
	private Customer customer = new Customer();
	private RedisTemplate<String, String> template;
	private SmsMqProducer producer;

	public SmsMqProducer getProducer() {
		return producer;
	}

	@Autowired
	public void setProducer(SmsMqProducer producer) {
		this.producer = producer;
	}

	public RedisTemplate<String, String> getTemplate() {
		return template;
	}

	@Autowired
	public void setTemplate(RedisTemplate<String, String> template) {
		this.template = template;
	}

	@Override
	public Customer getModel() {
		return customer;
	}

	@Action("customer_sendCheckcode")
	public String sendCheckcode() throws Exception {
		String telephone = ServletActionContext.getRequest().getParameter("telephone");
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			int random = (int) (Math.random() * 10);
			code.append(random);
		}
		String checkcode = code.toString();
		template.opsForValue().set(telephone, checkcode, 5, TimeUnit.MINUTES);

		Map<String, String> map = new HashMap<>();
		map.put("telephone", telephone);
		map.put("code", checkcode);
		producer.sendCustSignUpMessage(map);
		return NONE;
	}

}
