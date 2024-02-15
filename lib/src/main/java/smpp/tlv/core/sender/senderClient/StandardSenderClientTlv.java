package smpp.tlv.core.sender.senderClient;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.github.mikesafonov.smpp.core.connection.ConnectionManager;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.dto.MessageErrorInformation;
import com.github.mikesafonov.smpp.core.dto.MessageResponse;
import com.github.mikesafonov.smpp.core.exceptions.IllegalAddressException;
import com.github.mikesafonov.smpp.core.exceptions.SmppException;
import com.github.mikesafonov.smpp.core.sender.MessageBuilder;
import com.github.mikesafonov.smpp.core.sender.StandardSenderClient;
import lombok.extern.slf4j.Slf4j;
import smpp.tlv.core.sender.MessageBuilderTlv;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

@Slf4j
public class StandardSenderClientTlv extends StandardSenderClient {
    private static final int INVALID_PARAM = 101;
    private static final int INVALID_SENDING_ERROR = 102;

    /**
     * Request timeout in millis
     */
    private final long timeoutMillis;
    /**
     * Message builder
     */
    private final MessageBuilderTlv messageBuilderTlv;
    private final boolean ucs2Only;
    private final ConnectionManager connectionManager;
    public StandardSenderClientTlv(@NotNull ConnectionManager connectionManager,
                                   boolean ucs2Only, long timeoutMillis,
                                   @NotNull MessageBuilder messageBuilder,
                                   @NotNull MessageBuilderTlv messageBuilderTlv) {
        super(connectionManager, ucs2Only, timeoutMillis, messageBuilder);
        this.messageBuilderTlv = requireNonNull(messageBuilderTlv);
        this.connectionManager = requireNonNull(connectionManager);
        this.ucs2Only = ucs2Only;
        this.timeoutMillis = timeoutMillis;
    }

    @NotNull
    public MessageResponse send(@NotNull Message message, Tlv tlv) {

        requireNonNull(message);
        if (isNullOrEmptyTvl(message.getText())) {
            return MessageResponse.error(message, getId(), new MessageErrorInformation(0,
                    "Empty message text"));
        }

        try {
            SubmitSm submitSm = messageBuilderTlv.createSubmitSm(message, ucs2Only, tlv);
            SubmitSmResp send = sendTvl(submitSm);
            return analyzeResponseTvl(message, send);
        } catch (IllegalAddressException e) {
            log.error(e.getMessage(), e);
            return MessageResponse.error(message, getId(), new MessageErrorInformation(INVALID_PARAM, e.getMessage()));
        } catch (SmppException e) {
            return MessageResponse.error(message, getId(), new MessageErrorInformation(e.getErrorCode(),
                    e.getErrorMessage()));
        }
    }
    private boolean isNullOrEmptyTvl(String value) {
        return value == null || value.trim().isEmpty();
    }
    private SubmitSmResp sendTvl(SubmitSm sm){
        try {
            return connectionManager.getSession().submit(sm, timeoutMillis);
        } catch (SmppInvalidArgumentException ex) {
            log.error(ex.getMessage(), ex);
            throw new SmppException(INVALID_PARAM, "Invalid param");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new SmppException(INVALID_SENDING_ERROR, "Cant send message");
        }
    }

    private MessageResponse analyzeResponseTvl(Message message, SubmitSmResp submitSmResp) {
        if (submitSmResp.getCommandStatus() == SmppConstants.STATUS_OK)
            return MessageResponse.success(message, getId(), submitSmResp.getMessageId());
        else
            return MessageResponse.error(message, getId(), new MessageErrorInformation(INVALID_PARAM,
                    submitSmResp.getResultMessage()));
    }
}
