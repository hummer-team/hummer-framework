package com.hummer.api.service;

import com.hummer.api.dto.NoProcessOrderInfoRespDto;
import com.hummer.first.restfull.plugin.AfterHandlerProvider;
import com.hummer.rest.model.ResourceResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AfterHandlerProviderImpl implements AfterHandlerProvider<ResourceResponse<NoProcessOrderInfoRespDto>> {

    /**
     * business handler e.g: verify
     *
     * @param data api response
     */
    @Override
    public void handler(ResourceResponse<NoProcessOrderInfoRespDto> data) {
        log.info("verify resp..{}",data.getCode());
    }
}
