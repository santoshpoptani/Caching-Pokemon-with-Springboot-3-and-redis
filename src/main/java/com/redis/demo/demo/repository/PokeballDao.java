package com.redis.demo.demo.repository;

import com.redis.demo.demo.entity.Pokeball;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PokeballDao {
    public  static final String HASH_KEY_POKEBALL =  "POKEBALL";

    @Autowired
    public RedisTemplate<String , Object> template;

    public Pokeball save(Pokeball pokeball){
        template.opsForHash().put(HASH_KEY_POKEBALL , pokeball.getId() , pokeball);
        return pokeball;
    }

    public List<Pokeball> findAll(){
        System.out.println("Called FindAll from Db");
        return template.opsForHash().values(HASH_KEY_POKEBALL)
                .stream()
                .filter(Pokeball.class::isInstance) // .filter(value -> value instanceof Pokeball)
                .map(Pokeball.class::cast)          //.map(value -> (Pokeball) value)
                .collect(Collectors.toList());
    }
    public Pokeball findPokeballById(int id) {
        System.out.println("called findPokeballById() from db");
        return (Pokeball) template.opsForHash().get(HASH_KEY_POKEBALL, id);
    }

    public String deletePokeball(int id) {
        template.opsForHash().delete(HASH_KEY_POKEBALL, id);
        return "pokeball removed!!";
    }
}
