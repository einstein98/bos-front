package com.heima.front.web.action;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;

import com.heima.front.domain.Customer;
import com.heima.front.producer.SmsMqProducer;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.util.CompoundRoot;

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
	private final String crmURL = ResourceBundle.getBundle("webserviceURL").getString("crmURL");
	private final String SAVE_CUSTOMER_URI = "save";

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
		// template.opsForValue().set(telephone, checkcode, 5, TimeUnit.MINUTES);
		template.opsForValue().set(telephone, checkcode);

		Map<String, String> map = new HashMap<>();
		map.put("telephone", telephone);
		map.put("code", checkcode);
		producer.sendCustSignUpMessage(map);
		return NONE;
	}

	@Action(value = "customer_validateCheckcode", results = @Result(name = "validateCheckcode", type = "json"))
	public String validateCheckcode() throws Exception {
		CompoundRoot root = ActionContext.getContext().getValueStack().getRoot();
		HttpServletRequest request = ServletActionContext.getRequest();
		String telephone = request.getParameter("telephone");
		String checkcode = request.getParameter("checkcode");
		String redis_code = template.opsForValue().get(telephone);
		if (StringUtils.isNotBlank(redis_code)) {
			if (redis_code.equals(checkcode)) {
				root.push(true);
				return "validateCheckcode";

			}
		}
		root.push(false);
		return "validateCheckcode";
	}

	@Action(value = "customer_regist", results = @Result(name = "regist", type = "redirect", location = "/signup-success.html"))
	public String regist() throws Exception {

		WebClient.create(crmURL + SAVE_CUSTOMER_URI).accept(MediaType.APPLICATION_JSON).post(customer);
		return "regist";
	}

}
