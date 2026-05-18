package com.minitaskflow.dashboard;

import com.minitaskflow.users.AppUser;
import com.minitaskflow.users.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final CurrentUserService currentUserService;
    private final DashboardService dashboardService;

    public DashboardController(CurrentUserService currentUserService, DashboardService dashboardService) {
        this.currentUserService = currentUserService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse summary() {
        AppUser user = currentUserService.currentUser();
        return dashboardService.summary(user);
    }
}
