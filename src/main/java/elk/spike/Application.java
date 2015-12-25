package elk.spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@SpringBootApplication
public class Application {

    @RequestMapping("/hello")
    protected String hello() {
        prepareGreeting();
        return "hello";
    }

    private void prepareGreeting() {
        log.info("Saying hello");
        throw new RuntimeException("Oops");
    }

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity wrapThrown(Throwable thrown) {
        ErrorRepresentation representation = new ErrorRepresentation(thrown);
        log.error(format("error_id[%s]:%s", representation.errorId, thrown.getMessage()), thrown);
        return new ResponseEntity<Object>(representation, INTERNAL_SERVER_ERROR);
    }

    @Getter
    static class ErrorRepresentation {
        private String errorId;
        private String message;

        public ErrorRepresentation(Throwable thrown) {
            this.errorId = UUID.randomUUID().toString();
            this.message = thrown.getMessage();
        }
    }

    @Resource
    private ObjectMapper objectMapper;

    @PostConstruct
    protected void configObjectMapper() {
        objectMapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}