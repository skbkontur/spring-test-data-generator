package ru.kontur.kinfra.kfixture.generators

import ru.kontur.kinfra.kfixture.api.ValidParamGenerator
import ru.kontur.kinfra.kfixture.generators.operators.PlusSupplier
import javax.validation.constraints.Min

/**
 * @author Konstantin Volivach
 */
class MinGenerator<T : Comparable<T>>(
    private val creator: VariableCreator<T>,
    private val plusSupplier: PlusSupplier<T>
) : ValidParamGenerator<T, Min> {
    override fun process(param: T, annotation: Min): T? {
        val min = creator.create(annotation.value)

        return if (param < min) {
            plusSupplier.plus(min, creator.create(1))
        } else {
            param
        }
    }
}