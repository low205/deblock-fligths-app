package de.maltsev.deblock.math

import org.javamoney.moneta.Money

operator fun Money.minus(percentage: Percentage): Money = subtract(multiply(percentage.value()))

operator fun Money.plus(money: Money): Money = add(money)
