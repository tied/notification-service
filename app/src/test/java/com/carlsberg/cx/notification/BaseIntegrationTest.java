package com.carlsberg.cx.notification;

import static capital.scalable.restdocs.AutoDocumentation.description;
import static capital.scalable.restdocs.AutoDocumentation.methodAndPath;
import static capital.scalable.restdocs.AutoDocumentation.pathParameters;
import static capital.scalable.restdocs.AutoDocumentation.requestFields;
import static capital.scalable.restdocs.AutoDocumentation.requestParameters;
import static capital.scalable.restdocs.AutoDocumentation.responseFields;
import static capital.scalable.restdocs.AutoDocumentation.sectionBuilder;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import capital.scalable.restdocs.SnippetRegistry;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import com.carlsberg.cx.notification.containers.MongoDbContainer;
import com.carlsberg.cx.notification.services.email.EmailClient;
import com.carlsberg.cx.notification.services.email.EmailClientFactory;
import com.carlsberg.cx.notification.services.sms.SmsClient;
import com.carlsberg.cx.notification.services.sms.SmsClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("integration")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(classes = NotificationServicesApplication.class)
public class BaseIntegrationTest {

  private static final MongoDbContainer MONGO_DB_CONTAINER = new MongoDbContainer();

  static {
    MONGO_DB_CONTAINER.start();
  }

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  protected EmailClientFactory emailClientFactoryMock;

  @MockBean
  protected SmsClientFactory smsClientFactoryMock;

  private MockMvc mockMvc;

  @BeforeAll
  static void setProperties() {
    System.setProperty("MONGO_PORT", MONGO_DB_CONTAINER.getPort().toString());
  }

  @BeforeEach
  void setUp(RestDocumentationContextProvider restDocumentation) {

    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
            .apply(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
                    .uris()
                    .withScheme("http")
                    .withHost("cx-notification-services.host")
                    .and()
                    .snippets()
                    .withDefaults(
                        curlRequest(),
                        httpRequest(),
                        httpResponse(),
                        requestFields().failOnUndocumentedFields(true),
                        responseFields(),
                        pathParameters(), // .failOnUndocumentedParams(true),
                        requestParameters().failOnUndocumentedParams(true),
                        description(),
                        methodAndPath(),
                        sectionBuilder()
                            .snippetNames(
                                SnippetRegistry.AUTO_METHOD_PATH,
                                SnippetRegistry.AUTO_DESCRIPTION,
                                SnippetRegistry.AUTO_PATH_PARAMETERS,
                                SnippetRegistry.AUTO_REQUEST_PARAMETERS,
                                SnippetRegistry.AUTO_REQUEST_FIELDS,
                                SnippetRegistry.HTTP_REQUEST,
                                SnippetRegistry.CURL_REQUEST,
                                SnippetRegistry.HTTP_RESPONSE,
                                SnippetRegistry.AUTO_EMBEDDED,
                                SnippetRegistry.AUTO_LINKS)
                            .skipEmpty(true)
                            .build()))
            .build();
  }

  protected ResultActions perform(
      final MockHttpServletRequestBuilder request, String endpoint, String call) throws Exception {
    return mockMvc
        .perform(request)
        .andDo(
            document(
                endpoint + "/" + call,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
  }
}
