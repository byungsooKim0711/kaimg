package org.kimbs.ims.api.kakao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.kimbs.ims.api.kakao.service.cache.ImsServiceKeyCache;
import org.kimbs.ims.exception.ImsKafkaSendException;
import org.kimbs.ims.exception.ImsMandatoryException;
import org.kimbs.ims.exception.ImsServiceKeyException;
import org.kimbs.ims.exception.ImsTooLongMessageException;
import org.kimbs.ims.model.kakao.BtMessageReq;
import org.kimbs.ims.protocol.v1.ImsBizBtReq;
import org.kimbs.ims.util.RoundRobinUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class BtService extends AbstractImsService<ImsBizBtReq, BtMessageReq> {

    private final static int BT_MAX_LENGTH_MESSAGE = 1000;

    private final ImsServiceKeyCache imsServiceKeyCache;

    public BtService(ImsServiceKeyCache imsServiceKeyCache) {
        this.imsServiceKeyCache = imsServiceKeyCache;
    }

    @Override
    protected String checkServiceKey(String serviceKey) throws ImsServiceKeyException {
        return imsServiceKeyCache.findServiceKey(serviceKey);
    }

    @Override
    protected void checkSenderKeyAndTemplate(ImsBizBtReq request) {

    }

    @Override
    protected void checkMandatory(ImsBizBtReq request) {
        String contents = request.getContents();
//        String appUserId = request.getAppUserId();
        String phoneNumber = request.getPhoneNumber();

        if (!StringUtils.hasText(contents)) {
            throw new ImsMandatoryException("contents is empty.");
        }

        if (!StringUtils.hasText(phoneNumber)) {
            throw new ImsMandatoryException("appUserId and phoneNumber is empty.");
        }
    }

    @Override
    protected void checkLength(ImsBizBtReq request) {
        String message = request.getContents();

        if (StringUtils.hasText(message) && message.length() > BT_MAX_LENGTH_MESSAGE) {
            throw new ImsTooLongMessageException("Too long message. " + request.getContents().length());
        }
    }

    @Override
    protected void checkDuplicateMsgUid(ImsBizBtReq request) {

    }

    @Override
    protected BtMessageReq convert(ImsBizBtReq request) {
        return null;
    }

    @Override
    protected void send(BtMessageReq message) {
        List<String> btTopics = config.getTopics().getRecvBt();
        try {
            super.sendToKafka(RoundRobinUtils.getRoundRobinValue(RoundRobinUtils.RoundRobinKey.RECV_BT, btTopics), message);
        } catch (JsonProcessingException e) {
            throw new ImsKafkaSendException(e);
        }
    }

    @Override
    protected void onException(ImsBizBtReq request, Exception e) {
        log.error("exception occurred({}). msgUid: {}, senderKey: {}, phoneNumber: {}", e.getMessage(), request.getMsgUid(), request.getSenderKey(), request.getPhoneNumber());
    }
}
