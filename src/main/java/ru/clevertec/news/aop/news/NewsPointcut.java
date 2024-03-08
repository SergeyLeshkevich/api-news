package ru.clevertec.news.aop.news;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspect class defining pointcuts for methods in the NewsServiceImpl class.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Aspect
public class NewsPointcut {


    /**
     * Pointcut for the 'create' method in NewsServiceImpl.
     */
    @Pointcut("execution(* ru.clevertec.news.service.NewsServiceImpl.create(..))")
    public void pointcutCreateMethod() {
    }

    /**
     * Pointcut for the 'update' method in NewsServiceImpl.
     */
    @Pointcut("execution(* ru.clevertec.news.service.NewsServiceImpl.update(..))")
    public void pointcutUpdateMethod(){
    }

    /**
     * Pointcut for the 'archive' method in NewsServiceImpl.
     */
    @Pointcut("execution(* ru.clevertec.news.service.NewsServiceImpl.archive(..))")
    public void pointcutArchiveMethod() {
    }
}
