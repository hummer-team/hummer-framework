package com.hummer.doorgod.service.domain.configuration;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SentinelConfig {
    private List<FlowRule> flowRule;
    private List<DegradeRule> degradeRule2;
    private List<AuthorityRule> authorityRule;
    private Set<ApiDefinition> apiDefinitions;
    private Set<GatewayFlowRule> gatewayFlowRules;
    private SystemRule sysRule;
}
