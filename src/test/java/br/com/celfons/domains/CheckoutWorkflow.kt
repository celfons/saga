package br.com.celfons.domains

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
open class CheckoutWorkflow(
    @Id override val id: UUID? = UUID.randomUUID(), /* Overridden to use annotation */
    override var data: Product? = null, /* Overridden data type */
    override var status: Status? = Status.INITIAL, /* Overridden status type */
    @Autowired var repository: JpaRepository<CheckoutWorkflow, UUID>
) : Workflow(id, data, status) {

    @Transactional
    override fun save(): Workflow? = this
        .apply { repository.save(this) }
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
