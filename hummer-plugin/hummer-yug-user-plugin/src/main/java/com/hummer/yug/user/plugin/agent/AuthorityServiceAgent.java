package com.hummer.yug.user.plugin.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.utils.AppBusinessAssert;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.model.ResourceResponse;
import com.hummer.rest.utils.ResponseUtil;
import com.hummer.yug.tools.plugin.util.DistributionResponseUtil;
import com.hummer.yug.tools.plugin.util.DistributionWebResult;
import com.hummer.yug.user.plugin.dto.request.MemberValidReqDto;
import com.hummer.yug.user.plugin.dto.request.ShopInfoReqDto;
import com.hummer.yug.user.plugin.dto.response.ShopInfoRespDto;
import com.hummer.yug.user.plugin.holder.UserHolder;
import com.hummer.yug.user.plugin.user.UserContext;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AuthorityServiceAgent {
    private AuthorityServiceAgent() {
    }

    public static UserContext queryUserContext(final String sid, final String userToken) {
        MemberValidReqDto reqDto = new MemberValidReqDto();
        reqDto.setSid(sid);
        reqDto.setUserToken(userToken);
        String host = PropertiesContainer.valueOfStringWithAssertNotNull("app.yugyg.host");
        String url = String.format("%s/v1/member/validation", host);

        String response = HttpSyncClient.sendHttpPostByRetry(url
                , JSON.toJSONString(reqDto)
                , PropertiesContainer.valueOf(String.format("%s.timeout.millis", host)
                        , Long.class, 5000L)
                , TimeUnit.MILLISECONDS
                , 1);
        return DistributionResponseUtil.parseResponseV2WithStatus(response
                , new TypeReference<DistributionWebResult<UserContext>>() {
                });
    }

    public static ShopInfoRespDto queryShopByManagerAssert(Long operatorId, String shopCode) {
        List<ShopInfoRespDto> shopInfos = AuthorityServiceAgent.queryShopInfoByManager(UserHolder.getUserId(), shopCode);
        AppBusinessAssert.isTrue(CollectionUtils.isNotEmpty(shopInfos), 40101, "店铺不存在或该账户没有店铺管理权限");

        ShopInfoRespDto shopInfo = shopInfos.get(0);
        AppBusinessAssert.isTrue(shopInfo.getIsEnable(), 40102, "店铺已被禁用");
        return shopInfos.get(0);
    }

    public static List<ShopInfoRespDto> queryShopInfoByManager(Long operatorId, String shopCode) {
        ShopInfoReqDto reqDto = new ShopInfoReqDto();
        reqDto.setShopManagerUserId(operatorId);
        reqDto.setShopCode(shopCode);

        String host = PropertiesContainer.valueOfStringWithAssertNotNull("shopkeeper.service.host");
        String url = String.format("%s/v1/shop/query/base/infos", host);

        String response = HttpSyncClient.sendHttpPostByRetry(url
                , JSON.toJSONString(reqDto)
                , PropertiesContainer.valueOf(String.format("%s.timeout.millis", host)
                        , Long.class, 5000L)
                , TimeUnit.MILLISECONDS
                , 1);
        return ResponseUtil.parseResponseV2WithStatus(response
                , new TypeReference<ResourceResponse<List<ShopInfoRespDto>>>() {
                });
    }
}
