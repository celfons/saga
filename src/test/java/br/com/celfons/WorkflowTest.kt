package br.com.celfons

import br.com.celfons.domains.CheckoutWorkflow
import br.com.celfons.domains.CheckoutWorkflow.Status.ERROR
import br.com.celfons.domains.CheckoutWorkflow.Status.INITIAL
import br.com.celfons.domains.CheckoutWorkflow.Status.PROCESSING
import br.com.celfons.domains.CheckoutWorkflow.Status.SUCCESS
import br.com.celfons.domains.Product
import br.com.celfons.services.ClientService
import br.com.celfons.services.PaymentService
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
open class WorkflowTest {

    @Mock
    lateinit var repository: JpaRepository<CheckoutWorkflow, UUID>

    @Mock
    lateinit var clientService: ClientService

    @Mock
    lateinit var paymentService: PaymentService

    @Test
    fun execute() {

        val product = Product(id = Integer.MAX_VALUE, name = String.toString())

        val workflow = CheckoutWorkflow(data = product, repository = repository)
            .save(INITIAL)
            .flow(clientService, async = true)
            .save(PROCESSING)
            .insideFlow { conditional -> conditional.takeIf { that -> (that?.data as Product).goPay!! }
            ?.flow(paymentService) }
            .save(SUCCESS)

        /*

            TODO implements workflow rules

         */

        Assert.assertEquals(SUCCESS, workflow.status)

    }

    @Test
    fun executeMultiplesFlows() {

        val product = Product(id = Integer.MAX_VALUE, name = String.toString())

        val workflow1 = CheckoutWorkflow(data = product, repository = repository)
            .flow(clientService, async = true)
            .save(INITIAL)

        val workflow2 = repository.findById(workflow1.id!!)
            .takeIf { it.isPresent }?.get()
            ?.flow(paymentService)
            ?.save(PROCESSING)

        CheckoutWorkflow(workflow2)
            .insideFlow {
                conditional -> conditional.takeIf { that -> that?.rollback!! }
                ?.save(ERROR) ?: conditional?.save(SUCCESS)
            }

    }

}
