package smpp.tlv.core.sender;

import com.github.mikesafonov.smpp.core.dto.Message;
import smpp.tlv.core.sender.submitSmEncoder.FlashSubmitSmEncoderTlv;
import smpp.tlv.core.sender.submitSmEncoder.SilentSubmitSmEncoderTlv;
import smpp.tlv.core.sender.submitSmEncoder.SimpleSubmitSmEncoderTlv;
import smpp.tlv.core.sender.submitSmEncoder.SubmitSmEncoderTlv;

public class SubmitSmEncoderFactoryTlv {
    public SubmitSmEncoderTlv get(Message message) {
        switch (message.getMessageType()) {
            case SIMPLE:
            case DATAGRAM:
                return new SimpleSubmitSmEncoderTlv();
            case SILENT:
                return new SilentSubmitSmEncoderTlv();
            case FLASH:
                return new FlashSubmitSmEncoderTlv();
            default:
                throw new RuntimeException("Unable to find encoder for message type " + message.getMessageType());
        }
    }
}
