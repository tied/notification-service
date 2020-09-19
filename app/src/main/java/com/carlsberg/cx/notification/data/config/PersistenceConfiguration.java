package com.carlsberg.cx.notification.data.config;


import com.carlsberg.cx.notification.data.repositories.MongoRepositoryConfig;
import com.mongodb.MongoClientOptions;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackageClasses = {
    MongoRepositoryConfig.class
})
@Slf4j
public class PersistenceConfiguration {

  private static final String KEY_STORE_TYPE = "JKS";
  private static final String KEY_STORE_PROVIDER = "SUN";

  @Bean
  public MongoClientOptions options(@Value("${aws.pem}") Resource resourceFile) {
    return MongoClientOptions.builder()
        .sslContext(getSslContext(resourceFile))
        .build();
  }

  private SSLContext getSslContext(Resource resourceFile) {
    try {
      KeyStore myTrustStore = KeyStore.getInstance(KEY_STORE_TYPE, KEY_STORE_PROVIDER);
      myTrustStore.load(null);

      createCertificates(resourceFile).forEach(o -> setCertificate(myTrustStore, o));

      TrustManagerFactory tmf = TrustManagerFactory
          .getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(myTrustStore);

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, tmf.getTrustManagers(), null);
      return sslContext;
    } catch (IOException | KeyStoreException | NoSuchProviderException | CertificateException
        | NoSuchAlgorithmException | KeyManagementException e) {
      log.error("Error creating TrustStore");
      throw new RuntimeException("Error creating TrustStore", e);
    }
  }

  private void setCertificate(KeyStore myTrustStore, Certificate o) {
    try {
      myTrustStore.setCertificateEntry(UUID.randomUUID().toString(), o);
    } catch (KeyStoreException e) {
      log.error("Error adding certificate");
      throw new RuntimeException("Error adding certificate", e);
    }
  }

  private Collection<? extends Certificate> createCertificates(Resource resourceFile)
      throws CertificateException, IOException {
    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    return certFactory.generateCertificates(resourceFile.getInputStream());

  }

}


