package com.coditory.sherlock;

import java.util.HashMap;
import java.util.Map;

import static com.coditory.sherlock.Preconditions.expectNonNull;

final class PrecomputedBindingParameterMapper implements BindingParameterMapper {
    static PrecomputedBindingParameterMapper from(BindingParameterMapper mapper) {
        expectNonNull(mapper, "mapper");
        Map<BindingParameter, BindingParameterMapping> result = new HashMap<>();
        for (String param : SqlLockNamedQueriesTemplate.ParameterNames.ALL_PARAMS) {
            for (int i = 0; i < 10; ++i) {
                BindingParameter bindingParameter = new BindingParameter(i, param);
                BindingParameterMapping mapping = mapper.mapBinding(bindingParameter);
                result.put(bindingParameter, mapping);
            }
        }
        return new PrecomputedBindingParameterMapper(result);
    }

    private final Map<BindingParameter, BindingParameterMapping> mapping;

    private PrecomputedBindingParameterMapper(Map<BindingParameter, BindingParameterMapping> mapping) {
        this.mapping = mapping;
    }

    @Override
    public BindingParameterMapping mapBinding(BindingParameter bindingParameter) {
        BindingParameterMapping result = mapping.get(bindingParameter);
        if (result == null) {
            throw new IllegalArgumentException("Could not find precomputed binding parameter for: " + bindingParameter);
        }
        return result;
    }
}
