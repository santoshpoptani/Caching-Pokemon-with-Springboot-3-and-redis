package com.redis.demo.demo.controller;

import com.redis.demo.demo.entity.Pokedex;
import com.redis.demo.demo.repository.Pokedexrepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.redis.demo.demo.repository.Pokedexrepository.HASH_KEY_POKEDEX;

@Slf4j
@RestController
@RequestMapping("/pokedex")
public class PokedexController {

    private final Pokedexrepository pokedexrepository;

    @Autowired
    public PokedexController(Pokedexrepository pokedexrepository) {
        this.pokedexrepository = pokedexrepository;
    }

    @PostMapping
    @CacheEvict(value="pokeList", allEntries = true)
    public Pokedex save(@RequestBody Pokedex pokedex) {
        return pokedexrepository.save(pokedex);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "pokeList", allEntries = true)
    @CachePut(key="#id", value = HASH_KEY_POKEDEX, condition = "#id!=null")
    public Pokedex update(@PathVariable int id, @RequestBody Pokedex pokedex) {
        Pokedex updatedPokedex = pokedexrepository.findById(id).orElse(null);
        if (updatedPokedex != null && id == pokedex.getId()) {
            BeanUtils.copyProperties(pokedex, updatedPokedex);
            pokedexrepository.save(updatedPokedex);
        }
        return updatedPokedex;
    }

    @GetMapping
    @Cacheable(value="pokeList")
    public List<Pokedex> getAllPokemon() {
        log.info("retrieving getAllPokemon() from db");
        return pokedexrepository.findAll();
    }

    @GetMapping("/{id}")
    @Cacheable(key="#id", value=HASH_KEY_POKEDEX)
    public Pokedex findPokemon(@PathVariable int id) {
        Optional<Pokedex> optionalPokedex = pokedexrepository.findById(id);
        return optionalPokedex.orElse(null);
    }

    @DeleteMapping("/{id}")
    @Caching(evict = {
            @CacheEvict(key="#id", value=HASH_KEY_POKEDEX),
            @CacheEvict(value="pokeList", allEntries = true)
    })
    public void remove(@PathVariable int id) {
        pokedexrepository.deleteById(id);
        log.info("pokemon removed by id");
    }
}
