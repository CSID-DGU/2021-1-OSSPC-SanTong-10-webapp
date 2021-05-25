package com.dguossp.santong.repository.redis;

import com.dguossp.santong.entity.redis.RedisGameSearch;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableRedisRepositories
public interface RedisGameSearchRepository extends CrudRepository<RedisGameSearch, String> {


    @Override
    Iterable<RedisGameSearch> findAllById(Iterable<String> iterable);
}
