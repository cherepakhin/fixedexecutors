package ru.perm.v.fixedexecutors;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XStreamConfig {

    XStream xstream;

    public XStreamConfig() {
        this.xstream = new XStream(new StaxDriver());
        xstream.processAnnotations(new Class[]{
                ArticleXml.class
        });
    }

    @Bean
    public XStream getXstream() {
        return xstream;
    }
}
