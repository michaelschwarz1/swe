//package de.shop.util;
//import java.io.IOException;
//import java.io.Serializable;
//import javax.enterprise.context.ApplicationScoped;
//
//import org.jboss.as.controller.client.ModelControllerClient;
//import org.jboss.dmr.ModelNode;
//
//@ApplicationScoped
//public class SecurityCache implements Serializable {
//	private static final long serialVersionUID = -4609784144100179413L;
//
//public void flush(String username) throws IOException {
//	
//	ModelControllerClient client = ModelControllerClient.Factory.create("localhost", 9999);
//	ModelNode address = new ModelNode();
//	address.add("subsystem", "security");
//	address.add("security-domain", "shop");
//	ModelNode operation = new ModelNode();
//	operation.get("address").set(address);
//	operation.get("operation").set("flush-cache");
//	operation.get("principal").set(username);
//	client.close();
//	}
//}