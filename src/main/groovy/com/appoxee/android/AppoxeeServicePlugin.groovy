package com.appoxee.android

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Shivani Garg on 2/8/2017.
 */
class AppoxeeServicePlugin implements Plugin<Project> {

    public final static String JSON_FILE_NAME = 'appoxee.json'

    void apply(Project project) {

       if (!checkForKnownPlugins(project)) {
            project.plugins.whenPluginAdded {
                checkForKnownPlugins(project)
            }
        }
    }

    private void setupPlugin(Project project, boolean isLibrary) {
        if (isLibrary) {
            project.android.libraryVariants.all { variant ->
                handleVariant(project, variant)
            }
        } else {
            project.android.applicationVariants.all { variant ->
                handleVariant(project, variant)
            }
        }
    }

    private static void handleVariant(Project project, def variant) {
        File quickstartFile = project.file(JSON_FILE_NAME)
        File outputDir = project.file("$project.buildDir/generated/res/appoxee/$variant.dirName")

        AppoxeeServicesTask task = project.tasks.create("process${variant.name.capitalize()}AppoxeeServices",
                AppoxeeServicesTask)

        task.quickstartFile = quickstartFile
        task.intermediateDir = outputDir

        variant.registerResGeneratingTask(task, outputDir)
    }

    private boolean checkForKnownPlugins(Project project) {
        if (project.plugins.hasPlugin("android") ||
                project.plugins.hasPlugin("com.android.application")) {
            // this is a bit fragile but since this is internal usage this is ok
            // (another plugin could declare itself to be 'android')
            setupPlugin(project, false)
            return true
        } else if (project.plugins.hasPlugin("android-library") ||
                project.plugins.hasPlugin("com.android.library")) {
            // this is a bit fragile but since this is internal usage this is ok
            // (another plugin could declare itself to be 'android-library')
            setupPlugin(project, true)
            return true
        }
        return false
    }
}
