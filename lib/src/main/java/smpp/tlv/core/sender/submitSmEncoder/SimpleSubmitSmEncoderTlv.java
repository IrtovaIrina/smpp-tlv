package smpp.tlv.core.sender.submitSmEncoder;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.github.mikesafonov.smpp.core.dto.Message;
import com.github.mikesafonov.smpp.core.sender.SimpleSubmitSmEncoder;
import com.github.mikesafonov.smpp.core.utils.CountWithEncoding;
import com.github.mikesafonov.smpp.core.utils.MessageUtil;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;

public class SimpleSubmitSmEncoderTlv extends SimpleSubmitSmEncoder implements SubmitSmEncoderTlv {
    @Override
    @SneakyThrows
    public void encode(Message message, SubmitSm submitSm, boolean ucs2Only, Tlv tlv) {
        CountWithEncoding countWithEncoding = MessageUtil.calculateCountSMS(message.getText(), ucs2Only);
        byte coding = findCodingTlv(countWithEncoding.getCharset());
        submitSm.setDataCoding(coding);
        submitSm.setShortMessage(new byte[0]);
        submitSm.addOptionalParameter(tlv);
    }

    private byte findCodingTlv(@NotNull Charset charset) {
        return (charset == CharsetUtil.CHARSET_GSM) ? SmppConstants.DATA_CODING_DEFAULT :
                SmppConstants.DATA_CODING_UCS2;
    }
}
