package smpp.tlv.core.sender;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.Address;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageType;
import com.github.mikesafonov.smpp.core.exceptions.SmppMessageBuildingException;
import com.github.mikesafonov.smpp.core.sender.AddressBuilder;
import com.github.mikesafonov.smpp.core.sender.MessageBuilder;
import com.github.mikesafonov.smpp.core.sender.SubmitSmEncoderFactory;
import com.github.mikesafonov.smpp.core.sender.TypeOfAddressParser;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

@Slf4j
public class MessageBuilderTlv extends MessageBuilder {

    private final AddressBuilder addressBuilder;
    private final SubmitSmEncoderFactory encoderFactory;
    private final SubmitSmEncoderFactoryTlv encoderFactoryTlv;

    public MessageBuilderTlv(@NotNull TypeOfAddressParser typeOfAddressParser) {
        this(new AddressBuilder(typeOfAddressParser), new SubmitSmEncoderFactory(), new SubmitSmEncoderFactoryTlv());
    }

    public MessageBuilderTlv(@NotNull AddressBuilder addressBuilder,
                             @NotNull SubmitSmEncoderFactory encoderFactory,
                             SubmitSmEncoderFactoryTlv encoderFactoryTlv) {
        super(addressBuilder, encoderFactory);
        this.addressBuilder = addressBuilder;
        this.encoderFactory = encoderFactory;
        this.encoderFactoryTlv = encoderFactoryTlv;
    }

    @NotNull
    public SubmitSm createSubmitSm(@NotNull Message message, boolean ucs2Only, Tlv tlv) {
        try {
            Address sourceAddress = addressBuilder.createSourceAddress(message.getSource());
            Address destAddress = addressBuilder.createDestinationAddress(message.getMsisdn());

            return createSubmitSm(message, sourceAddress,
                    destAddress, ucs2Only, tlv);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SmppMessageBuildingException();
        }
    }

    @NotNull
    private SubmitSm createSubmitSm(@NotNull Message message, @NotNull Address sourceAddress, @NotNull Address destAddress,
                                    boolean ucs2Only, Tlv tlv) {

        byte esmClass = getEsmClass(message.getMessageType());
        SubmitSm sm = new SubmitSm();
        sm.setEsmClass(esmClass);
        sm.setSourceAddress(sourceAddress);
        sm.setDestAddress(destAddress);
        sm.setValidityPeriod(message.getValidityPeriod());
        encoderFactoryTlv.get(message).encode(message, sm, ucs2Only, tlv);

        if (message.getMessageType() == MessageType.SIMPLE) {
            sm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        }
        return sm;
    }

    private byte getEsmClass(MessageType messageType) {
        if (messageType == MessageType.DATAGRAM) {
            return SmppConstants.ESM_CLASS_MM_DATAGRAM;
        }
        if (messageType == MessageType.FLASH) {
            return SmppConstants.ESM_CLASS_MM_DEFAULT;
        }
        return SmppConstants.ESM_CLASS_MM_STORE_FORWARD;
    }
}
