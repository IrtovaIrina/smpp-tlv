package smpp.tlv.core.sender.submitSmEncoder;

import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.github.mikesafonov.smpp.core.dto.Message;

import javax.validation.constraints.NotNull;

public interface SubmitSmEncoderTlv {

    void encode(@NotNull Message message, @NotNull SubmitSm submitSm, boolean ucs2Only, Tlv tlv);
}
