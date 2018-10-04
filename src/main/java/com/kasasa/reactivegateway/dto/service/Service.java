package com.kasasa.reactivegateway.dto.service;

import com.kasasa.reactivegateway.middleware.MiddlewareType;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    private String id;
    private ResolveMethod resolveMethod;
    private ResolveInfo resolveInfo;
    private List<MiddlewareType> inputMiddleware;
    private List<MiddlewareType> outputMiddleware;
}
