package elk.spike;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@SpringBootApplication
public class Application {

    public static final String REQUEST_ID = "REQ_ID";

    @RequestMapping("/hello")
    protected String hello() {
        startToPrepareGreeting();
        finsihToPrepareGreeting();
        return "hello";
    }

    private void startToPrepareGreeting() {
        log.info("Start saying hello");
    }

    private void finsihToPrepareGreeting() {
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

    @Bean
    protected Filter injectLoggingMdcFilter() {
        return new Filter() {

            @Override
            public void init(FilterConfig filterConfig) throws ServletException {

            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                try {
                    /*
                    * This code puts the value "request_id" to the Mapped Diagnostic
                    * context.
                    */
                    MDC.put(REQUEST_ID, format("%s", UUID.randomUUID().toString()));

                    chain.doFilter(request, response);

                } finally {
                    MDC.remove(REQUEST_ID);
                }

            }

            @Override
            public void destroy() {

            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}