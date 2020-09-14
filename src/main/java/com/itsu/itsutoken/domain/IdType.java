package com.itsu.itsutoken.domain;

import cn.hutool.core.util.IdUtil;

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