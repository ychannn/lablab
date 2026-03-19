package org.ychan.lablab.config.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.ychan.lablab.eception.BusinessException;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class EntityLockAspect {

    private static final String LOCK_KEY_PREFIX = "entity-lock:";
    private static final long WAIT_SECONDS = 2;
    private static final long LEASE_SECONDS = 30;

    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(entityLock)")
    public Object around(ProceedingJoinPoint pjp, EntityLock entityLock) throws Throwable {
        Object id = resolveId(pjp, entityLock.idSpel());
        if (id == null) {
            return pjp.proceed();
        }
        String lockKey = LOCK_KEY_PREFIX + entityLock.prefix() + ":" + id;
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(WAIT_SECONDS, LEASE_SECONDS, TimeUnit.SECONDS);
            if (!acquired) {
                throw new BusinessException("该数据正在被他人编辑，请稍后重试");
            }
            return pjp.proceed();
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private Object resolveId(ProceedingJoinPoint pjp, String idSpel) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        try {
            return parser.parseExpression(idSpel).getValue(context);
        } catch (Exception e) {
            return null;
        }
    }
}
