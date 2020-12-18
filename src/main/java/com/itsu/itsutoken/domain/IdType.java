package com.itsu.itsutoken.domain;

import cn.hutool.core.util.IdUtil;

/**
 * 
 * @ClassName:     IdType.java
 * @Description:   内置的token表的主键id类型 
 * @author         Jerry Su
 * @Date           2020年12月17日 上午10:41:19
 */
public enum IdType {

    SIMPLE_UUID {

        @Override
        public String generateId() {
            return IdUtil.simpleUUID();
        }

    },
    RANDOM_UUID {

        @Override
        public String generateId() {
            return IdUtil.randomUUID();
        }

    },
    OBJECT_ID {

        @Override
        public String generateId() {
            return IdUtil.objectId();
        }

    },
    FAST_UUID {

        @Override
        public String generateId() {
            return IdUtil.fastUUID();
        }

    },
    FAST_SIMPLE_UUID {

        @Override
        public String generateId() {
            return IdUtil.fastSimpleUUID();
        }

    };

    public abstract String generateId();
}