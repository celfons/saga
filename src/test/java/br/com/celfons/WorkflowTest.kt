package br.com.celfons

import br.com.celfons.domains.CheckoutWorkflow
import br.com.celfons.domains.CheckoutWorkflow.Status.*
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
            .takeIf { (it.data as Product).isPay!! }
            .apply { this?.flow(paymentService, async = false) }!! /* TODO use conditional to different flow's */
            .save(SUCCESS)

        /*

            TODO implements workflow rules

         */

        Assert.assertEquals(SUCCESS, workflow.status)

    }

}
