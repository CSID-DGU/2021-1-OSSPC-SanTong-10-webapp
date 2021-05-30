package com.dguossp.santong.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Getter
@Setter
@RedisHash("search_queue")
public class RedisGameSearch implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String nickname;

    private String winningRate;

}
