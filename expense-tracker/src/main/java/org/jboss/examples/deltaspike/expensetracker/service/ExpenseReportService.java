package org.jboss.examples.deltaspike.expensetracker.service;

import java.util.Date;
import javax.inject.Inject;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.jboss.examples.deltaspike.expensetracker.data.ExpenseReportRepository;
import org.jboss.examples.deltaspike.expensetracker.model.Employee;
import org.jboss.examples.deltaspike.expensetracker.model.ExpenseReport;
import org.jboss.examples.deltaspike.expensetracker.model.ReportStatus;
import org.picketlink.authorization.annotations.LoggedIn;
import org.picketlink.authorization.annotations.RolesAllowed;
import static org.jboss.examples.deltaspike.expensetracker.app.security.EmployeeRole.*;

@Transactional
@LoggedIn
public class ExpenseReportService {

    @Inject
    private ExpenseReportRepository repo;

    @RolesAllowed(ACCOUNTANT_ROLE)
    public void assign(ExpenseReport report, Employee assignee) {
        if (report == null || assignee == null) {
            throw new IllegalArgumentException();
        }

        if (report.getAssignee() != null) {
            throw new RuntimeException("Report is already assigned");
        }

        if (report.getReporter().equals(assignee)) {
            throw new IllegalStateException("You can't assign a report to yourself");
        }

        if (report.getStatus() == ReportStatus.APPROVED
                || report.getStatus() == ReportStatus.REJECTED
                || report.getStatus() == ReportStatus.SETTLED) {
            throw new IllegalStateException("Report is already resolved");
        }

        report.setAssignee(assignee);

        repo.save(report);
    }

    @RolesAllowed(EMPLOYEE_ROLE)
    public void submit(ExpenseReport report) {
        if (report == null) {
            throw new IllegalArgumentException();
        }

        ReportStatus status = report.getStatus();
        if (status == ReportStatus.SUBMITTED || status == ReportStatus.APPROVED || status == ReportStatus.SETTLED) {
            throw new IllegalStateException("Report has already been submitted");
        }

        if (report.getAssignee() != null && status != ReportStatus.REJECTED) {
            throw new IllegalStateException("Report is already assigned");
        }

        report.setLastSubmittedDate(new Date(System.currentTimeMillis()));
        report.setStatus(ReportStatus.SUBMITTED);
        
        repo.save(report);
    }

    @RolesAllowed({ACCOUNTANT_ROLE, ADMIN_ROLE})
    public void reject(ExpenseReport report) {
        if (report == null) {
            throw new IllegalArgumentException("null");
        }
        if (report.getAssignee() == null) {
            throw new RuntimeException("No assignee");
        }

        report.setStatus(ReportStatus.REJECTED);
        repo.save(report);
    }

    @RolesAllowed({ACCOUNTANT_ROLE, ADMIN_ROLE})
    public void reimburse(ExpenseReport report) {
        if (report == null) {
            throw new IllegalArgumentException("null");
        }
        if (report.getStatus() != ReportStatus.APPROVED) {
            throw new RuntimeException("Report is not approved!");
        }

        report.setStatus(ReportStatus.SETTLED);
        repo.save(report);
    }

    @RolesAllowed({ACCOUNTANT_ROLE, ADMIN_ROLE})
    public void approve(ExpenseReport report) {
        if (report == null) {
            throw new IllegalArgumentException("null");
        }
        if (report.getAssignee() == null) {
            throw new RuntimeException("No assignee");
        }

        report.setApprovedDate(new Date(System.currentTimeMillis()));
        report.setStatus(ReportStatus.APPROVED);
        repo.save(report);
    }
}