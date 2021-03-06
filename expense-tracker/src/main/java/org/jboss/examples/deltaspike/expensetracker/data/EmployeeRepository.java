package org.jboss.examples.deltaspike.expensetracker.data;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.apache.deltaspike.data.api.*;
import org.jboss.examples.deltaspike.expensetracker.data.resources.MainEMResolver;
import org.jboss.examples.deltaspike.expensetracker.domain.model.Employee;

@ApplicationScoped
@Repository
@EntityManagerConfig(entityManagerResolver = MainEMResolver.class)
public interface EmployeeRepository extends EntityRepository<Employee, Long>, EntityManagerDelegate<Employee> {

    public List<Employee> findByEmail(String email);

    public List<Employee> findByLastNameLike(String lastName);

    @Query("select e from Employee e where e.firstName like :part or e.lastName like :part")
    public List<Employee> findByPartOfName(@QueryParam("part") String partOfName);

}
