package com.cyj.xx.Common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;


@Data
@Builder
@AllArgsConstructor
public class UserIdPrincipal implements Principal {
    private final Long userId;



    @Override
    public String getName() {
        return userId.toString();
    }

    public Long getUserId() {
        return userId;
    }
}
