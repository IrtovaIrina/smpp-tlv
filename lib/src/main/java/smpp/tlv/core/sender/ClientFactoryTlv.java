package smpp.tlv.core.sender;

import com.github.mikesafonov.smpp.config.SmppProperties;
import com.github.mikesafonov.smpp.core.ClientFactory;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.sender.MessageBuilder;
import com.github.mikesafonov.smpp.core.sender.SenderClient;
import com.github.mikesafonov.smpp.core.sender.TypeOfAddressParser;
import smpp.tlv.core.sender.senderClient.StandardSenderClientTlv;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.github.mikesafonov.smpp.core.utils.Utils.getOrDefault;
import static com.github.mikesafonov.smpp.core.utils.Utils.validateName;
import static java.util.Objects.requireNonNull;

public class ClientFactoryTlv extends ClientFactory {
    @Override
    public SenderClient standardSender(@NotBlank String name,
                                       @NotNull SmppProperties.Defaults defaults,
                                       @NotNull SmppProperties.SMSC smsc,
                                       @NotNull TypeOfAddressParser typeOfAddressParser,
                                       @NotNull ConnectionManager connectionManager) {
        validateName(name);
        requireNonNull(defaults);
        requireNonNull(smsc);
        requireNonNull(typeOfAddressParser);
        requireNonNull(connectionManager);

        boolean ucs2Only = getOrDefault(smsc.getUcs2Only(), defaults.isUcs2Only());
        long requestTimeout = getOrDefault(smsc.getRequestTimeout(), defaults.getRequestTimeout()).toMillis();

        return new StandardSenderClientTlv(connectionManager,
                ucs2Only, requestTimeout, new MessageBuilder(typeOfAddressParser), new MessageBuilderTlv(typeOfAddressParser));
    }
}
