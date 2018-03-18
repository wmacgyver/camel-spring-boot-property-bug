package app;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.util.List;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = Application.class)
@ComponentScan
@EnableAutoConfiguration
public class ExampleRouteTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Autowired
    @Produce(uri = "direct:startRoute")
    private ProducerTemplate template;

    @Value("${fromUser}")
    private String fromUser;

    @Value("${toUser}")
    private String toUser;

    @PropertyInject("{{fromUser}}")
    private String fromUserPropertyInject;

    @PropertyInject("{{toUser}}")
    private String toUserPropertyInject;

    @Test
    public void testWithContent() throws Exception{
        System.out.println(fromUser);
        System.out.println(toUser);

        String content = "testing content";

        resultEndpoint.expectedMessageCount(1);

        template.sendBodyAndHeader(
                content,
                "foo","bar");

        resultEndpoint.assertIsSatisfied();

        List<Message> inbox = Mailbox.get("user2@dummy.com");
        Address[] fromAddresses = inbox.get(0).getFrom();
        String fromAddress = ((InternetAddress) fromAddresses[0]).getAddress();
        assertEquals(fromAddress, "user1@dummy.com");
        System.out.println(inbox.get(0).getSubject());
        System.out.println(inbox.get(0).getContent());
        assertEquals(inbox.size(), 1); // was the e-mail really sent?

    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:startRoute")
                    .setBody(simple("Content coming through!"))
                    .to("smtp://localhost?from={{fromUser}}&to={{toUser}}&subject=Test+Done");
            }
        };
    }
}
