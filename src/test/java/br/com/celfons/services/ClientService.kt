package br.com.celfons.services

import br.com.celfons.domains.CheckoutWorkflow
import br.com.celfons.domains.Workflow
import org.springframework.stereotype.Service

@Service
open class ClientService: WorkflowService() {

    override fun defaultCall(workflow: Workflow): CheckoutWorkflow = workflow as CheckoutWorkflow /* TODO("not implemented")  */

    override fun rollbackCall(workflow: Workflow): CheckoutWorkflow = workflow as CheckoutWorkflow /* TODO("not implemented")  */

}
