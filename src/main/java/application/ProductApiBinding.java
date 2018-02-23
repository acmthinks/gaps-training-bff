package application;

//this is the class that binds to selected services
import application.model.Product;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@Component
public class ProductApiBinding  {


  public ApiResponseMessage<Product> delete(Long productID) {
      return new ApiResponseMessage<>(HttpStatus.INTERNAL_SERVER_ERROR, "not yet implemented", null);
  }
  public ApiResponseMessage<Product> get(Long productID) {
	  //invoke business or adapter microservice that pulls back product based on a productID
	  //The APIResponseMessage has a data element of type T (any type), so you probably want to have the Product microservice return a JSON document
	  
	  //Iteration 1: stub out a hard coded response
	  //Product p = new Product();
	  //p.setIdentifier(productID);
	  //p.setName("foo");
	  
	  //Iteration 2: replace the hard coded response with a real REST call 
	  String productServiceURL = "https://93fbc6fb-4202-4913-8666-fb7fed1ab335-bluemix.cloudant.com/product/{0}";
	  RestTemplate template = new RestTemplate();
	  Product p = template.getForObject(productServiceURL, Product.class, productID);
	  
	  //Iteration 3: If you wanted to get the productServiceURL from VCAP_SERVICES, you could do it this way
	  /*String VCAP_SERVICES = System.getenv("VCAP_SERVICES"); //retrieves IBM Cloud environment variables for this BFF microservice
	  if (VCAP_SERVICES != null) {
		  //Parse the VCAP_SERVICES JSON structure to find the the name/value pair
		  try {
			  String productServiceURI;
			  String productServiceUser;
			  JSONObject envVars = new JSONObject(VCAP_SERVICES).getJSONObject("cloudantNoSQLDB").getJSONObject("credentials");
			  productServiceURI = "http://" + envVars.getString("host") + "/product";
			  productServiceUser = envVars.getString("username");
			  System.out.println(productServiceURI);
			  System.out.println(productServiceUser);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }*/
	  
      return new ApiResponseMessage<>(HttpStatus.OK, "this is where the business microservice will return a Product", p);
  }
  public ApiResponseMessage<Product> update(Long productID, String productName) {
      return new ApiResponseMessage<>(HttpStatus.NOT_FOUND, "", null);
  }
}

