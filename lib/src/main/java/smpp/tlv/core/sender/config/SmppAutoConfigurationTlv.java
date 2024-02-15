package smpp.tlv.core.sender.config;

import com.github.mikesafonov.smpp.config.SmppAutoConfiguration;
import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.config.SmscConnectionFactoryBean;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.connection.ConnectionManagerFactory;
import com.github.mikesafonov.smpp.core.generators.SmppResultGenerator;
import com.github.mikesafonov.smpp.core.reciever.DeliveryReportConsumer;
import com.github.mikesafonov.smpp.core.sender.TypeOfAddressParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import smpp.tlv.core.sender.ClientFactoryTlv;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(SmppProperties.class)
public class SmppAutoConfigurationTlv extends SmppAutoConfiguration {

    @Override
    public ClientFactory clientFactory() {
        return new ClientFactoryTlv();
    }

    @Override
    public SmscConnectionFactoryBean senderClientFactoryBean(SmppProperties smppProperties,
                                                             SmppResultGenerator smppResultGenerator,
                                                             TypeOfAddressParser typeOfAddressParser,
                                                             List<DeliveryReportConsumer> deliveryReportConsumers,
                                                             ClientFactory clientFactory,
                                                             ConnectionManagerFactory connectionManagerFactory) {
        return new SmscConnectionFactoryBean(smppProperties, smppResultGenerator, deliveryReportConsumers,
                typeOfAddressParser, clientFactory, connectionManagerFactory);
    }
}

