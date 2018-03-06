package application;

//this is the class that binds to selected services
import application.model.Product;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static java.lang.Math.toIntExact;

@Component
public class ProductApiBinding  {


  public ApiResponseMessage<Product> delete(Long productID) {
      return new ApiResponseMessage<>(HttpStatus.INTERNAL_SERVER_ERROR, "not yet implemented", null);
  }
  public ApiResponseMessage<Product> get(Long productID) {
	  //invoke business or adapter microservice that pulls back product based on a productID
	  //The APIResponseMessage has a data element of type T (any type), so you probably want to have the Product microservice return a JSON document
	  String productServiceURL;
	  String host = "93fbc6fb-4202-4913-8666-fb7fed1ab335-bluemix.cloudant.com";
	  Product p = new Product();
	  RestTemplate template = new RestTemplate();
	  
	  //Iteration 1: stub out a hard coded response
	  p.setIdentifier(productID);
	  p.setName("foo");
	  
	  //Iteration 2: replace the hard coded response with a real REST call 
	  //productServiceURL = "https://93fbc6fb-4202-4913-8666-fb7fed1ab335-bluemix.cloudant.com/product/{0}";
	  //RestTemplate template = new RestTemplate();
	  //Product p = template.getForObject(productServiceURL, Product.class, productID);
	  
	  //Iteration 3: If you wanted to get the productServiceURL from VCAP_SERVICES, you could do it this way
	  // TODO move Cloudant host lookup to configuration class
	  String VCAP_SERVICES = System.getenv("VCAP_SERVICES"); //retrieves IBM Cloud environment variables for this BFF microservice
	  if (VCAP_SERVICES != null && VCAP_SERVICES.length() > 0) {
		  //Parse the VCAP_SERVICES JSON structure to find the the name/value pair
		  try {
			  JSONObject config = new JSONObject(VCAP_SERVICES);
			  JSONArray cloudantConfig = config.getJSONArray("cloudantNoSQLDB");
			  for (int i = 0; i < cloudantConfig.length(); i++)
			  {
				  JSONObject cloudantConfig_credentials = cloudantConfig.getJSONObject(i).getJSONObject("credentials");
				  host = cloudantConfig_credentials.getString("host");
				  System.out.println("FOUND \"host\" in VCAP_SERVICES: " + host);
			  }
			  
			  JSONArray redisConfig = config.getJSONArray("rediscloud");
			  //retrieve Redis configs
			  String redisHost="";
			  String password = "";
			  String port = "";
			  for (int i = 0; i < cloudantConfig.length(); i++)
			  {
				  JSONObject redisConfig_credentials = redisConfig.getJSONObject(i).getJSONObject("credentials");
				  redisHost = redisConfig_credentials.getString("hostname");
				  password = redisConfig_credentials.getString("password");
				  port = redisConfig_credentials.getString("port");
				  System.out.println("FOUND \"hostname\" in VCAP_SERVICES: " + redisHost);
				  System.out.println("FOUND \"password\" in VCAP_SERVICES: " + password);
				  System.out.println("FOUND \"port\" in VCAP_SERVICES: " + port);
			  }
			  JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), redisHost, Integer.parseInt(port), Protocol.DEFAULT_TIMEOUT, password);
			  Jedis jedis = jedisPool.getResource();
			  jedis.hsetnx("product:"+toIntExact(productID), "identifier", String.valueOf(productID));
			  jedis.hsetnx("product:"+toIntExact(productID), "name", "tostones");
			  System.out.println("jedis["+productID+"].identifer: " + jedis.hget("product:"+toIntExact(productID),"identifier"));
			  System.out.println("jedis["+productID+"].identifer: " + jedis.hget("product:"+toIntExact(productID),"name"));
			  // return the instance to the pool when you're done
			  jedisPool.returnResource(jedis);
			  p.setIdentifier(productID);
			  p.setName("tostones");
		} catch (JSONException jsonE) {
			// TODO Auto-generated catch block
			jsonE.printStackTrace();
			//if we can't get the value from VCAP, we'll use the default value of "host"
		} catch (Exception e) {
		  	e.printStackTrace();
	  }
		  
	  }
	  //retrieve product data from API
	  try {
		  if (productID < 4) {
			  productServiceURL = "https://" + host + "/product/" + productID;
			  System.out.println("productServiceURL: " + productServiceURL);
			  p = template.getForObject(productServiceURL, Product.class, productID);
		  }
	  } catch (Exception e) {
		  e.printStackTrace();
	  }

      return new ApiResponseMessage<>(HttpStatus.OK, "this is where the business microservice will return a Product", p);
  }
	  
  public ApiResponseMessage<Product> update(Long productID, String productName) {
      return new ApiResponseMessage<>(HttpStatus.NOT_FOUND, "", null);
  }
}

