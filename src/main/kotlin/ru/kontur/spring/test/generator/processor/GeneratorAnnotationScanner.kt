package ru.kontur.spring.test.generator.processor

import org.reflections.Reflections
import ru.kontur.spring.test.generator.api.ValidationParamResolver
import ru.kontur.spring.test.generator.api.ValidatorFor
import ru.kontur.spring.test.generator.exceptions.NotResolverException
import ru.kontur.spring.test.generator.exceptions.NotValidateAnnotationException
import ru.kontur.spring.test.generator.exceptions.ResolverNotFoundException
import javax.validation.Constraint
import kotlin.reflect.KClass

/**
 * @author Konstantin Volivach
 * Scan users code and get validations and resolvers for them
 */
class GeneratorAnnotationScanner {
    companion object {
        private const val LIBRARY_PATH = "ru.kontur.spring.test.generator"
        private const val JAVAX_PATH = "javax.validation.constraints"
    }

    fun getDefaultValidatorsMap(): Map<KClass<out Annotation>, ValidationParamResolver> {
        return internalValidatorsMap(JAVAX_PATH, LIBRARY_PATH)
    }

    fun getValidatorsMap(userPath: String): Map<KClass<out Annotation>, ValidationParamResolver> {
        return internalValidatorsMap(userPath, userPath)
    }

    private fun internalValidatorsMap(
        annotationPath: String,
        resolverPath: String
    ): Map<KClass<out Annotation>, ValidationParamResolver> {
        val annotationResolver = Reflections(annotationPath)
        val validationResolver = Reflections(resolverPath)

        val annotationValidators = annotationResolver.getTypesAnnotatedWith(Constraint::class.java).map { it.kotlin }
        val resolvers = validationResolver.getTypesAnnotatedWith(ValidatorFor::class.java).map { it.kotlin }

        val map = mutableMapOf<KClass<out Annotation>, ValidationParamResolver>()
        for (annotationClass in annotationValidators) {
            annotationClass as? KClass<out Annotation> ?: throw NotValidateAnnotationException(
                annotationClass
            )

            val paramResolver =
                resolvers.firstOrNull { it.java.getAnnotation(ValidatorFor::class.java).value == annotationClass }
                    ?: throw ResolverNotFoundException(annotationClass)

            val constructor = paramResolver.constructors.toMutableList()[0]
            val resolver = constructor.call()

            if (resolver is ValidationParamResolver) {
                map[annotationClass] = resolver
            } else {
                throw NotResolverException(paramResolver)
            }
        }
        return map
    }
}