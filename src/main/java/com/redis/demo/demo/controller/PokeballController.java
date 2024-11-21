package com.redis.demo.demo.controller;

import com.redis.demo.demo.entity.Pokeball;
import com.redis.demo.demo.repository.PokeballDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.redis.demo.demo.repository.PokeballDao.HASH_KEY_POKEBALL;

@RestController
@RequestMapping("/pokeball")
public class PokeballController {

    private final PokeballDao pokeballDao;

    @Autowired
    public PokeballController(PokeballDao pokeballDao) {
        this.pokeballDao = pokeballDao;
    }

    @PostMapping
    public Pokeball save(@RequestBody Pokeball pokeball) {
        return pokeballDao.save(pokeball);
    }
    /*
    * The @CachePut annotation is used to update the cache every time a method is executed.
    * It does not check the cache before executing the method; it always runs the method
    * and updates the cache with the results
    * */
    @PutMapping("/{id}")
    @CachePut(key="#id" , value = HASH_KEY_POKEBALL , condition = "#id!=null")
    public Pokeball update(@PathVariable int id , @RequestBody Pokeball pokeball){
        Pokeball updatePokeball = pokeballDao.findPokeballById(id);
        if(id == pokeball.getId()){
            BeanUtils.copyProperties(pokeball , updatePokeball);
        }
        return  updatePokeball;
    }

    @GetMapping
    public List<Pokeball> getAllPokeball(){
        return pokeballDao.findAll();
    }

    /*
    * The @Cacheable annotation is used to cache the result of a method so that the next time
    * the method is called with the same parameters, the cached result will be returned instead
    * of executing the method again.
    * */
    @GetMapping("/{id}")
    @Cacheable(key="#id" , value=HASH_KEY_POKEBALL , unless="#result.power > 1000")
    public Pokeball findPokeball(@PathVariable int id){
        return pokeballDao.findPokeballById(id);
    }

    /*
    * The @CacheEvict annotation is used to remove or evict cache entries.
    * It is typically used when data is updated or deleted, and you want to remove
    * the old or outdated data from the cache.
    * */
    @DeleteMapping("/id")
    @CacheEvict(key = "#id" , value = HASH_KEY_POKEBALL)
    public String remove(@PathVariable int id){
        return pokeballDao.deletePokeball(id);
    }
}
