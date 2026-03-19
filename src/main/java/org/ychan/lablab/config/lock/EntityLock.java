package org.ychan.lablab.config.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对单条实体编辑/删除加分布式锁，避免多管理员并发改同一条数据时互相覆盖。
 * 锁 key：entity-lock:{prefix}:{id}，id 由 idSpel 从方法参数中解析（如 #requestParam.id、#id）。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityLock {

    /**
     * 锁前缀，与 id 组成 key：entity-lock:{prefix}:{id}
     */
    String prefix();

    /**
     * SpEL 表达式，从方法参数中取实体 id，例如 #requestParam.id、#id
     */
    String idSpel();
}
