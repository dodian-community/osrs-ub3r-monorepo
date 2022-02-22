package net.dodian.central.services

import com.google.inject.Scope
import dev.misfitlabs.kotlinguice4.KotlinModule

class ServicesModule(private val scope: Scope) : KotlinModule() {

    override fun configure() {
        bind<WorldService>().`in`(scope)
    }
}
