package elk.spike;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.stringtemplate.v4.ST;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Slf4j
public class LoggingConceptualTest {

    @Test
    public void printSingleLine() {
        log.info(new ST("Spam: <uuid>").add("uuid", UUID.randomUUID().toString()).render());
    }

    @Test
    public void printMultiLine() {
        log.error(new ST("Error: <uuid>").add("uuid", UUID.randomUUID().toString()).render(), new RuntimeException("Oops.."));
    }
}
