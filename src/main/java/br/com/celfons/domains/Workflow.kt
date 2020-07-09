package br.com.celfons.domains

import br.com.celfons.services.WorkflowService
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.*
import kotlin.jvm.Transient

/*
    TODO extends to implements new workflow
*/

abstract class Workflow(
    open val id: UUID? = UUID.randomUUID(),
    open val data: Any? = null, /* TODO override data type  */
    open val status: Any? = null,
    open var rollback: Boolean? = false,
    open var creationDate: LocalDateTime? = now(),
    open var updatedDate: LocalDateTime? = now(),
    @Transient protected var flows: MutableMap<WorkflowService, Boolean>? = mutableMapOf()
) {

    internal fun flow(service: WorkflowService, async: Boolean? = false): Workflow =
        runCatching {
            this.takeIf { !rollback!! }
                ?.run { flows?.put(service, async!!) }
                .run { call(service, async!!) }
        }
            .onFailure { failure()?.rollback() }
            .getOrDefault(this)

    internal fun save(status: Any?): Workflow =
        runCatching {
            this.apply { updatedDate = now() }
                .apply { updateStatus(status)?.save() }
        }
            .onFailure { failure() }
            .getOrDefault(this)

    private fun rollback(): Workflow =
        this.apply { rollback = true }
            .apply {
                flows?.toList()?.asReversed()?.forEach {
                    runCatching { call(it.first, it.second) }
                        .onFailure { rollbackError() }
                        .getOrDefault(this)
                }
            }

    private fun call(service: WorkflowService, async: Boolean) = this.takeIf { async }
        ?.also { GlobalScope.launch { service.call(it) } }
        ?: also { service.call(it) }

    inline fun insideFlow(function: (workflow: Workflow?) -> Workflow?): Workflow = this

    protected open fun failure(): Workflow? = this /* TODO implements to failure */

    protected open fun updateStatus(status: Any? = null): Workflow? = this /* TODO implements update to workflow status */

    protected open fun save(): Workflow? = this /* TODO implements to persist workflow */

    protected open fun rollbackError(): Workflow? = this /* TODO implements rollback error */

}
