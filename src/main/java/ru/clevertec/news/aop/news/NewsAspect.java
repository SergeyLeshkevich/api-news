package ru.clevertec.news.aop.news;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.CacheFactory;
import ru.clevertec.news.entity.dto.NewsResponse;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Aspect class providing caching functionality for NewsServiceImpl methods.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Aspect
@Component
@Profile("prod")
public class NewsAspect {

    private final CacheFactory<Long, Mono<ResponseEntity<NewsResponse>>> cacheFactory;
    private final Cache<Long, Mono<ResponseEntity<NewsResponse>>> cache;
    private final Lock lock;

    /**
     * Constructor for CommentAspect.
     *
     * @param cacheFactory Factory for creating the cache.
     */
    public NewsAspect(CacheFactory<Long, Mono<ResponseEntity<NewsResponse>>> cacheFactory) {
        this.cacheFactory = cacheFactory;
        cache = cacheFactory.createCache();
        lock = new ReentrantLock();
    }


    /**
     * Implements cache via for the 'create' method in NewsServiceImpl, providing caching.
     */
    @Around("ru.clevertec.news.aop.news.NewsPointcut.pointcutCreateMethod()")
    public Mono<ResponseEntity<NewsResponse>> create(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Mono<ResponseEntity<NewsResponse>> response = (Mono<ResponseEntity<NewsResponse>>) joinPoint.proceed();
            Long id = response.block().getBody().getId();
            cache.put(id, response);
            return response;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Implements cache via for the 'archive' method in NewsServiceImpl, removing item from cache.
     */
    @Around("ru.clevertec.news.aop.news.NewsPointcut.pointcutArchiveMethod()")
    public void archived(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Long id = (Long) joinPoint.getArgs()[0];
            joinPoint.proceed();
            cache.removeByKey(id);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Implements cache via for the 'update' method in NewsServiceImpl, updating cache.
     */
    @Around("ru.clevertec.news.aop.news.NewsPointcut.pointcutUpdateMethod()")
    public Mono<ResponseEntity<NewsResponse>> patch(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Long id = (Long) joinPoint.getArgs()[0];
            Mono<ResponseEntity<NewsResponse>> response = (Mono<ResponseEntity<NewsResponse>>) joinPoint.proceed();
            cache.removeByKey(id);
            cache.put(id, response);
            return response;
        } finally {
            lock.unlock();
        }
    }
}
