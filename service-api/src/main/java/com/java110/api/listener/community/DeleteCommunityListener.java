package com.java110.api.listener.community;

import com.alibaba.fastjson.JSONObject;
import com.java110.api.bmo.community.ICommunityBMO;
import com.java110.api.listener.AbstractServiceApiPlusListener;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.core.smo.community.ICommunityInnerServiceSMO;
import com.java110.dto.community.CommunityDto;
import com.java110.core.event.service.api.ServiceDataFlowEvent;
import com.java110.utils.constant.ServiceCodeConstant;
import com.java110.utils.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * 保存小区侦听
 * add by wuxw 2019-06-30
 */
@Java110Listener("deleteCommunityListener")
public class DeleteCommunityListener extends AbstractServiceApiPlusListener {
    @Autowired
    private ICommunityBMO communityBMOImpl;
    @Autowired
    private ICommunityInnerServiceSMO communityInnerServiceSMOImpl;

    @Override
    protected void validate(ServiceDataFlowEvent event, JSONObject reqJson) {
        //Assert.hasKeyAndValue(reqJson, "xxx", "xxx");

        Assert.hasKeyAndValue(reqJson, "communityId", "小区ID不能为空");

    }

    @Override
    protected void doSoService(ServiceDataFlowEvent event, DataFlowContext context, JSONObject reqJson) {


        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId((String) reqJson.get("communityId"));
        List<CommunityDto> communityDtos = communityInnerServiceSMOImpl.queryCommunitys(communityDto);
        if (communityDtos.size() == 0 || communityDtos == null) {
            throw new IllegalArgumentException("没有查询到communityId为：" + communityDto.getCommunityId() + "小区信息");
        }
        if ("1100".equals(communityDtos.get(0).getState())) {
            throw new IllegalArgumentException("删除失败,该小区已审核通过");
        }
        //添加单元信息
        communityBMOImpl.deleteCommunity(reqJson, context);

    }


    @Override
    public String getServiceCode() {
        return ServiceCodeConstant.SERVICE_CODE_DELETE_COMMUNITY;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }


    public ICommunityInnerServiceSMO getCommunityInnerServiceSMOImpl() {
        return communityInnerServiceSMOImpl;
    }

    public void setCommunityInnerServiceSMOImpl(ICommunityInnerServiceSMO communityInnerServiceSMOImpl) {
        this.communityInnerServiceSMOImpl = communityInnerServiceSMOImpl;
    }
}
