package com.fox.platform.cgn;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.UpdateUserAttributesResult;

/**
 * Hello world!
 *
 */
public class App {
  private JwtConsumer jwtConsumer;
  
  public static void main(String[] args) {
    new App();
  }
  
  public App() {
    
    String jwt = "eyJraWQiOiJKd0lSK3Bjek1vTU5xUVZvQ2p2Umsrc1hWbE5OTlpKcjVabzNSXC9jeWJmVT0iLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiTXlsdjNIN3A5eEVya0lyTFZEdERuUSIsInN1YiI6IjVlNzk4M2E1LTFmMTAtNDQ0Ny04NTU2LTQ1OGJiYjRiZGEzYiIsImF1ZCI6IjNkN2NobzI0ZzQzdDNnc3Q3ZDFybWJkdDFiIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNTQ5Mzc3NTI4LCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9SSkZWdWxpQmIiLCJjb2duaXRvOnVzZXJuYW1lIjoiNWU3OTgzYTUtMWYxMC00NDQ3LTg1NTYtNDU4YmJiNGJkYTNiIiwiZXhwIjoxNTQ5MzgxMTI4LCJpYXQiOjE1NDkzNzc1MjgsImVtYWlsIjoiaC5nb256YWxlekBnbG9iYW50LmNvbSIsImN1c3RvbTp1cm5zIjoibXk6dXJuMSxteTp1cm4yIn0.COST5egCv4IXvy9fz2aDUlesU4ndg03FYvswObdz0OxnF0gTKJZKyg45X1ETi_AH2zZ6p6QuLPl5xeKvBy5X8S3-A99wKkS0EhYwfZfa9y3tSwrBWCuORRoGAur21QEA5rRCh4ExVY5gCBwWzr46jbI0OfmoJFN0JaTJ-o9RVl4uE0sZvwLzxHOn8VsVFuKcOHzRj7sBXvP1VDMZe82IpDawjmXnl7oBQUvaMku3dSpyWqEozfayXIPqirl51YhfWkrDi6e7fvXJEQPGptVpZgFzPqr8BGOd83so0-DNy8ggfDvxj9eoVkxfm2rI5wIvtXWYPTKh22Edhd1Qv5H4-A";
    String access_token = "eyJraWQiOiJ0NCtUUCtJa1FwdTdYblIzS3E2T2tsS2lsTm1oczRcL2haZVEyeDdxaFFXST0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI1ZTc5ODNhNS0xZjEwLTQ0NDctODU1Ni00NThiYmI0YmRhM2IiLCJldmVudF9pZCI6IjQ5YjUwMzE2LTI5NTEtMTFlOS05ZmZiLTQ3ODJmNTE1ODUzNSIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4gcGhvbmUgb3BlbmlkIHByb2ZpbGUgZW1haWwiLCJhdXRoX3RpbWUiOjE1NDkzNzY0NzEsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX1JKRlZ1bGlCYiIsImV4cCI6MTU0OTM4MDA3MSwiaWF0IjoxNTQ5Mzc2NDcxLCJ2ZXJzaW9uIjoyLCJqdGkiOiIwZWRkZTVkMy0xZjUyLTQwNjUtOTkxZi1hZGIwODA0MDBjNTAiLCJjbGllbnRfaWQiOiIzZDdjaG8yNGc0M3QzZ3N0N2Qxcm1iZHQxYiIsInVzZXJuYW1lIjoiNWU3OTgzYTUtMWYxMC00NDQ3LTg1NTYtNDU4YmJiNGJkYTNiIn0.KFeSDKdK_B2m6FXRk72CsL818QhPLREqliicwlpeJjEjAXN_9WGNlH2ROyXAmbq9OkXpNe2VagYYQpzZXvI5Ix_rNEYXUlkXLJCJk7_GNTrO0c9avIZ--NpcNH8-p9GMHic-TqshXXl5Pcxheg2yX2uw-3ZncIi7cu8KZrCKNebntiqqBESh9wsvrtJgIDJfkogSCIMrC3J79DIxSCeEip3z6hveIc4chBu4VLcqtJrUrkM7LW4eStYrSA0sPi-gtqHmngsGII0XZcKieI880lqnfkNSB_FZZknhS9R7K4uOEmNjjOjtzEo4vlKXTawtdjq_h4u3_QPE9EJf0MwHzw";
    
    
    DefaultAWSCredentialsProviderChain awsCreds = new DefaultAWSCredentialsProviderChain();
    AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder
        .standard()
        .withCredentials(awsCreds)
        .withRegion(Regions.US_EAST_1.getName())
        .build();
    
    AttributeType attributeType = new AttributeType();
    attributeType.setName("custom:urns");
    attributeType.setValue("my:urn1,my:urn2");
    
    UpdateUserAttributesRequest request = new UpdateUserAttributesRequest()
        .withUserAttributes(attributeType)
        .withAccessToken(access_token);
    
    
    try {
      UpdateUserAttributesResult result = cognitoIdentityProvider.updateUserAttributes(request);
      System.out.println(result);
    } catch(Exception ex) {
      ex.printStackTrace();
    }
    
    
    
    
    System.out.println("-------------");
    System.out.println("Validando JWT");
    System.out.println("-------------");
    
    
    jwtConsumer = createConsumer();
    try {
      validateJwt(jwt);
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  private HttpsJwksVerificationKeyResolver loadJksws() {
    HttpsJwks httpsJkws = new HttpsJwks("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_RJFVuliBb/.well-known/jwks.json");
    return new HttpsJwksVerificationKeyResolver(httpsJkws);
  }
  
  private JwtConsumer createConsumer() {
    return new JwtConsumerBuilder()
        .setExpectedAudience("3d7cho24g43t3gst7d1rmbdt1b")
        .setExpectedIssuer("https://cognito-idp.us-east-1.amazonaws.com/us-east-1_RJFVuliBb")
        .setVerificationKeyResolver(loadJksws())
        .setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256))
        .build();
  }
  
  private void validateJwt(String jwt) throws Exception{
    JwtContext jwtContext = jwtConsumer.process(jwt);
    
    jwtContext
      .getJwtClaims()
      .getClaimsMap()
      .entrySet()
      .stream()
      .forEach(entry -> {
        System.out.println("Claim name: " + entry.getKey() + " value: " + entry.getValue());
      });
  }
}
