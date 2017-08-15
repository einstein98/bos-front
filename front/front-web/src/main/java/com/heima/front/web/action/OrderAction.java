package com.heima.front.web.action;

import java.util.Date;
import java.util.ResourceBundle;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.heima.front.domain.Order;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * @author 作者 Eins98
 * @version 创建时间：2017年8月14日 下午9:47:22
 */
@Controller
@Scope("prototype")
@Namespace("/")
@ParentPackage("default")
public class OrderAction extends ActionSupport implements ModelDriven<Order> {
	private Order order = new Order();
	private final String BOS_URL = ResourceBundle.getBundle("webserviceURL").getString("bosURL");
	private final String SAVE_URI = "save";

	@Override
	public Order getModel() {
		return order;
	}

	@Action(value = "order_addOrder", results = @Result(name = "addOrder", type = "redirect", location = "index.html"))
	public String addOrder() throws Exception {
		getModel().setOrderTime(new Date());
		WebClient.create(BOS_URL + SAVE_URI).post(getModel());
		return "addOrder";
	}

}
