package xyz.nifeather.fmccl.network.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Environment
{
    EnvironmentType value();
}

