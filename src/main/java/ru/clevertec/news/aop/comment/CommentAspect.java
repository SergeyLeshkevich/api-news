package ru.clevertec.news.aop.comment;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.CacheFactory;
import ru.clevertec.news.entity.dto.CommentResponse;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Aspect class providing caching functionality for CommentServiceImpl methods.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Aspect
@Component
@Profile("prod")
public class CommentAspect {

    private final CacheFactory<Long, Mono<ResponseEntity<CommentResponse>>> cacheFactory;
    private final Cache<Long, Mono<ResponseEntity<CommentResponse>>> cache;
    private final Lock lock;

    /**
     * Constructor for CommentAspect.
     *
     * @param cacheFactory Factory for creating the cache.
     */
    public CommentAspect(CacheFactory<Long, Mono<ResponseEntity<CommentResponse>>> cacheFactory) {
        this.cacheFactory = cacheFactory;
        this.cache = cacheFactory.createCache();
        this.lock = new ReentrantLock();
    }

    /**
     * Implements cache via AOP for the 'get' method in CommentServiceImpl, providing caching.
     */
    @Around("ru.clevertec.news.aop.comment.CommentPointcut.pointcutGetMethod()")
    public Mono<ResponseEntity<CommentResponse>> get(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Long id = (Long) joinPoint.getArgs()[0];
            Mono<ResponseEntity<CommentResponse>> commentResponse = cache.get(id);
            if (commentResponse == null) {
                commentResponse = (Mono<ResponseEntity<CommentResponse>>) joinPoint.proceed();
            }
            cache.put(id, commentResponse);
            return commentResponse;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Implements cache via for the 'create' method in CommentServiceImpl, providing caching.
     */
    @Around("ru.clevertec.news.aop.comment.CommentPointcut.pointcutCreateMethod()")
    public Mono<ResponseEntity<CommentResponse>> create(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Mono<ResponseEntity<CommentResponse>> response = (Mono<ResponseEntity<CommentResponse>>) joinPoint.proceed();
            Long id = response.block().getBody().getNewsId();
            cache.put(id, response);
            return response;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Implements cache via for the 'archive' method in CommentServiceImpl, removing item from cache.
     */
    @Around("ru.clevertec.news.aop.comment.CommentPointcut.pointcutArchiveMethod()")
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
     * Implements cache via for the 'update' method in CommentServiceImpl, updating cache.
     */
    @Around("ru.clevertec.news.aop.comment.CommentPointcut.pointcutUpdateMethod()")
    public Mono<ResponseEntity<CommentResponse>> patch(ProceedingJoinPoint joinPoint) throws Throwable {
        lock.lock();
        try {
            Long id = (Long) joinPoint.getArgs()[0];
            Mono<ResponseEntity<CommentResponse>> response = (Mono<ResponseEntity<CommentResponse>>) joinPoint.proceed();
            cache.removeByKey(id);
            cache.put(id, response);
            return response;
        } finally {
            lock.unlock();
        }
    }
}
