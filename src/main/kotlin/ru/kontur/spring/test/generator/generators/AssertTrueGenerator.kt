package ru.kontur.spring.test.generator.generators

import ru.kontur.spring.test.generator.api.ValidationParamResolver
import kotlin.reflect.KClass
import kotlin.reflect.KType

class AssertTrueGenerator : ValidationParamResolver {
    override fun <T> process(generatedParam: T?, clazz: KClass<*>, type: KType): Any {
        return true
    }
}