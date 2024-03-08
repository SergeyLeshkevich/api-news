package ru.clevertec.news.aop.comment;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspect class defining pointcuts for methods in the CommentServiceImpl class.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Aspect
public class CommentPointcut {

    /**
     * Pointcut for the 'get' method in CommentServiceImpl.
     */
    @Pointcut("execution(* ru.clevertec.news.service.CommentServiceImpl.get(..))")
    public void pointcutGetMethod() { }

    /**
     * Pointcut for the 'create' method in CommentServiceImpl.
     */
    @Pointcut("execution(* ru.clevertec.news.service.CommentServiceImpl.create(..))")
    public void pointcutCreateMethod() { }

    /**
     * Pointcut for the 'update' method in CommentServiceImpl.
     */
    @Pointcut("execution(* ru.clevertec.news.service.CommentServiceImpl.update(..))")
    public void pointcutUpdateMethod() { }

    /**
     * Pointcut for the 'archive' method in CommentServiceImpl.
     */
    @Pointcut("execution(* ru.clevertec.news.service.CommentServiceImpl.archive(..))")
    public void pointcutArchiveMethod() { }
}
