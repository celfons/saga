package br.com.celfons.domains

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class CheckoutWorkflow(
    @Id override var id: UUID? = UUID.randomUUID(), /* Overridden to use annotation */
    override var data: Product? = Product(), /* Overridden data type */
    override var status: Status? = Status.INITIAL, /* Overridden status type */
    @Autowired var repository: JpaRepository<CheckoutWorkflow, UUID>? = null
) : Workflow(id, data, status) {

    constructor(workflow: Workflow?) :  this(workflow?.id, workflow?.data as? Product?, workflow?.status as? Status?)

    @Transactional
    override fun save(): Workflow? = this
        .apply { repository?.save(this) }
        .apply { println("saved: $this") }

    override fun rollbackError(): CheckoutWorkflow = this.apply { throw Exception() }

    override fun updateStatus(status: Any?): CheckoutWorkflow = this.apply { this.status = status as Status? }

    override fun failure(): Workflow? = save(Status.ERROR)

    override fun toString(): String {
        return "CheckoutWorkflow(id=$id, data=$data, status=$status, rollback=$rollback, creationDate=$creationDate, updatedDate=$updatedDate)"
    }

    enum class Status {
        INITIAL,
        PROCESSING,
        SUCCESS,
        ERROR
    }

}
