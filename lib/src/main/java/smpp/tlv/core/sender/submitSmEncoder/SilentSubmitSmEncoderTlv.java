package smpp.tlv.core.sender.submitSmEncoder;

import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.sender.DataCoding;
import com.github.mikesafonov.smpp.core.sender.SilentSubmitSmEncoder;
import lombok.SneakyThrows;

public class SilentSubmitSmEncoderTlv extends SilentSubmitSmEncoder implements SubmitSmEncoderTlv {
    @Override
    @SneakyThrows
    public void encode(Message message, SubmitSm submitSm, boolean ucs2Only, Tlv tlv) {
        submitSm.setDataCoding(DataCoding.SILENT_CODING);
        submitSm.setShortMessage(new byte[0]);
        submitSm.addOptionalParameter(tlv);
    }
}
