package com.hummer.doorgod.service.domain.configuration;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import lombok.Data;

import java.util.Set;

@Data
public class SentinelConfig {
    private FlowRule flowRule;
    private DegradeRule degradeRule;
    private AuthorityRule authorityRule;
    private Set<ApiDefinition> apiDefinitions;
    private Set<GatewayFlowRule> gatewayFlowRules;
}
