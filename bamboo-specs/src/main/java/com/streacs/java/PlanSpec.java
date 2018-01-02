package com.streacs.java;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepositoryIdentifier;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.BitbucketServerTrigger;
import com.atlassian.bamboo.specs.model.task.ScriptTaskProperties;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.trigger.ScheduledTrigger;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args) throws Exception {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("https://build.streacs.com");

        Plan plan = new PlanSpec().createPlan();

        bambooServer.publish(plan);

        PlanPermissions planPermission = new PlanSpec().createPlanPermission(plan.getIdentifier());

        bambooServer.publish(planPermission);
    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        Permissions permission = new Permissions()
            .userPermissions("sysadmin", PermissionType.ADMIN, PermissionType.CLONE, PermissionType.EDIT)
            .groupPermissions("crowd-administrators", PermissionType.ADMIN)
            .loggedInUserPermissions(PermissionType.VIEW)
            .anonymousUserPermissionView();
        return new PlanPermissions(planIdentifier.getProjectKey(), planIdentifier.getPlanKey()).permissions(permission);
    }

    Project project() {
        return new Project()
            .name("Docker Containers")
            .key("DCK");
    }

    Plan createPlan() {
        return new Plan(
            project(),
            "STREACS Atlassian Jira Software", "B4FE7A")
            .enabled(true)
            .noPluginConfigurations()
            .noNotifications()
            .linkedRepositories("DCK - STREACS Atlassian Jira Software (master)")
            .planBranchManagement(new PlanBranchManagement()
                .createForVcsBranchMatching("^feature/.*|^release/.*|^develop")
                .triggerBuildsLikeParentPlan()
                .delete(new BranchCleanup()
                    .whenInactiveInRepositoryAfterDays(5)))
            .description("Plan created from (https://scm.streacs.com/projects/DCK/repos/streacs_atlassian_jira_software)")
            .triggers(
                new ScheduledTrigger()
                    .description("Nightly Build")
                    .enabled(true)
                    .scheduleOnceDaily(LocalTime.of(00, 00)),
                new BitbucketServerTrigger()
                    .name("Bitbucket Server repository triggered")
                    .description("Commit Trigger")
            )
            .stages(
                new Stage("STAGE_01").jobs(
                    new Job("Default Job", "D43AA6")
                        .requirements(new Requirement("system.docker.executable"))
                        .tasks(
                            checkoutTask()
                        )
                        .tasks(
                            buildTask()
                        )
                        .tasks(
                            testTask()
                        )
                        .tasks(
                            deployTask()
                        )
                        .tasks(
                            removeTask()
                        )
                )
            );
    }

    VcsCheckoutTask checkoutTask() {
        return new VcsCheckoutTask()
            .description("Checkout Repository")
            .checkoutItems(new CheckoutItem()
                .repository(new VcsRepositoryIdentifier()
                    .name("DCK - STREACS Atlassian Jira Software (master)")
                )
            );
    }

    ScriptTask buildTask() {
        return new ScriptTask()
            .description("Build Docker container")
            .location(ScriptTaskProperties.Location.FILE)
            .fileFromPath("Buildfile")
            .argument("build");
    }

    ScriptTask testTask() {
        return new ScriptTask()
            .description("Build Docker container")
            .location(ScriptTaskProperties.Location.FILE)
            .fileFromPath("Buildfile")
            .argument("test");
    }
    ScriptTask deployTask() {
        return new ScriptTask()
            .description("Build Docker container")
            .location(ScriptTaskProperties.Location.FILE)
            .fileFromPath("Buildfile")
            .argument("deploy");
    }
    ScriptTask removeTask() {
        return new ScriptTask()
            .description("Build Docker container")
            .location(ScriptTaskProperties.Location.FILE)
            .fileFromPath("Buildfile")
            .argument("remove");
    }

}