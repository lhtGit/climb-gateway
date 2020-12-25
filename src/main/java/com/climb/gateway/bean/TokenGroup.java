package com.climb.gateway.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lht
 * @since 2020/11/26 18:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenGroup {
    /**
     *
     * @author lht
     * @since  2020/11/26 12:31
     */
    private String accessToken;
    /**
     *
     * @author lht
     * @since  2020/11/26 13:30
     */
    private String refreshToken;
}
