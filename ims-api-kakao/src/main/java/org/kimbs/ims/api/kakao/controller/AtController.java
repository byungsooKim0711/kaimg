package org.kimbs.ims.api.kakao.controller;

import lombok.extern.slf4j.Slf4j;
import org.kimbs.ims.api.kakao.controller.annotation.Version1;
import org.kimbs.ims.api.kakao.service.AtService;
import org.kimbs.ims.protocol.ImsCommonRes;
import org.kimbs.ims.protocol.v1.ImsBizAtReq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Version1
@RestController
public class AtController extends AbstractImsController<ImsBizAtReq> {

    private final AtService atService;

    public AtController(AtService atService) {
        this.atService = atService;
    }

    @Override
    @PostMapping("/at/sendMessage")
    protected ResponseEntity<ImsCommonRes<Void>> sendMessage(@PathVariable String serviceKey, @RequestBody ImsBizAtReq imsBizReq) {
        ImsCommonRes<Void> response = atService.sendMessage(serviceKey, imsBizReq);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
