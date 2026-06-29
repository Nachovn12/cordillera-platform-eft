package cl.duoc.cordillera.dataservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DataServiceApplicationTests {

    @Test
    void contextLoads() {
    }
    @Test
    void main_ejecutaCorrectamente() {
        DataServiceApplication.main(new String[]{"--spring.profiles.active=test", "--server.port=0"});
    }
}
