package com.streacs.java;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepository;
import com.atlassian.bamboo.specs.builders.repository.git.GitRepository;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;

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
        BambooServer bambooServer = new BambooServer("http://localhost:8085");

        Plan plan = new PlanSpec().createPlan();

        bambooServer.publish(plan);

        PlanPermissions planPermission = new PlanSpec().createPlanPermission(plan.getIdentifier());

        bambooServer.publish(planPermission);
    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        Permissions permission = new Permissions()
                .userPermissions("admin", PermissionType.ADMIN, PermissionType.CLONE, PermissionType.EDIT)
                .groupPermissions("bamboo-admin", PermissionType.ADMIN)
                .loggedInUserPermissions(PermissionType.VIEW)
                .anonymousUserPermissionView();
        return new PlanPermissions(planIdentifier.getProjectKey(), planIdentifier.getPlanKey()).permissions(permission);
    }

    Project project() {
        return new Project()
                .name("Project Name")
                .key("PRJ");
    }

    Plan createPlan() {
        return new Plan(
                project(),
                "Plan Name", "PLANKEY")
                .description("Plan created from (enter repository url of your plan)")
                .planRepositories(
                        gitRepository()
                )
                .stages(
                        new Stage("Stage 1").jobs(
                                new Job("Job Name", "JOBKEY")
                                        .tasks(
                                                gitRepositoryCheckoutTask(),
                                                scriptTask()
                                        )
                                        .artifacts(artifact())
                        )
                );
    }

    VcsRepository gitRepository() {
        return new GitRepository()
                .name("your-git-repository")
                .url("git@bitbucket.org:your-company/your-repository.git")
                .branch("master");
    }

    VcsCheckoutTask gitRepositoryCheckoutTask() {
        return new VcsCheckoutTask()
                .addCheckoutOfDefaultRepository();
    }

    ScriptTask scriptTask() {
        return new ScriptTask()
                .inlineBody("mkdir target; echo 'hello world' > target/console.out")
                .interpreterShell();
    }

    Artifact artifact() {
        return new Artifact("Build results")
                .location("target")
                .copyPattern("**/*");
    }

}
