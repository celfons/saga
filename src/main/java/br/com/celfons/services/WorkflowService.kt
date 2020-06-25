package br.com.celfons.services

import br.com.celfons.domains.Workflow

/*
    TODO extends to implements business rules
*/

abstract class WorkflowService {

    fun call(workflow: Workflow): Workflow = workflow.takeIf { it.rollback!! }
        ?.run { rollbackCall(workflow) }
        ?: run { defaultCall(workflow) }

    abstract fun defaultCall(workflow: Workflow): Workflow

    abstract fun rollbackCall(workflow: Workflow): Workflow

}
