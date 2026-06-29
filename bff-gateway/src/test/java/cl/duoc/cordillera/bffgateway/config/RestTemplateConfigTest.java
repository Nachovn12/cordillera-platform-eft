package cl.duoc.cordillera.bffgateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateConfigTest {

    @Test
    void restTemplate_debeUsarSimpleClientHttpRequestFactory() {
        RestTemplate restTemplate = new RestTemplateConfig().restTemplate();
        assertInstanceOf(SimpleClientHttpRequestFactory.class, restTemplate.getRequestFactory());
    }

    @Test
    void restTemplate_connectTimeout_debe_ser_2000() {
        RestTemplate restTemplate = new RestTemplateConfig().restTemplate();
        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        assertEquals(2000, ReflectionTestUtils.getField(factory, "connectTimeout"));
    }

    @Test
    void restTemplate_readTimeout_debe_ser_5000() {
        RestTemplate restTemplate = new RestTemplateConfig().restTemplate();
        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) restTemplate.getRequestFactory();
        assertEquals(5000, ReflectionTestUtils.getField(factory, "readTimeout"));
    }
}
